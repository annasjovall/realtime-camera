package client;

import gui.MainWindow;

public class UpdateGUIThread extends Thread {
	private SharedData monitor;
	private MainWindow window;

	public UpdateGUIThread(SharedData monitor, MainWindow window) {
		this.monitor = monitor;
		this.window = window;
	}

	public void run() {
		while (true) {
			try {
				DataFrame data = monitor.popUnpackedImage();
				if(monitor.getCameraId() == 1) 
					window.refreshCamera1(data.getFrames());
				else 
					window.refreshCamera2(data.getFrames());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}