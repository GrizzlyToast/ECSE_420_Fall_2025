package A2.src.ca.mcgill.ecse420.a2;

public class FilterLock implements Lock{
    private int[] level;
    private int[] victim;
    private int n;
    
    public FilterLock(int nThreads) {
        n = nThreads;
        level = new int[n];
        victim = new int[n];
        for (int i = 0; i < n; i++) {
            level[i] = 0;
        }
    }

    public void lock() {
        int currentThread = ThreadID.get();
        for (int L = 1; L < n; L++) { // one level at a time
            level[currentThread] = L; // intention to enter level L
            victim[L] = currentThread; // give priority to others
            boolean conflicted = true;
            while (conflicted) {
            conflicted = false;
            for (int k = 0; k < n; k++) {
                if (k != currentThread && level[k] >= L && victim[L] == currentThread) {
                    conflicted = true;
                    break;
                }
            }
        }
    }
}

    public void unlock() {
        int current = ThreadID.get();
        level[current] = 0;
    }
}