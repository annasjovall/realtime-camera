package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import gui.MainWindow;

public class WriterThread extends Thread {

	private SharedData clientMonitor;
	private MainWindow window;

	public WriterThread(SharedData monitor, MainWindow window) {
		this.clientMonitor = monitor;
		this.window = window;
	}

	public void run() {
		while (true) {
			try {
				clientMonitor.waitUntilServerIsActive();
				Socket socket = clientMonitor.getSocketWrite();
				OutputStream os = socket.getOutputStream();

				int mode = clientMonitor.getMode();
				window.statusRefresh(mode);
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
