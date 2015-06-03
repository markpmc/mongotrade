package mongotrade;

import com.mongodb.*;

import java.io.*;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.lang.System.out;

/**
 * Created by mark.mcclellan on 11/14/2014.
 */
public class MFConfig {


    //mongodb variables
    public static String mHost = ""; //"localhost"
    public static int mPort = 0; //27017
    private static DB db = null;
    private static String database = "mf_config";

    //symbol variables
    static String[] symbols = null;
    static String[] sources = null;

    static MFConfig config = new MFConfig();


    public static void main(String[] args) throws UnknownHostException, FileNotFoundException {
        out.println(config.checkConfig());
        try {
            config.loadConfig();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        config.fetch();
        //config.removeSymbol("^VIX");
    } //end main

    private static DBCollection checkConnection(String collection) throws UnknownHostException{
        if(db == null){
            config.checkConfig();
            db = (new MongoClient(mHost, mPort)).getDB(database);
        }
        return db.getCollection(collection);
    }

    public boolean checkConfig() {
        File configFile = new File("config.properties");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            return false;
        }

        //Load the config since we found it
        Properties props = new Properties();

        try {
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mHost = props.getProperty("host");
        mPort = parseInt(props.getProperty("port"));

        //System.out.println("loaded host:" + mHost + ":" + mPort);
        return true;
    }//end checkConfig

    public void updateConfig(String nHost, String nPort) throws IOException {

        Properties props = new Properties();

        props.setProperty("host", nHost);
        props.setProperty("port", nPort);

        //write prop file
        File configFile = new File("config.properties");
        FileWriter writer = new FileWriter(configFile);
        props.store(writer, "host settings");
        writer.close();


    }//end updateConfig

    public void batchSymbols() {


    }//end batchSymbols

    public DBCollection connect() throws UnknownHostException {
        DBCollection collection = checkConnection("symbols");
        return collection;
    } //end connect

    public void loadConfig() throws UnknownHostException {

        //query for a list of symbols
        DBCollection coll = connect();
        DBCursor cursor2 = coll.find();

        if (cursor2.count() == 0) {
            out.println("it's blank jim");
            //Nothing setup, put a few symbols in
            config.addSymbol("^GSPC", "yahoo");
            config.addSymbol("^DJI", "yahoo");
            config.addSymbol("^IXIC", "yahoo");
            config.addSymbol("^VIX", "yahoo");
        } //end if no symbols

        //load the config data
        DBCursor curssc = coll.find();

        while (curssc.hasNext()) {

            DBObject e = curssc.next();
            // System.out.println(e.get("Symbols")) ;
            // System.out.println(e.get("Source")) ;
            BasicDBList symList = (BasicDBList) e.get("Symbols");
            BasicDBList srcList = (BasicDBList) e.get("Source");


            //load the symbol,source list into the arrays
            symbols = symList.toArray(new String[0]);
            sources = srcList.toArray(new String[0]);
        }


    } //end loadConfig


    public void fetch() throws UnknownHostException {
        YData2 yhttp = new YData2();
        GData2 ghttp = new GData2();
        NetFonds nhttp = new NetFonds();

        //make sure the symbol list is loaded.
        if(symbols == null) {
            loadConfig();
        }

        int ctr = 0;

        for (Object str : symbols) {
            out.println(str);
            try {
                if (sources[ctr].toString().equals("yahoo")) {
                    yhttp.fetchData(str.toString());
                } else if (sources[ctr].toString().equals("google")) {
                    ghttp.fetchDataG(str.toString());
                } else if (sources[ctr].toString().equals("netfonds")) {
                    nhttp.fetchDataNF(str.toString());
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ctr++;
        } //end for

    } //end fetch

    public void addSymbol(String ticker, String source) throws UnknownHostException {
        DBCollection collection = connect();
        //construct basic config document
        BasicDBObject searchQ = new BasicDBObject();
        searchQ.put("name", "default");

        //add the ticker symbol to the Symbols list
        DBObject modifiedObject = new BasicDBObject();
        modifiedObject.put("$push", new BasicDBObject().append("Symbols", ticker).append("Source", source.toLowerCase()));
        collection.update(searchQ, modifiedObject, true, false);

        out.println("added " + ticker + ":" + source);

    } //end addSymbol

    public void removeSymbol(String ticker) throws UnknownHostException {
        //Load the arrays. Find the symbol to remove, remove the source.
        //rewrite document
        String[] symbols = null;
        String[] sources = null;

        DBCollection collection = connect();

        DBCursor curssc = collection.find();
        while (curssc.hasNext()) {

            DBObject e = curssc.next();
            out.println(e.toString());
            // System.out.println(e.get("Source")) ;
            BasicDBList symList = (BasicDBList) e.get("Symbols");
            BasicDBList srcList = (BasicDBList) e.get("Source");

            //  System.out.println(symList);

            symbols = symList.toArray(new String[0]);
            sources = srcList.toArray(new String[0]);
        }

        //We have the config in memory.remove the old one
        //remove current config
        BasicDBObject searchQ = new BasicDBObject();
        searchQ.put("name", "default");

        collection.remove(searchQ);
        //its gone jim


        List newSym = new LinkedList();
        List newSrc = new LinkedList();

        int ctr = 0;

        out.println("removing " + ticker);
        for (Object str : symbols) {
            //  System.out.println(str);
            try {
                if (!str.equals(ticker)) {
                    newSym.add(str);
                    newSrc.add(sources[ctr]);
                    out.println("keeping " + valueOf(str));
                    config.addSymbol(valueOf(str), sources[ctr]);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ctr++;
        } //end for

        //System.out.println(newSym.toString());
        //System.out.println(newSrc.toString());

        //we have the new arrays.
        //lets update the config document
    } //end removeSymbol

    public void listSymbols() {
        try {
            config.checkConfig();
            config.loadConfig();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        int ctr = 0;
        for (Object str : symbols) {
            out.println("Symbol: " + valueOf(str) + ":" + sources[ctr]);
            ctr++;
        } //end for
        //System.out.println(symbols.toString());
        //System.out.println(sources.toString());
    }
} //end MFConfig
