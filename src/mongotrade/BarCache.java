package mongotrade;

import java.util.Date;


public class BarCache {

    private String h_id;
    private String day;
    private String open;
    private String high;
    private String low;
    private String close;
    private long volume;
    private String ticker;
    private String source;


    public void setH_id(String id) {this.h_id = id;}
    public String getH_id() {return this.h_id;}

    public void setVolume(long volume){
        this.volume += volume;
    }
    public String getVolume() {
        return String.valueOf(volume);
    }
    public void setOpen(String open){
        if(this.open.equals("0")){
            this.open = open;
        }
    }
    public String getOpen() {return this.open;}

    public void setHigh(String high){
        float max = Float.parseFloat(this.high);
        float h = Float.parseFloat(high);

        if (this.high.equals("0")) {
            this.high = high;
        }else if( h>max) {
            this.high = high;
        }
    }

    public String getHigh() {return this.high;}
    public void setLow(String low) {
        float min = Float.parseFloat(this.low);
        float m = Float.parseFloat(low);

        if (this.low.equals("0")){
            this.low = low;
        } else if(m<min) {
            this.low = low;
        }
    }
    public String getLow() {
        return this.low;
    }
    public void setClose(String close){
        this.close = close;
    }
    public String getClose() {
        return this.close;
    }
    public void setDay(String day) {
        this.day = day;
    }
    public String getDay() {
        return this.day;
    }

    public void initDay(){
        this.h_id = "";
        this.day = "";
        this.open = "0";
        this.high = "0";
        this.low = "0";
        this.close = "0";
        this.volume = 0;
    }

    public void BarCache(){
        initDay();
    }

    public String getTicker() {
        return this.ticker;
    }
    public void setTicker(String ticker) {

        this.ticker = ticker;
    }
    public String getSource() {
        return this.source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString(){
        return "\n::";
    }
} //end QuoteHeader
