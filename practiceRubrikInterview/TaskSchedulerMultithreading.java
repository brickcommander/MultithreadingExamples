package practiceRubrikInterview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/*
Implement two methods in a class, schedule() and waitUntilComplete()
schedule should enqueue the work and be non-blocking
waitUntilComplete() should wait until all the pending tasks are completed and should be blocking
 */

public class TaskSchedulerMultithreading {
    private final ExecutorService threadPool;
    private final List<Future> futures;

    public TaskSchedulerMultithreading(int poolSize) {
        threadPool = Executors.newFixedThreadPool(poolSize);
        futures = new ArrayList<>();
    }

    public void schedule(Runnable task) {
        futures.add(threadPool.submit(task));
    }

    public void waitUntilComlete() throws ExecutionException, InterruptedException {
        for(Future future: futures) {
            future.get();
        }
    }

}
