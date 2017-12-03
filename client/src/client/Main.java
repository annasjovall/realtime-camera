package client;
import java.io.IOException;
import java.net.UnknownHostException;


import gui.MainWindow;

public class Main {

	public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {
		System.out.println("Starting network client");
		ClientMonitor camera1monitor = new ClientMonitor(1);
		ClientMonitor camera2monitor = new ClientMonitor(2);
	    MainWindow mw = new MainWindow(camera1monitor, camera2monitor);
		
		Thread[] threads = new Thread[] {
			new ClientReceiver(camera1monitor),
			new ClientConnectionThread(camera1monitor, "argus-4.student.lth.se", 22000, 19999),
			new EventDispatcher(camera1monitor, mw),
			new MotionDetector(camera1monitor, "http://argus-4.student.lth.se", 9094),
			new StatusDispatcher(camera1monitor,mw),
			new ClientSender(camera1monitor),
			new ClientReceiver(camera2monitor),
			new ClientConnectionThread(camera2monitor, "argus-3.student.lth.se", 22000, 19999),
			new EventDispatcher(camera2monitor, mw),
			new MotionDetector(camera2monitor, "http://argus-3.student.lth.se", 9094),
			new StatusDispatcher(camera2monitor,mw)
		};
		
		// Start threads
		for (Thread thread : threads) thread.start();
		
		// Interrupt threads after some time
		Thread.sleep(60000);
		System.out.println("Interrupting client threads");
		for (Thread thread : threads) thread.interrupt(); // Interrupt threads
		for (Thread thread : threads) thread.join(); // Wait for threads to die

		System.out.println("Network client finished");
	}
}
