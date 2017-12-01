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
	private int photoIndex = 0;
	private File photo;
	private FileOutputStream fos;

	public ClientReadThread(ClientSharedData mon) {
		monitor = mon;
	}

	// Receive packages of random size from active connections.
	public void run() {
		while (!monitor.isShutdown()) {
			try {
				// Wait for active connection
				monitor.waitUntilReadActive();
				InputStream is = monitor.getSocketRead().getInputStream();
				sleep(100);
				// monitor.setWriteActive(false);
				while (true) {
					byte[] headerBuffer = new byte[4];
					ByteBuffer jpegSizeByteBuffer = ByteBuffer.wrap(headerBuffer);
					int jpegSize = jpegSizeByteBuffer.getInt();
					System.out.println(jpegSize);
					byte[] imageBuffer = new byte[jpegSize];
					getJPEGSize(is, imageBuffer);
					//createJPEG(imageBuffer);
				}
			} catch (IOException e) {
				monitor.setWriteActive(false);
				Utils.println("No connection on client side");
			} catch (InterruptedException e) {
				monitor.shutdown();
				break;
			}
		}
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

	/*
	 * Retrieves the size of the JPEG file and removes header from input stream
	 * OBS doesn't remove timestamp when added
	 * 
	 * @return int representing how many bytes that were read, -1 if it failed
	 */
	private int getJPEGSize(InputStream is, byte[] headerBuffer) throws IOException {
		return is.read(headerBuffer, 0, 4);
	}

	/*
	 * Reads bytes from inputstream and places them in buffer
	 * 
	 * @return int representing how many bytes that were read, -1 if it failed
	 */
	private int getJPEG(InputStream is, byte[] imageBuffer) throws IOException {
		// The offset is set to 0 since the header is read before
		// TODO: Double check that the file is a JPEG file
		return is.read(imageBuffer, 0, imageBuffer.length);
	}

	/*
	 * private byte[] fixImage(byte[] image) { byte[] tempBuffer = image; int
	 * endIndex = image.length - 1; for (int i = 0; i < image.length; i++) { if
	 * (((image[i] & 0xFF) == 0xff) && ((image[i + 1] & 0xFF) == 0xd8) &&
	 * ((image[i + 2] & 0xFF) == 0xff)) { tempBuffer = Arrays.copyOfRange(image,
	 * i, endIndex); } } return tempBuffer; }
	 */
}
