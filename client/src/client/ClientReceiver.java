package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;		
import java.io.File;		
import java.io.FileNotFoundException;		
import java.io.FileOutputStream;



// This thread has the double responsibility of connecting 
// and reading data from the socket
public class ClientReceiver extends Thread {

	private ClientMonitor clientMonitor;
	private InputStream is;
	
	public ClientReceiver(ClientMonitor mon) {
		clientMonitor = mon;
		System.out.println("Create ClientReceiver");
	}
	
	// Receive packages of random size from active connections.
	public void run() {
		while(true) {
			clientMonitor.waitUntilConnect();
			while (!clientMonitor.isShutdown())
			{
				try {
					// Wait for active connection
					clientMonitor.waitUntilReadActive();
					
					is = clientMonitor.getSocketRead().getInputStream();
					DataInputStream in = new DataInputStream(is);
					
					int len = in.readInt();
					int time_stamp = in.readInt(); // this should be used
					//System.out.println("The length is: " + len + " Timestamp: " + time_stamp);
					byte[] data = new byte[len];
					
					for(int i = 0; i < len; i++){
						data[i] = in.readByte();
					}
					
					
					//String fileName = "fun.jpg";
					//createFile(data, fileName, false);

					clientMonitor.addToQueue("", data, data.length);
				} catch (IOException e) {
					// Something happened with the connection
					//
					// Example: the connection is closed on the server side, but
					// the client is still trying to write data.
					clientMonitor.setReadActive(false);
					// System.out.println("No connection on client side");
				} catch (InterruptedException e) {
					clientMonitor.shutdown();
					e.printStackTrace();
				}
			}
			System.out.println("Exiting ClientReadThread");
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void createFile(byte[] data, String fileName, boolean append) throws IOException {		
		try {				
			File asd = new File(fileName);
			OutputStream out = new FileOutputStream(asd, append);		
			out.write(data);		
			out.flush();		
			out.close();		
		} catch (FileNotFoundException e) {		
			// TODO Auto-generated catch block		
			e.printStackTrace();
		}
	}
}
