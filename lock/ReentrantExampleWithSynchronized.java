package lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantExampleWithSynchronized {
    // same behaviour as ReentrantLock
    
    private final Lock lock = new ReentrantLock();

    public synchronized void outerMethod() {
        // lock.lock();
        try {
            System.out.println("Outer method");
            innerMethod();
        } finally {
            // lock.unlock();
        }
    }

    public synchronized void innerMethod() {
        // lock.lock();
        try {
            System.out.println("Inner method");
        } finally {
            // lock.unlock();
        }
    }

    public static void main(String[] args) {
        ReentrantExample example = new ReentrantExample();
        example.outerMethod();
    }
}
