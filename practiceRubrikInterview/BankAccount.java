package practiceRubrikInterview;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private int balance = 40;
    Lock lock = new ReentrantLock();

    public void withdraw(int amount) {
        System.out.println("1 " + Thread.currentThread().getName() + " : " + amount);
        for(long i=0; i<10000000000L; i++) {

        }
        try {
            if(lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                if(balance >= amount) {
                    try {
                        System.out.println("2 " + Thread.currentThread().getName());
                        Thread.sleep(3000);
                        balance -= amount;
                        System.out.println("3 " + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        System.out.println("9 " + Thread.currentThread().getName());
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock();
                    }
                } else {
                    System.out.println("4 " + Thread.currentThread().getName());
                    lock.unlock();
                }
            } else {
                System.out.println("5 " + Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            System.out.println("8 " + Thread.currentThread().getName() + " " + balance);
            Thread.currentThread().interrupt();
        }

        System.out.println("6 " + Thread.currentThread().getName() + " " + balance);

        if(Thread.currentThread().isInterrupted()) {
            System.out.println("7 " + Thread.currentThread().getName() + " " + balance);
        }
    }
}
