package practiceRubrikInterview.practice;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
/*
we run lots of tasks in our nightly pipeline. Tasks can require external resources to run. For example, the network, an API, file server, etc. These external resources can be flaky. If a resource is down, the task will fail with an environment error. When this happens, you know that all subsequent tasks will fail.

Write a task scheduler
give a graph, each of the edge is dependency
node -> task

catch: even if one of the tasks fail, then we will consider this a failure
and we need to abort
*/

class TaskSchedulerMultithreading {
    public static class Node {
        private Runnable task;
        public final int id;
        private Thread t;
        public Node(int id) {
            this.id = id;
            task = () -> {
                try {
                    Thread.sleep((long) (500 * Math.random() * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
        }
        public Runnable getTask() {
            return task;
        }
        public void runTask() {
            t = new Thread(task);
            t.start();
        }
        public Thread getThread() {
            return t;
        }
    }

    private List<Thread> workerThreads = new ArrayList<>();
    private Queue<Node> q = new LinkedList<>();
    private int[] inDegree;
    private int n;
    private int processed;
    private Object obj = new Object(); // monitor

    public void initGraph(List<List<Integer>> graph, Map<Integer, Node> map) {
        n = graph.size();
        synchronized(obj) {
            processed=0;
            inDegree = new int[n];
            for(int i=0; i<n; i++) {
                for(int j=0; j<graph.get(i).size(); j++) {
                    inDegree[graph.get(i).get(j)]++;
                }
            }

            for(int i=0; i<n; i++) {
                if(inDegree[i] == 0) {
                    q.add(map.get(i));
                }
            }
        }
    }

    public boolean topologicalSorting(List<List<Integer>> graph, Map<Integer, Node> map) throws InterruptedException {
        while(true) {
            synchronized(obj) {
                if(q.size() == 0) {
                    wait();
                }
                if(q.size() == 0 && processed == n) {
                    return true;
                }

                Node f = q.remove();
                f.runTask();
                f.getThread().join();
                processed++;
                if(f.getThread().isInterrupted()) {
                    return false;
                }

                for(int j=0; j<graph.get(f.id).size(); j++) {
                    int child = graph.get(f.id).get(j);
                    inDegree[child]--;
                    if(inDegree[child] == 0) {
                        q.add(map.get(child));
                        notifyAll();
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        TaskSchedulerMultithreading solution = new TaskSchedulerMultithreading();

        List<List<Integer>> graph = new ArrayList<>();
        Map<Integer, Node> hashMap = new HashMap<>();
        // populate the graph and hashmap


        List<Thread> masterThreads = new ArrayList<>();
        int count = 5;
        AtomicBoolean isEverythingGood = new AtomicBoolean(true);

        solution.initGraph(graph, hashMap);
        Runnable bfs = () -> {
            try {
                boolean b = solution.topologicalSorting(graph, hashMap);
                if(!b) {
                    isEverythingGood.set(false);
                }
            } catch (InterruptedException e) {
                // new thread of something
            }
        };

        // calling bfs
        for(int i=0; i<count; i++) {
            masterThreads.add(new Thread(bfs));
        }

        while(true) {
            if(!isEverythingGood.get()) {
                for(Thread thread: masterThreads) {
                    thread.interrupt();
                }
                for(Thread thread: solution.workerThreads) {
                    thread.interrupt();
                }
            }
        }

    }
}