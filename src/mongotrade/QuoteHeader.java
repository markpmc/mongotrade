package mongotrade;

import java.util.Date;


public class QuoteHeader {

    private String h_id;
	private String day;
    private String open;
    private String high;
    private String low;
    private String close;
    private long volume;
    private long entries;
    private String uri;
	private String ticker;
    private String tickername;
    private String source;
    private String tz;
    private String currency;
    private String unit;
    private boolean dirty;


    public void setH_id(String id) {this.h_id = id;}
    public String getH_id() {return this.h_id;}

    public void setClean() {
        this.dirty = false;
    }
    public boolean checkDirty(){
        return this.dirty;
    }
    public void setEntry(){
        this.entries ++;
    }
    public long getEntries(){ return this.entries;}

    public void setVolume(long volume){
    	this.volume = this.volume + volume;
    }
    public String getVolume() {
    	return String.valueOf(volume);
    }
    public void setOpen(String open){this.open = open;}
    public String getOpen() {return open;}
    public void setHigh(String high){this.high = high;}
    public String getHigh() {return high;}
    public void setLow(String low){this.low = low;}
    public String getLow() {
    	return String.valueOf(low);
    }
    public void setClose(String close){
    	this.close = close;
    }
    public String getClose() {
    	return close;
    }
    public void setDay(String day) {
    	this.day = day;
    }
    public String getDay() {
    	return day;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getTimeZone() {
        return tz;
    }
    public void setTimeZone(String timezone) {
        this.tz = timezone;
    }
    public String getTickerName() {
        return tickername;
    }
    public void setTickerName(String tickername) {
        this.tickername = tickername;
    }
    public String getTicker() {
        return ticker;
    }
    public void setTicker(String ticker) {

        this.ticker = ticker;
    }
    public String getUri() {
    	return uri;
    }
    public void setUri(String uri){
    	this.uri = uri;
    }
    
    public void initDay(){
        this.h_id = "";
        this.day = "";
    	this.open = "";
    	this.high = "";
    	this.low = "";
    	this.close = "";
    	this.volume = 0;
        this.entries = 0;
        this.dirty = false;
        //this.source = "";
        //this.ticker = "";
    }
    
    public void QuoteHeader(){
    	initDay();
    }
    
    @Override
    public String toString(){
        return "\n::Ticker="+getTicker()+"::TName="+getTickerName()+"::TZ="+getTimeZone();
    }
} //end QuoteHeader
