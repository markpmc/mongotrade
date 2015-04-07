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
import java.util.ArrayList;
import java.util.List;

public class MongoLayerRT {
    static MongoClient mongo = null;
    static DBCollection collection = null;
    //static QuoteHeader qheader = new QuoteHeader();
    //   static QuoteBody qbody = new QuoteBody();
    static String s_curDay = "";

    public MongoLayerRT() {

    }

    public static void main(String[] args) throws UnknownHostException {
        MongoLayerRT mgr;
        mgr = new MongoLayerRT();

        mgr.connect("^spc");

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
    public void connect(String strTicker) throws UnknownHostException {
        mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB("quotes");
        collection = db.getCollection(strTicker);


    }

    //store the quotes
    public static void mongo_store (QuoteHeader header,List<String> bodyList)throws UnknownHostException {
        QuoteBody body = new QuoteBody();
        YMUtils ymutil = new YMUtils();
        DBCursor cursor;
        BasicDBObject query;
        //System.out.println("mongo store" + body.getTimestamp());

        //donot process something empty
        if(bodyList.size() == 0){
            return;
        }

        // System.out.println("Bodylist=" + bodyList.size());

        //connect to mongo
        mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB("quotes");
        collection = db.getCollection(header.getTicker());




        //Prep the array for the minute bars
        BasicDBObject dB = new BasicDBObject();
        DBObject              dbo;
        ArrayList< DBObject > array = new ArrayList< DBObject >();
        String day = new String();

        for (String line : bodyList) {
            String[] section = line.split(",", -1);
            if (section[0].length() > 6) {  //empty line check
                //check for multi-days in data
                day = ymutil.unix2day(Long.parseLong(section[0]));
                body.setTimestamp(section[0]);
                body.setClose(section[1]);
                header.setClose(Float.parseFloat(section[1]));
                body.setHigh(section[2]);
                header.setHigh(Float.parseFloat(section[2]));
                body.setLow(section[3]);
                header.setLow(Float.parseFloat(section[3]));
                body.setOpen(section[4]);
                header.setOpen(Float.parseFloat(section[4]));
                body.setVolume(section[5]);
                header.setVolume(Long.parseLong(section[5]));
                header.setEntry();

                String s_id = header.getTicker() +":"+ day;
                //update the header
                dB.put("_id",s_id);
                dB.put("symbol", header.getTicker());
                dB.put("Day", day);
                dB.put("Open", header.getOpen());
                dB.put("High", header.getHigh());
                dB.put("Low", header.getLow());
                dB.put("Close", header.getClose());
                dB.put("Volume", header.getVolume());
                dB.put("Source", header.getSource());
                dB.put("Entries", header.getEntries());

                //upsert - update or insert
                BasicDBObject searchQ = new BasicDBObject();
                searchQ.put("Day", day);
                searchQ.put("symbol", header.getTicker());
                collection.update(searchQ, dB, true, false);

                //Work on pushing the detail data to the array
                //body.generateId();


                dbo = body.bsonFromPojo();
                array.add( dbo );
                // dB.put("minutes", array);
                //dB.put("$push", new BasicDBObject( "minutes", array ));










            } //end length check
        } //end for bodyList


        //Update the header.
        //upsert - update or insert
        //Do we already have a document in the db?
        BasicDBObject searchQ = new BasicDBObject();
        searchQ.put("Day", day);
        searchQ.put("symbol", header.getTicker());
/*
        //collection.update(searchQ, push_data);
       collection.update(searchQ, dB, true, false);
*/
        BasicDBObject update = new BasicDBObject();
        //update.put( "$push", new BasicDBObject( "minutes", array ) );
        //update the header
        update.put("symbol", header.getTicker());
        update.put("Day", day);
        update.put("Open", header.getOpen());
        update.put("High", header.getHigh());
        update.put("Low", header.getLow());
        update.put("Close", header.getClose());
        update.put("Volume", header.getVolume());
        update.put("Source", header.getSource());
        update.put("Entries", header.getEntries());
        update.append("minutes",array);
        //update.put("minutes",array);
        collection.update( searchQ, update, true, false );

        //let see what we have
        DBCursor tcursor = collection.find();
        int i=1;
        while (tcursor.hasNext()) {
            System.out.println("ML2 Inserted Document: "+i);
            System.out.println(tcursor.next());
            i++;
        }


        //what did we update
        /**** Find and display ****/
/*
        DBCursor cursor2 = collection.find(searchQ);

        while (cursor2.hasNext()) {
            System.out.println(cursor2.next());
        }
*/
    } //end mongo store

    public static void mongo_store_rt (QuoteHeader header,List<String> bodyList)throws UnknownHostException {
     //Do not reply on header. Goal is to update header if required.
        MongoLayerRT mlrt = new MongoLayerRT();
        QuoteBody body = new QuoteBody();
        YMUtils ymutil = new YMUtils();
        DBCursor cursor;
        BasicDBObject query;
        //System.out.println("mongo store" + body.getTimestamp());

        //donot process something empty
        if(bodyList.size() == 0){
            return;
        }

        //connect to db
        mlrt.connect(header.getTicker());

        //Prep the array for the minute bars
        BasicDBObject dB = new BasicDBObject();
        //BasicDBObject metaD = new BasicDBObject();
        DBObject              dbo;
        ArrayList< DBObject > array = new ArrayList< DBObject >();
        String day = new String();

        for (String line : bodyList) {
            String[] section = line.split(",", -1);
            if (section[0].length() > 6) {  //empty line check
                //check for multi-days in data
                day = ymutil.unixtodate(Long.parseLong(section[0]));
                body.setTimestamp(section[0]);
                body.setClose(section[1]);
                header.setClose(Float.parseFloat(section[1]));
                body.setHigh(section[2]);
                header.setHigh(Float.parseFloat(section[2]));
                body.setLow(section[3]);
                header.setLow(Float.parseFloat(section[3]));
                body.setOpen(section[4]);
                header.setOpen(Float.parseFloat(section[4]));
                body.setVolume(section[5]);
                header.setVolume(Long.parseLong(section[5]));
                header.setEntry();

                String s_id = header.getTicker() +":"+ day;
                //update the header
                dB.append("_id", s_id);
                dB.append("symbol", header.getTicker());
                dB.append("DayTime", day);
                dB.append("Open", section[4]);
                dB.append("High", section[2]);
                dB.append("Low", section[3]);
                dB.append("Close", section[1]);
                dB.append("Volume", section[5]);
                dB.append("Source", header.getSource());

                //upsert - update or insert
                BasicDBObject searchQ = new BasicDBObject();
                searchQ.put("DayTime", day);
                searchQ.put("symbol", header.getTicker());

                //store the minute bar data
                collection.update(searchQ, dB, true, false);


                //build the _id and day for the meta record
                day = ymutil.unix2day(Long.parseLong(section[0]));
                s_id = header.getTicker() +":"+ day+ ":meta";

                //stackoverflow example
                //update = update.append("$set", new BasicDBObject().append("endTime", time));
                //collection.update( new BasicDBObject().append("_id", pageId), update, true, false);

                //update the meta stub
                BasicDBObject metaD = new BasicDBObject();
               // metaD.append("_id", s_id); Document id is handed by the search query
               // metaD.append("$setOnInsert",new BasicDBObject().append("symbol", header.getTicker()));
               // metaD.append("$setOnInsert",new BasicDBObject().append("Source", header.getSource()));
               // metaD.append("$setOnInsert",new BasicDBObject().append("Day", day));


                //open. $setOnInsert only stores value upon document creation
                metaD.append("$setOnInsert", new BasicDBObject().append("Open", section[4]));

                //high. $max only stores value if larger than stored value
                metaD.append("$max", new BasicDBObject().append("High", section[2]));

                //low. $min only stores value if smaller than stored value
                metaD.append("$min", new BasicDBObject().append("Low", section[3]));

                //close. just set. should be last updated value
                metaD.append("$set", new BasicDBObject().append("Close", section[1]));

                //volume. $inc increment value by given amount
                metaD.append("$inc", new BasicDBObject().append("Volume", Long.parseLong(section[5])));

               // System.out.println(metaD.toString());


                //upsert - update or insert
                BasicDBObject metaQ = new BasicDBObject();
                metaQ.put("_id", s_id);

                //store the minute bar data
                collection.update(metaQ, metaD, true, false);
/*
                DBCursor mC = collection.find(metaQ);
                while (mC.hasNext()) {
                    System.out.println(mC.next());
                }
*/

            } //end length check
        } //end for bodyList

        //let see what we have
        DBCursor tcursor = collection.find();
        int i=1;
        while (tcursor.hasNext()) {
            System.out.println("MLRT Inserted Document: "+i);
            System.out.println(tcursor.next());
            i++;
        }

    }

} //end class