package mongotrade;

import java.net.UnknownHostException;

/**
 * Created by markpmc on 12/11/15.
 */
public class ExportSymbols {
    static MFConfig config = new MFConfig();
    static MongoLayerRT ml = new MongoLayerRT();
    public static void main(String[] args) throws UnknownHostException {

        config.export();

    } //end main



}
