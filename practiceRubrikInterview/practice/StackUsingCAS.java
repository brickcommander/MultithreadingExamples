package practiceRubrikInterview.practice;


import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementing Stack Using Compare and Set
 */

public class StackUsingCAS<T> {
    private static class Node<T> {
        public T value;
        public Node<T> next;
        public Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    private AtomicReference<Node<T>> head;

    public StackUsingCAS() {
        head = new AtomicReference<>();
    }

    public void push(T value) {
        Node<T> currentHead;
        Node<T> newNode;
        do {
            currentHead = head.get();
            newNode = new Node<>(value, currentHead);
        } while(!head.compareAndSet(currentHead, newNode));
    }

    public T pop() {
        Node<T> currentHead;
        Node<T> nextNode;
        do {
            currentHead = head.get();
            if(currentHead == null) return null;
            nextNode = head.get().next;
        } while(!head.compareAndSet(currentHead, nextNode));
        return currentHead.value;
    }

    public boolean isEmpty() {
        return head.get()==null;
    }

    public static void main(String[] args) throws InterruptedException {
        StackUsingCAS<Integer> stack = new StackUsingCAS<>();
        Runnable pushTask = () -> {
            for(int i=0; i<10; i++) {
                stack.push(i);
            }
        };

        Runnable popTask = () -> {
            for(int i=0; i<15; i++) {
                System.out.println(stack.pop());
            }
        };

        Thread t1 = new Thread(pushTask);
        Thread t2 = new Thread(popTask);
        Thread t3 = new Thread(pushTask);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }
}
