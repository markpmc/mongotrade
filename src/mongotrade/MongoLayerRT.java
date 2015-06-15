package mongotrade;

/**
 * Created by mark.mcclellan on 4/6/2015.
 This is a rewrite on ML2. ML@ is perfectly suited for end of day (EOD) batch quote storage.
 MLRT separates the metadata (summary) from the minutes array. The minutes are now stored
 individually. Followed by an update to the metadata summary stub that can be used for a
 daily quote.
 */

import com.mongodb.*;

import java.io.*;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MongoLayerRT {
   // static MongoClient mongo = null;
    private static DBCollection collection = null;
    private static DB db = null;
    private static String database = "quotes";
    private static String exPath = "";

    static String s_curDay = "";

    MFConfig config = new MFConfig();
    static MongoLayerRT mgr = new MongoLayerRT();

    public static void main(String[] args) throws UnknownHostException {
       // MongoLayerRT mgr = new MongoLayerRT();


        //collection = mgr.checkConnection("gspc");
        //BarArray ba = mgr.getData("gspc", 10, "M15");
        //mgr.export2Txt("gspc","M15",ba);
        mgr.GTexport("^VIX",10,"M30");
        //System.out.println(ba.getDateArray().toString());
        ///@@
        // Delete All documents before running example again
        //WriteResult result = collection.remove(new BasicDBObject());
        //System.out.println(result.toString());
        ////


        // mongo_store(collection);

/*        System.out.println("running query");


        //let see what we have
        DBCursor tcursor = collection.find();
        int i=1;
        while (tcursor.hasNext()) {
            System.out.println("MLRT Inserted Document: "+i);
            System.out.println(tcursor.next());
            i++;
        }
*/
    }  //end main

    public DBCollection checkConnection(String collection) throws UnknownHostException{
        if(db == null){
            config.checkConfig();
            db = (new MongoClient(config.mHost, config.mPort)).getDB(database);
            exPath = config.exPath;
        }

        //System.out.println("collection=" + collection);

        return db.getCollection(collection);
    }

    public static void mongo_store_bar (BarCache bar, boolean incVol) {
        //Do not reply on header. Goal is to update header if required.
        MongoLayerRT mlrt = new MongoLayerRT();
        YMUtils utils = new YMUtils();
        Date barDate = utils.dateFromString(bar.getDay(),bar.getTz());


        try {
            collection = mlrt.checkConnection(bar.getTicker());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

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
                    metaD.append("$set", new BasicDBObject().append("Date", barDate));
                    metaD.append("$set", new BasicDBObject().append("Type", bar.getType()));
                    //high. $max only stores value if larger than stored value
                    metaD.append("$max", new BasicDBObject().append("High", bar.getHigh()));
                    //low. $min only stores value if smaller than stored value
                    metaD.append("$min", new BasicDBObject().append("Low", bar.getLow()));
                    //volume. $inc increment value by given amount
                    metaD.append("$inc", new BasicDBObject().append("Volume", bar.getVolume()));
                } else {
                    //close. just set. should be last updated value
                    metaD.append("Date", barDate);
                    metaD.append("Type", bar.getType());
                    metaD.append("Open", bar.getOpen());
                    metaD.append("High", bar.getHigh());
                    metaD.append("Low", bar.getLow());
                    metaD.append("Close", bar.getClose());
                    metaD.append("Volume", bar.getVolume());
                }

                 System.out.println(metaQ.toString() + metaD.toString());

                //store the meta data bar data
                collection.update(metaQ, metaD, true, false);


    } //end mongo_store bar


    //get the requested data from the db
    //rollup the bars if required
    public BarArray getData (String ticker,int bars,String type){
        BarArray quote = new BarArray();
        quote.init();
        String rollup = "";
        bars = bars * -1;

        // type
        //D = daily,eod
        //M = 1 mintute bars
        //M5 = 5 minute bars
        //M15 = 15 minute bars
        //M30 = 30 minute bars
        //M60 = 60 minute bars

        if(type.indexOf("M") > -1){
            rollup = type;
            type = "M";
        }

        //Do not reply on header. Goal is to update header if required.
        MongoLayerRT mlrt = new MongoLayerRT();

        try {
            collection = mlrt.checkConnection(ticker);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //find the date range for the query
        Date gtDate = null;
        Date lteDate = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        lteDate = new Date();
        //String todate = dateFormat.format(lteDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, bars);
        gtDate = cal.getTime();
        //String frDate = dateFormat.format(gtDate);

        //System.out.println("From=" + frDate + "::To=" + todate);

        BasicDBObject dateQueryObj = new BasicDBObject("Date",  new BasicDBObject("$gt", gtDate).append("$lte", lteDate));
        BasicDBObject sortPredicate = new BasicDBObject();
        sortPredicate.put("Date", 1);

        //let see what we have
        dateQueryObj.put("Type", type);  //Add type to date range query
        //System.out.println(dateQueryObj.toString());
       // DBCursor cursor = collection.find(dateQueryObj).sort(sortPredicate);
        DBCursor cursor = collection.find(dateQueryObj);

       // System.out.println("doc count=" + tcursor.count());
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            //System.out.println(cursor.curr());

            String sI = getDateFromId(obj.getString("_id"));
            quote.addDate(sI);
            quote.addOpen(Double.parseDouble(obj.getString("Open")));
            quote.addHigh(Double.parseDouble(obj.getString("High")));
            quote.addLow(Double.parseDouble(obj.getString("Low")));
            quote.addClose(Double.parseDouble(obj.getString("Close")));
            quote.addVolume(Double.parseDouble(obj.getString("Volume")));

        } //end while

        //Do we need to rollup the data
        if(!rollup.equals("")){
            quote = rollupBars(quote,rollup);
        }
        return quote;

    } //end getData

    private String getDateFromId(String sid){
        //String date is 3rd member in array
        String sa[] = sid.split(":");
        return sa[2].toString();
    }//end getDateFromId

    //time series management. Rollup minute bars into
    //higher group ie 5 min, 15 min,
    // 30 and 60 min bars require more work
    private BarArray rollupBars(BarArray minutes, String target){
        BarCache cache = new BarCache();
        BarArray newBars = new BarArray();
        cache.init();
        newBars.init();

        String[] date = minutes.getDateArray();
        double[] open = minutes.getOpenArray();
        double[] high = minutes.getHighArray();
        double[] low = minutes.getLowArray();
        double[] close = minutes.getCloseArray();
        double[] vol = minutes.getVolArray();

        int inc = Integer.parseInt(target.replace("M", ""));
        long c_date = 0; //current date
        String sVol = "";
        long lDay = 0;

        int mod = -1;
        int mins = -1;

        //date variables to prevent mixing days if dealing with multiday results
        String c_Day = "";  //curr day
        String l_Day = "first";  //last day

        //loop thru the array grouping the bars by the inc variables
        //be careful not to mix days!
        for(int i = 0; i < date.length; i++) {
            c_Day = date[i].substring(0, 8);
            c_date = Long.parseLong(date[i]);
            mins = Integer.parseInt(date[i].substring(10));
            if(mins == 0){mins=60;}

            mod = mins % inc;

            if(i == 0) { //first run set things up
                cache.setDay(date[i]);
                l_Day = c_Day;
            }

            if (!c_Day.equals(l_Day)) {
                //System.out.println("roll day here");
                //cache.setDay(date[i]);
                l_Day = c_Day;
                mod = 0;  //force mod to ensure last bar of old day is rolled
            }

            if((mod == 0) && (i !=0)){
                //System.out.println("should roll here");
                //move cache to new array
                newBars.addDate(cache.getDay());
                newBars.addOpen(cache.getOpen());
                newBars.addHigh(cache.getHigh());
                newBars.addLow(cache.getLow());
                newBars.addClose(cache.getClose());
                newBars.addVolume(cache.getVolume());
                cache.init();
                cache.setDay(date[i]);
            }

          //  System.out.println("caching -> i="+i+" cDate="+c_date+" Cache="+ cache.getDay());
            cache.setOpen(open[i]);
            cache.setHigh(high[i]);
            cache.setLow(low[i]);
            cache.setClose(close[i]);

            sVol = String.valueOf((int)vol[i]);
            cache.setVolume(Long.parseLong(sVol));
        } //end for date

        //do not abandon partial last roll

        //System.out.println("rolling last minutes");
        //System.out.println("cDate=" + c_date + " Cache" + cache.getDay());
        newBars.addDate(cache.getDay());
        newBars.addOpen(cache.getOpen());
        newBars.addHigh(cache.getHigh());
        newBars.addLow(cache.getLow());
        newBars.addClose(cache.getClose());
        newBars.addVolume(cache.getVolume());

        return newBars;
    } //end rollupBars

    public void export2Txt(String symbol,String period, BarArray bars) {
        YMUtils utils = new YMUtils();
        //build the filename
        String fname = exPath+symbol +"_"+period+".txt";

        //Filename Smybol_timeframe.txt     IBM_10min.txt
        //yyyy-MM-dd hh:mm:ss

        //0: open
        //1: high
        //2: low
        //3: close
        //4: volume
        //5: datetime
        System.out.println("exporting: " +fname);

        //remove file if it exists
        try{
            File oldFile = new File(fname);
            if (oldFile.exists()){
                oldFile.delete();
            }
        }catch(Exception e){
            // if any error occurs
            e.printStackTrace();
        }

        //get the data
        String[] date = bars.getDateArray();
        double[] open = bars.getOpenArray();
        double[] high = bars.getHighArray();
        double[] low = bars.getLowArray();
        double[] close = bars.getCloseArray();
        double[] vol = bars.getVolArray();

        String f_date = "";

        for(int i = 0; i < date.length; i++) {

            //format the date
            f_date = utils.formatExportDate(date[i]);
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fname, true)))) {
                out.println(open[i]+"\t"+high[i]+"\t"+low[i]+"\t"+close[i]+"\t"+vol[i]+"\t"+f_date+"\n");
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        } //end for date.length
    } //end export2Txt

    //GTexport("gspc",10,"M15")
    public void GTexport(String symbol,int days,String period){
        //Get the data
        symbol = symbol.replaceAll("^[^a-zA-Z0-9_-]", "").toLowerCase();
        BarArray ba = mgr.getData(symbol, days, period);

        //write the file
        mgr.export2Txt(symbol,period,ba);
    }
} //end class