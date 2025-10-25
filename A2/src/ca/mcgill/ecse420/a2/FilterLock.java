package A2.src.ca.mcgill.ecse420.a2;

public class FilterLock implements Lock{
    private int[] level;
    private int[] victim;
    private int n;
    
    public FilterLock(int n) {
        this.n = n;
        level = new int[n];
        victim = new int[n];
        for (int i = 1; i < n; i++) {
            level[i] = 0;
        }
    }

    public void lock() {
        int currentThread = ThreadID.get();
        for (int L = 1; L < n; L++) { // one level at a time
            level[currentThread] = L; // intention to enter level L
            victim[L] = currentThread; // give priority to others

            for (int k = 0; k < n; k++) {
                while ((k != currentThread) && (level[k] >= L && victim[L] == currentThread)) {
                    // condition: Wait as long as someone is at same or higher level, and I'm disgnated as a victim
                }
            }
    }
}

    public void unlock() {
        int current = ThreadID.get();
        level[current] = 0;
    }
}