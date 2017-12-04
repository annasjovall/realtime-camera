package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import gui.MainWindow;

public class WriterThread extends Thread {

	private SharedData clientMonitor;
	private CamerasSharedData camerasMonitor;
	private MainWindow window;

	public WriterThread(SharedData monitor, MainWindow window, CamerasSharedData camMon) {
		this.clientMonitor = monitor;
		this.window = window;
		this.camerasMonitor = camMon;
	}

	public void run() {
		while (true) {
			try {
				clientMonitor.waitUntilServerIsActive();
				Socket socket = clientMonitor.getSocketWrite();
				OutputStream os = socket.getOutputStream();
				sleep(1000);
				int mode = camerasMonitor.getMode();
				window.modeStatusRefresh(mode);

				byte[] byteMode = new byte[1];
				byteMode[0] = (byte) mode;
				os.write(byteMode);
				os.flush();
			} catch (IOException e) {
				clientMonitor.disconnect();
			} catch (InterruptedException e) {
				clientMonitor.disconnect();
				e.printStackTrace();
			}
		}
	}
}
