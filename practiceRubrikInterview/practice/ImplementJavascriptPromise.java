package practiceRubrikInterview.practice;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

class Promise {
    private final Consumer<Integer> onComplete;
    private final Consumer<String> onReject;
    private Thread t;

    public Promise(Callable<Integer> task, Consumer<Integer> onComplete, Consumer<String> onReject) {
        this.onComplete = onComplete;
        this.onReject = onReject;
        execute(task);
    }

    public void execute(Callable<Integer> task) {
        t = new Thread(() -> {
            try {
                Thread.sleep(1000);
                Integer response = task.call();
                onComplete.accept(response);
            } catch (Exception e) {
                onReject.accept("OnFailed!");
            }
        });
        t.start();
    }

    public void join() throws InterruptedException {
        t.join();
    }

    public Promise then(Runnable task) {
        // we can do chaining
        return this;
    }

    public void interrupt() {
        t.interrupt();
    }
}

public class ImplementJavascriptPromise {
    public static void main(String[] args) throws InterruptedException {
        Promise promise = new Promise(() -> 5, (x) -> System.out.println("Answer: " + x), (x) -> System.out.println("Failed: " + x));
        System.out.println("Main");
        promise.interrupt();
        promise.join();
    }
}
