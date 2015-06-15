package mongotrade;

/**
 * Created by mark.mcclellan on 4/22/2015.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        BarCache minute_cache = new BarCache();
        BarCache daily_cache = new BarCache();

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

        //start processing
        for(String line : d_array){
            //System.out.println(line);
            ictr++;

                minute_cache.setTicker(ticker);
                minute_cache.setSource("NF");
                minute_cache.setTz("GMT+0:00");
                daily_cache.setTicker(ticker);
                daily_cache.setSource("NF");
                daily_cache.setTz("GMT+0:00");
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
                        minute_cache.init();
                        daily_cache.init();
                    } else if (!curDate.equals(date)){
                        //load current barcache into mongo.
                        //Store the minute bar
                        //build the _id for the minute bar.
                        String bar_id = minute_cache.getTicker() + ":" + minute_cache.getSource() + ":" + date;
                        minute_cache.setH_id(bar_id);

                        ml.mongo_store_bar(minute_cache,false);
                        minute_cache.init();
                        curDate = date;
                    }


                    //build the minute bar
                    minute_cache.setDay(date);
                    minute_cache.setType("M");
                    minute_cache.setOpen(Double.parseDouble(tchlov[1]));
                    minute_cache.setHigh(Double.parseDouble(tchlov[1]));
                    minute_cache.setLow(Double.parseDouble(tchlov[1]));
                    minute_cache.setClose(Double.parseDouble(tchlov[1]));
                    minute_cache.setVolume(Long.parseLong(tchlov[2]));

                    //build the daily bar
                    daily_cache.setDay(day);
                    daily_cache.setType("S");  //S for Summary
                    daily_cache.setOpen(Double.parseDouble(tchlov[1]));
                    daily_cache.setHigh(Double.parseDouble(tchlov[1]));
                    daily_cache.setLow(Double.parseDouble(tchlov[1]));
                    daily_cache.setClose(Double.parseDouble(tchlov[1]));
                    daily_cache.setVolume(Long.parseLong(tchlov[2]));

                } //end if isNumeric
        } //end for array

        if (date.length() > 10) {
            //make sure we didn't strand a bar
            String bar_id = minute_cache.getTicker() + ":" + minute_cache.getSource() + ":" + date;
            minute_cache.setH_id(bar_id);

            ml.mongo_store_bar(minute_cache, false);

            //we only have data for one day. so store the daily_cache summary bay
            //build the _id for the daily bar.
            bar_id = daily_cache.getTicker() + ":" + daily_cache.getSource() + ":" + day;
            daily_cache.setH_id(bar_id);
            ml.mongo_store_bar(daily_cache, false);

            minute_cache.init();
            daily_cache.init();
        }

    } //end process nf csv

    //convert time from Netfonds central european time to eastern standard time
    public String CET2EST(String time){


        return time;
    } //end convert timezone

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    } //end isNumeric

} // end class

