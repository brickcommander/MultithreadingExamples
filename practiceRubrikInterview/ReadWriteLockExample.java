package practiceRubrikInterview;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();
    private int count = 0;

    private void increment() {
        writeLock.lock();
        try {
            count++;
        } finally {
            writeLock.unlock();
        }
    }

    private int getCount() {
        readLock.lock();
        try {
            return count;
        } finally {
            readLock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReadWriteLockExample ex = new ReadWriteLockExample();
        Runnable readTask = new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<10; i++) {
                    System.out.println("Read: " + Thread.currentThread().getName() + " : " + ex.getCount());
                }
            }
        };

        Runnable writeTask = new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<10; i++) {
                    ex.increment();
                    System.out.println("Write: " + Thread.currentThread().getName());
                }
            }
        };

        Thread t1 = new Thread(readTask, "Reader Thread 1");
        Thread t2 = new Thread(writeTask, "Writer Thread 2");
        Thread t3 = new Thread(readTask, "Reader Thread 3");
        Thread t4 = new Thread(writeTask, "Writer Thread 4");
        Thread t5 = new Thread(readTask, "Reader Thread 5");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        Thread.sleep(1000);
        System.out.println(ex.count);
    }
}
