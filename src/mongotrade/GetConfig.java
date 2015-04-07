package mongotrade;

import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.util.*;

import com.mongodb.*;

/**
 * Created by mark.mcclellan on 11/14/2014.
 */
public class GetConfig {

    static GetConfig config = new GetConfig();
    YData yhttp = new YData();
    GData ghttp = new GData();

    public static void main(String[] args) throws UnknownHostException {

        config.loadConfig();

    } //end main

    public DBCollection connect() throws UnknownHostException {
        /**** Connect to MongoDB ****/
        MongoClient mongo = new MongoClient("localhost", 27017);

        /**** Get database ****/
        // if database doesn't exists, MongoDB will create it for you
        DB db = mongo.getDB("mf_config");

        /**** Get collection / table from 'testdb' ****/
        // if collection doesn't exists, MongoDB will create it for you
        DBCollection collection = db.getCollection("symbols");

        return collection;
    } //end connect
    public void loadConfig() throws UnknownHostException {

        String[] symbols = null;
        String[] sources = null;


        /**** Connect to MongoDB ****/
//        MongoClient mongo = new MongoClient("localhost", 27017);

        /**** Get database ****/
        // if database doesn't exists, MongoDB will create it for you
//        DB db = mongo.getDB("mf_config");

        /**** Get collection / table from 'testdb' ****/
        // if collection doesn't exists, MongoDB will create it for you
//        DBCollection coll = db.getCollection("symbols");

        DBCollection coll = connect();

        //query for a list of symbols

        DBCursor cursor2 = coll.find();

        if(cursor2.count() == 0) {
            System.out.println("it's blank jim");
            //Nothing setup, put a few symbols in
            config.addSymbol("^GSPC","yahoo");
            config.addSymbol("^DJI","yahoo");
            config.addSymbol("^IXIC","yahoo");
            config.addSymbol("^VIX","yahoo");




        }

        //config.removeSymbol("how");


        DBCursor curssc = coll.find();
        while(curssc.hasNext()) {

            DBObject e = curssc.next();
            // System.out.println(e.get("Symbols")) ;
            // System.out.println(e.get("Source")) ;
            BasicDBList symList = (BasicDBList) e.get("Symbols");
            BasicDBList srcList = (BasicDBList) e.get("Source");

            //  System.out.println(symList);

            symbols = symList.toArray(new String[0]);
            sources = srcList.toArray(new String[0]);
        }

        int ctr=0;

        for(Object str : symbols) {
              System.out.println(str);
            try {
                if (sources[ctr].toString().equals("yahoo")) {
                    yhttp.fetchData(str.toString());
                } else if (sources[ctr].toString().equals("google")) {
                    ghttp.fetchDataG(str.toString());
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ctr++;
        } //end for

    } //end loadConfig

    public void addSymbol(String ticker,String source) throws UnknownHostException {
        DBCollection collection = connect();
        //construct basic config document
        BasicDBObject searchQ = new BasicDBObject();
        searchQ.put("name", "default");

        //add the ticker symbol to the Symbols list
        DBObject modifiedObject =new BasicDBObject();
        modifiedObject.put("$push", new BasicDBObject().append("Symbols", ticker).append("Source",source));
        collection.update(searchQ, modifiedObject,true,false);

        //System.out.println("added " + ticker);

    } //end addSymbol

    public void removeSymbol(String ticker) throws UnknownHostException {
        //Load the arrays. Find the symbol to remove, remove the source.
        //rewrite document
        String [] symbols = null;
        String [] sources = null;

        DBCollection collection = connect();

        DBCursor curssc = collection.find();
        while(curssc.hasNext()) {

            DBObject e = curssc.next();
             System.out.println(e.toString()) ;
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

        int ctr=0;

        for(Object str : symbols) {
            //  System.out.println(str);
            try {
                if (!str.equals(ticker)) {
                    newSym.add(str);
                    newSrc.add(sources[ctr]);
                    config.addSymbol(String.valueOf(str),sources[ctr]);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ctr++;
        } //end for

       // System.out.println(newSym.toString());
       // System.out.println(newSrc.toString());

        //we have the new arrays.
        //lets update the config document




    } //end removeSymbol
} //end GetConfig
