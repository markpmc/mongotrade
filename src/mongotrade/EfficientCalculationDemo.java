package mongotrade;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

        import com.tictactec.ta.lib.Core;
        import com.tictactec.ta.lib.MAType;
        import com.tictactec.ta.lib.MInteger;
        import com.tictactec.ta.lib.RetCode;
        import java.io.IOException;

/**
 *
 * @author josh taylor
 */
public class EfficientCalculationDemo {
    //first things first, declare all your variables that will be needed by
    //TA-Lib

    //private double input[]; //used in some TA-Lib examples but not needed here. We are using the "close" array in this example to make it intuitive.
    //private int inputInt[]; //used in some TA-Lib examples but not needed here
    private double output[];
    //private int outputInt[]; //used in some TA-Lib examples but not needed here
    private MInteger outBegIdx;
    private MInteger outNbElement;
    private RetCode retCode;
    private Core lib;
    private int lookback;//surprisingly useful for this demo
    //The above are the core variables required for most TA-Lib functions.
    //See the contructor for complete initialization.

    //the next initialization is the array of "close" values for a data set
    //which might track the last value at a given period of time. This array
    //also serves as the record of data in the application over time. Therefore,
    //this is the array that will grow over time with updates.
    public static double[] close = new double[]{91.500000, 94.815000, 94.375000, 95.095000, 93.780000, 94.625000, 92.530000, 92.750000, 90.315000, 92.470000, 96.125000,
            97.250000, 98.500000, 89.875000, 91.000000, 92.815000, 89.155000, 89.345000, 91.625000, 89.875000, 88.375000,
            87.625000, 84.780000, 83.000000, 83.500000, 81.375000, 84.440000, 89.250000, 86.375000, 86.250000, 85.250000,
            87.125000, 85.815000, 88.970000, 88.470000, 86.875000, 86.815000, 84.875000, 84.190000, 83.875000, 83.375000,
            85.500000, 89.190000, 89.440000, 91.095000, 90.750000, 91.440000, 89.000000, 91.000000, 90.500000, 89.030000,
            88.815000, 84.280000, 83.500000, 82.690000, 84.750000, 85.655000, 86.190000, 88.940000, 89.280000, 88.625000,
            88.500000, 91.970000, 91.500000, 93.250000, 93.500000, 93.155000, 91.720000, 90.000000, 89.690000, 88.875000,
            85.190000, 83.375000, 84.875000, 85.940000, 97.250000, 99.875000, 104.940000, 106.000000, 102.500000, 102.405000,
            104.595000, 106.125000, 106.000000, 106.065000, 104.625000, 108.625000, 109.315000, 110.500000, 112.750000, 123.000000,
            119.625000, 118.750000, 119.250000, 117.940000, 116.440000, 115.190000, 111.875000, 110.595000, 118.125000, 116.000000,
            116.000000, 112.000000, 113.750000, 112.940000, 116.000000, 120.500000, 116.620000, 117.000000, 115.250000, 114.310000,
            115.500000, 115.870000, 120.690000, 120.190000, 120.750000, 124.750000, 123.370000, 122.940000, 122.560000, 123.120000,
            122.560000, 124.620000, 129.250000, 131.000000, 132.250000, 131.000000, 132.810000, 134.000000, 137.380000, 137.810000,
            137.880000, 137.250000, 136.310000, 136.250000, 134.630000, 128.250000, 129.000000, 123.870000, 124.810000, 123.000000,
            126.250000, 128.380000, 125.370000, 125.690000, 122.250000, 119.370000, 118.500000, 123.190000, 123.500000, 122.190000,
            119.310000, 123.310000, 121.120000, 123.370000, 127.370000, 128.500000, 123.870000, 122.940000, 121.750000, 124.440000,
            122.000000, 122.370000, 122.940000, 124.000000, 123.190000, 124.560000, 127.250000, 125.870000, 128.860000, 132.000000,
            130.750000, 134.750000, 135.000000, 132.380000, 133.310000, 131.940000, 130.000000, 125.370000, 130.130000, 127.120000,
            125.190000, 122.000000, 125.000000, 123.000000, 123.500000, 120.060000, 121.000000, 117.750000, 119.870000, 122.000000,
            119.190000, 116.370000, 113.500000, 114.250000, 110.000000, 105.060000, 107.000000, 107.870000, 107.000000, 107.120000,
            107.000000, 91.000000, 93.940000, 93.870000, 95.500000, 93.000000, 94.940000, 98.250000, 96.750000, 94.810000,
            94.370000, 91.560000, 90.250000, 93.940000, 93.620000, 97.000000, 95.000000, 95.870000, 94.060000, 94.620000,
            93.750000, 98.000000, 103.940000, 107.870000, 106.060000, 104.500000, 105.000000, 104.190000, 103.060000, 103.420000,
            105.270000, 111.870000, 116.000000, 116.620000, 118.280000, 113.370000, 109.000000, 109.700000, 109.250000, 107.000000,
            109.190000, 110.000000, 109.200000, 110.120000, 108.000000, 108.620000, 109.750000, 109.810000, 109.000000, 108.750000,
            107.870000};

    //This is the array of "update" data that will provide "future" data as
    //"close values," which will obviously be added to the close array. These
    //values will be added one at a time in order to mimic an update process as
    //if it were to happen in real-time.
    public static double[] updates = new double[]{
            107.000000, 91.000000, 93.940000, 93.870000, 95.500000, 93.000000, 94.940000, 98.250000, 96.750000, 94.810000,
            94.370000, 91.560000, 90.250000, 93.940000, 93.620000, 97.000000, 95.000000, 95.870000, 94.060000, 94.620000,
            93.750000, 98.000000, 103.940000, 107.870000, 106.060000, 104.500000, 105.000000, 104.190000, 103.060000, 103.420000,
            105.270000, 111.870000, 116.000000, 116.620000, 118.280000, 113.370000, 109.000000, 109.700000, 109.250000, 107.000000,
            109.190000, 110.000000, 109.200000, 110.120000, 108.000000, 108.620000, 109.750000, 109.810000, 109.000000, 108.750000,
            107.870000};

    //This is the period for the indicator
    public int period = 0;

    public EfficientCalculationDemo() {
        //initialize everything required for holding data
        lib = new Core();
        //input = new double[close.length];//used in some TA-Lib examples but not needed here
        //inputInt = new int[close.length];//used in some TA-Lib examples but not needed here
        output = new double[close.length];
        //outputInt = new int[close.length];//used in some TA-Lib examples but not needed here
        outBegIdx = new MInteger();
        outNbElement = new MInteger();

        //again, keeping this simple so excuse the sloppiness...
        simpleMovingAverageCall();
    }

    /**
     * resets the arrays used in this application since they are only
     * initialized once
     */
    private void resetArrayValues() {
        //Even though the next chunck of code is commented out, pay attention.
        //you will probably be finding that you need to initialize your unused
        //arrays with default values in your application simply to make avoiding
        //nulls easy.

        //provide default "fill" values to avoid nulls.

        //for (int i = 0; i < input.length; i++) {
        //    input[i] = (double) i;
        //    inputInt[i] = i;
        //}
        //for (int i = 0; i < output.length; i++) {
        //    output[i] = (double) -999999.0;
        //    outputInt[i] = -999999;
        //}

        //provide some "fail" values up front to ensure completion if correct.
        outBegIdx.value = -1;
        outNbElement.value = -1;
        retCode = RetCode.InternalError;
        lookback = -1;

    }

    private void showDefaultTALIBOutput(){
        System.out.println("\nPrinting the default TA-LIB output to the screen");
        System.out.println("lookback = " + lookback + "\noutBegIdx=" + outBegIdx.value
                + "\noutNbElement=" + outNbElement.value + "\nretCode=" + retCode);
        System.out.println("output.length = " + output.length);//your array holding your calculated indicator values
        System.out.println("close.length = " + close.length);//your array holding your original source data set

        System.out.println("\nThe most important variable from the list above");
        System.out.println("is 'lookback.' For this example, which uses ");
        System.out.println("a simple moving average, it is really just your period");
        System.out.println("minus 1 because lookback serves as the array position equivilent");
        System.out.println("value for whatever the moving average period may be.");
        System.out.println("(at least this is true for moving average in TA-Lib)");

        System.out.println("\nYou are in the \"showDefaultTALIBOutput()\" method");

        System.out.println("\nThe following is the default TA-Lib output after calling");
        System.out.println("the moving average function.");
        System.out.println("\nClose  \t" + "Indicator");

        //just print out the outputs as TA-Lib structures them
        for (int i = 0; i < output.length; i++) {
            System.out.println(close[i] + ",\t " + output[i]);
        }

        System.out.println("\nNotice the default values, 0.0, are at the end of the list when they");
        System.out.println("should be correlating with their proper data values at the beginning");
        System.out.println("of the list. This is TA-lib's way of doing business.");
        System.out.println("\nThere's issues with doing it this way. The biggest is that");
        System.out.println("every developer is expecting the calculated outputs to be lined");
        System.out.println("up nice and neat with the value to which it actually correlates");
        System.out.println("TA-Lib does NOT do this for us!");
        System.out.println("\nAnother point is that you probably don't want 0.0 as your default");
        System.out.println("value in the output array. You're probably going to want something more outrageous");
        System.out.println("like -999999.0 which you can always check for instead of NULL.");
        System.out.println("I'm going to change the 0.0 values in my output array to this outrageous");
        System.out.println("number to make it easier on implementation later.");
        System.out.println("So here's my NEW recommendation if you are using TA-Lib in its raw format:");
        System.out.println("Look at the next set of outputs from this example...\n");
    }

    private void showFinalOutput() {
        System.out.println("You are now in the 'showFinalOutput()' method and the");
        System.out.println("values entering the next step of this demo are:");
        System.out.println("lookback = " + lookback + "\noutBegIdx=" + outBegIdx.value
                + "\noutNbElement=" + outNbElement.value + "\nretCode=" + retCode);
        System.out.println("output.length = " + output.length);//your array holding your calculated indicator values
        System.out.println("close.length = " + close.length);//your array holding your original source data set

        System.out.println("\nFirst, we'll fix the output array so that it is structured");
        System.out.println("correctly. In the previous demo called, \"TALibDemo,\" a");
        System.out.println("different approach was taken in that the output array was");
        System.out.println("left alone and the output from that array was phinegled");
        System.out.println("in a for loop to LOOK LIKE it was outputing the calculated");
        System.out.println("values in correlation to the correct index in the original");
        System.out.println("data array. Now we will actually restructure it to match index");
        System.out.println("for index against the original data. This is as one would");
        System.out.println("naturally expect to see the data in correlation with itself.");

        System.out.println("\nYou are currently in the 'showFinalOutput()' method, which");
        System.out.println("should easily be changed to 'fixOutputArray()' in your code.");
        System.out.println("(Once you strip out all the System.out's)");

        //**********************************
        //fix the output array
        //
        //you'll notice how extremely valuable that strange "lookback" variable
        //is...
        double tempOutput[] = new double[output.length];
        for (int i = 0; i < tempOutput.length; i++) {
            if(i<lookback)
                tempOutput[i] = -999999.0;
        }
        System.arraycopy(output, 0, tempOutput, lookback, output.length-lookback);//extremely fast array copy

        output = tempOutput;//notice that by referencing, you don't have to
        //re-copy the new temporary array into the output variable, which is
        //your key variable that controls the indicator's calculated values.
        //You'll use this pattern over and over again when you simulate updates
        //in the next step of the demo.

        //output array is fixed
        //***********************************

        System.out.println("\nPrinting the output you'd PREFER to see to the screen");

        for (int i = 0; i < output.length; i++) {
            System.out.println("output " + i + " = " + output[i]);
        }

        System.out.println("\nYou'll notice everything is set up just as it should be");
        System.out.println("from index to index. That being -999999.0 should be in the ");
        System.out.println("first positions where the moving average with a period of 10");
        System.out.println("in this example is not yet represented because not enough positions");
        System.out.println("have been calculated. And this time in this demo it is actually");
        System.out.println("structured the proper way in the indicator's 'output' array as opposed");
        System.out.println("to the previous demo which used a pattern to make it 'look' like");
        System.out.println("it was correlating. So to complete the proof, we will print it out:");
        System.out.println("You'll notice in the code itself that 'i' is the control variable");
        System.out.println("for both the 'close' array and 'output' array, which verifies the one.");
        System.out.println("to one correlation.");

        System.out.println("\nClose  \t" + "Indicator");
        //'close' and 'output' should match index for index now...
        for (int i = 0; i < output.length; i++) {
            System.out.println(close[i] + ",\t " + output[i]);
        }

        System.out.println("\nAnd now the values are actually lining up without being forced.");
        System.out.println("This is what every developer really wants.");

        System.out.println("\n**********************************************************");
        System.out.println("One point to make here is that anyone who strips the");
        System.out.println("System.out's out of this class will have methods that a utility class");
        System.out.println("can use. You'll have to create your own utility class.");
        System.out.println("You'll have to restructure it some for your implementation.");
        System.out.println("**********************************************************");

        System.out.println("\nNext, we'll take this to 'the next level' and simulate an update");
        System.out.println("We'll do two things:");
        System.out.println("\t1. We'll use an array called 'update' to hold data values to simulate future data");
        System.out.println("\t2. We'll go a step further and create an efficient indicator calculation pattern");
        System.out.println("\t   that TA-Lib does, in fact, allow us to do.");

        System.out.println("\nThe idea behind efficient calculation of the indicator is that we");
        System.out.println("do NOT want to be forever calculating over the entire data set every");
        System.out.println("time we have an update to the most recent data item. What we really");
        System.out.println("want to do is calculate only the absolute minimum amount of data items");
        System.out.println("as is necessary to get an accurate indicator value based on the period");
        System.out.println("of that indicator.");

        System.out.println("\nFor our purposes with the MA indicator of 10, we'll add a data item");
        System.out.println("to our data set [the 'close' array in the class] and calculate");
        System.out.println("the next indicator's value based on only the previous 10 data");
        System.out.println("values in the close array. This pattern has greater and greater");
        System.out.println("returns in performance for your application the larger your data");
        System.out.println("set grows. The data item's value is represented as 'd' in the following.");
        System.out.println("Notice also the 'close' array [your original data record], and the");
        System.out.println("'output' array [your indicator data record], have grown by one.");
    }

    public void calculateAllUpdates(){
        //this method is provided to show the flow of how to make it happen

        //we'll just start over at position 0 in the update array so the
        //output will show two valued at 107.0 for positions 253 and 254
        for (int i = 0; i < updates.length; i++) {
            double d = updates[i];//your update listener has fired...
            double temp[] = new double[close.length+1];

            System.arraycopy(close, 0, temp, 0, close.length);
            temp[temp.length-1] = d;
            close = temp; //you've now added a new updated value to your
            //application's "record" of raw data, which is held in the "close"
            //array

            double shortCalc[] = new double[period];
            double tempOut[] = new double[output.length+1];
            System.arraycopy(output, 0, tempOut, 0, output.length);
            tempOut[tempOut.length-1] = -999999.0;//just a junk value to avoid nulls

            output = tempOut;//very important to not forget to re-reference this new array of a new length
            //for your primary output array whose whole purpose is to be the record of all indicator values!

            retCode = lib.movingAverage(close.length - period, close.length - 1, close, period, MAType.Sma, outBegIdx, outNbElement, shortCalc);

            tempOut[tempOut.length-1] = shortCalc[shortCalc.length-1];//don't forget to update
            //the temporary output array's final position with the newly calculated
            //value in the shortCalc's final position. Else you'll just be left with -999999.0
            //Since the output array is referencing this object, you're good to go!
        }

    }

    private void showOutputAgain(){
        System.out.println("\nClose  \t" + "Indicator");
        for (int i = 0; i < output.length; i++) {
            System.out.println(close[i] + ",\t " + output[i]);
        }
    }

    public void simpleMovingAverageCall() {
        resetArrayValues();
        period = 10;//a MA indicator with a 10 period
        lookback = lib.movingAverageLookback(period, MAType.Sma);

        //outBegIdx.value = lookback;
        //outNbElement.value = close.length - outBegIdx.value + 1;
        System.out.println("\nStarting everything off...");
        System.out.println("We'll have a moving average indicator with a period of 10:");
        System.out.println("Lookback=" + lookback);
        System.out.println("outBegIdx.value=" + outBegIdx.value);
        System.out.println("outNbElement.value=" + outNbElement.value);
        retCode = lib.movingAverage(0, close.length - 1, close, period, MAType.Sma, outBegIdx, outNbElement, output);
        //   retCode = lib.movingAverage(0,input.length-1,input,10,MAType.Sma,outBegIdx,outNbElement,output);
        showDefaultTALIBOutput();
        showFinalOutput();

        //now that you've gone through the whole process once at data load time,
        //let's throw in an update process and repeat...
        mimicUpdate();
    }

    public void mimicUpdate(){
        System.out.println("\nBy the way, you are now in the 'mimicUpdate()' method");

        //the following for loop is the outline for the entire process...

        //grab an update value from the update array. We are mimicing an update
        //listener here. Just trying to keep this ridicously simple while
        //showing the steps in a "compressed" and logical format

        double d = updates[0];//your update listener has fired...
        System.out.println("d= " + d);
        //reinitialize the "close" array, which is the actual record of your
        //data in your application, to one index larger because we are adding
        //one data item at a time. Use a temporary array to acheive this.
        System.out.println("close length BEFORE the update = " + close.length);

        double temp[] = new double[close.length+1];

        System.arraycopy(close, 0, temp, 0, close.length);
        temp[temp.length-1] = d;
        close = temp; //you've now added a new updated value to your
        //application's "record" of raw data, which is held in the "close"
        //array
        System.out.println("close length AFTER the update = " + close.length);

        System.out.println("\nLook in the class's mimicUpdate() method for a description of what is happening now...");
        //now here's where the efficiency comes in, DO NOT spend time
        //recalculating the entire output array which holds your indicator
        //values. here's the process:
        //1. create a temporary array that is one position larger than the output array
        //2. create a 'shortCalc' array that is only the size of your indicator's period
        //3. copy the output array to temporary array, which leaves the last slot empty
        //4. update the end index of the temp array with your default value -999999.0
        //5. set output array equal to the temp array
        //6. do the calculation using using the following:
        //6a - the start index of the calculation will be the [length of the close array minus the period]
        //6b - use only the 'shortCalc' array to temporarily hold the indicator values
        //      which will immediately be updated in the temporary output array,
        //      which itself is referenced by the actual 'output' array
        //7. Run calculation over a minimum number of indexes in the close array

        double shortCalc[] = new double[period];
        double tempOut[] = new double[output.length+1];
        System.arraycopy(output, 0, tempOut, 0, output.length);
        tempOut[tempOut.length-1] = -999999.0;//just a junk value to avoid nulls

        output = tempOut;//very important to not forget to re-reference this new array of a new length
        //for you primary output array whose whole purpose is to be the record of all indicator values!

        retCode = lib.movingAverage(close.length - period, close.length - 1, close, period, MAType.Sma, outBegIdx, outNbElement, shortCalc);
        tempOut[tempOut.length-1] = shortCalc[shortCalc.length-1];//don't forget to update
        //the temporary output array's final position with the newly calculated
        //value in the shortCalc's final position. Else you'll just be left with -999999.0
        //Since the output array is referencing this object, you're good to go!

        System.out.println("\nIt's important at this point in time to show you the output");
        System.out.println("from the 'shortCalc' array");
        System.out.println("\n**********************\nshowing the shortCalc....");
        for (int i = 0; i < shortCalc.length; i++) {
            double e = shortCalc[i];
            System.out.println("shortCalc " + i + " = " + shortCalc[i]);
        }

        System.out.println("\nNotice the first value is exactly the same value from above");
        System.out.println("that is in the 9th from the last position and each succeeding");
        System.out.println("value is exactly eqivalent to the output array above for each position");
        System.out.println("thereafter. (remember that output was BEFORE the update so it's one");
        System.out.println("position shorter than the 10th position for our 10 period indicator");
        System.out.println("These were not copied in 'shortCalc.' They were actually ");
        System.out.println("calculated. At this point you are probably wondering how the first");
        System.out.println("value in shortCalc was calculated so accurately. The answer is ");
        System.out.println("if you trace the TA-Lib code, you'll see they automatically count");
        System.out.println("back the right number of positions in your original data array ");
        System.out.println("to make the first calculated position accurate. TA-Lib has already");
        System.out.println("figured this out for us on our behalf. This is the correct approach.");

        System.out.println("\nSo now we have added a data item to the 'close' array and calculated");
        System.out.println("the new indicator value in the output array at the same index position.");
        System.out.println("And the end result is super fast even in Java with guaranteed improved rates");
        System.out.println("of return for you the larger your data set grows. This is because the");
        System.out.println("requirements for the quick calculation never grow being that they are limited.");
        System.out.println("to only as much as your indicator period requires.");

        System.out.println("\nThe updated outout looks like the following:");

        showOutputAgain();

        System.out.println("You are now at the end of mimicUpdate() and the end of the Demo");
        System.out.println("If you want you can uncomment the last two lines of mimicUpdate()");
        System.out.println("and re-run the demo to see use of the 'calculateAllUpdates()'");
        System.out.println("method. It's sole purpose is to give you something you can copy/paste");
        System.out.println("into your own code which will give you the basic flow of iterating");
        System.out.println("through successive updates in your application");

        System.out.println("\n a final note: Read and Use comments in the class as well as this demo.");

        //calculateAllUpdates();
        //showOutputAgain();

        System.out.println("\nThe overall approach you must take for your application is to:");
        System.out.println("1. Load your data at startup");
        System.out.println("2. calculate over the entire array of loaded data at least once");
        System.out.println("3. Now and only now can you utilize the efficient calculation approach");
        System.out.println("   shown in this demo with TA-Lib for each successive update");

        System.out.println("\n\nGood Luck! - JT");
    }

    public static void main(String[] args){
        //keeping this really simple...
        EfficientCalculationDemo demo = new EfficientCalculationDemo();
    }
}