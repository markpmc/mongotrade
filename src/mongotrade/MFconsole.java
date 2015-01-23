package mongotrade;

import javax.swing.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;


public class MFconsole {

    static GetConfig config = new GetConfig();

    public enum Action {
        A, //Add
        D, //Remove
        L,  //List
        I //intialize
    }

    private static final String fQUIT = "quit";
    private static final String fEXIT = "exit";
    private static final String fHELP = "help";


    public static void main(String args[]) throws IOException {
        boolean hasRequestedQuit = false;
        boolean needHelp = false;
        Action action_input = Action.I;

        while(! hasRequestedQuit) {

            //get input from user from command prompt
            System.out.println("Please enter requested action: (help means help)");

            Scanner inputReader = new Scanner(System.in);

            //Getting input in String format
            String action = inputReader.nextLine();

            if(action.trim().length() < 1){action_input = Action.I;}

            //check for a quit
            hasRequestedQuit = action.trim().equalsIgnoreCase(fQUIT) || action.trim().equalsIgnoreCase(fEXIT);

            //check for help request
            needHelp = action.trim().equalsIgnoreCase(fHELP);

            if (needHelp) { printHelp();}

            if (action.trim().equalsIgnoreCase("A")){
                action_input = Action.A;
            } else if (action.trim().equalsIgnoreCase("D")){
                action_input = Action.D;
            } else if(action.trim().equalsIgnoreCase("L")){
                action_input = Action.L;
            }

            switch (action_input) {
                case A:
                    System.out.println("Running Add");
                    addSymbol(inputReader);
                    action_input = Action.I;
                    break;
                case D:
                    System.out.println("Running Delete");
                    deleteSymbol(inputReader);
                    action_input = Action.I;
                    break;
                case L:
                    System.out.println("Running List");
                    action_input = Action.I;
                    break;
                default:
                    if(!needHelp){printHelp();}
                    break;

            } //end switch





        } //end while
    }  //end main

    private static void parseCommand(String cmd){


    } //end parseCommand
    private static void printHelp(){

        System.out.println("exit means exit");

        //System.out.println("To Add a symbol enter the ticker and source ie Add ^gspc,yahoo");
        System.out.println("A. Add a symbol");

        //System.out.println("To Delete a symbol enter the ticker ie Delete ^gspc");
        System.out.println("D. Delete a symbol");

        //System.out.println("To List the current symbols enter List");
        System.out.println("L. List the current symbols");
    } //end printHelp

    private static void deleteSymbol (Scanner inputReader) throws UnknownHostException {
        System.out.println("Enter ticker symbol to remove");
        String ticker = inputReader.nextLine();

        config.removeSymbol(ticker);

    } //deleteSymbol
    private static void addSymbol(Scanner inputReader) throws UnknownHostException {
        System.out.println("Enter ticker symbol to add");
        String ticker = inputReader.nextLine();
        System.out.println("Enter Source [Yahoo]");
        String source = inputReader.nextLine();


        if(source.length() <1){source = "yahoo";}
        while(!source.trim().equalsIgnoreCase("google") && (!source.trim().equalsIgnoreCase("yahoo"))){

            System.out.println("Please enter a valid quote source [Yahoo or Google]");
            source = inputReader.nextLine();
            if(source.length() <1){source = "yahoo";}
        }

        config.addSymbol(ticker,source);
    } //end addSymbol
} //end class