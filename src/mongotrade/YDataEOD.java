package mongotrade;


/**
 * Created by mark.mcclellan on 4/20/2015.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.csvreader.CsvReader;

public class YDataEOD {
    private final String USER_AGENT = "Mozilla/5.0";
    static YDataEOD http = new YDataEOD();
    static MongoLayerRT ml = new MongoLayerRT();

    public static void main(String[] args) throws Exception {
        String symbol = "^GSPC";
       // String symbol = "EURUSD=X";
        http.fetchData(symbol);


    } //end main

    public void fetchData(String ticker) throws Exception {
        //http://ichart.finance.yahoo.com/table.csv?s=YHOO&a=06&b=9&c=1996&d=06&e=20&f=2010&g=d
        //http://download.finance.yahoo.com/d/quotes.csv?s=%40%5EDJI,GOOG&f=sd1ohgl1v&e=.csv
        String url1 = "http://download.finance.yahoo.com/d/quotes.csv?s=";

        //String url2 = getURLDates(3);
        //url2 contains the start and end dates
        String url2 = "&f=sd1ohgl1v&e=.csv";

        String url = url1 + URLEncoder.encode(ticker,"UTF-8") + url2;

        // System.out.println("Testing 1 - Send Http GET request");
        StringBuffer result = http.sendGet(url);

        http.process_yahoo_eod(result);

        //export the data as 5 & 30 min bars for GT
       // ml.GTexport(ticker,20,"M5");
       // ml.GTexport(ticker, 20, "M30");

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
    public void process_yahoo_eod(StringBuffer payload) throws UnknownHostException {

        BarCache day_quote = new BarCache();
        YMUtils ymutil = new YMUtils();
        day_quote.init();
        String day = "";
        //start processing
        CsvReader csvReader = new CsvReader(new StringReader((payload.toString())));

        try {
           // csvReader.readHeaders();

            while (csvReader.readRecord()) {
                day_quote.setType("D");
                day_quote.setSource("Y");
                day_quote.setTicker(csvReader.get(0));
                day_quote.setDay(ymutil.formatYEODDate(csvReader.get(1)));
                day_quote.setOpen(Double.parseDouble(csvReader.get(2)));
                day_quote.setHigh(Double.parseDouble(csvReader.get(3)));
                day_quote.setLow(Double.parseDouble(csvReader.get(4)));
                day_quote.setClose(Double.parseDouble(csvReader.get(5)));
                day_quote.setVolume(Double.parseDouble(csvReader.get(6)));

                day = day_quote.getDay().substring(0,8);
                day_quote.setH_id(day_quote.getTicker() + ":" + day_quote.getSource() + ":" + day );

                //Store the daily bar
                ml.mongo_store_bar(day_quote, false);
            } //end while

        } catch (IOException e) {
            e.printStackTrace();
        } //end try

    } //end process y eod
} // end class
