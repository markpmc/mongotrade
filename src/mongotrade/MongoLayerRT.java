package mongotrade;

/**
 * Created by mark.mcclellan on 4/6/2015.
 This is a rewrite on ML2. ML@ is perfectly suited for end of day (EOD) batch quote storage.
 MLRT separates the metadata (summary) from the minutes array. The minutes are now stored
 individually. Followed by an update to the metadata summary stub that can be used for a
 daily quote.
 */

import com.mongodb.*;

import java.net.UnknownHostException;

public class MongoLayerRT {
    static MongoClient mongo = null;
    static DBCollection collection = null;
    //static QuoteHeader qheader = new QuoteHeader();
    //   static QuoteBody qbody = new QuoteBody();
    static String s_curDay = "";



    public static void main(String[] args) throws UnknownHostException {
        MongoLayerRT mgr;
        mgr = new MongoLayerRT();

        mgr.connect();

        DB db = mongo.getDB("quotes");
        collection = db.getCollection("^gspc");

        ///@@
        // Delete All documents before running example again
        //WriteResult result = collection.remove(new BasicDBObject());
        //System.out.println(result.toString());
        ////


        // mongo_store(collection);

        System.out.println("running query");


        //let see what we have
        DBCursor tcursor = collection.find();
        int i=1;
        while (tcursor.hasNext()) {
            System.out.println("MLRT Inserted Document: "+i);
            System.out.println(tcursor.next());
            i++;
        }

    }  //end main

    //handle the connection to mongo and the correct collection
    public MongoClient connect() throws UnknownHostException {
        MFConfig config = new MFConfig();
        config.checkConfig();
        mongo = new MongoClient(config.mHost, config.mPort);

       // DB db = mongo.getDB("quotes");
       // collection = db.getCollection(strTicker);

        return mongo;
    }

    public static void mongo_store_bar (BarCache bar, boolean incVol) {
        //Do not reply on header. Goal is to update header if required.
        MongoLayerRT mlrt = new MongoLayerRT();

        //connect to db
        try {
            mlrt.connect();
        } catch (UnknownHostException e) {
            System.out.println("failed to connect");
            e.printStackTrace();
        }


        DB db = mongo.getDB("quotes");

        collection = db.getCollection(bar.getTicker());


                //_id is built in calling proc and is simply passed in
                //expected ticker:source:dayframe

                BasicDBObject metaQ = new BasicDBObject();
                metaQ.put("_id", bar.getH_id());

                //update the meta stub
                BasicDBObject metaD = new BasicDBObject();


                if (incVol) {
                    metaD.append("$setOnInsert", new BasicDBObject().append("Open", bar.getOpen()));
                    //close. just set. should be last updated value
                    metaD.append("$set", new BasicDBObject().append("Close", bar.getClose()));
                    //high. $max only stores value if larger than stored value
                    metaD.append("$max", new BasicDBObject().append("High", bar.getHigh()));
                    //low. $min only stores value if smaller than stored value
                    metaD.append("$min", new BasicDBObject().append("Low", bar.getLow()));
                    //volume. $inc increment value by given amount
                    metaD.append("$inc", new BasicDBObject().append("Volume", Long.parseLong(bar.getVolume())));
                } else {
                    //close. just set. should be last updated value
                    metaD.append("Open", bar.getOpen());
                    metaD.append("High", bar.getHigh());
                    metaD.append("Low", bar.getLow());
                    metaD.append("Close", bar.getClose());
                    metaD.append("Volume", Long.parseLong(bar.getVolume()));
                }

                 System.out.println(metaQ.toString() + metaD.toString());

                //store the meta data bar data
                collection.update(metaQ, metaD,true,false);


        // System.out.println(metaD.toString());
        //let see what we have
/*        Pattern p = Pattern.compile("20150428");
       //"gspc:Y:20150422(\\s|$)"  -- match just daily summary

        BasicDBObject spxQ = new BasicDBObject();
        spxQ.put("_id", p );
        DBCursor tcursor = collection.find(spxQ); //collection that was just updated.
        int i=1;
       // System.out.println("doc count=" + tcursor.count());
        while ((tcursor.hasNext()) && (i < 11)) {
            System.out.println("MLRT Inserted Document: "+i);
            System.out.println(tcursor.next());
            i++;

        } //end while
*/

    } //end mongo_store bar



} //end class