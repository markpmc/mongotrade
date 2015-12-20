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
       // long cet = System.currentTimeMillis();  // supply your timestamp here
        //long cet = "201505042130";
     /*   Date d = ymutil.dateFromString("201505202055","CET");

        // timezone symbol (z) included in the format pattern for debug
        DateFormat format = new SimpleDateFormat("yyyyMMddhhmm z");

        System.out.println("input="+d.toString());

        String etzid = "CDT";
        TimeZone etz = TimeZone.getTimeZone(etzid);

        String ctzid = "EST";
        TimeZone ctz = TimeZone.getTimeZone(ctzid);

        d = ymutil.shiftTimeZone(d,ctz,etz );
        // format date in default timezone
        //System.err.println(format.format(d));

        // format date in target timezone
        format.setTimeZone(ctz);
        System.err.println("source=" + format.format(d));

        format.setTimeZone(etz);
        System.err.println("target="+format.format(d));
     */

        System.out.println("6/17/2015 is:" + ymutil.formatYEODDate("6/17/2015"));
    } //end main

    public String unixtodate (long unixSeconds) {
		 //Date convesion trial
        //unixSeconds = 1400592600;
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
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

    private Date shiftTimeZone(Date date, TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        Calendar sourceCalendar = Calendar.getInstance();
        sourceCalendar.setTime(date);
        sourceCalendar.setTimeZone(sourceTimeZone);

        Calendar targetCalendar = Calendar.getInstance();
        for (int field : new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}) {
            targetCalendar.set(field, sourceCalendar.get(field));
        }
        targetCalendar.setTimeZone(targetTimeZone);

        return targetCalendar.getTime();
    }

    public Date dateFromString(String sdate,String zone){

        if (sdate.length() == 8){
           // dfm = new SimpleDateFormat("yyyyMMdd");
            sdate += "0000";
            zone = "GMT-0:00";  //override the timezone since it's a day
        }

        DateFormat dfm = new SimpleDateFormat("yyyyMMddHHmm");

        dfm.setTimeZone(TimeZone.getTimeZone(zone));//Specify your timezone
        Date date = null;

        
        try {
            date = (Date)dfm.parse(sdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    } //end dateFromString

    public String formatExportDate(String sdate){

        if(sdate.length()< 12) {return sdate;}

        //format 201506030830 to yyyy-MM-dd hh:mm:ss
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            sdate = myFormat.format(fromUser.parse(sdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sdate;
    } //end formatExportDate

    public String formatYEODDate(String sdate){
        //format 201506030830 to yyyy-MM-dd hh:mm:ss
        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        try {

            sdate = myFormat.format(fromUser.parse(sdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sdate;
    }
} //end YMUtils
