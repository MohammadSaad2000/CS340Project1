import java.util.Vector;

public class ID_Checker implements Runnable {

    public static Vector<ID_Checker> IDCheckers = new Vector<>();
    public static Vector<Voter> votersLine = new Vector<>();

    public Thread thread;

    private int threadID = 0;


    public ID_Checker(int threadID) {
        this.threadID = threadID;
        thread = new Thread(this);
        IDCheckers.add(this);

    }


    @Override
    public void run() {

        while (!Voter.allVotersLeft.get()) {


            try {
                if (!votersLine.isEmpty()) {
                    Voter current = votersLine.remove(0);
                    checkVoter(current);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }


        }

        msg("Leaves.");

    }


    public void checkVoter(Voter voter) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        msg("Checking " + voter.getName() + ".");
        voter.checkID();
    }

    public String getName() {
        return "ID Checker " + threadID;
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - Main.time) + "] " + getName() + ": " + m);
    }
}
