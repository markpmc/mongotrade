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
 
public class YData {
	private final String USER_AGENT = "Mozilla/5.0";
    static YData http = new YData();

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
		public void process_yahoo_csv(StringBuffer payload) {
	        String s_curDay = "Dummy";
	        QuoteHeader qheader = new QuoteHeader();
	        QuoteBody qbody = new QuoteBody(); 
	        YMUtils ymutil = new YMUtils();
            MongoLayerRT ml = new MongoLayerRT();
            List<String> bodyList = new ArrayList<String>(); // or LinkedList<String>();

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
                        qheader.setSource("yahoo");
                    //end load header info


                } else{ //process body
                    //we're in the quote section of the data.
                    //load it into the body data structure
                    String[] section = line.split(",", -1);
                    if (line.length() > 6) {  //empty line check
                        ictr++;
                        //System.out.println("Body ct= " + ictr);
                        //check for multi-days in data
                        String day = ymutil.unix2day(Long.parseLong(section[0]));
                        if(!day.equals(s_curDay)){
                            s_curDay = day;

                           // System.out.println("Changing day: " + s_curDay);
                            try {
                                //  ml.connect(qheader.getTicker());
                               // System.out.println("Calling mongo_store= " + ictr);
                                ml.mongo_store_rt(qheader,bodyList);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }

                           // System.out.println("Cleaning header");
                            qheader.initDay(); //init the header for new day
                            bodyList.clear();
                        }

                        //Timestamp,close,high,low,open,volume
                        //add the body elements to the bodyList

                        bodyList.add(line);





                    } //end empty line check


                } //end process



            } //end for array

            //process the final day that didn't get stored by changing day
            try {
                //  ml.connect(qheader.getTicker());
                ml.mongo_store_rt(qheader,bodyList);
               // System.out.println("Cleaning header");
                qheader.initDay(); //init the header for new day
                bodyList.clear();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

			
		} //end process y csv
} // end class
