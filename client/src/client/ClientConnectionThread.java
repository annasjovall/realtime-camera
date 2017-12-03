package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

// This thread has the double responsibility of connecting 
// and reading data from the socket
public class ClientConnectionThread extends Thread {

	private ClientMonitor clientMonitor;
	private String host;
	private int readPort;
	private int writePort;
	private boolean reconnect;
	
	public ClientConnectionThread(ClientMonitor mon, String host, int writePort, int readPort) {
		clientMonitor = mon;
		this.host = host;
		this.readPort = readPort;
		this.writePort = writePort;
		reconnect = false;
		System.out.println("Create ClientConnectionThread");
	}
	
	// Connect and reconnect if connection is dropped.
	public void run() {
		while(true) {
			clientMonitor.waitUntilConnect();
			while (!clientMonitor.isShutdown())
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
					
					// Inform clientMonitor there is a connection
					clientMonitor.setSocketRead(socketRead);
					clientMonitor.setSocketWrite(socketWrite);
					clientMonitor.setReadActive(true);
					clientMonitor.setWriteActive(true);
					
					clientMonitor.waitUntilNotActive();
					
				} catch (UnknownHostException e) {
					// Occurs if the socket cannot find the host
				} catch (IOException e) {
					// Something happened with the connection
					//
					// Example: the connection is closed on the server side, but
					// the client is still trying to write data.
					clientMonitor.setWriteActive(false);
					clientMonitor.setReadActive(false);
					System.out.println("No connection on client side");
				} catch (InterruptedException e) {
					// Occurs when interrupted
					clientMonitor.shutdown();
					break;
				}
				
				// Next connection is a reconnect attempt
				reconnect = true;
	
				// Close the socket before attempting reconnect
				try {
					Socket socketRead = clientMonitor.getSocketRead();
					Socket socketWrite = clientMonitor.getSocketWrite();
					if (socketRead != null) socketRead.close();
					if (socketWrite != null) socketWrite.close();
				} catch (IOException e) {
					// Occurs if there is an error in closing the socket.
				}
			}
		}
	}
}
