package mongotrade;


/**
 * Created by mark.mcclellan on 4/20/2015.
 */

import com.csvreader.CsvReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class YPulse {
    private final String USER_AGENT = "Mozilla/5.0";
    static YPulse http = new YPulse();
    static MongoLayerRT ml = new MongoLayerRT();
    static MFConfig cfg = new MFConfig();

    public static void main(String[] args) throws Exception {
        //String symbol = cfg.getSymbolString("yahoo");
       System.out.println("last close=" +http.yahoo_pulse("^gspc"));

    } //end main

    public StringBuffer fetchData(String ticker) throws Exception {
        //http://download.finance.yahoo.com/d/quotes.csv?s=%40%5EDJI,GOOG&f=sd1ohgl1v&e=.csv
        String url1 = "http://download.finance.yahoo.com/d/quotes.csv?s=";
        //http://download.finance.yahoo.com/d/quotes.csv?s=^gspc&f=l1
        //String url2 = getURLDates(3);
        //url2 contains the field options
        String url2 = "&f=l1&e=.csv";

        String url = url1 + URLEncoder.encode(ticker,"UTF-8") + url2;

        // System.out.println("Testing 1 - Send Http GET request");
        StringBuffer result = http.sendGet(url);

        //http.yahoo_pulse(result);
        return result;
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
    public double yahoo_pulse(String symbol) throws Exception {

        //retrieve the sysbol data
        StringBuffer payload = http.fetchData(symbol);
        return Double.parseDouble(payload.toString());
    } //end yahoo_pulse
} // end class
