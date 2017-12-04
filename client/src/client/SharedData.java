package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class SharedData {
	public static final int IDLE_MODE = 0;
	public static final int MOVIE_MODE = 1;

	private Queue<DataFrame> unPackedImages;
	private boolean serverActive;
	private boolean isConnected;
	private Socket socketRead;
	private Socket socketWrite;
	//private Socket socketMotion;
	private String host = "";
	private int serverReadPort;
	private int serverWritePort;
	// private final int MOTION_PORT = 9094;
	private volatile boolean forceMode;
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
		while (!serverActive) wait();
	}
	
	public synchronized DataFrame popUnpackedImage() throws InterruptedException {
		while (unPackedImages.isEmpty())
			wait();
		return unPackedImages.poll();
	}

	public synchronized void waitUntilDisconnect() throws InterruptedException {
		while (isConnected) wait();
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

//	public synchronized Socket getSocketMotion() {
//		return socketMotion;
//	}

	public synchronized void trySetMode(int mode) {
		if (!forceMode) {
			prevMode = this.mode;
			this.mode = mode;
			notifyAll();
		}
	}

	public synchronized int getMode() throws InterruptedException {
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

	public synchronized void createSockets() throws SocketException {
		try {
			socketRead = new Socket(host, serverWritePort);
			socketWrite = new Socket(host, serverReadPort);
			// socketMotion = new Socket(host, MOTION_PORT);
		} catch (UnknownHostException e) {
			// TODO: Set error to user
		} catch (IOException e) {
			isConnected = false;
		}
		// TODO: Catch exception
		socketRead.setTcpNoDelay(true);
		socketWrite.setTcpNoDelay(true);
		serverActive = true;
		notifyAll();
	}

	public synchronized void closeSockets() throws IOException {
		OutputStream os = socketWrite.getOutputStream();
		byte[] disconnect = new byte[1];
		disconnect[0] = (byte) 9;
		os.write(disconnect);
		socketRead.close();
		socketWrite.close();
		// socketMotion.close();
		serverActive = false;
		notifyAll();
	}
}
