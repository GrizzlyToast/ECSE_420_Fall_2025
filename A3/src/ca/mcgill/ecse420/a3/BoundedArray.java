package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.atomic.AtomicInteger;

public class BoundedArray<T> {
    private final T[] items;
    private final int capacity;

    private final ReentrantLock enqLock = new ReentrantLock();
    private final ReentrantLock deqLock = new ReentrantLock();

    private final Condition notFullCondition = enqLock.newCondition();
    private final Condition notEmptyCondition = deqLock.newCondition();

    private final AtomicInteger size = new AtomicInteger(0);

    private int headIdx = 0;
    private int tailIdx = 0;

    public BoundedArray(int capacity) {
        this.capacity = capacity;
        this.items = (T[]) new Object[capacity];
    }


    public void enq(T x) {
        boolean mustWakeDequeuers = false;
        enqLock.lock();
        try {
            while (size.get() == capacity) {
                notFullCondition.await();
            }

        items[tailIdx] = x;
        tailIdx = (tailIdx + 1) % capacity;

            if (size.getAndIncrement() == 0) {
                mustWakeDequeuers = true;
            }
        } catch(InterruptedException ie) {
                ie.printStackTrace();
        } finally {
            enqLock.unlock();   
        }

        if (mustWakeDequeuers) {
            deqLock.lock();
            try {
                notEmptyCondition.signalAll();
            } finally {
                deqLock.unlock();
            }
        }
    }

}
