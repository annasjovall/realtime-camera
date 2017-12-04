package client;

import java.io.IOException;

import gui.MainWindow;

public class ConnectionThread extends Thread {

	private SharedData monitor;
	private MainWindow window;
	private int cameraID;

	public ConnectionThread(SharedData monitor, MainWindow window, int cameraID) {
		this.monitor = monitor;
		this.window = window;
		this.cameraID = cameraID;
	}

	public void run() {
		while (true) {
			try {
				monitor.tryConnect();
				if (monitor.createSockets()) {
					monitor.closeSockets();
					System.out.println("disconnect");
				} else {
					window.setErrorMessage("Failed to connect to server.", cameraID);
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
