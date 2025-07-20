package practiceRubrikInterview.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyScheduledExecutorService {
    private static class MyRunnable implements Runnable {
        public long delay;
        public final Type type;
        public long fixedRate;
        public long nextRunTime;
        public final Runnable runnable;

        public enum Type {
            FIXED_DELAY,
            FIXED_RATE,
            ONE_TIME;
        }

        public MyRunnable(long delay, long fixedRate, Runnable runnable) {
            this.delay = delay;
            this.runnable = runnable;
            this.type = Type.FIXED_RATE;
            this.fixedRate = fixedRate;
            this.nextRunTime = System.currentTimeMillis() + delay;
        }

        public MyRunnable(long delay, Runnable runnable) {
            this.runnable = runnable;
            this.type = Type.FIXED_DELAY;
            this.delay = delay;
            this.nextRunTime = System.currentTimeMillis() + delay;
        }

        public MyRunnable(Runnable runnable) {
            this.runnable = runnable;
            this.type = Type.ONE_TIME;
            this.nextRunTime = System.currentTimeMillis();
        }

        public boolean updateNextRunTime() {
            switch (type) {
                case FIXED_DELAY:
                    nextRunTime = System.currentTimeMillis() + delay;
                    return true;
                case FIXED_RATE:
                    nextRunTime = System.currentTimeMillis() + fixedRate;
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void run() {
            runnable.run();
        }
    }

    PriorityQueue<MyRunnable> taskQueue;
    private final int concurrency;
    private final List<Thread> workers;
    private final Lock lock;
    private final Condition queueNotEmpty;
    private AtomicBoolean shutdown;

    MyScheduledExecutorService(int concurrency) {
        this.concurrency = concurrency;
        workers = new ArrayList<>();
        taskQueue = new PriorityQueue<>((a, b) -> a.nextRunTime < b.nextRunTime ? -1 : 1);
        lock = new ReentrantLock();
        queueNotEmpty = lock.newCondition();
        shutdown = new AtomicBoolean(false);

        for(int i=0; i<concurrency; i++) {
            Thread t = new Thread(this::execute);
            workers.add(t);
            t.start();
        }
    }

    public void scheduleWithFixedDelay(Runnable task, long delay) {
        addTask(new MyRunnable(delay, task));
    }

    public void scheduleAtFixedRateWithFixedDelay(Runnable task, long delay, long fixedRate) {
        addTask(new MyRunnable(delay, fixedRate, task));
    }

    public void schedule(Runnable task) {
        addTask(new MyRunnable(task));
    }

    private void addTask(MyRunnable runnable) {
        if(shutdown.get()) {
            // throws exception
            return;
        }
        lock.lock();
        try {
            taskQueue.offer(runnable);
            queueNotEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        shutdown.set(true);
    }

    private void execute() {
        while(!shutdown.get()) {
            MyRunnable task;
            lock.lock();
            try {
                while(taskQueue.isEmpty()) {
                    queueNotEmpty.await();
                }
                task = taskQueue.poll();
            } catch (InterruptedException e) {
                break;
            } finally {
                lock.unlock();
            }

            if(shutdown.get()) {
                break;
            }

            // insert next entry in taskQueue if fixedRate
            if(task.type.equals(MyRunnable.Type.FIXED_RATE)) {
                task.nextRunTime = task.nextRunTime + task.fixedRate;
                addTask(task);
            }

            task.run();

            // insert next entry in taskQueue if fixedDelay
            if(task.type.equals(MyRunnable.Type.FIXED_DELAY)) {
                task.nextRunTime = System.currentTimeMillis() + task.delay;
                addTask(task);
            }
        }
        System.out.println(Thread.currentThread().getName() + " shutting down");
    }

    public static void main(String[] args) throws InterruptedException {
        MyScheduledExecutorService scheduler = new MyScheduledExecutorService(3);
        Runnable task = () -> {
            System.out.println("Starting task " + Thread.currentThread().getName() + " " + System.currentTimeMillis());
            try {
                Thread.sleep((long) (500 + 3000 * Math.random()));
            } catch (InterruptedException e) {

            }
            System.out.println("Finished task " + Thread.currentThread().getName() + " " + System.currentTimeMillis());
        };

        scheduler.scheduleWithFixedDelay(task, 1000);
        scheduler.scheduleWithFixedDelay(task, 1000);
        scheduler.scheduleWithFixedDelay(task, 1000);
        scheduler.scheduleWithFixedDelay(task, 1000);
        scheduler.scheduleWithFixedDelay(task, 1000);
        scheduler.scheduleWithFixedDelay(task, 1000);

        scheduler.schedule(task);

        scheduler.scheduleAtFixedRateWithFixedDelay(task, 1000, 1500);

        scheduler.scheduleAtFixedRateWithFixedDelay(task, 1000, 1500);

        scheduler.scheduleWithFixedDelay(task, 1000);

        scheduler.scheduleWithFixedDelay(task, 1000);

        Thread.sleep(10000);
        scheduler.shutdown();
    }
}
