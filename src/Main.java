import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> queue1 = new ArrayBlockingQueue<String>(100);
        BlockingQueue<String> queue2 = new ArrayBlockingQueue<String>(100);
        BlockingQueue<String> queue3 = new ArrayBlockingQueue<String>(100);

        AtomicInteger aCounter = new AtomicInteger();
        AtomicInteger bCounter = new AtomicInteger();
        AtomicInteger cCounter = new AtomicInteger();
        AtomicReference<String> aString = new AtomicReference<>();
        AtomicReference<String> bString = new AtomicReference<>();
        AtomicReference<String> cString = new AtomicReference<>();


        Thread threadGenerator = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                if (i % 100 == 0) {
                    System.out.println("Добавлено " + i);
                }
                try {
                    queue1.put(generateText("abc", 100_000));
                    queue2.put(generateText("abc", 100_000));
                    queue3.put(generateText("abc", 100_000));

                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread threadA = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                if (i % 100 == 0) {
                    System.out.println("Потоком А обработано " + i);
                }
                try {
                    String str = queue1.take();
                    int counter = str.replaceAll("[^a]", "").length();
                    if (counter > aCounter.get()) {
                        aCounter.set(counter);
                        aString.set(str);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread threadB = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                if (i % 100 == 0) {
                    System.out.println("Потоком B обработано " + i);
                }
                try {
                    String str = queue2.take();
                    int counter = str.replaceAll("[^b]", "").length();
                    if (counter > bCounter.get()) {
                        bCounter.set(counter);
                        bString.set(str);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread threadC = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                if (i % 100 == 0) {
                    System.out.println("Потоком С обработано " + i);
                }
                try {
                    String str = queue3.take();
                    int counter = str.replaceAll("[^c]", "").length();
                    if (counter > cCounter.get()) {
                        cCounter.set(counter);
                        cString.set(str);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        threadGenerator.start();
        threadA.start();
        threadB.start();
        threadC.start();
        threadGenerator.join();
        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("\nМаксимальное количество символов а - " + aCounter);
        System.out.println("Строка с максимальным количеством символов а \n" + aString);
        System.out.println("\nМаксимальное количество символов b - " + bCounter);
        System.out.println("Строка с максимальным количеством символов b \n" + bString);
        System.out.println("\nМаксимальное количество символов c - " + cCounter);
        System.out.println("Строка с максимальным количеством символов c \n" + cString);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}