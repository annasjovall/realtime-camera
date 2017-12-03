package Client;

public class Main {

	private static class StartClient extends Thread {
		public void run() {
			try {
				System.out.println("Starting network client");
				ClientSharedData monitor = new ClientSharedData();
				Thread[] threads = new Thread[] {
					new ClientReceiver(monitor),
					new ClientWriteThread(monitor),
					new ClientConnectionThread(monitor, "argus-2.student.lth.se", 19999, 22000),
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

	

	public static void main(String[] args) {
		try {
			Thread b = new StartClient();
			b.start();
			b.join();
		} catch (InterruptedException e) {
		}
	}
}
