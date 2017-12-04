package client;

public class CamerasSharedData {
	public static final int IDLE_MODE = 0;
	public static final int MOVIE_MODE = 1;
	public static final int SYNC_MODE = 0;
	public static final int ASYNC_MODE = 1;

	private int mode;
	private int displayMode;
	private boolean forceMode = false;

	public CamerasSharedData() {
		mode = IDLE_MODE;
		displayMode = SYNC_MODE;
	}

	public synchronized int getMode() {
		return mode;
	}

	public synchronized int getDisplayMode() throws InterruptedException {
		return displayMode;
	}

	public synchronized boolean trySetDisplayMode(int displayMode) {
		if (!forceMode) {
			this.displayMode = displayMode;
			notifyAll();
			return true;
		}
		return false;
	}

	public synchronized boolean trySetMode(int mode) {
		if (!forceMode) {
			this.mode = mode;
			notifyAll();
			return true;
		}
		return false;
	}

	public synchronized void forceSetMode(int mode) {
		forceMode = true;
		this.mode = mode;
		notifyAll();
	}
	
	public synchronized void forceSetDisplayMode(int mode) {
		forceMode = true;
		this.displayMode = mode;
		notifyAll();
	}


	public void exitForceMode() {
		forceMode = false;
		notifyAll();
	}
}
