package Client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



// This thread has the double responsibility of connecting 
// and reading data from the socket
public class ClientReceiver extends Thread {
	private int photoIndex = 0;
	private File photo;
	private FileOutputStream fos;

	private ClientSharedData clientMonitor;
	
	public ClientReceiver(ClientSharedData mon) {
		clientMonitor = mon;
		System.out.println("Create ClientReceiver");
	}
	
	// Receive packages of random size from active connections.
	public void run() {
		while (!clientMonitor.isShutdown())
		{
			try {
				// Wait for active connection
				try {
					clientMonitor.waitUntilReadActive();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				InputStream is = clientMonitor.getSocketRead().getInputStream();
				OutputStream os = clientMonitor.getSocketRead().getOutputStream();
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();

				int nRead;
				byte[] data = new byte[50000];

				while ((nRead = is.read(data, 0, data.length)) != -1) {
				  buffer.write(data, 0, nRead);
				}
				System.out.println("HEj");
				byte[] test = new byte[1];
				test[0] = 'c';
				os.write(test);
				data = buffer.toByteArray();

				buffer.flush();
												
				int start = startOfJpg(data);
				int end = endOfJpg(data);
				data = resize(data, start, end);
				createJPEG(data);
				//createFile(ra, fileName, false);
				//clientMonitor.addToQueue("", ra, data.length);
		
				// System.out.println("SIZE: " + data.length);
				
				is.close();
			} catch (IOException e) {
				// Something happened with the connection
				//
				// Example: the connection is closed on the server side, but
				// the client is still trying to write data.
				clientMonitor.setReadActive(false);
				// System.out.println("No connection on client side");
			}
		}
		System.out.println("Exiting ClientReadThread");
	}
	
//	private byte[] removeHeader(byte[] array){
//		byte[] ra = new byte[array.length]; 
//		for(int i = 0; i < array.length - 64; i++){
//			ra[i] = array[i+64];
//		}
//		return ra;
//	}
	
	private int startOfJpg(byte[] array){
		int start = -1;
		for(int i = 0; i < array.length - 2; i++){
			if(((array[i] & 0xFF) ==  0xff) && ((array[i+1] & 0xFF) ==  0xd8) && ((array[i+2] & 0xFF) ==  0xff)){
				// System.out.println("JPG starts at " + i);
				return i;
			}
		}
		// System.out.println("JPG starts at " + start);
		return start;
	}
	
	private int endOfJpg(byte[] array){
		for(int i = 0; i < array.length - 1; i++){
			if(((array[i] & 0xFF) ==  0xff) && ((array[i+1] & 0xFF) ==  0xd9)){
				// System.out.println("JPG ends at " + (i + 1));
				return (i + 2);
			}
		}
		//System.out.println("JPG did not end in this package");
		return 0;
	}
	
	private byte[] resize(byte[] array, int start, int end){
		byte[] ra = new byte[end-start];
		
		for(int i = 0; i < end-start; i++){
			ra[i] = array[i+start];
		}
		return ra;
	}
	
	public void createJPEG(byte[] image) {
		photo = new File(photoIndex + "photo.jpeg");
		photoIndex++;
		try {
			fos = new FileOutputStream(photo.getPath());
			fos.write(image);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
