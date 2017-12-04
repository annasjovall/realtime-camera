package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class SharedData {
	public static final int IDLE_MODE = 0;
	public static final int MOVIE_MODE = 1;
	public static final int DISCONNECT_MODE = 9;

	private Queue<DataFrame> unPackedImages;
	private boolean serverActive;
	private boolean isConnected;
	private Socket socketRead;
	private Socket socketWrite;
	private String host = "";
	private int serverReadPort;
	private int serverWritePort;
	private boolean forceMode;
	private int prevMode = IDLE_MODE;
	private int mode = IDLE_MODE;

	public SharedData() {
		this.unPackedImages = new LinkedList<>();
		this.forceMode = false;
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

	public synchronized void disconnect() {
		isConnected = false;
		notifyAll();
	}

	public synchronized void connect(String host, String serverReadPort, String serverWritePort) {
		this.host = host;
		isConnected = true;
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

	public synchronized boolean trySetMode(int mode) {
		if (!forceMode) {
			prevMode = this.mode;
			this.mode = mode;
			notifyAll();
			return true;
		}
		return false;
	}

	public synchronized int getMode() throws InterruptedException, IOException {
		while (mode == prevMode)
			wait();
		prevMode = mode;
		return mode;
	}

	public synchronized void forceSetMode(int mode) {
		prevMode = this.mode;
		this.mode = mode;
		forceMode = true;
		notifyAll();
	}

	public synchronized void exitForceMode() {
		forceMode = false;
		notifyAll();
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
		while (isConnected){
			wait();
		}
		this.mode = DISCONNECT_MODE;
		serverActive = false;
		notifyAll();
	}
}
