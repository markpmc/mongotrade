package mongotrade;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


import javax.net.ssl.HttpsURLConnection;

public class YData2 {
    private final String USER_AGENT = "Mozilla/5.0";
    static YData2 http = new YData2();

    public static void main(String[] args) throws Exception {
        String symbol = "^gspc";
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
        String s_curDay = "Dummy";
        QuoteHeader qheader = new QuoteHeader();
        QuoteBody qbody = new QuoteBody();
        YMUtils ymutil = new YMUtils();
        MongoLayerRT ml = new MongoLayerRT();
        List<String> bodyList = new ArrayList<String>(); // or LinkedList<String>();

        //ml.connect();

        //process the csv file begining with the header
        boolean b_header = true;
        //convert to string
        String data = payload.toString();
        //split into array
        String[] d_array = data.split("\\n",-1);

        int ictr = 0;

        //start processing
        for(String line : d_array){
            if(b_header) { //process header
                String[] section = line.split(":", -1);

                if(section[0].toString().equals("volume")){
                    b_header = false;
                } //end volume tag check

                //populate the header data structure with values
                if(section[0].equals("ticker")){
                    qheader.setTicker(section[1].toString());
                }else if(section[0].equals("Company-Name")){
                    qheader.setTickerName(section[1].toString());
                }
                qheader.setSource("Y");
                //end load header info


            } else{ //process body
                //we're in the quote section of the data.
                //load it into the body data structure
                String[] section = line.split(",", -1);
                if (line.length() > 6) {  //empty line check
                    ictr++;
                    //System.out.println("Body ct= " + ictr);
                    String day = ymutil.unix2day(Long.parseLong(section[0]));

                    String date = ymutil.unixtodate(Long.parseLong(section[0]));

                        qheader.setOpen(section[4]);
                        qheader.setHigh(section[2]);
                        qheader.setLow(section[3]);
                        qheader.setClose(section[1]);
                        qheader.setVolume(Long.parseLong(section[5]));

                        //builder the _id for the data bar.
                        String bar_id = qheader.getTicker()+":"+qheader.getSource()+":"+date;
                        qheader.setH_id(bar_id);

                        //Store the minute bar
                        ml.mongo_store_bar(qheader);

                        //build the daily key
                        bar_id = qheader.getTicker()+":"+qheader.getSource()+":"+day;
                       qheader.setH_id(bar_id);

                        //Store the daily bar
                       ml.mongo_store_bar(qheader);
                } //end empty line check

            } //end process header-body

        } //end for array

    } //end process y csv
} // end class
