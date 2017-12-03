package client;

import gui.MainWindow;

public class EventDispatcher extends Thread {
	//private GuiMonitor guiMonitor;
	private ClientMonitor clientMonitor;
	private MainWindow mw;

	public EventDispatcher(ClientMonitor clientMonitor, MainWindow mw) {
		//this.guiMonitor = guiMonitor;
		this.clientMonitor = clientMonitor;
		this.mw = mw;
	}

	public void run() {
		while (true) {
			ClientMonitor.Data addToGui = clientMonitor.popUnpackedImage();
			//mw.printToConsole("I got something");

			if(clientMonitor.getCameraId() == 1) {
				mw.refreshCamera1(addToGui.buffer, 0);
			} else {
				mw.refreshCamera2(addToGui.buffer, 0);
			}
		
		}
	}
}