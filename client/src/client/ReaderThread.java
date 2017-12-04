package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReaderThread extends Thread {

	private SharedData monitor;

	public ReaderThread(SharedData monitor) {
		this.monitor = monitor;
	}

	public void run() {
		while (true) {
			try {
				monitor.waitUntilServerIsActive();
				InputStream inputStream = monitor.getSocketRead().getInputStream();
				DataInputStream dataInputStream = new DataInputStream(inputStream);

				int packageSize = dataInputStream.readInt();
				long timeStamp = dataInputStream.readLong();

				byte[] frames = new byte[packageSize];
				for (int i = 0; i < packageSize; i++)
					frames[i] = dataInputStream.readByte();
				monitor.addToQueue(new DataFrame(timeStamp, frames));

			} catch (IOException e) {
				monitor.disconnect();
			} catch (InterruptedException e) {
				monitor.disconnect();
				e.printStackTrace();
			}
		}
	}
}
