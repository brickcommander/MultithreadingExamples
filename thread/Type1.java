package thread;

public class Type1 extends Thread {
    @Override
    public void run() {
        for(int i=0; i<10; i++) {
            System.out.println("Thread Example: " + Thread.currentThread().getName());
        }
    }
}
