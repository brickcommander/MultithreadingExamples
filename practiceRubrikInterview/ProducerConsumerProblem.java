package practiceRubrikInterview;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class ProducerConsumerProblem<T> {
    private Queue<T> buffer = new LinkedList<>();
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public ProducerConsumerProblem(int n) {
        capacity = 5;
    }

    public void put(T x) {
        lock.lock();
        try {
            while(buffer.size() == capacity) {
                System.out.println("Buffer full. Producer waiting...");
                notFull.await(); // wait until space is available
            }
            buffer.add(x);
            System.out.println("Produced : " + x);
            notEmpty.signal();
        } catch (InterruptedException e) {

        } finally {
            System.out.println("Unlocking : put");
            lock.unlock();
        }
    }

    public T take() {
        lock.lock();
        try {
            while(buffer.isEmpty()) {
                System.out.println("Buffer empty. Consumer waiting...");
                notEmpty.await();
            }
            T ans = buffer.remove();
            System.out.println("Consumed : " + ans);
            notFull.signal();
            return ans;
        } catch (InterruptedException e) {

        } finally {
            System.out.println("Unlocking : take");
            lock.unlock();
        }
        return null;
    }
}
