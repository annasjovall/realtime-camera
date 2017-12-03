package Client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class ClientWriteThread extends Thread {

	private ClientSharedData monitor;
	private byte[] buffer;
	
	public ClientWriteThread(ClientSharedData mon) {
		monitor = mon;
		buffer = new byte[8192];
	}
	
	// Receive packages of random size from active connections.
	public void run() {
		while (!monitor.isShutdown())
		{
			try {
				// Blocking wait for connection
				monitor.waitUntilWriteActive();
				Socket socket = monitor.getSocketWrite();
				OutputStream os = socket.getOutputStream();
				// Send data packages of different sizes
				while (true) {

					byte[] test = new byte[1];
					test[0] = 'c';
					os.write(test);
					// Flush data
					os.flush();

					// "Fake" work done before sending next package
					Thread.sleep(100);
				}
			} catch (IOException e) {
				// Something happened with the connection
				//
				// Occurs if there is an error trying to write data,
				// for instance that the connection suddenly closed.
				monitor.setWriteActive(false);
				Utils.println("No connection on client side");
			} catch (InterruptedException e) {
				// Occurs when interrupted
				monitor.shutdown();
				break;
			}
		}
		
		Utils.println("Exiting ClientWriteThread");
	}
}
