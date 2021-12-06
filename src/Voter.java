import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Voter implements Runnable {


    public static AtomicInteger votersTerminated = new AtomicInteger(0);
    public static AtomicBoolean allVotersLeft = new AtomicBoolean(false);

    public Thread thread;

    private int threadID = 0;
    private AtomicBoolean IDChecked = new AtomicBoolean(false);
    private AtomicBoolean reachedFrontOfKioskLine = new AtomicBoolean(false);
    private AtomicInteger usingScanningMachine = new AtomicInteger(-1);


    public Voter(int threadID) {
        this.threadID = threadID;
        thread = new Thread(this);

    }

    @Override
    public void run() {
        arrive();
        waitInLine();

        int shortestLineIndex = Kiosk_Helper.getShortestKioskLineIndex();

        waitInKioskLine(shortestLineIndex);
        waitForKioskHelper(shortestLineIndex);
        useKiosk(shortestLineIndex);

        goToScanningRoom();
        waitForScanningMachine();
        scanBallot();

        leave();
    }

    public void arrive() {
        int randomNumber = Main.randomNumber(0, 5000);

        try {
            Thread.sleep(randomNumber);
        } catch (InterruptedException e) {

        }

        ID_Checker.votersLine.add(this);

    }

    public void waitInLine() {

        msg("Arrived at voting place. Waiting in line for ID to be checked.");
        while (!IDChecked.get()){}
    }

    public void waitInKioskLine(int kioskLineIndex) {

        Kiosk_Helper.kioskLines[kioskLineIndex].add(this);
        msg("Enters kiosk line " + (kioskLineIndex + 1) + ".");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reachedFrontOfKioskLine.set(true);
        msg("Reaches front of kiosk line " + (kioskLineIndex + 1) + ".");

    }

    public void waitForKioskHelper(int kioskLineIndex) {
        while (true) {
            try {
                if (this.thread.isInterrupted()) {
                    throw new InterruptedException();
                }
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    public void useKiosk(int kioskLineIndex) {
        msg("Enters Kiosk " + (kioskLineIndex + 1) + ". (Thread Interrupted By Kiosk Helper)");
        try {

            Thread.sleep(Main.randomNumber(2000, 4000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        msg("Done using kiosk " + (kioskLineIndex + 1) + ".");
        Kiosk_Helper.kiosksOccupied[kioskLineIndex].set(false);

    }

    public void goToScanningRoom() {
        int currentPriority = thread.getPriority();
        thread.setPriority(currentPriority + 1);
        msg("Rushing to scanning room. (Priority Increased)");
        try {
            Thread.sleep(Main.randomNumber(1000, 3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.setPriority(currentPriority);
        msg("Entered scanning room and is waiting to use scanning machine. (Priority Restored to Original Value)");

        Scanning_Helper.votersInScanningRoom.add(this);
        Scanning_Helper.votersInScanningRoom.sort(Comparator.comparingInt(o -> o.threadID));
        Scanning_Helper.votersWaitingForScanningMachine.add(this);

    }

    public void waitForScanningMachine() {

        while (usingScanningMachine.get() == -1) { }

    }

    public void scanBallot() {
        msg("Allowed to use scanning machine " + (usingScanningMachine.get() + 1) + ", but slows down because he/she is nervous. ('yield()' Called Twice)");
        Thread.yield();
        Thread.yield();
        msg("Scanning ballot at scanning machine " + (usingScanningMachine.get() + 1) + ".");
        try {
            Thread.sleep(Main.randomNumber(1000, 3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msg("Done using scanning machine " + (usingScanningMachine.get() + 1) + ".");
        Scanning_Helper.scanningMachines[usingScanningMachine.get()].set(false);
    }

    public void leave() {

        Voter highestVoter = Scanning_Helper.votersInScanningRoom.get(Scanning_Helper.votersInScanningRoom.size() - 1);
        if (highestVoter != this && highestVoter.thread.isAlive()) {
            try {
                msg("Ready to leave and joins " + highestVoter.getName() + ".");
                highestVoter.thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        msg("Leaves.");
        Scanning_Helper.votersInScanningRoom.remove(this);

        if (votersTerminated.incrementAndGet() >= Main.num_voters)
            allVotersLeft.set(true);

    }


    public void checkID() {
        IDChecked.set(true);
    }


    public boolean reachedFrontOfKioskLine() {
        return reachedFrontOfKioskLine.get();
    }

    public void useScanningMachine(int index) {
        Scanning_Helper.scanningMachines[index].set(true);
        usingScanningMachine.set(index);
    }

    public String getName() {
        return "Voter " + threadID;
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - Main.time) + "] " + getName() + ": " + m);
    }


}
