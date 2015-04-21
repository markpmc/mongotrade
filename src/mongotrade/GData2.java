package mongotrade;

/**
 * Created by mark.mcclellan on 11/21/2014.
 */

import com.sun.deploy.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static com.sun.deploy.util.StringUtils.*;

public class GData2 {
    private final String USER_AGENT = "Mozilla/5.0";
    static GData2 http = new GData2();
    List<String> bodyList = new ArrayList<String>(); // or LinkedList<String>();

    public static void main(String[] args) throws Exception {
        String symbol = ".INX";
        http.fetchDataG(symbol);


    } //end main

    public void fetchDataG(String ticker) throws Exception {

        //10 day
        //String url1 =  "http://www.google.com/finance/getprices?i=60&p=10d&f=d,o,h,l,c,v&df=cpct&q=";

        //1 day
        String url1 =  "http://www.google.com/finance/getprices?i=60&p=1d&f=d,o,h,l,c,v&df=cpct&q=";


        String url = url1 + URLEncoder.encode(ticker,"UTF-8");

        // System.out.println("Testing 1 - Send Http GET request");
        StringBuffer result = http.sendGet(url);

        //System.out.println("google=\n" + result);

        http.process_google_csv(ticker, result);

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
    public void process_google_csv(String ticker, StringBuffer payload) throws UnknownHostException {
        String s_curDay = "Dummy";
        QuoteHeader qheader = new QuoteHeader();
        QuoteBody qbody = new QuoteBody();
        YMUtils ymutil = new YMUtils();
        MongoLayerRT ml = new MongoLayerRT();

        String workingDayTime = "";

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

            if(line.length()<2){
                break;
            }

            String[] section = line.split("=", -1);

            if(b_header) {
                //populate the header data structure with values
                qheader.setTicker(ticker);
                qheader.setSource("G");
                if(section[0].toString().equals("TIMEZONE_OFFSET")){
                    b_header = false;
                } //end timezone tag check
            } else {
                //process body
                String[] tchlov = line.split(",", -1);
                String uDateStamp = tchlov[0];



                //is this a new day
                if (uDateStamp.contains("a")){

                    //init for new data.
                    qheader.initDay(); //init the header for new day

                    ictr=0;

                    workingDayTime = uDateStamp.substring(1);
                    tchlov[0] = workingDayTime;
                    //System.out.println("time=" + workingDayTime);
                    day = get_gday(workingDayTime);

                } else {
                    //not a new day. correct timestamp and add to array
                    // dt = datetime.datetime.fromtimestamp(day+(interval_seconds*offset))
                    Long curTimestamp = Long.parseLong(workingDayTime) + (60* Long.parseLong(tchlov[0]));
                    //System.out.println("curr=" + curTimestamp);

                    String fooDate = ymutil.unixtodate(curTimestamp);
                    //System.out.println("running date " + fooDate);
                    tchlov[0] = fooDate;
                    date = fooDate;

                    qheader.setDay(tchlov[0]);

                    ictr++;
                } //end if date logic

                //mongo insert goes here

                qheader.setOpen(tchlov[4]);
                qheader.setHigh(tchlov[2]);
                qheader.setLow(tchlov[3]);
                qheader.setClose(tchlov[1]);
                qheader.setVolume(Long.parseLong(tchlov[5]));

                //builder the _id for the data bar.
                String bar_id = qheader.getTicker()+":"+qheader.getSource()+":"+date;
                qheader.setH_id(bar_id);

                //Store the minute bar
                ml.mongo_store_bar(qheader);

                //store the daily bar
                //builder the _id for the data bar.
                bar_id = qheader.getTicker()+":"+qheader.getSource()+":"+day;
                qheader.setH_id(bar_id);

                //Store the daily bar
                ml.mongo_store_bar(qheader);
            } //end b_header

        } //end for array



    } //end process y csv

    //Handles the the 2 diff timestamps
    private void addToBody(String[] data){
 /*
        Long curTimestamp = Long.parseLong(workingDayTime) + (60* Long.parseLong(tchlov[0]));
        //System.out.println("curr=" + curTimestamp);

        String fooDate = ymutil.unixtodate(curTimestamp);
        //System.out.println("running date " + fooDate);
        tchlov[0] = curTimestamp.toString();
        line = ymutil.implodeArray(tchlov,",");
        bodyList.add(line);
*/
    }
    private String get_gday(String uDate) {
        YMUtils ymutil = new YMUtils();
        //it's an 'a' followed by a unix date
        //uDate = uDate.substring(1);
        String day = ymutil.unix2day(Long.parseLong(uDate));
        return day;
    }
} // end class

