package mongotrade;

import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mongodb.*;

/**
 * Created by mark.mcclellan on 11/14/2014.
 */
public class GetConfig {

    public static void main(String[] args) {

        GetConfig config = new GetConfig();
        YData http = new YData();

        try{
        /**** Connect to MongoDB ****/
        MongoClient mongo = new MongoClient("localhost", 27017);

        /**** Get database ****/
        // if database doesn't exists, MongoDB will create it for you
        DB db = mongo.getDB("mf_config");

        /**** Get collection / table from 'testdb' ****/
        // if collection doesn't exists, MongoDB will create it for you
        DBCollection coll = db.getCollection("symbols");

        //query for a list of symbols
            /**** Find and display ****/
            //BasicDBObject searchQuery2
            //        = new BasicDBObject().append("name", "mkyong-updated");

            DBCursor cursor2 = coll.find();

            if(cursor2.count() == 0) {
                System.out.println("it's blank jim");
                //Nothing setup, put a few symbols in
                config.addSymbol(coll,"^GSPC");
                config.addSymbol(coll,"^DJI");
                config.addSymbol(coll,"^IXIC");

                //let see what we have
                DBCursor tcursor = coll.find();
                int i=1;
                while (tcursor.hasNext()) {
                    System.out.println("Inserted Document: "+i);
                    System.out.println(tcursor.next());
                    i++;
                }

            }


            //get the list of symbols
           BasicDBList e = (BasicDBList) cursor2.next().get("Symbols");

            Object[] symList = e.toArray();
            for(Object str : symList){
               // System.out.println(str);
                try {
                    http.fetchData(str.toString());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }




            /**** Done ****/
            System.out.println("Done");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }


    } //end main

    public void addSymbol(DBCollection collection,String ticker){
        //construct basic config document
        BasicDBObject searchQ = new BasicDBObject();
        searchQ.put("name", "default");

        //add the ticker symbol to the Symbols list
        DBObject modifiedObject =new BasicDBObject();
        modifiedObject.put("$push", new BasicDBObject().append("Symbols", ticker));
        collection.update(searchQ, modifiedObject,true,false);


    } //end addSymbol
} //end GetConfig
