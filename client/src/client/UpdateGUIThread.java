package client;

import gui.MainWindow;

public class UpdateGUIThread extends Thread {
	private SharedData monitor;
	private MainWindow window;
	private int cameraID;

	public UpdateGUIThread(SharedData monitor, MainWindow window, int cameraID) {
		this.monitor = monitor;
		this.window = window;
		this.cameraID = cameraID;
	}

	public void run() {
		while (true) {
			try {
				if(cameraID == 1) {
					window.refreshCamera1(monitor.popUnpackedImage());
				}else {
					window.refreshCamera2(monitor.popUnpackedImage());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}