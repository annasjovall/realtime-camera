package client;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MotionDetector extends Thread {
	private ClientMonitor clientMonitor;
	HttpURLConnection connection;
	int[] prev;
	String URL;
	int port;
	
	//constructor
	public MotionDetector(ClientMonitor m, String URL, int port) {
		this.clientMonitor = m;
		prev = new int[3];
		prev[0] = 0;
		this.URL = URL;
		this.port = port;
	}

	private String httpRequest(String target) throws Exception {
		StringBuffer response;

			URL url = new URL(target);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream()));
		
			response = new StringBuffer();
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				// System.out.println("I got an input!!");
				response.append(inputLine);
			}
			in.close();
	
		// System.out.println(response.toString());
		return response.toString();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				
				int[] timeStamp = stringToInt(httpRequest(URL + ":" + port));
				if(timeStamp[0] - prev[0] >= 5) {
					clientMonitor.setIdle(true);	//movie->idle
				}
				else if(timeStamp[0] - prev[0] < 5) {	//idle->movie
					clientMonitor.setIdle(false);
				}
				
				prev = timeStamp;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private int[] stringToInt(String s) {
		String[] one = s.split(":");
		int[] oneInt = new int[3];
		for (int i = 0; i < 3; i++) {
			oneInt[i] = Integer.parseInt(one[i]);
		}
		return oneInt;
	}
}
