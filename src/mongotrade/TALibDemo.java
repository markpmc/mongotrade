package mongotrade;

import com.mongodb.*;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

import java.net.UnknownHostException;

/**
 * Created by mark.mcclellan on 5/16/2015.
 */

public class TALibDemo {
    MongoLayerRT ml = new MongoLayerRT();
    //you need to instantiate some basic variables
    private double input[];
    private int inputInt[];
    private double output[];
    private int outputInt[];

    private MInteger outBegIdx;
    private MInteger outNbElement;
    private RetCode retCode;
    private Core lib;
    private int lookback;

    public String[] inDate;
    public  double[] inOpen;
    public  double[] inHigh;
    public  double[] inLow;
    public  double[] inClose;
    public  double[] inVol;


    public TALibDemo() {
        //initialize everything required for holding data
        BarArray ba = ml.getData("dji", 3, "M1");

        inDate = ba.getDateArray();
        inOpen = ba.getOpenArray();
        inHigh = ba.getHighArray();
        inLow = ba.getLowArray();
        inClose = ba.getCloseArray();
        inVol = ba.getVolArray();

        lib = new Core();
        input = new double[inClose.length];
        inputInt = new int[inClose.length];
        output = new double[inClose.length];
        outputInt = new int[inClose.length];
        outBegIdx = new MInteger();
        outNbElement = new MInteger();

        //keeping it simple here...
        //simpleMovingAverageCall();
        candleScan();
        dojiScan();
    }


    private void getData4Symbol(String symbol){


    }
    /**
     * resets the arrays used in this application since they are only
     * initialized once
     */
    private void resetArrayValues() {
        //provide default "fill" values to avoid nulls.

        for (int i = 0; i < input.length; i++) {
            input[i] = (double) i;
            inputInt[i] = i;
        }
        for (int i = 0; i < output.length; i++) {
            output[i] = (double) -999999.0;
            outputInt[i] = -999999;
        }

        //provide some "fail" values up front to ensure completion if correct.
        outBegIdx.value = -1;
        outNbElement.value = -1;
        retCode = RetCode.InternalError;
        lookback = -1;

    }

    private void showDefaultTALIBOutput(){
        System.out.println("Printing the default TALIB output to the screen");
        System.out.println("lookback = " + lookback + ", outBegIdx=" + outBegIdx.value
                + ", outNbElement=" + outNbElement.value + "\nretCode=" + retCode);
        System.out.println("\nClose  \t" + "Indicator");
/*        int j=0;
        for (int i = 0; i < output.length; i++) {
            System.out.println(inClose[i] + ",\t " + output[i]);
        }
*/
        for (int i = 0; i < outNbElement.value; i++) {
            if (output[i] != 0)
                System.out.println(inDate[outBegIdx.value+i]+" integer ["+i+"] is "+output[i]);
        }

    }


    public void simpleMovingAverageCall() {
        //you'll probably have to do this next call every time you add a value to your data array
        //I haven't checked this in a live app yet
        resetArrayValues();

        //The "lookback" is really your indicator's period minus one because it's expressed as an array index
        //At least that's true for movingAverage(...)
        lookback = lib.movingAverageLookback(10, MAType.Sma);

        System.out.println("Starting everthing off...");
        System.out.println("Lookback=" + lookback);
        System.out.println("outBegIdx.value=" + outBegIdx.value);
        System.out.println("outNbElement.value=" + outNbElement.value);
        retCode = lib.movingAverage(0, inClose.length - 1, inClose, lookback + 1, MAType.Sma, outBegIdx, outNbElement, output);

        showDefaultTALIBOutput();
    }

    public void candleScan(){

        MInteger outNBElement = new MInteger();
        int startIdx = 0;
        int endIdx = inOpen.length-1;
        int outInteger = inOpen.length;

        for (int k=0; k<inDate.length; k++)  // display data
            System.out.println(k+". "+inDate[k]+","+inOpen[k]+","+inHigh[k]+","+inLow[k]+","+inClose[k]);

        //RetCode ret = lib.cdlEngulfing(startIdx, endIdx, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
        RetCode ret = lib.cdlEngulfing(startIdx,endIdx,inOpen,inHigh,inLow,inClose,outBegIdx,outNBElement,outputInt);
        System.out.println("ret to string is " +ret.toString());
        System.out.println("out beg idx is "+outBegIdx.value);
        System.out.println("out NB element is "+outNBElement.value);
        System.out.println("out integer length is " + outputInt.length);

        for (int i = 0; i < outNBElement.value; i++) {
            if (outputInt[i] != 0)
                System.out.println(inDate[startIdx+outBegIdx.value+i]+" integer ["+i+"] is "+outputInt[i]);
        }

    } //end candle scan
    public void dojiScan(){

        MInteger outNBElement = new MInteger();
        int startIdx = 0;
        int endIdx = inOpen.length-1;
        int outInteger = inOpen.length;

        //RetCode ret = lib.cdlEngulfing(startIdx,endIdx,inOpen,inHigh,inLow,inClose,outBegIdx,outNBElement,outputInt);
        RetCode ret = lib.cdlDoji(startIdx,endIdx,inOpen,inHigh,inLow,inClose,outBegIdx,outNBElement,outputInt);
        System.out.println("ret to string is " +ret.toString());
        System.out.println("out beg idx is "+outBegIdx.value);
        System.out.println("out NB element is "+outNBElement.value);
        System.out.println("out integer length is "+ outputInt.length);

        for (int i = 0; i < outNBElement.value; i++) {
            if (outputInt[i] != 0)
                System.out.println(inDate[startIdx+outBegIdx.value+i]+" integer ["+i+"] is "+outputInt[i]);
        }

    }
    public static void main(String[] args){
        //keeping this really simple...
        TALibDemo demo = new TALibDemo();
    } //end main



} //end class