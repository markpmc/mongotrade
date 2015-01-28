package mongotrade;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.bson.types.ObjectId;

public class YMUtils {

	public String unixtodate (long unixSeconds) {
		 //Date convesion trial
        //unixSeconds = 1400592600;
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); // the format of your date
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        String formattedDate = sdf.format(date);
       // System.out.println(formattedDate);
		return formattedDate;
	} //end unixtodate
	
	public String unix2day (long unixSeconds) {
		 //Date convesion trial
       //unixSeconds = 1400592600;
       Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
       SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // the format of your date
       //sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
       String formattedDate = sdf.format(date);
       //System.out.println(formattedDate);
		return formattedDate;
	} //end unit2day
	
	// This function returns an ObjectId embedded with a given datetime
	// Accepts both Date object and string input
    public ObjectId objectIdWithTimestamp(long unixSeconds) {
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        // Create an ObjectId with that hex timestamp
        ObjectId constructedObjectId;
        constructedObjectId = new ObjectId(date);
        return constructedObjectId;
    }



    public static String implodeArray(String[] inputArray, String glueString) {

/** Output variable */
        String output = "";

        if (inputArray.length > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(inputArray[0]);

            for (int i=1; i<inputArray.length; i++) {
                sb.append(glueString);
                sb.append(inputArray[i]);
            }

            output = sb.toString();
        }

        return output;
    }
} //end YMUtils
