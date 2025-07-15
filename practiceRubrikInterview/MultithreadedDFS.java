package practiceRubrikInterview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class DFS_v1 {
    private final Map<Integer, Integer> hashMap;
    private final Lock lock;
    private final List<List<Integer>> G;

    public DFS_v1(List<List<Integer>> g) {
        lock = new ReentrantLock();
        hashMap = new HashMap<>();
        this.G = g;
    }

    public void DFS(Integer root) {
        lock.lock();
        try {
            if(hashMap.containsKey(root)) {
                return;
            }
            hashMap.put(root, 1);
            System.out.println(Thread.currentThread().getName());
        } finally {
            lock.unlock();
        }


        List<Thread> childWorkers = new ArrayList<>();

        for(Integer child: G.get(root)) {
            Integer currChild = child;

            Runnable task = () -> {
                DFS(currChild);
            };

            Thread worker = new Thread(task, "Child-"+child);
            worker.start();
            childWorkers.add(worker);
        }

        for(Thread worker: childWorkers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + worker.getName());
            }
        }
    }
}

public class MultithreadedDFS {
    public static void main(String[] args) throws InterruptedException {

        List<List<Integer>> G = new ArrayList<>();
        int nodes = 10;
        for(int i=0; i<nodes; i++) {
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

        DFS_v1 dfs = new DFS_v1(G);
        dfs.DFS(0);

        DFS_v2 dfs2 = new DFS_v2(G); // if threads < nodes -> deadlock situation
        System.out.println(dfs2.DFS(0));
        dfs2.shutdown();
    }
}

/*
Problem:
- Suppose you're using a fixed thread pool with, say, 2 threads.
- dfs(node) submits tasks for 3 children: A, B, and C.
- A and B are picked up by the thread pool.
- A and B also call dfs(...) and submit more tasks, but the pool has no free threads.
- A and B block on task.get() waiting for child tasks to finish — but the pool is full and can’t run those tasks.
- So now:
    - A is waiting for its children.
    - B is waiting for its children.
    - No new threads are available.
    - 🧨 Deadlock.
 */

class DFS_v2 {
    private final Map<Integer, Integer> hashMap;
    private final Semaphore semaphore;
    private final List<List<Integer>> G;
    private final ExecutorService executors = Executors.newFixedThreadPool(3);

    public DFS_v2(List<List<Integer>> g) {
        semaphore = new Semaphore(1);
        hashMap = new HashMap<>();
        this.G = g;
    }

    public void shutdown() {
        executors.shutdown();
    }

    public int DFS(Integer root) throws InterruptedException {
        synchronized (hashMap){
            if(hashMap.containsKey(root)) {
                return 0;
            }
            hashMap.put(root, 1);
            System.out.println(Thread.currentThread().getName());
        }

        int sum = root;
        List<Future> childWorkers = new ArrayList<>();

        for(Integer child: G.get(root)) {

            Callable<Integer> task = () -> {
                return DFS(child);
            };

            childWorkers.add(executors.submit(task));
        }

        for(Future worker: childWorkers) {
            try {
                sum += (int)worker.get();
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            } catch (ExecutionException e) {
                System.out.println("ExecutionException");
            }
        }

        return sum;
    }
}