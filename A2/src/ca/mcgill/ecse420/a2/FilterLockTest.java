package A2.src.ca.mcgill.ecse420.a2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FilterLockTest {
    private final int nThreads;
    private final int iters;
    private final int PER_THREAD;

    private final AtomicInteger counter;
    private int[] counterArray;
    private int[] threadIdArray;

    private FilterLock lock;

    public FilterLockTest(int nThreads, int iters) {
        this.nThreads = nThreads;
        this.iters = iters; // dynamically assign to ensure results are not truncated
        this.PER_THREAD = iters / nThreads;
        
        this.lock = new FilterLock(nThreads);
        ThreadID.reset(); 

        this.counter = new AtomicInteger(0);
        this.counterArray = new int[iters];
        this.threadIdArray = new int[iters];
    }
    
    private class FilterThread implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < PER_THREAD; i++) {
                lock.lock();
                try {
                    int counterValue = counter.getAndIncrement();
                    counterArray[counterValue] += 1;
                    threadIdArray[counterValue] = ThreadID.get();

                    // Allow threads to interleave
                    try {
                        Thread.sleep((long) Math.random()); 
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); 
                        return;
                    }

                } finally {
                    lock.unlock();
                }
            }
        }
    }
    
    public void runTest() {
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < nThreads; i++) {
            executor.execute(new FilterThread());
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            // wait for all threads to finish
        }

        // Verify the results
        System.out.flush();
        for(int i = 0; i < iters; i++) {
            System.out.println("ID: " + threadIdArray[i] + " counter = " + (i + 1));
        }
        System.out.println("Final counter value: " + counter.get());
    }
    
    public static void main(String[] args) {
        int[] nThreadsOptions = {2, 3, 4, 5, 6, 7, 8};
        for (int n : nThreadsOptions) {
            System.out.println("\n--- Running FilterLock Test with N=" + n + " threads ---");
            FilterLockTest test = new FilterLockTest(n, (n*10));
            test.runTest();
        }
    }
}