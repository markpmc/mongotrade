package mongotrade;

/**
 * Created by mark.mcclellan on 5/28/2015.
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


public class CandlestickChart {
    static MongoLayerRT ml = new MongoLayerRT();
    static YMUtils ut = new YMUtils();

    public static void main(String args[]) {

        // 1. Download MSFT quotes from Yahoo Finance and store them as OHLCDataItem
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
        String[] inDate;
        double[] inOpen;
        double[] inHigh;
        double[] inLow;
        double[] inClose;
        double[] inVol;

       /* try {
            String strUrl = "http://ichart.yahoo.com/table.csv?s=^gspc&a=3&b=1&c=2014&d=3&e=15&f=2050&g=d";
            URL url = new URL(strUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            DateFormat df = new SimpleDateFormat("y-M-d");

            String inputLine;
            in.readLine();
            while ((inputLine = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                Date date = df.parse(st.nextToken());
                double open = Double.parseDouble(st.nextToken());
                double high = Double.parseDouble(st.nextToken());
                double low = Double.parseDouble(st.nextToken());
                double close = Double.parseDouble(st.nextToken());
                double volume = Double.parseDouble(st.nextToken());
                double adjClose = Double.parseDouble(st.nextToken());

                // adjust data:
                open = open * adjClose / close;
                high = high * adjClose / close;
                low = low * adjClose / close;

                OHLCDataItem item = new OHLCDataItem(date, open, high, low, adjClose, volume);
                dataItems.add(item);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
      */
        BarArray ba = ml.getData("sso", 5, "M5");

        inDate = ba.getDateArray();
        inOpen = ba.getOpenArray();
        inHigh = ba.getHighArray();
        inLow = ba.getLowArray();
        inClose = ba.getCloseArray();
        inVol = ba.getVolArray();

        for (int k = 0; k < inDate.length; k++){
            System.out.println("date="+inDate[k]);
           OHLCDataItem item = new OHLCDataItem(ut.dateFromString(inDate[k],"EST"), inOpen[k], inHigh[k], inLow[k], inClose[k], inVol[k]);
            dataItems.add(item);
        }

        Collections.reverse(dataItems); // Data from Yahoo is from newest to oldest. Reverse so it is oldest to newest.
        //Convert the list into an array
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);
        OHLCDataset dataset = new DefaultOHLCDataset("SP500", data);

        // 2. Create chart
        JFreeChart chart = ChartFactory.createCandlestickChart("SP500", "Time", "Price", dataset, false);

        // 3. Set chart background
        chart.setBackgroundPaint(Color.white);

        // 4. Set a few custom plot features
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE); // light yellow = new Color(0xffffe0)
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);
        ((NumberAxis) plot.getRangeAxis()).setAutoRangeIncludesZero(false);


        DateAxis axis = new DateAxis();
        long FIFTEEN_MINUTE_SEGMENT_SIZE = 15 * 60 * 1000;
        SegmentedTimeline timeline = new SegmentedTimeline(FIFTEEN_MINUTE_SEGMENT_SIZE,26,70);
        timeline.setStartTime(SegmentedTimeline.FIRST_MONDAY_AFTER_1900 +
                (38 * timeline.getSegmentSize()));
        timeline.setBaseTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        //help with segmented timeline
/*        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date from = cal.getTime();

        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date to = cal.getTime();
        timeline.addBaseTimelineExclusions(from.getTime(),to.getTime());
*/

        // 5. Skip week-ends on the date axis
      //  ((DateAxis) plot.getDomainAxis()).setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        // 6. No volume drawn
        ((CandlestickRenderer) plot.getRenderer()).setDrawVolume(false);

        // 7. Create and display full-screen JFrame
        JFrame myFrame = new JFrame();
        myFrame.setResizable(true);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.add(new ChartPanel(chart), BorderLayout.CENTER);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Insets insets = kit.getScreenInsets(myFrame.getGraphicsConfiguration());
        Dimension screen = kit.getScreenSize();
        myFrame.setSize((int) (screen.getWidth() - insets.left - insets.right), (int) (screen.getHeight() - insets.top - insets.bottom));
        myFrame.setLocation((int) (insets.left), (int) (insets.top));
        myFrame.setVisible(true);
    } //end main
}//end class