import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Scanning_Helper implements Runnable {

    public static AtomicBoolean[] scanningMachines = new AtomicBoolean[Main.num_sm];
    public static Vector<Voter> votersWaitingForScanningMachine = new Vector<>();
    public static Vector<Voter> votersInScanningRoom = new Vector<>();

    public Thread thread;

    Scanning_Helper() {
        thread =  new Thread(this);
    }

    @Override
    public void run() {

        while (!Voter.allVotersLeft.get()) {


            try {
                if (scanningMachinesAreAvailable()
                    && !votersWaitingForScanningMachine.isEmpty()) {
                    for (int i = 0; i < scanningMachines.length; i++) {
                        Voter voter = votersWaitingForScanningMachine.remove(0);
                        msg("Helping " + voter.getName() + ".");
                        voter.useScanningMachine(i);
                    }

                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }

        }
        msg("Leaves.");

    }


    public static boolean scanningMachinesAreAvailable() {
        for (AtomicBoolean scanningMachine : scanningMachines)
            if (scanningMachine.get())
                return false;

        return true;
    }

    public String getName() {
        return "Scanning Helper";
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - Main.time) + "] " + getName() + ": " + m);
    }

}
