package mongotrade;

/**
 * Created by mark.mcclellan on 4/22/2015.
 */

import com.sun.deploy.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;

import static com.sun.deploy.util.StringUtils.*;

public class NetFonds {
    private final String USER_AGENT = "Mozilla/5.0";
    static NetFonds http = new NetFonds();
    List<String> bodyList = new ArrayList<String>(); // or LinkedList<String>();

    public static void main(String[] args) throws Exception {
        String symbol = "QQQ.O";
        http.fetchDataNF(symbol);


    } //end main

    public void fetchDataNF(String ticker) throws Exception {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        String url1 = "http://www.netfonds.no/quotes/tradedump.php?date=" + sdf.format(date) + "&paper=";
        String url2 = "&csv_format=csv";

        String url = url1 + URLEncoder.encode(ticker,"UTF-8") + url2;

        StringBuffer result = http.sendGet(url);

        //System.out.println("netfonds=\n" + result);

       http.process_netfonds_csv(ticker, result);

    } //end fetchData

    // HTTP GET request
    private StringBuffer sendGet(String url) throws Exception {

        //String url = "http://chartapi.finance.yahoo.com/instrument/1.0/%5EGSPC/chartdata;type=quote;range=10d/json";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
            //System.out.println(inputLine);
        }
        in.close();

        return response;

    } //end sendGet

    //receives a String Buffer containing csv data from Yahoo.
    //loops thru processing the header and body.
    //replies upon QuoteBody and QuoteHeader as data structures
    public void process_netfonds_csv(String ticker, StringBuffer payload) throws UnknownHostException {
        String s_curDay = "Dummy";
        //QuoteHeader qheader = new QuoteHeader();
        //QuoteBody qbody = new QuoteBody();
        BarCache minute_cache = new BarCache();

        YMUtils ymutil = new YMUtils();
        MongoLayerRT ml = new MongoLayerRT();

        String workingDayTime = "";

        //convert to string
        String data = payload.toString();
        //split into array
        String[] d_array = data.split("\\n",-1);

        int ictr = 0;

        String day = "";
        String date = "";
        String time = "";
        String curDate = "";

        int d_len = d_array.length;

        //start processing
        for(String line : d_array){
        //for (int i = 0; i < d_len; i++){
            ictr++;

                minute_cache.setTicker(ticker);
                minute_cache.setSource("NF");

                //process body
                String[] tchlov = line.split(",", -1);
                String uDateStamp = tchlov[0];

                //len check eliminates the header line
                if (uDateStamp.length() > 10) {

                    day = tchlov[0].substring(0,8);  //8 digit date
                    time = tchlov[0].substring(9,13);//HHMM
                    date = day+time;

                    if(curDate.equals("")){
                        curDate = date;
                        minute_cache.initDay();
                    } else if (!curDate.equals(date)){
                        //load current barcache into mongo.
                        //Store the minute bar
                        //build the _id for the minute bar.
                       // System.out.println("calling mongo store");
                        String bar_id = minute_cache.getTicker() + ":" + minute_cache.getSource() + ":" + date;
                        minute_cache.setH_id(bar_id);

                        ml.mongo_store_bar(minute_cache);
                        //build the _id for the daily bar.
                        bar_id = minute_cache.getTicker() + ":" + minute_cache.getSource() + ":" + day;
                        minute_cache.setH_id(bar_id);
                        ml.mongo_store_bar(minute_cache);
                        minute_cache.initDay();
                        curDate = date;
                    }


                    //build the minute bar
                    minute_cache.setDay(date);
                    minute_cache.setOpen(tchlov[1]);
                    minute_cache.setHigh(tchlov[1]);
                    minute_cache.setLow(tchlov[1]);
                    minute_cache.setClose(tchlov[1]);
                    minute_cache.setVolume(Long.parseLong(tchlov[2]));





                    //build the _id for the daily bar.
                  //  bar_id = qheader.getTicker() + ":" + qheader.getSource() + ":" + day;
                  //  qheader.setH_id(bar_id);
                    //System.out.println("storing " + qheader.getOpen() + " " + qheader.getVolume());

                    //Store the daily bar
                   // ml.mongo_store_bar(qheader);
                } //end if isNumeric
        } //end for array

        //make sure we didn't strand a bar
        String bar_id = minute_cache.getTicker() + ":" + minute_cache.getSource() + ":" + date;
        minute_cache.setH_id(bar_id);

        ml.mongo_store_bar(minute_cache);

        //build the _id for the daily bar.
        bar_id = minute_cache.getTicker() + ":" + minute_cache.getSource() + ":" + day;
        minute_cache.setH_id(bar_id);
        ml.mongo_store_bar(minute_cache);

        minute_cache.initDay();


    } //end process nf csv

    //convert time from Netfonds central european time to eastern standard time
    public String CET2EST(String time){
        Calendar europeCal = new GregorianCalendar(TimeZone.getTimeZone(""));
/*
        // Given a time of 10am in Japan, get the local time
       // japanCal = new GregorianCalendar(TimeZone.getTimeZone("Japan"));
        europeCal.set(Calendar.HOUR_OF_DAY, 10);            // 0..23
        europeCal.set(Calendar.MINUTE, 0);
        europeCal.set(Calendar.SECOND, 0);

// Create a Calendar object with the local time zone and set
// the UTC from japanCal
        Calendar estCal = new GregorianCalendar();
        estCal.setTimeInMillis(europeCal.getTimeInMillis());

// Get the time in the local time zone
        hour = local.get(Calendar.HOUR);                   // 5
        minutes = local.get(Calendar.MINUTE);              // 0
        seconds = local.get(Calendar.SECOND);              // 0
        am = local.get(Calendar.AM_PM) == Calendar.AM;     // false
*/
        return time;
    } //end convert timezone

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
    private String get_gday(String uDate) {
        YMUtils ymutil = new YMUtils();
        //it's an 'a' followed by a unix date
        //uDate = uDate.substring(1);
        String day = ymutil.unix2day(Long.parseLong(uDate));
        return day;
    }
} // end class

