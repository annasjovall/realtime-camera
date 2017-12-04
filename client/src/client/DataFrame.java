package client;

public class DataFrame {
	private long timeStamp;
	private byte[] frames;

	public DataFrame(long timeStamp, byte[] frames) {
		this.frames = frames;
		this.timeStamp = timeStamp;
	}
	
	public byte[] getFrames() {
		return frames;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
}
