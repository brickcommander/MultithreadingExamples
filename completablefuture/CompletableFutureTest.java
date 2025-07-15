package completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureTest {

    public static void main(String[] args) {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("Worker " + Thread.currentThread().getName());
            } catch (Exception e) {
    
            }
            return "OK";
        }); // creates daemon thread by default (JVM won't wait)

        try {
            System.out.println("Main waiting");
            String res2 = completableFuture.getNow("NOT OK"); // return "NOT OK" if completableFuture is not completed else actual value
            System.out.println("Main " + res2);
            String res = completableFuture.get(); // now main thread will wait for completableFuture to finish its task
            System.out.println("Main " + res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("Main " + Thread.currentThread().getName());
    }



}
