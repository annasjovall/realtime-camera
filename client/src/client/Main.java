package client;

import gui.MainWindow;

public class Main {

	public static void main(String[] args) {
		SharedData monitor1 = new SharedData();
		SharedData monitor2 = new SharedData();
		MainWindow window = new MainWindow(monitor1, monitor2);

		Thread[] threads = { 
			new ReaderThread(monitor1), 
			new ConnectionThread(monitor1, window, 1),
			new UpdateGUIThread(monitor1, window, 1),
			new MotionDetector(monitor1, monitor2, window, 1),
			new WriterThread(monitor1, window), 
			
			new ReaderThread(monitor2),
			new ConnectionThread(monitor2, window, 2), 
			new UpdateGUIThread(monitor2, window, 2),
			new MotionDetector(monitor2, monitor1, window, 2),
			new WriterThread(monitor2, window), 
		};
		for (Thread thread : threads)
			thread.start();
	}
}
