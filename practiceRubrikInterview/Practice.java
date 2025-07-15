package practiceRubrikInterview;

public class Practice {
    public void fun() throws InterruptedException {
//        MyThread t1 = new MyThread();
//        t1.run();
//        t1.start();
//        MyThread t2 = new MyThread();
//        t2.start();

//        MyRunnable runnable = new MyRunnable();
//        Thread t3 = new Thread(runnable);
//        t3.start();

        /*
        Object o = new Object();
        Runnable task = () -> {
            System.out.println("Practice : fun() : task : " + Thread.currentThread().getName() + " : " + Thread.currentThread().getPriority()
                    + " : " + Thread.currentThread().getThreadGroup() + " : " + Thread.currentThread().threadId());
            synchronized (o) {
                try {
//                Thread.sleep(1000);
                    o.wait();
                } catch (InterruptedException e) {
                    System.out.println("Exception Occured :-> " + Thread.currentThread().getName() + " : " + e);
                }
            }
        };
        Thread t4 = new Thread(task);
        t4.start();
        Thread.sleep(5000);
        t4.interrupt();
        System.out.println("Done");
*/

        BankAccount account = new BankAccount();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                account.withdraw(50);
            }
        };

        Thread t5 = new Thread(task, "Thread-t5");
        Thread t6 = new Thread(task, "Thread-t6");

        t5.start();
        t5.interrupt();
        t6.start();
        t6.interrupt();

    }
}
