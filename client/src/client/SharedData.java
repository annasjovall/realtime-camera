package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class SharedData {

	private Queue<DataFrame> unPackedImages;
	private boolean serverActive;
	private boolean isConnected;
	private Socket socketRead;
	private Socket socketWrite;
	private String host = "";
	private int serverReadPort;
	private int serverWritePort;
	
	public SharedData() {
		this.unPackedImages = new LinkedList<>();
		this.isConnected = false;
	}

	public synchronized void addToQueue(DataFrame dataFrame) {
		unPackedImages.add(dataFrame);
		notifyAll();
	}

	public synchronized void waitUntilServerIsActive() throws InterruptedException {
		while (!serverActive)
			wait();
	}

	public synchronized DataFrame popUnpackedImage() throws InterruptedException {
		while (unPackedImages.isEmpty())
			wait();
		return unPackedImages.poll();
	}
	
	public synchronized boolean hasImage() {
		return !unPackedImages.isEmpty();
	}

	public synchronized void disconnect() {
		isConnected = false;
		notifyAll();
	}

	public synchronized void connect(String host, String serverReadPort, String serverWritePort) {
		isConnected = true;
		this.host = host;
		// TODO: Kolla så att dom är rimliga
		this.serverReadPort = Integer.parseInt(serverReadPort);
		this.serverWritePort = Integer.parseInt(serverWritePort);
		notifyAll();
	}

	public synchronized void tryConnect() throws InterruptedException {
		while (!isConnected)
			wait();
	}

	public synchronized Socket getSocketWrite() {
		return socketWrite;
	}

	public synchronized Socket getSocketRead() {
		return socketRead;
	}

	public synchronized String getHost() {
		return host;
	}

	public synchronized boolean createSockets() {
		try {
			socketRead = new Socket(host, serverWritePort);
			socketWrite = new Socket(host, serverReadPort);
			socketRead.setTcpNoDelay(true);
			socketWrite.setTcpNoDelay(true);
			serverActive = true;
		} catch (IOException e) {
			serverActive = false;
			isConnected = false;
		}
		notifyAll();
		return serverActive;
	}

	public synchronized void closeSockets() throws IOException, InterruptedException {
		while (isConnected)
			wait();
		OutputStream os = socketWrite.getOutputStream();
		byte[] disconnect = new byte[1];
		disconnect[0] = (byte) 9;
		os.write(disconnect);
		socketRead.close();
		socketWrite.close();
		os.flush();
		serverActive = false;
		notifyAll();
	}

	public boolean isConnected() {
		return isConnected;
	}
}
