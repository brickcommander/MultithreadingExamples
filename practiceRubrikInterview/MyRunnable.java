package practiceRubrikInterview;

public class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("MyRunnable : run : " + Thread.currentThread().getName());
    }
}
