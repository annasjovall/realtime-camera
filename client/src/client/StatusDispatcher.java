package client;

import gui.MainWindow;

public class StatusDispatcher extends Thread{

	private ClientMonitor clientMonitor;
	private MainWindow mw;

	public StatusDispatcher(ClientMonitor clientMonitor, MainWindow mw) {
		//this.guiMonitor = guiMonitor;
		this.clientMonitor = clientMonitor;
		this.mw = mw;
	}

	public void run() {
		while (true) {
			boolean idle = clientMonitor.getIdleMode();
			// mw.printToConsole("Switched modes");
			mw.statusRefresh(idle);
		}
	}
}
