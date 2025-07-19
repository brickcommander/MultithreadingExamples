package practiceRubrikInterview.practice;

import java.util.concurrent.Semaphore;

public class LRUCacheMultithreading {

    public static void main(String[] args) {
        Semaphore s = new Semaphore(1);
        s.release();
        s.release();
        s.release();
        System.out.println(s.availablePermits());
    }
}
