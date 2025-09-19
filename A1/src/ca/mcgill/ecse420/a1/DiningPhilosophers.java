package ca.mcgill.ecse420.a1;

import java.util.concurrent.Semaphore;

public class DiningPhilosophers {
	private static final int N = 5;  // Number of philosophers
	private static final Semaphore[] chopsticks = new Semaphore[N];
	/* Q3.2 Fair semaphore to ensure FIFO order:
	 * The semaphore queue ensures that only N-1 philosophers are permitted to pick up chopsticks. If the queue is full, once a philosopher has eaten, they are dequeued and the awaiting philospher is allowed to join the queue. This solves the issue of deadlocks occuring
	 * 
	 * Furthermore, semphores enforce a fairness policy based on longest wait time, therefore the ordering of the queue is based on FIFO. Meaning, the queue ensures that all philosphers get a chance to eat based on the order they joined the queue. This solution avoids starvation while promoting fairness.
	 */
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
			/* Q3.1 How deadlock occurs
			* Deadlock will occur when every philospher picks up their left chopstick and are then stuck waiting for the right chopstick.
			*/
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
