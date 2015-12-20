package mongotrade;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;

/**
 * Created by markpmc on 12/11/15.
 */
public class RetrieveSymbols {
    public static void main(String[] args) throws UnknownHostException, FileNotFoundException {
        //out.println(config.checkConfig());
        //try {
        //    config.loadConfig();
        //} catch (UnknownHostException e) {
        //    e.printStackTrace();
        //}


        MFConfig cfg = new MFConfig();
        cfg.fetch();
        //config.removeSymbol("^VIX");
    } //end main


} //end class
