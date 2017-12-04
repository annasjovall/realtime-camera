package client;

import gui.MainWindow;

public class Main {

	public static void main(String[] args) {
		SharedData monitor1 = new SharedData();
		SharedData monitor2 = new SharedData();
		MainWindow window = new MainWindow(monitor1, monitor2);

		Thread[] threads = { 
			new ReaderThread(monitor1), 
			new ConnectionThread(monitor1),
			new UpdateGUIThread(monitor1, window, 1),
			new MotionDetector(monitor1, "http://argus-2.student.lth.se", 9094),
			new WriterThread(monitor1, window), 
			
			new ReaderThread(monitor2),
			new ConnectionThread(monitor2), 
			new UpdateGUIThread(monitor2, window, 2),
			new MotionDetector(monitor2, "http://argus-3.student.lth.se", 9094),
			new WriterThread(monitor2, window), 
		};
		for (Thread thread : threads)
			thread.start();
	}
}
