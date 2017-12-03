package client;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ClientMonitor {
	private Queue<Data> unPackedImages;
	private volatile boolean readIsActive;
	private volatile boolean writeIsActive;
	private volatile boolean shutdown;
	private volatile Socket socketRead;
	private volatile Socket socketWrite;
	private int camera;
	
	private volatile boolean forceMode;
	private volatile boolean prevIdle;
	private volatile boolean idle;
	
	public ClientMonitor(int camera) {
		this.camera = camera;
		this.idle = true;
		this.unPackedImages = new LinkedList<Data>();
		this.forceMode = false;
		this.shutdown = false;
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

	public int getCameraId(){
		return camera;
	}

	public synchronized void addToQueue(String string, byte[] buffer, int size) {
		Data data = new Data(string, buffer, size);
		unPackedImages.add(data);
		notifyAll();
	}

	public synchronized void setReadActive(boolean active) {
		readIsActive = active;
		notifyAll();
	}
	
	public synchronized void setWriteActive(boolean active) {
		writeIsActive = active;
		notifyAll();
	}
	
	public synchronized void waitUntilReadActive() throws InterruptedException {
		while (!readIsActive) wait();
	}

	public synchronized void waitUntilWriteActive() throws InterruptedException {
		while (!writeIsActive) wait();
	}
	
	public synchronized void waitUntilNotActive() throws InterruptedException {
		while (writeIsActive && readIsActive) wait();
	}
	
	public synchronized void shutdown() {
		writeIsActive = false;
		readIsActive = false;
		shutdown = true;
		notifyAll();
	}
	
	public synchronized Socket getSocketWrite() {
		return socketWrite;
	}

	public synchronized void setSocketWrite(Socket socketWrite) {
		this.socketWrite = socketWrite;
	}

	public synchronized Socket getSocketRead() {
		return socketRead;
	}

	public synchronized void setSocketRead(Socket socketRead) {
		this.socketRead = socketRead;
	}

	public synchronized boolean isShutdown() {
		return shutdown;
	}
	
	public synchronized boolean waitUntilIdleChanged() {
		while(prevIdle == idle) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return idle;
	}

	public synchronized Data popUnpackedImage() {
		while(unPackedImages.isEmpty()) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return unPackedImages.poll();
	}

	public synchronized void setIdle(boolean idle) {
		if(!forceMode) {
			prevIdle = this.idle;
			this.idle = idle;
		}
		notifyAll();
	}
	
	public synchronized void buttonIdleMovie(boolean mode, boolean idle) {
		prevIdle = this.idle;
		this.idle = idle;
		forceMode = mode;
		System.out.println("idle: " + idle + " forceMode: " + forceMode);
		notifyAll();
	}
	
	public synchronized void buttonAuto() {
		forceMode = false;
		notifyAll();
	}
	
	public synchronized void buttonConnection(boolean connect) {
		if (connect) {
			shutdown = false;
			System.out.println("connect");
		} else {
			shutdown();
			System.out.println("disconnect");
		}
		notifyAll();
	}
	
	public synchronized void waitUntilConnect() {
		while (shutdown) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
