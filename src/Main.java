import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static long time = System.currentTimeMillis();
    public static int num_voters = 20;
    public static int num_ID_checkers = 3;
    public static int num_k = 3;
    public static int num_sm = 4;

    public static void main(String[] args) {

        try {

            // Verify argument input
            if (args.length > 0) {
                if (args.length != 1)
                    throw new IllegalArgumentException();
                num_voters = Integer.parseInt(args[0]);
            }

            // Start Voter threads
            Voter[] voters = new Voter[num_voters];
            for (int i = 0; i < voters.length; i++) {
                voters[i] = new Voter(i + 1);
                voters[i].thread.start();
            }

            // Start ID Checker threads
            ID_Checker[] checkers = new ID_Checker[num_ID_checkers];
            for (int i = 0; i < 3; i++) {
                checkers[i] = new ID_Checker(i + 1);
                checkers[i].thread.start();
            }

            // Start Kiosk Helper thread
            Kiosk_Helper kiosk_helpers = new Kiosk_Helper();
            for (int i = 0; i < Kiosk_Helper.kioskLines.length; i++) {
                Kiosk_Helper.kioskLines[i] = new Vector<>();
            }
            for (int i = 0; i < Kiosk_Helper.kiosksOccupied.length; i++) {
                Kiosk_Helper.kiosksOccupied[i] = new AtomicBoolean(false);
            }
            kiosk_helpers.thread.start();

            //Start Scanning Helper thread
            Scanning_Helper scanning_helper = new Scanning_Helper();
            for (int i = 0; i < Scanning_Helper.scanningMachines.length; i++) {
                Scanning_Helper.scanningMachines[i] = new AtomicBoolean(false);
            }
            scanning_helper.thread.start();

        } catch (IllegalArgumentException e) {

            // Print Error Message
            if (e instanceof NumberFormatException)
                System.out.println("Argument must be of type integer.");
            else
                System.out.println("Must contain 1 integer argument parameter.");

        }

    }

    // We will use this static helper method for random sleep in the thread classes
    public static int randomNumber(int max, int min) {
        return (int) (Math.floor(Math.random() * (max - min + 1) + min));
    }
}
