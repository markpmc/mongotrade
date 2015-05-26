package mongotrade;

import java.util.ArrayList;

/**
 * Created by mark.mcclellan on 5/19/2015.
 */
public class BarArray {
    private ArrayList<String> bDate = new ArrayList<String>();
    private ArrayList<Double> bOpen = new ArrayList<Double>();
    private ArrayList<Double> bHigh = new ArrayList<Double>();
    private ArrayList<Double> bLow = new ArrayList<Double>();
    private ArrayList<Double> bClose = new ArrayList<Double>();
    private ArrayList<Double> bVolume = new ArrayList<Double>();


  public void init(){
      bDate.clear();
      bOpen.clear();
      bHigh.clear();
      bLow.clear();
      bClose.clear();
      bVolume.clear();
  }

    public void addDate(String d){
        this.bDate.add(d);
    }
    public String[] getDateArray(){
        return exportSrings(bDate);
    }
  public void addOpen(Double f){
      this.bOpen.add(f);
  }
    public double[] getOpenArray(){
        return exportArray(bOpen);
    }
    public void addHigh(Double f){
        this.bHigh.add(f);
    }
    public double[] getHighArray(){
        return exportArray(bHigh);
    }
    public void addLow(Double f){
        this.bLow.add(f);
    }
    public double[] getLowArray(){
        return exportArray(bLow);
    }
    public void addClose(Double f){
        this.bClose.add(f);
    }
    public double[] getCloseArray(){
        return exportArray(bClose);
    }

    public void addVolume(Double f){
        this.bVolume.add(f);
    }
    public double[] getVolArray(){
        return exportArray(bVolume);
    }

    private double[] exportArray (ArrayList<Double> input){
        double[] target = new double[input.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = input.get(i);                // java 1.5+ style (outboxing)
        }
        return target;
    }

    private String[] exportSrings (ArrayList<String> input){
        String[] target = new String[input.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = input.get(i);                // java 1.5+ style (outboxing)
        }
        return target;
    }




} //end BarArray

