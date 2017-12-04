package client;

import gui.MainWindow;

public class EventDispatcher extends Thread {
	private SharedData monitor;
	private MainWindow window;

	public EventDispatcher(SharedData monitor, MainWindow window) {
		this.monitor = monitor;
		this.window = window;
	}

	public void run() {
		while (true) {
			try {
				SharedData.Data data = monitor.popUnpackedImage();
				if(monitor.getCameraId() == 1) 
					window.refreshCamera1(data.buffer);
				else 
					window.refreshCamera2(data.buffer);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}