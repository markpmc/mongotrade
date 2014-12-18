package mongotrade;

import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mongodb.*;

/**
 * Created by mark.mcclellan on 11/14/2014.
 */
public class GetConfig {

    public static void main(String[] args) throws UnknownHostException {

        GetConfig config = new GetConfig();
        YData yhttp = new YData();
        GData ghttp = new GData();

        String[] symbols = null;
        String[] sources = null;


        /**** Connect to MongoDB ****/
        MongoClient mongo = new MongoClient("localhost", 27017);

        /**** Get database ****/
        // if database doesn't exists, MongoDB will create it for you
        DB db = mongo.getDB("mf_config");

        /**** Get collection / table from 'testdb' ****/
        // if collection doesn't exists, MongoDB will create it for you
        DBCollection coll = db.getCollection("symbols");

        //query for a list of symbols

            DBCursor cursor2 = coll.find();

            if(cursor2.count() == 0) {
                System.out.println("it's blank jim");
                //Nothing setup, put a few symbols in
                config.addSymbol(coll,"^GSPC","yahoo");
                config.addSymbol(coll,"^DJI","yahoo");
                config.addSymbol(coll,"^IXIC","yahoo");
                config.addSymbol(coll,"^VIX","yahoo");
                config.addSymbol(coll,"^BKX","yahoo");
                config.addSymbol(coll,"^SOX","yahoo");
                config.addSymbol(coll,"^NDX","yahoo");
                config.addSymbol(coll,"EURUSD=X","yahoo");
                config.addSymbol(coll,"EURJPY=X","yahoo");
                config.addSymbol(coll,"GBPUSD=X","yahoo");
                config.addSymbol(coll,".INX","google");
                config.addSymbol(coll,".DJI","google");
                config.addSymbol(coll,".IXIC","google");
                config.addSymbol(coll,"VIX","google");
                config.addSymbol(coll,"AXTEN","google");


            }

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
          //  System.out.println(str);
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


 /*       //let see what we have
        DBCursor tcursor = coll.find();
        int i=1;
        while (tcursor.hasNext()) {
            System.out.println("Inserted Document: "+i);
            System.out.println(tcursor.next());
            i++;
        }
*/

    } //end main

    public void addSymbol(DBCollection collection,String ticker,String source){
        //construct basic config document
        BasicDBObject searchQ = new BasicDBObject();
        searchQ.put("name", "default");

        //add the ticker symbol to the Symbols list
        DBObject modifiedObject =new BasicDBObject();
        modifiedObject.put("$push", new BasicDBObject().append("Symbols", ticker).append("Source",source));
        collection.update(searchQ, modifiedObject,true,false);


    } //end addSymbol
} //end GetConfig
