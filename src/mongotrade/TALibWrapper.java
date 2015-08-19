package mongotrade;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

/**
 * Created by mark.mcclellan on 5/16/2015.
 */

public class TALibWrapper {
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


    public TALibWrapper() {
        //initialize everything required for holding data
        BarArray ba = ml.getData("gspc", 4, "M5");

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

        //nice for debuggin
        //for (int k=0; k<inDate.length; k++)  // display data
           // System.out.println(k+". "+inDate[k]+","+inOpen[k]+","+inHigh[k]+","+inLow[k]+","+inClose[k]+","+inVol[k]);

        //keeping it simple here...
        double[] op = sMA(8,inClose);
      // System.out.println("returned L="+op.length);
/*        int diff = inDate.length - op.length;
        System.out.println("diff=" + diff +" last ele="+op[op.length-1]);
        for (int i = 0; i < op.length; i++) {
            if (op[i] != 0)
                System.out.println(inDate[diff+i]+" integer ["+i+"] is "+op[i]);
        }
*/        //candleScan();
        //dojiScan();
        //williamsMFI(ba);
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



    private double[] prepReturn(double[] in){
        double[] back = in;

            for(int i=0;i<in.length;i++){
                back[i] = (double) i;
            }

        return back;
    }

    public double[] sMA(int period, double[] inValue) {

        //The "lookback" is really your indicator's period minus one because it's expressed as an array index
        //At least that's true for movingAverage(...)
        lookback = lib.movingAverageLookback(period-1, MAType.Sma);

        retCode = lib.movingAverage(0, inValue.length - 1, inValue, lookback + 1, MAType.Sma, outBegIdx, outNbElement, output);

        System.out.println("O=" + output.length + " RO=" + outNbElement.value + "BG=" + outBegIdx.value);

        //prep the output array
        double[] outTemp = new double[outNbElement.value];

        for (int i = 0; i < outNbElement.value; i++) {
          //  System.out.println(inDate[i+outBegIdx.value] + " index [" + i + "] is " + output[i]);
            outTemp[i] = output[i];
        }

        System.out.println("returning");
        return outTemp;
    } //end sma

    public double[] Stochastic(int period, double[] inValue) {

        //The "lookback" is really your indicator's period minus one because it's expressed as an array index
        //At least that's true for movingAverage(...)
        //lookback = lib.movingAverageLookback(period-1, MAType.Sma);

        retCode = lib.movingAverage(0, inValue.length - 1, inValue, lookback + 1, MAType.Sma, outBegIdx, outNbElement, output);
        // System.out.println("O=" + output.length);

        //prep the output array
        double[] outTemp = new double[outNbElement.value];
        for (int i = 0; i < outNbElement.value; i++) {
            if (output[i] != 0)
                // System.out.println(inDate[outBegIdx.value+i]+" index ["+i+"] is "+output[i]);
                outTemp[i] = output[i];
        }

        return outTemp;
    }

    public void candleScan(){

        MInteger outNBElement = new MInteger();
        int startIdx = 0;
        int endIdx = inOpen.length-1;
        int outInteger = inOpen.length;

        //RetCode ret = lib.cdlEngulfing(startIdx, endIdx, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
        RetCode ret = lib.cdlEngulfing(startIdx,endIdx,inOpen,inHigh,inLow,inClose,outBegIdx,outNBElement,outputInt);
        System.out.println("engulfing candle scan " +ret.toString());
       // System.out.println("out beg idx is "+outBegIdx.value);
      //  System.out.println("out NB element is "+outNBElement.value);
       // System.out.println("out integer length is " + outputInt.length);

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
        RetCode ret = lib.cdlDoji(startIdx, endIdx, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outputInt);
        System.out.println("doji candle scan " +ret.toString());
        //System.out.println("out beg idx is "+outBegIdx.value);
       // System.out.println("out NB element is "+outNBElement.value);
       // System.out.println("out integer length is "+ outputInt.length);

        for (int i = 0; i < outNBElement.value; i++) {
            if (outputInt[i] != 0)
                System.out.println(inDate[startIdx+outBegIdx.value+i]+" integer ["+i+"] is "+outputInt[i]);
        }

    }

    private int williamsMFI(BarArray bar){
        int barColor = -1;

        //1 = Green
        //2 = Fade
        //3 = Fake
        //4 = Squat

        //high,low and volume.
        //range      High-Low
        //divided by Volume

        inDate = bar.getDateArray();
        inOpen = bar.getOpenArray();
        inHigh = bar.getHighArray();
        inLow = bar.getLowArray();
        inClose = bar.getCloseArray();
        inVol = bar.getVolArray();
        double[] mfi = bar.getVolArray();

        for (int j = 0; j < inVol.length; j++) {
           mfi[j] = 0;
        }

        double range=0;
        for (int i = 0; i < inVol.length; i++) {
           if(inVol[i] != 0) {
               range = (inHigh[i] - inLow[i]);
               mfi[i] = range / inVol[i];
               //System.out.println("item="+i+" mfi=" + mfi[i] + " vol=" + inVol[i]);
           }
        }

        //Identify the MFI Bars
        //index=1 to begin
        for (int k=1; k < inVol.length;k++){
            barColor = -1;
            //Green if both mfi and vol increaed over previous day
            if ((mfi[k-1] < mfi[k]) && (inVol[k-1] < inVol[k])){   // both increased Green
              //  barColor = 1;
            } else if ((mfi[k-1] > mfi[k]) && (inVol[k-1] > inVol[k])){  //both decreased Fade
                barColor = 2;
            } else if ((mfi[k-1] < mfi[k]) && (inVol[k-1] > inVol[k])){ // mfi up, vol down Fake
                barColor = 3;
            } else if ((mfi[k-1] > mfi[k]) && (inVol[k-1] < inVol[k])) { // mfi down, vol up Squat
                barColor = 4;
            }  // end MFI bars

            if(barColor > 0){
                System.out.println("item="+k+" Date "+ inDate[k] + " Color= " + barColor);
            }
        }

        return barColor;

    }
    public static void main(String[] args){
        //keeping this really simple...
        TALibWrapper demo = new TALibWrapper();
    } //end main



} //end class