public class Main {

	private static class StartClient extends Thread {
		public void run() {
			try {
				System.out.println("Starting network client");
				ClientSharedData monitor = new ClientSharedData();
				Thread[] threads = new Thread[] {
					new ClientReadThread(monitor),
					new ClientWriteThread(monitor),
					new ClientConnectionThread(monitor, "localhost", 22222),
					new ClientShutdownThread(monitor)
				};

				// Start threads
				for (Thread thread : threads) thread.start();

				// Interrupt threads after some time
				Thread.sleep(10000);
				System.out.println("Interrupting client threads");
				for (Thread thread : threads) thread.interrupt(); // Interrupt threads
				for (Thread thread : threads) thread.join(); // Wait for threads to die

				System.out.println("Network client finished");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
