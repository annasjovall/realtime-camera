package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReaderThread extends Thread {

	private SharedData monitor;
	private InputStream inputStream;

	public ReaderThread(SharedData monitor) {
		this.monitor = monitor;
	}

	public void run() {
		while (true) {
			try {
				monitor.waitUntilServerIsActive();
				inputStream = monitor.getSocketRead().getInputStream();
				DataInputStream dataInputStream = new DataInputStream(inputStream);

				int packageSize = dataInputStream.readInt();
				
				String timeStamp = parseTimeStamp(dataInputStream.readInt());

				byte[] frames = new byte[packageSize];
				for (int i = 0; i < packageSize; i++)
					frames[i] = dataInputStream.readByte();

				monitor.addToQueue(timeStamp, frames);
				
			} catch (IOException e) {
				monitor.disconnect();
			} catch (InterruptedException e) {
				monitor.disconnect();
				e.printStackTrace();
			}
		}
	}

	private String parseTimeStamp(int cameraTime) {
		long currentTime = System.currentTimeMillis();
		return (currentTime - cameraTime) + "";
	}
}
