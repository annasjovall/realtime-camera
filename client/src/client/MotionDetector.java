package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import gui.MainWindow;

public class MotionDetector extends Thread {
	private SharedData monitor;
	private CamerasSharedData cameraMonitor;
	private MainWindow window;
	private int clientID;
	
	private final int PORT = 9094;
	
	public MotionDetector(SharedData monitor, MainWindow window, int clientID, CamerasSharedData cameraMonitor) {
		this.monitor = monitor;	
		this.cameraMonitor = cameraMonitor;
		this.window = window;
		this.clientID = clientID;
	}

	public void run() {
		while (true) {
			try {
				sleep(1000);
				monitor.waitUntilServerIsActive();
				URL url = new URL("http://" + monitor.getHost() + ":" + PORT);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String input = bufferedReader.readLine();
				String inputFirstColumn = input.split(":")[0];
				long timeStamp = Long.parseLong(inputFirstColumn);
				if((System.currentTimeMillis() - timeStamp*1000) < 1000){
					if(cameraMonitor.trySetMode(CamerasSharedData.MOVIE_MODE)){
						window.setCameraCausedMoveMode(clientID);
					}
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
