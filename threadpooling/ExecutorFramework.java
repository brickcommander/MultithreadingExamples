package threadpooling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorFramework {
    /*
     * The Executor Framework was introduced in Java 5 as part of the java.util.concurrent package
     * to simplify the development of concurrent applications by abstracting away many of the
     * complexities involved in creating and managing threads.
     * Problems:
     * a) Manual Thread Management
     * b) Resource Management
     * c) Scalability
     * d) Thread Reuse
     * e) Error Handling
     * 
     * 3 Core parts:
     * a) Executor
     * b) ExecutorService
     * c) ScheduledExecutorService
     */

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 1; i < 10; i++) {
            int finalI = i;
            executor.submit(() -> {
                long result = factorial(finalI);
                System.out.println(result);
            });
        }
        
        executor.shutdown(); // will wait for all the threads to finish the task, after that shutdown happens. NOTE: Main thread won't wait here.
        // executor.shutdownNow(); // immediately shutdown
        try {
            // waits for 1 second, if the executor is not shutdown by then, throw InterruptedException
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Total time " + (System.currentTimeMillis() - startTime));
    }

    private static long factorial(int n) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}

