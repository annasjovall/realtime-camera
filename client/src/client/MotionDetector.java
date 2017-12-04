package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import gui.MainWindow;

public class MotionDetector extends Thread {
	private SharedData monitor;
	private SharedData otherMonitor;
	private final int PORT = 9091;
	private MainWindow window;
	private int cameraId;
	private int previousTimeStamp;

	public MotionDetector(SharedData monitor, SharedData otherMonitor, MainWindow window, int cameraId) {
		this.monitor = monitor;
		this.otherMonitor = otherMonitor;
		this.window = window;
		this.cameraId = cameraId;
	}

	public void run() {
		while (true) {
			try {
				monitor.waitUntilServerIsActive();
				int timeStamp = getTimeStamp(httpRequest("http://" + monitor.getHost() + ":" + PORT))[0];
				if (timeStamp - previousTimeStamp >= 5) {
					if (monitor.trySetMode(SharedData.IDLE_MODE)) {
						otherMonitor.forceSetMode(SharedData.IDLE_MODE);
					}
				} else if (timeStamp - previousTimeStamp < 5) {
					if (monitor.trySetMode(SharedData.MOVIE_MODE)) {
						otherMonitor.forceSetMode(SharedData.MOVIE_MODE);
						window.setCameraCausedMoveMode(cameraId);
					}
				}
				previousTimeStamp = timeStamp;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private String httpRequest(String target) throws Exception {
		StringBuffer response = new StringBuffer();

		URL url = new URL(target);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String inputLine = null;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}
	
	private int[] getTimeStamp(String httpResponse) {
		String[] httpResponseFields = httpResponse.split(":");
		int[] httpResponseFieldsInt = new int[3];
		for (int i = 0; i < 3; i++) {
			httpResponseFieldsInt[i] = Integer.parseInt(httpResponseFields[i]);
		}
		return httpResponseFieldsInt;
	}
}
