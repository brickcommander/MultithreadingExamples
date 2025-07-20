package practiceRubrikInterview.practice;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem: Implement a multithreaded Task Scheduler
 * - There a limit on concurrency (number of tasks which can run in parallel)
 * - The thread should not wait and return immediately after submitting the task (so that the client does not face any delay)
 * Solution: Above constraint can be implemented using a task queue to hold the tasks and an executable queue to hold threads
 * which can run currently.  As soon as a thread in executable queue finishes, pick the next job from task queue.
 */

class TaskScheduler2 {
    private final int concurrency;
    private final AtomicInteger runningTask = new AtomicInteger(0);
    private final Queue<Runnable> taskQueue;
    private final List<Thread> executors;
    private final Lock queueLock;
    private final Condition check;
    private final AtomicBoolean isShutdown;

    public void submitTask(Runnable task) {
        queueLock.lock();
        try {
            taskQueue.add(task);
            check.signalAll();
        } finally {
            queueLock.unlock();
        }
    }

    public void shutdown() {
        if(isShutdown.get()) return;
        isShutdown.set(true);
        for(Thread t: executors) {
            t.interrupt();
        }
        System.out.println("Shutdown initiated");
    }

    public TaskScheduler2(int concurrency) {
        this.concurrency = concurrency;
        taskQueue = new LinkedList<>();
        executors = new ArrayList<>();
        queueLock = new ReentrantLock();
        check = queueLock.newCondition();
        isShutdown = new AtomicBoolean(false);

        for(int i=0; i<concurrency; i++) {
            Thread t = new Thread(this::execute);
            executors.add(t);
            t.start();
        }
    }

    private void execute() {
        System.out.println(Thread.currentThread().getName() + " executing...");
        while(!isShutdown.get()) {
            Runnable task = null;
            queueLock.lock();
            try {
                while (taskQueue.isEmpty() && !isShutdown.get()) {
                    try {
                        check.await();
                    } catch (InterruptedException e) {
                        // Exit cleanly on interrupt
                        System.out.println(Thread.currentThread().getName() + " interrupted while waiting.");
                        return;
                    }
                }

                // Exit if interrupted before polling
                if (isShutdown.get()) {
                    return;
                }

                task = taskQueue.poll();
            } finally {
                queueLock.unlock();
            }

            runningTask.incrementAndGet();
            System.out.println(runningTask.get());
            task.run();
            runningTask.decrementAndGet();
        }
        System.out.println(Thread.currentThread().getName() + " exited.");
    }
}

public class TaskScheduler_v2 {
    public static void main(String[] args) throws InterruptedException {
        TaskScheduler2 taskScheduler = new TaskScheduler2(5);
        for(int i=0; i<20; i++) {
            final int finalI = i;
            taskScheduler.submitTask(() -> {
                System.out.println(Thread.currentThread().getName() + " task executing " + finalI);
                try {
                    Thread.sleep((long) (Math.random() * 1000 + 1000));
                } catch (InterruptedException ignored) {}
                System.out.println(Thread.currentThread().getName() + " task completed " + finalI);
            });
            System.out.println("Main " + i + " task submitted");
        }

        Thread.sleep(1000);
        taskScheduler.shutdown();
    }
}
