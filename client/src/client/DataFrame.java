package client;

public class DataFrame {
	private String timeStamp;
	private byte[] frames;

	public DataFrame(String timeStamp, byte[] frames) {
		this.frames = frames;
		this.timeStamp = timeStamp;
	}
	
	public byte[] getFrames() {
		return frames;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
}
