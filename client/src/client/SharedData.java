package client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class SharedData {
	public static final int IDLE_MODE = 0;
	public static final int MOVIE_MODE = 1;

	private Queue<Data> unPackedImages;
	private boolean serverActive;
	private boolean isConnected;
	private Socket socketRead;
	private Socket socketWrite;
	private Socket socketMotion;
	private int camera;
	private String host = "";
	private int serverReadPort;
	private int serverWritePort;
	// private final int MOTION_PORT = 9094;
	private volatile boolean forceMode;
	private int prevMode = IDLE_MODE;
	private int mode = IDLE_MODE;

	public SharedData(int camera) {
		this.camera = camera;
		this.unPackedImages = new LinkedList<>();
		this.forceMode = false;
		this.isConnected = false;
	}

	public class Data {
		public String info;
		public byte[] buffer;
		public int size;

		public Data(String inf, byte[] buf, int size) {
			info = inf;
			buffer = buf;
			this.size = size;
		}
	}

	public int getCameraId() {
		return camera;
	}

	public synchronized void addToQueue(String string, byte[] buffer, int size) {
		Data data = new Data(string, buffer, size);
		unPackedImages.add(data);
		notifyAll();
	}

	public synchronized void waitUntilServerIsActive() throws InterruptedException {
		while (!serverActive) wait();
	}

	public synchronized void waitUntilNotActive() throws InterruptedException {
		while (serverActive) wait();
	}

	public synchronized void disconnect() {
		isConnected = false;
		notifyAll();
	}

	public synchronized void connect(String host, String serverReadPort, String serverWritePort) {
		System.out.println("SERVER READ PORT: " + serverReadPort + "\nSERVER WRITE PORT: " + serverWritePort);
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

	public synchronized Socket getSocketMotion() {
		return socketMotion;
	}

	public synchronized Data popUnpackedImage() throws InterruptedException {
		while (unPackedImages.isEmpty())
			wait();
		return unPackedImages.poll();
	}

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

	public synchronized void closeForceMode() {
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
		socketRead.close();
		socketWrite.close();
		// socketMotion.close();
		serverActive = false;
		notifyAll();
	}
}
