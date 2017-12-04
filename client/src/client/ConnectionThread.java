package client;

import java.io.IOException;

public class ConnectionThread extends Thread {

	private SharedData clientMonitor;

	public ConnectionThread(SharedData mon) {
		clientMonitor = mon;
	}

	public void run() {
		while (true) {
			try {
				clientMonitor.tryConnect();
				clientMonitor.createSockets();
				clientMonitor.waitUntilDisconnect();
				System.out.println("DISCONNECT");
				clientMonitor.closeSockets();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
