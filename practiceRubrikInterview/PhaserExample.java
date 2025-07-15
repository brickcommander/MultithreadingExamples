package practiceRubrikInterview;

import java.util.concurrent.Phaser;

public class PhaserExample {
    static class CarManufacturingLine {
        private final Phaser phaser;
        public CarManufacturingLine(Phaser phaser) {
            this.phaser = phaser;
        }
        public void manufactureCar() {
            System.out.println("Manufacturing Car... " + Thread.currentThread().getName());
            System.out.println("Car Manufactured. " + Thread.currentThread().getName());
            phaser.arriveAndAwaitAdvance();
        }
        public void paintCar() {
            System.out.println("Painting Car... " + Thread.currentThread().getName());
            System.out.println("Car Painted. " + Thread.currentThread().getName());
            phaser.arriveAndAwaitAdvance();
        }
        public void testCar() {
            System.out.println("Testing Car... " + Thread.currentThread().getName());
            System.out.println("Car Tested. " + Thread.currentThread().getName());
            phaser.arriveAndAwaitAdvance();
        }
    }

    private static final Phaser phaser = new Phaser(3) {
        // task to be performed one completion of each phase
        protected boolean onAdvance(int phase, int registeredParties) {
            System.out.println("Phase " + phase + " completed. : " + registeredParties);
            return registeredParties==0; // terminate when all deregistered
        }
    };

    public static void main(String[] args) throws InterruptedException {
        CarManufacturingLine car = new CarManufacturingLine(phaser);
        Runnable task = () -> {
            car.manufactureCar();
            car.paintCar();
            car.testCar();
            System.out.println("Completed. " + Thread.currentThread().getName());
            phaser.arriveAndDeregister(); // when all parties call this, phaser is automatically terminates. After this all arrive and awaitAdvance() call will be ignored
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        Thread t4 = new Thread(task); // the phaser is terminated so it won't be blocked
        t4.start();
        t4.join();

//        phaser.forceTermination();
    }
}
