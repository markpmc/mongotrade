package mongotrade;

public class BarCache {

    private String h_id;
    private String day;
    private String type;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private String ticker;
    private String source;
    private String tz;

    public void setTz(String tz){
        this.tz = tz;
    }
    public String getTz(){
        return this.tz;
    }

    public void setH_id(String id) {this.h_id = id;}
    public String getH_id() {return this.h_id;}

    public void setVolume(double volume){
        this.volume += volume;
    }
    public double getVolume() {
        return this.volume;
    }
    public void setOpen(double open){
            if(this.open == 0){
                this.open = open;
            }
    }
    public double getOpen() {return this.open;}
    public void setType(String type){
        this.type = type;
    }
    public String getType() {return this.type;}
    public void setHigh(double high){

        if (this.high == 0) {
            this.high = high;
        }else if( high > this.high) {
            this.high = high;
        }
    }

    public double getHigh() {return this.high;}
    public void setLow(double low) {
        if (this.low == 0){
            this.low = low;
        } else if(low < this.low) {
            this.low = low;
        }
    }
    public double getLow() {
        return this.low;
    }
    public void setClose(double close){
        this.close = close;
    }
    public double getClose() {
        return this.close;
    }
    public void setDay(String day) {
        //this.day = day.substring(0,8);
        this.day = day;
    }
    public String getDay() {
        return this.day;
    }

    public void init(){
        this.h_id = "";
        this.day = "";
        this.type = "";
        this.open = 0;
        this.high = 0;
        this.low = 0;
        this.close = 0;
        this.volume = 0;
        this.tz = "";
    }

    public void BarCache(){
        init();
    }

    public String getTicker() {
        return this.ticker;
    }
    public void setTicker(String ticker) {

        this.ticker = ticker.replaceAll("^[^a-zA-Z0-9_-]", "");
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
