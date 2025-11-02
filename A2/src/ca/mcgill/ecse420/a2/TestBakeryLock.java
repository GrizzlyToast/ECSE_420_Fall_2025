package A2.src.ca.mcgill.ecse420.a2;

import java.util.concurrent.ExecutorService;

public class TestBakeryLock {
    private final static int PER_THREAD = 128;
    private static volatile int lockCounter;
    private static int[] counterArray;
    private static int[] threadIDArray;
    private static BakeryLock lock;

    public static class BakeryThread implements Runnable {
        public void run() {
            for (int i = 0; i < PER_THREAD; i++) {
                lock.lock();
                try {
                    lockCounter = lockCounter + 1;
                    counterArray[lockCounter-1] = lockCounter;
                    threadIDArray[lockCounter-1] = ThreadID.get();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static void runTest(int nThreads) {
        System.out.println("\n=== Testing with " + nThreads + " threads ===");
        
        // Reset everything for this test
        ThreadID.reset();
        lockCounter = 0;
        int COUNT = nThreads * PER_THREAD;
        counterArray = new int[COUNT];
        threadIDArray = new int[COUNT];
        lock = new BakeryLock(nThreads);

        ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(nThreads);

        for (int i = 0; i < nThreads; i++) {
            executor.execute(new BakeryThread());
        }
        
        executor.shutdown();
        while ( !executor.isTerminated()) {
            // wait for all threads to finish
        }

        System.out.flush();
        System.out.println("Final value of shared counter (should be: " + COUNT + ") = " + lockCounter);
    }

    public static void main(String[] args) {
        int[] threadCounts = {2, 3, 4, 5, 6, 7, 8};
        for (int nThreads : threadCounts) {
            runTest(nThreads);
        }
    }
}
