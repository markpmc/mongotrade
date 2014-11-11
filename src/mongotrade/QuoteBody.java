package mongotrade;

import org.bson.types.ObjectId;

public class QuoteBody {
	
	private ObjectId _id;
    private String timestamp;
    private String close;
    private String high;
    private String low;
    private String open;
    private String volume;
    
   
    public void setId( ObjectId _id ) { 
    	this._id = _id;
    }
    
    public void generateId() {
    	if( this._id == null ) 
    			this._id = new ObjectId();
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getClose() {
        return close;
    }
    public void setClose(String close) {
        this.close = close;
    }
    public String getHigh() {
        return high;
    }
    public void setHigh(String high) {
        this.high = high;
    }
    public String getLow() {
        return low;
    }
    public void setLow(String low) {
        this.low = low;
    }
    public String getOpen() {
        return open;
    }
    public void setOpen(String open) {
        this.open = open;
    } 
    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }
    @Override
    public String toString(){
        return "\nTS="+getTimestamp()+"::Close="+getClose()+"::High="+getHigh()+"::Low="+getLow();
    }
} //end QuoteObj
