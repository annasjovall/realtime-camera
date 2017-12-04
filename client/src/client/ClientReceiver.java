package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClientReceiver extends Thread {

	private SharedData clientMonitor;
	private InputStream is;

	public ClientReceiver(SharedData mon) {
		clientMonitor = mon;
	}

	public void run() {
		while (true) {
			try {
				clientMonitor.waitUntilServerIsActive();
				is = clientMonitor.getSocketRead().getInputStream();
				DataInputStream in = new DataInputStream(is);

				int len = in.readInt();
				int timeStamp = in.readInt(); // TODO: Use Timestamp
				System.out.println("LENGTH: " + len);
				byte[] data = new byte[len];
				for (int i = 0; i < len; i++) {
					data[i] = in.readByte();
				}
				System.out.println(data.length);

				clientMonitor.addToQueue("", data, data.length);
			} catch (IOException e) {
				clientMonitor.disconnect();
			} catch (InterruptedException e) {
				clientMonitor.disconnect();
				e.printStackTrace();
			}
		}
	}
}
