package mongotrade;

/**
 * Created by mark.mcclellan on 6/22/2015.
 */

//import com.std.YStockQuote;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;


public class IntradayChart {
    static MongoLayerRT ml = new MongoLayerRT();
    static YMUtils ut = new YMUtils();
    ChartPanel chartPanel;
    public long FIFTEEN_MINUTE_SEGMENT_SIZE = 15 * 60 * 1000;


    public static void main(String args[]) {
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
        String[] inDate;
        double[] inOpen;
        double[] inHigh;
        double[] inLow;
        double[] inClose;
        double[] inVol;

        BarArray ba = ml.getData("oex", 3, "M5");

        inDate = ba.getDateArray();
        inOpen = ba.getOpenArray();
        inHigh = ba.getHighArray();
        inLow = ba.getLowArray();
        inClose = ba.getCloseArray();
        inVol = ba.getVolArray();

        for (int k = 0; k < inDate.length; k++){
            System.out.println("date="+inDate[k]);

            OHLCDataItem item = new OHLCDataItem(ut.dateFromString(inDate[k],"New York City"), inOpen[k], inHigh[k], inLow[k], inClose[k], inVol[k]);
            dataItems.add(item);
        } //end for date

        //Convert the list into an array
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);
        OHLCDataset dataset = new DefaultOHLCDataset("SP500", data);

        IntradayChart mychart = new IntradayChart(dataset);
    } //end main
    public IntradayChart(OHLCDataset dataset) {



        //JFreeChart chart = ChartFactory.createTimeSeriesChart(
        JFreeChart chart = ChartFactory.createCandlestickChart(
                "SP500 " + "(gspc)" + " Intraday",
                "Date",
                "Price",
                dataset,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();

        //set a gradient background
        plot.setBackgroundPaint(new GradientPaint(0, 0, Color.black, 200, 200, Color.blue, false));
        plot.setBackgroundAlpha(0.5f);

        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);


        ValueAxis yAxis = (ValueAxis) plot.getRangeAxis();
        ((NumberAxis) plot.getRangeAxis()).setAutoRangeIncludesZero(false);

        SegmentedTimeline timeline = new SegmentedTimeline(
                SegmentedTimeline.FIFTEEN_MINUTE_SEGMENT_SIZE, 28, 68);
        timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900() + 32
                * timeline.getSegmentSize());
        timeline.setBaseTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());


        //MarketTimeLine mtl = new MarketTimeLine(5,930,1600);
        //SegmentedTimeline timeline = mtl.getNormalHoursTimeline();

        DateAxis xAxis = (DateAxis) chart.getXYPlot().getDomainAxis();
        xAxis.setTimeline(timeline);

        xAxis.setTimeZone(TimeZone.getTimeZone("New York City"));
        xAxis.setDateFormatOverride(new SimpleDateFormat("h:m a"));
        xAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        xAxis.setVerticalTickLabels(true);


        chartPanel = new ChartPanel(chart);
        chart.setBackgroundPaint(chartPanel.getBackground());
        plot.setDomainGridlinePaint(Color.black);
        plot.setRangeGridlinePaint(Color.black);
        chartPanel.setVisible(true);
        chartPanel.revalidate();
        chartPanel.repaint();

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
    }

    private void removeGaps(String[] inDate){



    } //end removeGaps

    public ChartPanel getChartPanel()
    {
        return this.chartPanel;
    }

}
