package Client;
import java.net.Socket;

public class ClientSharedData {
	private volatile Socket socketRead;
	private volatile Socket socketWrite;
	private volatile boolean readIsActive;
	private volatile boolean writeIsActive;
	private volatile boolean shutdown;

	
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
	
	public synchronized boolean isShutdown() {
		return shutdown;
	}
	
	public synchronized void waitUntilShutdown() throws InterruptedException {
		while (!shutdown) wait();
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
}
