package practiceRubrikInterview.practice;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * You need to implement a LeaseManager that allows clients to acquire leases on resources. A lease:
 * Is exclusive (only one client can hold it at a time).
 * Is time-bound (expires after a timeout unless renewed).
 * Can be renewed or released before expiration.
 * Must be thread-safe (used in multithreaded environments).
 */
public class LeaseManager {
    private final Lock lock;
    private String currentLeaseHolder = null;
    private long expiryTime = 0;
    private long leaseTimeMs = 0;
    private final ScheduledExecutorService scheduler;

    public LeaseManager() {
        lock = new ReentrantLock();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::expireLease, 0, 1, TimeUnit.MILLISECONDS);
    }

    private void expireLease() {
        long now = System.currentTimeMillis();
        if(expiryTime <= now) {
            lock.lock();
            try {
                if(expiryTime <= now) {
                    currentLeaseHolder = null;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    boolean acquire(String clientId, long leaseTimeMs) {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            if(currentLeaseHolder == null || now >= expiryTime) {
                currentLeaseHolder = clientId;
                this.leaseTimeMs = leaseTimeMs;
                expiryTime = now + leaseTimeMs;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    boolean renew(String clientId) {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            if(now >= expiryTime) {
                currentLeaseHolder = null;
                return false;
            } else if(!clientId.equals(currentLeaseHolder)) {
                return false;
            } else {
                expiryTime = (expiryTime - now) + leaseTimeMs;
                return true;
            }
        } finally {
            lock.unlock();
        }
    }
    void release(String clientId) {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            if(clientId.equals(currentLeaseHolder) && now < expiryTime) {
                currentLeaseHolder = null;
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        LeaseManager manager = new LeaseManager();
        ExecutorService executors = Executors.newFixedThreadPool(20);
        Runnable acquireTask = () -> {
            manager.acquire("1", 1);
        };
    }
}
