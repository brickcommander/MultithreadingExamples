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

public class PlaygroundProblemMultithreading {
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

    private final int capacity;
    private final Playground playground;
    public PlaygroundProblemMultithreading(int n) {
        playground = Playground.getInstance();
        capacity = n;
    }

    void playerEnter(int teamId) { // thread id is player id
        synchronized (this) {
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
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
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
        }).start();
    }

    void playerExit(int teamId) {
        synchronized (this) {
            System.out.println("Player=" + Thread.currentThread().getName() + " of team=" + teamId + " exited.");
            playground.decrementPlayers();
            notifyAll();
        }
    }

    public static void main(String[] args) {
        PlaygroundProblemMultithreading p = new PlaygroundProblemMultithreading(10);
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
