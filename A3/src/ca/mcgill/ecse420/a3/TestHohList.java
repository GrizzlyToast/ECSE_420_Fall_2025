package ca.mcgill.ecse420.a3;

public class TestHohList {

    public static void main(String[] args) throws InterruptedException {
        HohList<Integer> list = new HohList<>();

        // Prepopulate the list with values 0..9
        System.out.println("Initializing list...");
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        System.out.println("Initial list setup complete.\n");

        System.out.println(list);

        // Worker threads:
        // Thread A: repeatedly adds numbers 0..9
        // Thread B: repeatedly removes numbers 0..9
        // Main thread: calls contains()

        Thread adder = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                int value = (int)(Math.random() * 10);
                boolean result = list.add(value);
                System.out.println("[Adder] add(" + value + ") → " + result);
                sleep(20);
            }
        });

        Thread remover = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                int value = (int)(Math.random() * 10);
                boolean result = list.remove(value);
                System.out.println("[Remover] remove(" + value + ") → " + result);
                sleep(20);
            }
        });

        System.out.println("Starting threads...\n");
        adder.start();
        remover.start();

        for (int i = 0; i < 100; i++) {
            int value = (int)(Math.random() * 10);
            boolean result = list.contains(value);
            System.out.println("[Viewer] " + list);
            System.out.println("[Main] contains(" + value + ") → " + result);
            sleep(15);
        }

        adder.join();
        remover.join();

        System.out.println("\n=== Test complete ===");
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
