package mongotrade;

/**
 * Created by mark.mcclellan on 5/21/2015.
 */

import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

import java.net.UnknownHostException;

public class MFCandleScan {
    MongoLayerRT ml = new MongoLayerRT();
/*
    private double input[];
    private int inputInt[];
    private double output[];
    private int outputInt[];
    private MInteger outBegIdx;
    private MInteger outNbElement;
    private RetCode retCode;
    private Core lib;
    private int lookback;

    //Engulfing candle example

    MInteger outNBElement = new MInteger();
    startIdx = 0;
    endIdx = inOpen.length-1;
    outInteger = new int[inOpen.length];
    for (int k=0; k<inDate.length; k++)  // display data
            System.out.println(k+". "+inDate[k]+","+inOpen[k]+","+inHigh[k]+","+inLow[k]+","+inClose[k]);

    RetCode ret =
            core.cdlEngulfing(startIdx, endIdx, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
    System.out.println("ret to string is " +ret.toString());
    System.out.println("out beg idx is "+outBegIdx.value);
    System.out.println("out NB element is "+outNBElement.value);
    System.out.println("out integer length is "+ outInteger.length);
    for (int i = 0; i < outInteger.length; i++) {
        if (outInteger[i] != 0)
            System.out.println(inDate[startIdx+i]+" integer ["+i+"] is "+outInteger[i]);
    }

    //Feedback from author to correct errors.
    Your interpretation of the output is off by two bars.

Output logic should be something like:

Code:

for (int i = 0; i < outNBElement.value; i++) {
   if (outInteger[i] != 0)
      System.out.println(inDate[startIdx+outBegIdx.value+i]+" integer ["+i+"] is "+outInteger[i]);
   }
}


The important change is that the output is offset relative to the input by as much as indicated by outBegIdx.

Also, the number of elements written in the output is indicated by outNBElement.


*/
} //end candle scan
