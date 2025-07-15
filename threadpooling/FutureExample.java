package threadpooling;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(() -> System.out.println("Hello")); // runnable parameter
        System.out.println(future.get()); // blocking call ( null ); .get() waits for thread to complete
        if(future.isDone()){ // isDone() don't wait for thread to complete
            System.out.println("Task is done !");
        }
        executorService.shutdown();
        executorService.isShutdown();
        executorService.isTerminated();
    }
}
