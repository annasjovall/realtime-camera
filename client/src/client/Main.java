package client;

import gui.MainWindow;

public class Main {

	public static void main(String[] args) {
		SharedData monitor1 = new SharedData(1);
		SharedData monitor2 = new SharedData(2);
		MainWindow window = new MainWindow(monitor1, monitor2);

		Thread[] threads = { 
			new ClientReceiver(monitor1), 
			new ClientConnectionThread(monitor1),
			new EventDispatcher(monitor1, window),
			new MotionDetector(monitor1, "http://argus-2.student.lth.se", 9094),
			new ClientSender(monitor1, window), 
			
			new ClientReceiver(monitor2),
			new ClientConnectionThread(monitor2), 
			new EventDispatcher(monitor2, window),
			new MotionDetector(monitor2, "http://argus-3.student.lth.se", 9094),
			new ClientSender(monitor2, window), 
		};
		for (Thread thread : threads)
			thread.start();
	}
}
