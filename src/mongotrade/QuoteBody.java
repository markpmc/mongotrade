package mongotrade;

import org.bson.types.ObjectId;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class QuoteBody {
	
	private ObjectId _id;
    private String timestamp;
    private String close;
    private String high;
    private String low;
    private String open;
    private String volume;
    

    public QuoteBody(){}

    public QuoteBody(String timestamp, String close, String high, String low, String open, String volume) {
            this.timestamp = timestamp;
            this.close = close;
            this.high = high;
            this.low = low;
            this.open = open;
            this.volume = volume;
    }
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

    public DBObject bsonFromPojo(){
        BasicDBObject document = new BasicDBObject();
        document.put( "_id",    this._id );
        document.put( "timestamp",   this.timestamp );
        document.put( "close", this.close );
        document.put( "high",   this.high );
        document.put( "low",  this.low );
        document.put( "open",  this.open );
        document.put( "volume",  this.volume );
        return document;
    }
    public void makePojoFromBson( DBObject bson ){
        BasicDBObject b = ( BasicDBObject ) bson;
        this._id    = ( ObjectId ) b.get( "_id" );
        this.timestamp   = ( String )  b.get( "timestamp" );
        this.close = ( String )   b.get( "close" );
        this.high   = ( String )   b.get( "high" );
        this.low  = ( String )   b.get( "low" );
        this.open   = ( String )   b.get( "open" );
        this.volume  = ( String )   b.get( "volume" );
    }
    @Override
    public String toString(){
        return "\nTS="+getTimestamp()+"::Close="+getClose()+"::High="+getHigh()+"::Low="+getLow();
    }
} //end QuoteObj
