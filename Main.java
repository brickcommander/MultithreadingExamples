import thread.Type1;
import thread.Type2;

public class Main {
    public static void main(String[] args) {
        Type1 threadExample = new Type1();
        threadExample.start();

        Type2 type2 = new Type2();
        Thread runnableExample = new Thread(type2);
        runnableExample.start();

        return;
    }
}