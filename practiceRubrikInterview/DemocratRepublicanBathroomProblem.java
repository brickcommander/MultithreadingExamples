package practiceRubrikInterview;

/*
Problem Statement:
Bathroom Problem or DemocratRepublican Problem
There is only one bathroom with capacity = 3
At one time, either only men (or democrat) can use or only women (or republican) can use the bathroom
There should never be more than capacity people in the bathroom simultaneously
The solution should avoid deadlocks, for not don't worry about starvation
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DemocratRepublicanBathroomProblem {
    private int turn; // turn = 0: none, turn = 1: democrat, turn = 2: republican
    int capacity;
    private final Lock lock;
    private final Condition waitCondition;

    public DemocratRepublicanBathroomProblem(int capacity) {
        turn = 0;
        this.capacity = capacity;
        lock = new ReentrantLock(false);
        waitCondition = lock.newCondition();
    }

    public void democratUseBathroom(String name) throws InterruptedException {
        lock.lock();
        while((turn == 2) || (turn == 1 && capacity == 0)) {
            waitCondition.await();
        }
        turn = 1;
        capacity--;
        System.out.println("Currently D=" + (3-capacity));
        democratProcess(name);
        lock.unlock();
    }

    private void democratProcess(String name) {
        new Thread(() -> {
            try {
                System.out.println("Democrat processing   :" + name);
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
            } finally {
                exitPerson();
            }
        }).start();
    }

    public void republicanUseBathroom(String name) throws InterruptedException {
        lock.lock();
        while((turn == 1) || (turn == 2 && capacity == 0)) {
            waitCondition.await();
        }
        turn = 2;
        capacity--;
        System.out.println("Currently R=" + (3-capacity));
        republicanProcess(name);
        lock.unlock();
    }

    private void republicanProcess(String name) {
        new Thread(() -> {
            try {
                System.out.println("Republican processing :" + name);
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
            } finally {
                exitPerson();
            }
        }).start();
    }

    private void exitPerson() {
        lock.lock();
        capacity++;
        if(capacity == 3) turn = 0;
        waitCondition.signalAll();
        lock.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        DemocratRepublicanBathroomProblem p = new DemocratRepublicanBathroomProblem(3);
        List<Thread> threads = new ArrayList<>();
        for(int i=0; i<200; i++) {
            int finalI = i;
            if(i%2==0) {
                threads.add(new Thread(() -> {
                    try {
                        p.republicanUseBathroom("R:" + finalI);
                    } catch (InterruptedException e) {
                        System.out.println("Exception Occured R:" + finalI);
                    }
                }));
            } else {
                threads.add(new Thread(() -> {
                    try {
                        p.democratUseBathroom("D:" + finalI);
                    } catch (InterruptedException e) {
                        System.out.println("Exception Occured D:" + finalI);
                    }
                }));
            }
        }

        for(int i=0; i<200; i++) {
            Thread.sleep(50);
            threads.get(i).start();
        }
    }
}
