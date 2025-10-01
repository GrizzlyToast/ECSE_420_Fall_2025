package ca.mcgill.ecse420.a1;

import java.util.concurrent.Semaphore;

public class DiningPhilosophers {
	private static final int N = 5;  // Number of philosophers
	private static final Semaphore[] chopsticks = new Semaphore[N];
	private static final Semaphore queue = new Semaphore(N - 1, true);

	public static void main(String[] args) {
		for (int i = 0; i < N; i++) {
			chopsticks[i] = new Semaphore(1);
		}
		for (int i = 0; i < N; i++) {
			new Thread(new Philosopher(i)).start();
		}
	}

	public static class Philosopher implements Runnable {
		private final int id;

		Philosopher(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			while (true) {
				System.out.println("Philosopher " + id + " is thinking...");

				try {
					// Request to enter the queue (FIFO)
					queue.acquire();

					// Pick up left chopstick
					chopsticks[id].acquire();

					// Pick up right chopstick
					chopsticks[(id + 1) % N].acquire();

					System.out.println("Philosopher " + id + " is eating...");

					// Simulate eating
					Thread.sleep(100);

					// Put down left chopstick
					chopsticks[id].release();

					// Put down right chopstick
					chopsticks[(id + 1) % N].release();

					// Leave the queue
					queue.release();

					System.out.println("Philosopher " + id + " finished eating and put down chopsticks.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
