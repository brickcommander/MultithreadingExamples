package synchronization;

public class MyThread_UsingThread extends Thread {
    private Counter counter;

    @Override
    public void run() {
        for(int i=0; i<10000000; i++) {
            counter.increment();
        }
    }

    public MyThread_UsingThread(Counter counter) {
        this.counter = counter;
    }
}
