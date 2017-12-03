package Client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

// This thread has the double responsibility of connecting 
// and reading data from the socket
public class ClientConnectionThread extends Thread {

	private ClientSharedData monitor;
	private String host;
	private int readPort;
	private int writePort;
	private boolean reconnect;
	
	public ClientConnectionThread(
			ClientSharedData mon, 
			String host, 
			int readPort,
			int writePort
		) {
		monitor = mon;
		this.host = host;
		this.writePort = writePort;
		this.readPort = readPort;
		reconnect = false;
	}
	
	// Connect and reconnect if connection is dropped.
	public void run() {
		while (!monitor.isShutdown())
		{
			try {
				// In case of a reconnect, wait some time to avoid busy wait
				if (reconnect) Thread.sleep(1000);
				
				// Establish connection
				Socket socketRead = new Socket(host, readPort);
				Socket socketWrite = new Socket(host, writePort);
				
				// Configure socket to immediately send data.
				// This is good for streaming.
				socketRead.setTcpNoDelay(true);
				socketWrite.setTcpNoDelay(true);
				
				// Inform monitor there is a connection
				monitor.setSocketRead(socketRead);
				monitor.setSocketWrite(socketWrite);
				monitor.setReadActive(true);
				monitor.setWriteActive(true);
				
				monitor.waitUntilNotActive();
				
			} catch (UnknownHostException e) {
				// Occurs if the socket cannot find the host
			} catch (IOException e) {
				// Something happened with the connection
				//
				// Example: the connection is closed on the server side, but
				// the client is still trying to write data.
				monitor.setWriteActive(false);
				monitor.setReadActive(false);
				Utils.println("No connection on client side");
			} catch (InterruptedException e) {
				// Occurs when interrupted
				monitor.shutdown();
				break;
			}
			
			// Next connection is a reconnect attempt
			reconnect = true;

			// Close the socket before attempting reconnect
			try {
				Socket socketRead = monitor.getSocketRead();
				Socket socketWrite = monitor.getSocketWrite();
				if (socketRead != null) socketRead.close();
				if (socketWrite != null) socketWrite.close();
			} catch (IOException e) {
				// Occurs if there is an error in closing the socket.
			}
		}
		
		// No resources to dispose since this is the responsibility
		// of the shutdown thread.
		Utils.println("Exiting ClientConnectionThread");
	}
}