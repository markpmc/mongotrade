package mongotrade;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.*;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JScrollBar;

public class CandlestickChart extends JFrame {
    final int maxdays = 100;

    private class Day {
        public Date id;
        public double open;
        public double max;
        public double min;
        public double close;
        public long vol;
        public int signalType;
        public String colour;
        public double body_inf;
        public double body_sup;

        public Day(String ids, double open, double max, double min, double close,long vol) throws ParseException {
            DateFormat df = new SimpleDateFormat("y-M-d");
            this.id=df.parse(ids);
            this.open=open;
            this.max=max;
            this.min=min;
            this.close=close;
            this.vol=vol;
            if(close>=open){
                this.colour="G";
                this.body_inf=this.open;
                this.body_sup=this.close;
            } else {
                this.colour="R";
                this.body_inf=this.close;
                this.body_sup=this.open;
            }
        }
    }

    LinkedList<Day> list = new LinkedList<Day>();

    public CandlestickChart(String title) throws IOException, NumberFormatException, ParseException{
        super(title + ": Chart + Buy/Sell signals");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        readData();

        //Shared date axis
        DateAxis            domainAxis = new DateAxis("Date");
        domainAxis.setTickMarkPosition( DateTickMarkPosition.START );
        domainAxis.setTimeline( SegmentedTimeline.newMondayThroughFridayTimeline() );
        domainAxis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yy"));

        //Build Candlestick Chart based on stock price OHLC
        OHLCDataset         priceDataset  = getPriceDataSet(title);
        NumberAxis          priceAxis     = new NumberAxis("Price");
        CandlestickRenderer priceRenderer = new CandlestickRenderer();
        XYPlot              pricePlot     = new XYPlot(priceDataset, domainAxis, priceAxis, priceRenderer);
        priceRenderer.setSeriesPaint(0, Color.BLACK);
        priceRenderer.setDrawVolume(true);
        priceAxis.setAutoRangeIncludesZero(false);

        OHLCDataset         signalDataset  = getSignalDataSet(title);
        NumberAxis signalAxis     = new NumberAxis("Signal");
        CandlestickRenderer signalRenderer = new CandlestickRenderer();
        XYPlot              signalPlot     = new XYPlot(signalDataset, domainAxis, signalAxis, signalRenderer);
        signalRenderer.setSeriesPaint(0, Color.BLACK);
        signalRenderer.setDrawVolume(false);
        signalAxis.setAutoRangeIncludesZero(true);

        //Build Combined Plot
        CombinedDomainXYPlot mainPlot = new CombinedDomainXYPlot(domainAxis);
        mainPlot.add(pricePlot,4);
        mainPlot.add(signalPlot,1);

        JFreeChart chart = new JFreeChart(title, null, mainPlot, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setSize(600,600);
        chartPanel.setVisible(true);
        this.add(chartPanel);
        this.add(getScrollBar(domainAxis), BorderLayout.SOUTH);
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
    }

    private OHLCDataset getPriceDataSet(String symbol) {
        ArrayList<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
        int counter = 0;
        ListIterator<Day> it = list.listIterator(list.size());
        while(it.hasPrevious() /*&& counter<maxdays*/){
            Day day = it.previous();
            OHLCDataItem item = new OHLCDataItem(day.id, day.open, day.max, day.min, day.close, day.vol);
            dataItems.add(item);
            counter++;
        }
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);
        return new DefaultOHLCDataset(symbol, data);
    }

    private OHLCDataset getSignalDataSet(String symbol) {
        ArrayList<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
        int counter = 0;
        ListIterator<Day> it = list.listIterator(list.size());
        while(it.hasPrevious() && counter<maxdays){
            Day day = it.previous();
            if(day.signalType>0){
                OHLCDataItem item = new OHLCDataItem(day.id, 0, day.signalType, 0, day.signalType, 0);
                dataItems.add(item);
            } else if(day.signalType<0){
                OHLCDataItem item = new OHLCDataItem(day.id, 0, 0, day.signalType, day.signalType, 0);
                dataItems.add(item);
            }
            counter++;
        }
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);
        return new DefaultOHLCDataset(symbol, data);
    }

    private JScrollBar getScrollBar(final DateAxis domainAxis){
        final double r1 = domainAxis.getLowerBound();
        final double r2 = domainAxis.getUpperBound();
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 100, 0, 400);
        scrollBar.addAdjustmentListener( new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                double x = e.getValue() *60 *60 * 1000L;
                domainAxis.setRange(r1+x, r2+x);
            }
        });
        return scrollBar;
    }

    private void readData() throws IOException, NumberFormatException, ParseException {
        URL url = new URL("http://real-chart.finance.yahoo.com/table.csv?s=GOOG&d=5&e=23&f=2015&g=d&a=2&b=27&c=2010&ignore=.csv");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        in.readLine();
        String line;
        try {
            while ((line = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",");
                String id = st.nextToken().trim();
                String open = st.nextToken().trim();
                String max = st.nextToken().trim();
                String min = st.nextToken().trim();
                st.nextToken().trim();
                String vol = st.nextToken().trim();
                String cload = st.nextToken().trim();
                addRecord(id, Double.parseDouble(open), Double.parseDouble(max),
                        Double.parseDouble(min), Double.parseDouble(cload), Long.parseLong(vol));
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {}
        Collections.reverse(list);

    }

    private void addRecord(String id, double open,
                           double max, double min, double close,
                           long vol) throws ParseException {
        list.add(new Day(id,open,max,min,close,vol));
    }

    public static void main(String[] args) throws IOException, NumberFormatException, ParseException {
        new CandlestickChart("Daily Chart").setVisible(true);
    }

} 