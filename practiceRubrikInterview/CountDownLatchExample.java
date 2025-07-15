package practiceRubrikInterview;

import java.util.concurrent.*;

class DependentService implements Callable<String> {
    private final CountDownLatch countDownLatch;

    public DependentService(CountDownLatch latch) {
        countDownLatch = latch;
    }

    @Override
    public String call() {
        try {
            System.out.println(Thread.currentThread().getName() + " service started.");
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
        countDownLatch.countDown();
        return "";
    }
}

public class CountDownLatchExample {
    public static void main(String[] args) throws InterruptedException {
        int n = 3;
        CountDownLatch latch = new CountDownLatch(n);
        ExecutorService service = Executors.newFixedThreadPool(n);
        for(int i=0; i<n; i++) {
            service.submit(new DependentService(latch));
        }
        latch.await();
        System.out.println("Main");
        service.shutdown();
    }
}
