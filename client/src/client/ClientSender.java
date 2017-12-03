package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClientSender extends Thread {

	private ClientMonitor clientMonitor;
	private String buffer;

	public ClientSender(ClientMonitor monitor) {
		this.clientMonitor = monitor;
		buffer = "";
	}

	// Receive packages of random size from active connections.
	public void run() {
		try {
			clientMonitor.waitUntilWriteActive();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (!clientMonitor.isShutdown()) {
			try {
				Thread.sleep(1000);
				// Blocking wait for connection

				Socket socket = clientMonitor.getSocketWrite();
				OutputStream os = socket.getOutputStream();
				boolean idle = clientMonitor.getIdleMode();

				// Send data packages of different sizes

				int size = 100;
				// fillBuffer(idle);
				System.out.print("ClientWriteThread, size: " + size + " " + buffer);

				byte[] test2 = new byte[1];
				int idleByte = (idle) ? 0 : 1;
				test2[0] = (byte) idleByte;
				os.write(test2);

				// Flush data
				os.flush();

			} catch (IOException e) {
				// Something happened with the connection
				//
				// Occurs if there is an error trying to write data,
				// for instance that the connection suddenly closed.
				clientMonitor.setWriteActive(false);
				System.out.println("No connection on client side");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Exiting ClientWriteThread");
	}

	private void fillBuffer(Boolean mode) {
		buffer = mode.toString();

	}
}
