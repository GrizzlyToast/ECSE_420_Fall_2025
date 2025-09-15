package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplication {
	
	private static final int NUMBER_THREADS = 1;
	private static final int MATRIX_SIZE = 2;

        public static void main(String[] args) {
		
		// Generate two random matrices, same size
		double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		System.out.println("Sequential");
		printMatrix(sequentialMultiplyMatrix(a, b));
		System.out.println("Parallel");
		printMatrix(parallelMultiplyMatrix(a, b));	
	}
	
	/**
	 * Returns the result of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
		double c[][] = new double[MATRIX_SIZE][MATRIX_SIZE];

        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                for (int k = 0; k < MATRIX_SIZE; k++)
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
        public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
			double[][] c = new double[MATRIX_SIZE][MATRIX_SIZE];

			ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

			for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                // Each element calculation is a task
                Runnable task = new DotProduct(a, b, c, i, j);
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

		public DotProduct(double[][] a, double[][] b, double[][] c, int row, int col) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.row = row;
			this.col = col;
		}

		@Override
		public void run() {
			// Calculate the dot product for one element
			double sum = 0;
			for (int k = 0; k < MATRIX_SIZE; k++) {
				sum += a[row][k] * b[k][col];
			}
			c[row][col] = sum;
    }

	}

	static void printMatrix(double M[][])
    {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++)
                System.out.print(M[i][j] + " ");

            System.out.println();
        }
    }
	
}
