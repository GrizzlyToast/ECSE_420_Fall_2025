package ca.mcgill.ecse420.a2;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TestFilterLock {
    private final int nThreads;
    private final int iters;
    private final int PER_THREAD;

    private final AtomicInteger counter;
    private int[] counterArray;
    private int[] threadIdArray;

    private ArrayList<Integer> waiting = new ArrayList<>();
    private int overtakeCount = 0;

    private FilterLock lock;

    public TestFilterLock(int nThreads, int iters) {
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
                
                System.out.println("WAITING: Thread " + ThreadID.get());
                waiting.add(ThreadID.get());
                
                lock.lock();
                try {
                    // if (waiting.get(0) != ThreadID.get()) {
                    //     overtakeCount++;
                    //     System.out.println("OVERTAKE DETECTED by Thread " + ThreadID.get());
                    // }   
                    System.out.println("RUNNING: Thread " + ThreadID.get());

                    int counterValue = counter.getAndIncrement();
                    counterArray[counterValue] += 1;
                    threadIdArray[counterValue] = ThreadID.get();
                    
                    waiting.remove(Integer.valueOf(ThreadID.get()));
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
        System.out.println("Final value of shared counter (should be: " + iters + ") = " + counter.get());
        System.out.println("Number of overtakes detected: " + overtakeCount);
    }
    
    public static void main(String[] args) {
        int[] nThreadsOptions = {2, 3, 4, 5, 6, 7, 8};
        for (int n : nThreadsOptions) {
            System.out.println("\n--- Running FilterLock Test with N=" + n + " threads ---");
            TestFilterLock test = new TestFilterLock(n, (n*10));
            test.runTest();
        }
    }
}