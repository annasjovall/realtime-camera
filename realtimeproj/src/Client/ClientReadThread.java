package Client;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

// This thread has the double responsibility of connecting 
// and reading data from the socket
public class ClientReadThread extends Thread {

	private ClientSharedData monitor;
	private byte[] buffer;
	private int photoIndex = 0;
	private File photo;
	private FileOutputStream fos;

	public ClientReadThread(ClientSharedData mon) {
		monitor = mon;
		buffer = new byte[70000];
	}


	// Receive packages of random size from active connections.
	public void run() {
		while (!monitor.isShutdown()) {
			try {
				// Wait for active connection
				monitor.waitUntilReadActive();
				InputStream is = monitor.getSocketRead().getInputStream();

				ByteArrayOutputStream bufferos = new ByteArrayOutputStream();				
				OutputStream os = monitor.getSocketRead().getOutputStream();
				sleep(100);
				//monitor.setWriteActive(false);
				// Receive data packages of different sizes
				while (true) {
					int nRead;
					while ((nRead = is.read(buffer, 0, buffer.length)) != -1) {
						  bufferos.write(buffer, 0, nRead);
					}
					
					buffer = bufferos.toByteArray();
					
					createJPEG(buffer);
					/*byte[] test = new byte[1];
					test[0] = 'c';
					os.write(test);
					// Flush data
					os.flush();*/
				}
			} catch (IOException e) {
				// Something happened with the connection
				//
				// Example: the connection is closed on the server side, but
				// the client is still trying to write data.
				monitor.setWriteActive(false);
				Utils.println("No connection on client side");
			} catch (InterruptedException e) {
				// Occurs when interrupted
				monitor.shutdown();
				break;
			}
		}

		// No resources to dispose since this is the responsibility
		// of the shutdown thread.
		Utils.println("Exiting ClientReadThread");
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

	public byte[] fixImage(byte[] image) {
		byte[] tempBuffer = image;
		int endIndex = image.length - 1;
		for (int i = 0; i < image.length; i++) {
			if (((image[i] & 0xFF) == 0xff) && ((image[i + 1] & 0xFF) == 0xd8) && ((image[i + 2] & 0xFF) == 0xff)) {
				tempBuffer = Arrays.copyOfRange(image, i, endIndex);
			}
		}
		return tempBuffer;
	}

	private byte[] cutBuffer() {
		byte[] tempBuffer = buffer;
		for (int i = buffer.length - 1; i > 0; i--) {
			if (buffer[i] != 0) {
				tempBuffer = Arrays.copyOfRange(buffer, 0, i + 1);
				break;
			}
		}
		return tempBuffer;
	}
}
