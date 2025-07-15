package synchronization;

public class Test {
    public static void main(String[] args) {
        Counter counter = new Counter();
        
        MyThread_UsingRunnable myt = new MyThread_UsingRunnable(counter);
        Thread t1 = new Thread(myt);

        MyThread_UsingRunnable myt2 = new MyThread_UsingRunnable(counter);
        Thread t2 = new Thread(myt2);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (Exception e) {

        }

        System.out.println("Impl Using Runnable: " + counter.getCount());

        test2();
    }

    private static void test2() {
        Counter counter = new Counter();
        MyThread_UsingThread myt = new MyThread_UsingThread(counter);
        MyThread_UsingThread myt2 = new MyThread_UsingThread(counter);

        myt.start();
        myt2.start();

        try {
            myt.join();
            myt2.join();
        } catch (Exception e) {

        }

        System.out.println("Impl Using Thread: " + counter.getCount());
    }
}
