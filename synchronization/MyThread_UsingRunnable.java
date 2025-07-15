package synchronization;



public class MyThread_UsingRunnable implements Runnable {
    private Counter counter;

    @Override
    public void run() {
        for(int i=0; i<10000000; i++) {
            counter.increment();
        }
    }

    public MyThread_UsingRunnable(Counter counter) {
        this.counter = counter;
    }
}
