package mongotrade;

import java.util.Scanner;

public class MFconsole {
    public static void main(String[] args) {

        System.out.println("Enter something here : ");

        String sWhatever;

        Scanner scanIn = new Scanner(System.in);
        sWhatever = scanIn.nextLine();

        scanIn.close();
        System.out.println(sWhatever);
    }
}