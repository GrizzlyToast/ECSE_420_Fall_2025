package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.FileWriter;
import java.io.IOException;

public class MatrixMultiplication {
	
	private static final int NUMBER_THREADS = 13;
	private static final int MATRIX_SIZE = 2000;

        public static void main(String[] args) {
		
		// Generate two random matrices, same size
		// double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		// double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		// System.out.println("-------------------------Parallel-----------------------------");
		// printMatrix(parallelMultiplyMatrix(a, b));	
		// System.out.println("-------------------------Sequential----------------------------");
		// printMatrix(sequentialMultiplyMatrix(a, b, NUMBER_THREADS));

		// System.out.println("Sequential time: " + measurePerformance(MATRIX_SIZE)[0]);
		// System.out.println("Parallel time: " + measurePerformance(MATRIX_SIZE)[1]);

		createMatrixMultiplicationCSV(new int[] {100, 200, 500, 1000, 2000});
		//createThreadPerformanceCSV(Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * Returns the result of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b, int size) {
		double c[][] = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++)
                    c[i][j] += a[i][k] * b[k][j];
            }
        }
		return c;
	}
	
	/**
	 * Returns the result of a concurrent matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
        public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b, int size, int threads) {
			double[][] c = new double[size][size];

			ExecutorService executor = Executors.newFixedThreadPool(threads);

			for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Each element calculation is a task
                DotProduct task = new DotProduct(a, b, c, i, j, size);
                executor.execute(task);
            }
        }

		executor.shutdown();
		    try {
				// Wait for all tasks to finish (timeout after 1 minute)
				executor.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return c;
	}
        
        /**
         * Populates a matrix of given size with randomly generated integers between 0-10.
         * @param numRows number of rows
         * @param numCols number of cols
         * @return matrix
         */
        private static double[][] generateRandomMatrix (int numRows, int numCols) {
             double matrix[][] = new double[numRows][numCols];
        for (int row = 0 ; row < numRows ; row++ ) {
            for (int col = 0 ; col < numCols ; col++ ) {
                matrix[row][col] = (double) ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }


	public static class DotProduct implements Runnable {
		private double[][] a;
		private double[][] b;
		private double[][] c;
		private int row;
		private int col;
		private int size;

		public DotProduct(double[][] a, double[][] b, double[][] c, int row, int col, int size) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.row = row;
			this.col = col;
			this.size = size;
		}

		@Override
		public void run() {
			// Calculate the dot product for one element
			double sum = 0;
			for (int k = 0; k < size; k++) {
				sum += a[row][k] * b[k][col];
			}
			c[row][col] = sum;
    }

	}

	static void printMatrix(double M[][])
    {
        for (int i = 0; i < M[0].length; i++) {
            for (int j = 0; j < M[0].length; j++)
                System.out.print(M[i][j] + " ");

            System.out.println();
        }
    }

	static String[] measurePerformance(int size) {
		double[][] a = generateRandomMatrix(size, size);
		double[][] b = generateRandomMatrix(size, size);

		long startSeq = System.currentTimeMillis();
		sequentialMultiplyMatrix(a, b, size);
		long endSeq = System.currentTimeMillis();

		long startPar = System.currentTimeMillis();
		parallelMultiplyMatrix(a, b, size, NUMBER_THREADS);
		long endPar = System.currentTimeMillis();

		String seqTime = String.valueOf(endSeq - startSeq);
		String parTime = String.valueOf(endPar - startPar);

		return new String[] {seqTime, parTime};
	}

	static void createMatrixMultiplicationCSV(int[] sizes) {
		String csvFile = "matrix_multiplication_performance.csv";
		try (FileWriter writer = new FileWriter(csvFile, false)) { // 'false' to overwrite file each run
			writer.append("Matrix Size,Sequential Time(ms),Parallel Time(ms)\n");
			writer.flush();
			for (int size : sizes) {
				String[] times = measurePerformance(size);
				writer.append(size + "," + times[0] + "," + times[1] + "\n");
				writer.flush(); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void createThreadPerformanceCSV(int threads) {
		String csvFile = "thread_performance.csv";
		try (FileWriter writer = new FileWriter(csvFile)) {
			writer.append("Threads,Time(ms)\n");
			writer.flush(); 
			for (int i = 1; i < (threads+1); i++) {
				double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
				double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
				long startPar = System.currentTimeMillis();
				parallelMultiplyMatrix(a, b, MATRIX_SIZE, i);
				long endPar = System.currentTimeMillis();
				writer.append(i + "," + (endPar - startPar) + "\n");
				writer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
