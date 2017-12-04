package client;

import java.io.IOException;

public class ClientConnectionThread extends Thread {

	private SharedData clientMonitor;

	public ClientConnectionThread(SharedData mon) {
		clientMonitor = mon;
	}

	public void run() {
		while (true) {
			try {
				clientMonitor.tryConnect();
				System.out.println("TRY CONNECT");
				clientMonitor.createSockets();
				System.out.println("SOCKETS CREATED");
				clientMonitor.waitUntilNotActive();
				System.out.println("DISCONNECT");
				clientMonitor.closeSockets();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}

		}
	}
}
