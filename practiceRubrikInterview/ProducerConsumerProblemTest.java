package practiceRubrikInterview;

public class ProducerConsumerProblemTest {
    public static void main(String[] args) {
        ProducerConsumerProblem<String> pc = new ProducerConsumerProblem<>(5);

        Thread producer = new Thread(() -> {
            for(int i=0; i<10; i++) {
                try {
                    Thread.sleep(500);
                    pc.put("p" + i);
                } catch (InterruptedException e) {
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for(int i=0; i<10; i++) {
                try {
                    Thread.sleep(2000);
                    System.out.println("Consumer: " + pc.take());
                } catch (InterruptedException e) {
                }
            }
        });

        producer.start();
        consumer.start();
    }
}
