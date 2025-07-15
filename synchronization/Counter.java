package synchronization;

public class Counter {
    private int count = 0;

    public  void increment() { // mutual exclusion using synchronized
        synchronized (this) {
            count++;
        }
    }

    public int getCount() {
        return count;
    }
}
