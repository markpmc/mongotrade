package mongotrade;

import java.util.Date;


public class QuoteHeader {

	private String day;
    private float open;
    private float high;
    private float low;
    private float close;
    private long volume;
    private long entries;
    private String uri;
	private String ticker;
    private String tickername;
    private String source;
    private String tz;
    private String currency;
    private String unit;


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
    public void setOpen(float open){
    	if (this.open == 0) {
    		this.open = open;
    	}
    }
    public String getOpen() {
    	return String.valueOf(open);
    }
    public void setHigh(float high){
    	if(high > this.high){
    		this.high = high;
    	}
    }
    public String getHigh() {
    	return String.valueOf(high);
    }
    public void setLow(float low){
    	if((this.low == 0)||(this.low > low)){
    		this.low = low;
    	}
    	
    }
    public String getLow() {
    	return String.valueOf(low);
    }
    public void setClose(float close){
    	this.close = close;
    }
    public String getClose() {
    	return String.valueOf(close);
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
        this.day = "";
    	this.open = 0;
    	this.high = 0;
    	this.low = 0;
    	this.close = 0;
    	this.volume = 0;
        this.entries = 0;
    }
    
    public void QuoteHeader(){
    	initDay();
    }
    
    @Override
    public String toString(){
        return "\n::Ticker="+getTicker()+"::TName="+getTickerName()+"::TZ="+getTimeZone();
    }
} //end QuoteHeader
