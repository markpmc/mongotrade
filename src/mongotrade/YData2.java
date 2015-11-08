package mongotrade;


/**
 * Created by mark.mcclellan on 4/20/2015.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class YData2 {
    private final String USER_AGENT = "Mozilla/5.0";
    static YData2 http = new YData2();
    static MongoLayerRT ml = new MongoLayerRT();

    public static void main(String[] args) throws Exception {
        //String symbol = "^gspc";
        String symbol = "CLV15.NYM";
        //String symbol = "EURUSD=X";
        http.fetchData(symbol);


    } //end main

    public void fetchData(String ticker) throws Exception {

        String url1 = "http://chartapi.finance.yahoo.com/instrument/1.0/";
        String url2 = "/chartdata;type=quote;range=1d/csv";

        String url = url1 + URLEncoder.encode(ticker,"UTF-8") + url2;

        // System.out.println("Testing 1 - Send Http GET request");
        StringBuffer result = http.sendGet(url);

        //System.out.println("result=" + result);

        http.process_yahoo_csv(result);

        //export the data as 5 & 30 min bars in CSV
        ml.CSVexport (ticker,20,"M5");
        ml.CSVexport(ticker, 20, "M30");
        ml.GTexport(ticker,20,"M5");
        ml.GTexport(ticker, 20, "M30");


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
    public void process_yahoo_csv(StringBuffer payload) throws UnknownHostException {
        String s_curDay = "";
        //QuoteHeader qheader = new QuoteHeader();
        BarCache min_quote = new BarCache();
        BarCache day_quote = new BarCache();

        QuoteBody qbody = new QuoteBody();
        YMUtils ymutil = new YMUtils();
        List<String> bodyList = new ArrayList<String>(); // or LinkedList<String>();

        min_quote.init();
        day_quote.init();

        //process the csv file begining with the header
        boolean b_header = true;
        //convert to string
        String data = payload.toString();
        //split into array
        String[] d_array = data.split("\\n",-1);

        int ictr = 0;
        String day = "";
        String date = "";

        //start processing
        for(String line : d_array){
           // System.out.println(line);
            if(b_header) { //process header
                String[] section = line.split(":", -1);

                if(section[0].toString().equals("volume")){
                    b_header = false;
                } //end volume tag check

                //populate the header data structure with values
                if(section[0].equals("ticker")){
                    min_quote.setTicker(section[1].toString());
                    day_quote.setTicker(section[1].toString());
                }else if(section[0].equals("Company-Name")){
                    //qheader.setTickerName(section[1].toString());
                }
                min_quote.setSource("Y");
                min_quote.setTz("GMT-4:00");
                day_quote.setSource("Y");
                day_quote.setTz("GMT-4:00");

                //end load header info


            } else{ //process body
                //we're in the quote section of the data.
                //load it into the body data structure
                String[] section = line.split(",", -1);
                if (line.length() > 6) {  //empty line check
                    ictr++;
                    //System.out.println("Body ct= " + ictr);
                    day = ymutil.unix2day(Long.parseLong(section[0]));

                    date = ymutil.unixtodate(Long.parseLong(section[0]));

                    if(s_curDay.equals("")) {s_curDay = day;}

                    //yahoo sends multiple day in the file.
/*                    //check before we store the new quote
                    if((!s_curDay.equals(day)) && (!s_curDay.equals(""))) {
                        //build the daily bar
                        day_quote.setH_id(day_quote.getTicker() + ":" + day_quote.getSource() + ":" + s_curDay);

                        //Store the daily bar
                        ml.mongo_store_bar(day_quote, false);
                        s_curDay = day;
                    } //end current day check
*/
                    // adjust data:
                    //open = open * adjClose / close;
                    //high = high * adjClose / close;
                    //low = low * adjClose / close;


                    min_quote.setOpen(Double.parseDouble(section[4]));
                    min_quote.setHigh(Double.parseDouble(section[2]));
                    min_quote.setLow(Double.parseDouble(section[3]));
                    min_quote.setClose(Double.parseDouble(section[1]));
                    min_quote.setVolume(Long.parseLong(section[5]));
                    min_quote.setDay(date);
                    min_quote.setType("M");

                    day_quote.setOpen(Double.parseDouble(section[4]));
                    day_quote.setHigh(Double.parseDouble(section[2]));
                    day_quote.setLow(Double.parseDouble(section[3]));
                    day_quote.setClose(Double.parseDouble(section[1]));
                    day_quote.setVolume(Long.parseLong(section[5]));
                    day_quote.setDay(day);
                    day_quote.setType("S"); //S for Summary

                    //build the _id for the data bar.
                    String bar_id = min_quote.getTicker()+":"+ min_quote.getSource()+":"+date;
                    min_quote.setH_id(bar_id);

                    //Store every minute bar we build
                    ml.mongo_store_bar(min_quote,false);
                    min_quote.init();

                } //end empty line check

            } //end process header-body

        } //end for array

        //protect against stranding a daily update
        //build the daily bar
 //       day_quote.setH_id(day_quote.getTicker() + ":" + day_quote.getSource() + ":" + day);

        //Store the daily bar
//        ml.mongo_store_bar(day_quote, false);
    } //end process y csv
} // end class
