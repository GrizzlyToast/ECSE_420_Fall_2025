package ca.mcgill.ecse420.a2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class BakeryLock implements Lock {
    volatile boolean[] flag;
    volatile int[] label;

    public BakeryLock(int n) {
        flag = new boolean[n];
        label = new int[n];
        for (int i = 0; i < n; i++) {
            flag[i] = false;
            label[i] = 0;
        }
    }

    public void lock() {
        int i = ThreadID.get();
        flag[i] = true;
        // Find max label by reading all labels
        int max = 0;
        for (int j = 0; j < label.length; j++) {
            if (label[j] > max) {
                max = label[j];
            }
        }
        label[i] = max + 1;

        // Wait until no one has priority
        for (int k = 0; k < flag.length; k++) {
            if (k == i) continue;
            while (flag[k]) {
                if (label[k] != 0 && (label[k] < label[i] || (label[k] == label[i] && k < i))) {
                    // Busy wait
                } else {
                    break;
                }
            }
        }
    }   
    public void unlock() {
        int i = ThreadID.get();
        label[i] = 0;
        flag[i] = false;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'lockInterruptibly'");
    }

    @Override
    public Condition newCondition() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'newCondition'");
    }

    @Override
    public boolean tryLock() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tryLock'");
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tryLock'");
    }
}
