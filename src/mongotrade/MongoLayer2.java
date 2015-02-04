package mongotrade;

/**
 * Created by mark.mcclellan on 10/6/2014.
 */

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MongoLayer2 {
    static MongoClient mongo = null;
    static DBCollection collection = null;
  //static QuoteHeader qheader = new QuoteHeader();
 //   static QuoteBody qbody = new QuoteBody();
    static String s_curDay = "";

    public MongoLayer2() {

    }

    public static void main(String[] args) throws UnknownHostException {
        MongoLayer2 mgr;
        mgr = new MongoLayer2();

        mgr.connect("^spc");

        ///@@
        // Delete All documents before running example again
        //WriteResult result = collection.remove(new BasicDBObject());
        //System.out.println(result.toString());
        ////


       // mongo_store(collection);

        DBCursor cursor = collection.find();
        while(cursor.hasNext()) {
          //  System.out.println(cursor.next());
        }
    }

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








                //QuoteBody qb = new QuoteBody();
                //add the row of bar data
                //array.add(new BasicDBObject("time", body.getTimestamp()));
                //array.add(new BasicDBObject("open", body.getOpen()));
                //array.add(new BasicDBObject("high", body.getHigh()));
                //array.add(new BasicDBObject("low", body.getLow()));
                //array.add(new BasicDBObject("close", body.getClose()));
                //array.add(new BasicDBObject("volume", body.getVolume()));
                //dB.put("minutes", array);


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



} //end class