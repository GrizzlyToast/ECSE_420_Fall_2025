package ca.mcgill.ecse420.a1;
public class DeadlockExample {
    private static final Object resourceA = new Object();
    private static final Object resourceB = new Object();

    public static void main(String[] args) {
        // Thread 1: Locks resourceA then tries to lock resourceB
        Thread thread1 = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println("Thread 1: Locked Resource A");
                try {
                    // Introduce a small delay to allow Thread 2 to acquire its lock
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread 1: Waiting for Resource B");
                synchronized (resourceB) {
                    System.out.println("Thread 1: Locked Resource B");
                }
            }
        });

        // Thread 2: Locks resourceB then tries to lock resourceA
        Thread thread2 = new Thread(() -> {
            synchronized (resourceB) {
                System.out.println("Thread 2: Locked Resource B");
                try {
                    // Introduce a small delay to ensure Thread 1 has acquired its lock
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread 2: Waiting for Resource A");
                synchronized (resourceA) {
                    System.out.println("Thread 2: Locked Resource A");
                }
            }
        });

        thread1.start();
        thread2.start();
    }
}
