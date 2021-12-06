import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Kiosk_Helper implements Runnable {

    public static Vector<Voter>[] kioskLines = new Vector[Main.num_k];
    public static AtomicBoolean[] kiosksOccupied = new AtomicBoolean[Main.num_k];

    public Thread thread;

    public Kiosk_Helper() {
        thread = new Thread(this);
    }

    @Override
    public void run() {
        while (!Voter.allVotersLeft.get()) {

            for (int i = 0; i < kioskLines.length; i++) {

                if (!kioskLines[i].isEmpty()
                        && kioskLines[i].get(0).reachedFrontOfKioskLine()) {

                    if (!kiosksOccupied[i].get()) {
                        kiosksOccupied[i].set(true);
                        Voter voter = kioskLines[i].remove(0);
                        msg("Helping " + voter.getName() + ". (Interrupting Voter Thread)");
                        voter.thread.interrupt();
                    }

                }

            }

        }
        msg("Leaves.");
    }

    public static int getShortestKioskLineIndex() {
        int shortestLineIndex = 0;
        for (int i = 0; i < kioskLines.length; i++) {
            if (kioskLines[i].size() < kioskLines[shortestLineIndex].size()) {
                shortestLineIndex = i;
            }
        }
        return shortestLineIndex;
    }

    public String getName() {
        return "Kiosk Helper";
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - Main.time) + "] " + getName() + ": " + m);
    }
}
