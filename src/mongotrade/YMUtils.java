package mongotrade;

import org.bson.types.ObjectId;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class YMUtils {
    static YMUtils ymutil = new YMUtils();

    public static void main(String[] args) throws Exception {
        String tzid = "CET";
        TimeZone tz = TimeZone.getTimeZone(tzid);

        String est = "EST";
        TimeZone etz = TimeZone.getTimeZone(est);

        long cet = System.currentTimeMillis();  // supply your timestamp here
        //long cet = "201505042130";
        Date d = new Date(cet);

        // timezone symbol (z) included in the format pattern for debug
        DateFormat format = new SimpleDateFormat("yy/M/dd hh:mm a z");

        // format date in default timezone
        System.err.println(format.format(d));

        // format date in target timezone
        format.setTimeZone(tz);
        System.err.println(format.format(d));

        format.setTimeZone(etz);
        System.err.println(format.format(d));


    } //end main

    public String unixtodate (long unixSeconds) {
		 //Date convesion trial
        //unixSeconds = 1400592600;
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm"); // the format of your date
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
    }//end implode array

    public String shiftTimeZone(String dateString, TimeZone sourceTimeZone, TimeZone targetTimeZone) throws ParseException {

        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");
        Date date = df.parse(dateString);

        Calendar sourceCalendar = Calendar.getInstance();
        sourceCalendar.setTime(date);
        sourceCalendar.setTimeZone(sourceTimeZone);

        Calendar targetCalendar = Calendar.getInstance();
        for (int field : new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}) {
            targetCalendar.set(field, sourceCalendar.get(field));
        }
        targetCalendar.setTimeZone(targetTimeZone);

        return targetCalendar.getTime().toString();
       // DateFormat format = new SimpleDateFormat("ddMMyyhhmmss");
       // return format.format(targetCalendar).toString();
    }

} //end YMUtils
