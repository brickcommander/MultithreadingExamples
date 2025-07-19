package practiceRubrikInterview;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierExample {
    public static void main(String[] args) {
        final int THREADS = 3;
        final int CYCLES = 5;

        CyclicBarrier barrier = new CyclicBarrier(THREADS, () -> {
            System.out.println("All threads reached the barrier. Proceeding to next cycle.");
        });

        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            for(int i=1; i<=CYCLES; i++) {
                try {
                    System.out.println("Working in cycle " + i);
                    Thread.sleep((long) (Math.random() * 5000));
                    System.out.println("Waiting at barrier in cycle " + i);
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        };

        for(int i=0; i<THREADS; i++) {
            new Thread(task).start();
        }
    }
}
