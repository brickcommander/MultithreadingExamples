package practiceRubrikInterview;


/*
Problem Statement:
Let's say you have a playground where at max 10 person can be present at once
1. All people in the playground should be from the same team.
2. There are multiple people(thread) from different teams are waiting outside the playground to enter.
Solve this problem such that deadlock and starvation don't happen
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// v2: using Reentrant Lock (fair mode) instead of synchronized
public class PlaygroundProblemMultithreading_v2 {
    public static class Playground {
        private int currentPlayers;
        private int currentTeamId;
        private static Playground instance;

        public Playground() {
            currentPlayers = 0;
            currentTeamId = -1;
        }

        public static Playground getInstance() {
            if(instance == null) {
                synchronized (Playground.class) {
                    if(instance == null) {
                        instance = new Playground();
                    }
                }
            }
            return instance;
        }

        public int getCurrentPlayers() {
            return currentPlayers;
        }

        public void incrementPlayers() {
            this.currentPlayers++;
        }
        public void decrementPlayers() {
            this.currentPlayers--;
        }

        public int getCurrentTeamId() {
            return currentTeamId;
        }

        public void setCurrentTeamId(int currentTeamId) {
            this.currentTeamId = currentTeamId;
        }
    }

    private final Lock lock;
    private final Condition canNotEnter;
    private final int capacity;
    private final Playground playground;

    public PlaygroundProblemMultithreading_v2(int n) {
        playground = Playground.getInstance();
        capacity = n;
        lock = new ReentrantLock(true);
        canNotEnter = lock.newCondition();
    }

    void playerEnter(int teamId) { // thread id is player id
        lock.lock();
        try {
            while(true) {
                if(playground.getCurrentPlayers() == 0) {
                    playground.incrementPlayers();
                    playground.setCurrentTeamId(teamId);
                    playerPlaying(teamId);
                    break;
                } else if(playground.getCurrentTeamId() == teamId && playground.getCurrentPlayers() < capacity) {
                    playground.incrementPlayers();
                    playerPlaying(teamId);
                    break;
                } else {
                    try {
                        canNotEnter.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    void playerPlaying(int teamId) {
        new Thread(() -> {
            System.out.println("Player=" + Thread.currentThread().getName() + " of team=" + teamId + " is playing. Total : " + playground.getCurrentPlayers());
            try {
                Thread.sleep((long) (Math.random() * 1000 + 500));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Player=" + Thread.currentThread().getName() + " of team=" + teamId + " has played.");
            playerExit(teamId);
        }, "R-" + Thread.currentThread().getName()).start();
    }

    void playerExit(int teamId) {
        lock.lock();
        try {
            System.out.println("Player=" + Thread.currentThread().getName() + " of team=" + teamId + " exited.");
            playground.decrementPlayers();
        } finally {
            canNotEnter.signalAll();
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        PlaygroundProblemMultithreading_v2 p = new PlaygroundProblemMultithreading_v2(10);
        ExecutorService executorService = Executors.newFixedThreadPool(25);
        Runnable task = () -> {
            p.playerEnter((int) Math.ceil(Math.random() * 10));
        };

        for(int i=0; i<100; i++) {
            executorService.submit(task);
        }
        executorService.shutdown();
    }
}
