package thread;

public class Type2 implements Runnable {

    @Override
    public void run() {
        for(int i=0; i<10; i++) {
            System.out.println("Runnable Example: " + Thread.currentThread().getName());
        }
    }
    
}
