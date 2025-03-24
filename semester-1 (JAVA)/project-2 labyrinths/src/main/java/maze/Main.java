package maze;

import lombok.experimental.UtilityClass;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class Main {

    public static void main(String[] args) {
//        InputOutputClass inputOutputClass = new InputOutputClass();
//        inputOutputClass.startProgram();
            AtomicInteger counter1 = new AtomicInteger(0);
            AtomicInteger counter2 = new AtomicInteger(0);

            for (int i = 0; i < 1000000; i++) {
                new Thread(() -> counter1.addAndGet(1)).start();
                new Thread(() -> counter2.addAndGet(1)).start();
            }
        System.out.println(counter1);
        System.out.println(counter2);
    }
}
