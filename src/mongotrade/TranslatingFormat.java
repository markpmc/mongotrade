package mongotrade;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mark.mcclellan on 6/22/2015.
 */
public class TranslatingFormat extends DecimalFormat {
    final TranslatingDataset    xlateDS;
    final DateFormat fmt = new SimpleDateFormat("HH:mm:ss");

    public TranslatingFormat(TranslatingDataset ds) {
        xlateDS = ds;
    }
    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        if (Double.isNaN(number))
            return toAppendTo;
        double timeval = xlateDS.getDisplayXValue(0, (int)number);
        if (Double.isNaN(timeval)){
            return toAppendTo;
        }
        return toAppendTo.append(fmt.format(new Date((long)timeval)));
    }
}
