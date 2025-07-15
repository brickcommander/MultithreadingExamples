package practiceRubrikInterview;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultithreadedBFS {

    private Map<Integer, Integer> visited = new HashMap<>();
    private Queue<Integer> queue = new LinkedList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition check = lock.newCondition();

    public void executeBFS(final List<List<Integer>> G) throws InterruptedException {
        while (true) {
            int currNode = -1;
            if (lock.tryLock(10, TimeUnit.MILLISECONDS)) {
                while (queue.isEmpty() && visited.size() != 10) {
                    System.out.println(Thread.currentThread().getName() + " Waiting..." + visited.size());
                    check.await(10, TimeUnit.MILLISECONDS);
                }
                if(visited.size() == 10 && queue.isEmpty()) {
                    System.out.println(Thread.currentThread().getName() + " Exiting from loop!");
                    break;
                }
                currNode = queue.poll();
                System.out.println(Thread.currentThread().getName() + " Node: " + currNode);
                check.signalAll();
                lock.unlock();
            } else {
                System.out.println(Thread.currentThread().getName() + " Exiting from loop!");
                break;
            }

            for (Integer neighbour : G.get(currNode)) {
                lock.lock();
                if (!visited.containsKey(neighbour)) {
                    visited.put(neighbour, 1);
                    queue.offer(neighbour);
                }
                check.signalAll();
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        List<List<Integer>> G = new ArrayList<>();
        int nodes = 10;
        for (int i = 0; i < nodes; i++) {
            G.add(new ArrayList<>());
        }

        G.get(0).add(1);
        G.get(0).add(4);
        G.get(1).add(2);
        G.get(1).add(3);
        G.get(2).add(5);
        G.get(3).add(5);
        G.get(4).add(3);
        G.get(5).add(6);
        G.get(5).add(7);
        G.get(6).add(7);
        G.get(7).add(8);
        G.get(7).add(9);

        MultithreadedBFS bfs = new MultithreadedBFS();
        bfs.queue.offer(0);
        bfs.visited.put(0, 1);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for(int i=0; i<5; i++) {
            executorService.submit(() -> {
                try {
                    bfs.executeBFS(G);
                } catch (InterruptedException e) {}
            });
        }

        executorService.shutdown();
    }
}
