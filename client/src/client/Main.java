package client;

import gui.MainWindow;

public class Main {

	public static void main(String[] args) {
		SharedData monitor1 = new SharedData();
		SharedData monitor2 = new SharedData();
		CamerasSharedData camerasMonitor = new CamerasSharedData();
		MainWindow window = new MainWindow(monitor1, monitor2, camerasMonitor);

		Thread[] threads = { 
			new ReaderThread(monitor1), 
			new ConnectionThread(monitor1, window, 1),
			new MotionDetector(monitor1, window, 1, camerasMonitor),
			new WriterThread(monitor1, window, camerasMonitor), 	
			new ReaderThread(monitor2),
			new ConnectionThread(monitor2, window, 2), 
			new MotionDetector(monitor2, window, 2, camerasMonitor),
			new WriterThread(monitor2, window, camerasMonitor),
			new UpdateGUIThread(monitor1, monitor2, window, camerasMonitor)
		};
		for (Thread thread : threads)
			thread.start();
	}
}
