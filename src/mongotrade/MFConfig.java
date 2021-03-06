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
    public static String exPath="";
    public static String exFile="";

    //symbol variables
    public static String[] symbols = null;
    public static String[] sources = null;

    static MFConfig config = new MFConfig();

    public static void main(String[] args) throws UnknownHostException, FileNotFoundException {
        //out.println(config.checkConfig());
        //try {
        //    config.loadConfig();
        //} catch (UnknownHostException e) {
        //    e.printStackTrace();
        //}


        MFConfig cfg = new MFConfig();
        cfg.fetch();
        //config.removeSymbol("^VIX");
    } //end main

    public MFConfig(){
       // MFConfig config = new MFConfig();
        this.checkConfig();
        try {
            this.loadConfig();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    public String getSymbolString(String src){
        String symlist = "";
        if(symbols == null){
            config.checkConfig();
            try {
                config.loadConfig();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } //end try
        } //end symbols
        int ctr = 0;
        for (Object str : sources) {
            if (valueOf(str).equals(src)){
                symlist = symlist + "," + symbols[ctr].toString();
            }
            ctr++;
        } //end for

        return symlist;
    } //end getSymbolString
    private DBCollection checkConnection(String collection) throws UnknownHostException{
        if(db == null){
            this.checkConfig();
            db = (new MongoClient(mHost, mPort)).getDB(database);
        }
        return db.getCollection(collection);
    }

    public boolean checkConfig() {

        //if(this.mHost != null) return true;

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
        exPath = props.getProperty("expath");
        exFile = props.getProperty("exfile");

        //System.out.println("loaded exfile:" + exFile);
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
        if(symbols != null) return;

        //query for a list of symbols
        DBCollection coll = connect();
        DBCursor cursor2 = coll.find();

        if (cursor2.count() == 0) {
            out.println("it's blank jim");
            //Nothing setup, put a few symbols in

            BasicDBObject searchQ = new BasicDBObject();
            searchQ.put("name", "default");

            //add the ticker symbol to the Symbols list
            DBObject modifiedObject = new BasicDBObject();
            modifiedObject.put("$push", new BasicDBObject().append("Symbols", "^GSPC").append("Source", "yahoo"));
            coll.update(searchQ, modifiedObject, true, false);

            out.println("added default GSPC:yahoo");

        } //end if no symbols

        //load the config data
        DBCursor curssc = coll.find();

        while (curssc.hasNext()) {

            DBObject e = curssc.next();
            System.out.println(e.get("Symbols")) ;
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
        YDataEOD yeod = new YDataEOD();
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
                    //out.println("keeping " + valueOf(str));
                    config.addSymbol(valueOf(str), sources[ctr]);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ctr++;
        } //end for

        //force reload of symbols
        symbols = null;
        sources = null;

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

    public void export() throws UnknownHostException {
        MongoLayerRT ml = new MongoLayerRT();
        //make sure the symbol list is loaded.
        if(symbols == null) {
            loadConfig();
        }

        if(!exFile.equals("")){
            //process the user created export list
            try {
                BufferedReader reader = new BufferedReader(new FileReader(exFile));
                String line;
                String sym; //symbol
                String tf;  //timeframe
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if(parts[3].equals("tab")) {
                        ml.GTexport(parts[0], parseInt(parts[2]), parts[1]);
                    }else if(parts[3].equals("csv")) {
                        ml.CSVexport(parts[0], parseInt(parts[2]), parts[1]);
                    } //end if
                } //end while
                reader.close();
            } catch (Exception e) {
                System.err.format("Exception occurred trying to read '%s'.", exFile);
                e.printStackTrace();
            }

        } else {
            //user did not create export list.
            //export all symbols with default settings
            int ctr = 0;

            for (Object str : symbols) {
                out.println(str);
                ml.GTexport(str.toString(),7,"M5");
                ml.GTexport(str.toString(), 15, "M30");
                ml.GTexport(str.toString(),200,"D");
                ctr++;
            } //end for

        } //end if
    } //end export

} //end MFConfig
