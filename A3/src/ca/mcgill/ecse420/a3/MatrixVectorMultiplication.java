package ca.mcgill.ecse420.a3;

import java.util.*;
import java.util.concurrent.*;

public class MatrixVectorMultiplication {

    // Sequential multiply: y = A * x
    public static double[] multiplySeq(double[][] A, double[] x) {
        int n = x.length;
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            double[] Ai = A[i];
            for (int j = 0; j < n; j++) {
                sum += Ai[j] * x[j];
            }
            y[i] = sum;
        }
        return y;
    }

    // Parallel multiply: for each row submit a task that computes row dot-product
    public static double[] multiplyParallel(double[][] A, double[] x, ExecutorService pool, int threshold)
            throws InterruptedException, ExecutionException {
        int n = x.length;
        double[] y = new double[n];
        List<Future<Double>> futures = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            final int row = i;
            futures.add(pool.submit(() -> {
                double sum = 0.0;
                double[] Ai = A[row];
                for (int j = 0; j < n; j++) sum += Ai[j] * x[j];
                return sum;
            }));
        }

        for (int i = 0; i < n; i++) {
            y[i] = futures.get(i).get();
        }
        return y;
    }

    private static double[][] randomMatrix(int n, long seed) {
        Random rnd = new Random(seed);
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            double[] row = A[i];
            for (int j = 0; j < n; j++) row[j] = rnd.nextDouble();
        }
        return A;
    }

    private static double[] randomVector(int n, long seed) {
        Random rnd = new Random(seed + 1);
        double[] v = new double[n];
        for (int i = 0; i < n; i++) v[i] = rnd.nextDouble();
        return v;
    }

    // Equality check for two result vectors (with tolerance)
    private static boolean equalWithin(double[] a, double[] b, double tol) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i] - b[i]) > tol) return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        final int n = 4000;                
        final long seed = 12345L;
        final int[] thresholds = {64, 128, 256, 512, 1024, 2048};
        final double tol = 1e-8;

        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Problem size: n = " + n + " => matrix " + n + "x" + n);

        System.out.println("Allocating matrix and vector...");
        double[][] A = randomMatrix(n, seed);
        double[] x = randomVector(n, seed);

        // Run sequential measurement
        System.out.println("Running sequential multiply...");
        long s0 = System.nanoTime();
        double[] ySeq = multiplySeq(A, x);
        long s1 = System.nanoTime();
        double seqTime = (s1 - s0) / 1e9;
        System.out.printf("Sequential time: %.3f s\n", seqTime);

        // Create pool
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        // Sweep thresholds to find best speedup
        System.out.println("\nTask thresholds: " + Arrays.toString(thresholds));
        for (int th : thresholds) {
            System.out.println("Threshold = " + th);

            long p0 = System.nanoTime();
            double[] yPar = multiplyParallel(A, x, pool, th);
            long p1 = System.nanoTime();
            double parTime = (p1 - p0) / 1e9;

            // Validate and show Results
            boolean ok = equalWithin(ySeq, yPar, tol);
            System.out.printf("Parallel time: %.3f s. Correct: %b. Speedup: %.2fx\n",
                    parTime, ok, seqTime / parTime);
        }

        pool.shutdown();
        System.out.println("Done.");
    }
}
