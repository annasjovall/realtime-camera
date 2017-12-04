package client;

import gui.MainWindow;

public class UpdateGUIThread extends Thread {
	private SharedData monitor1;
	private SharedData monitor2;
	private CamerasSharedData camerasMonitor;
	private MainWindow window;
	private int outOfSync = 0;

	public UpdateGUIThread(SharedData monitor1, SharedData monitor2, MainWindow window,
			CamerasSharedData camerasMonitor) {
		this.monitor1 = monitor1;
		this.monitor2 = monitor2;
		this.window = window;
		this.camerasMonitor = camerasMonitor;
	}

	public void run() {
		while (true) {
			try {
				if (monitor1.isConnected() && !monitor2.isConnected()) {
					window.refreshCamera1(monitor1.popUnpackedImage());
				} else if (!monitor1.isConnected() && monitor2.isConnected()) {
					window.refreshCamera2(monitor2.popUnpackedImage());
				} else if (camerasMonitor.getDisplayMode() == CamerasSharedData.ASYNC_MODE) {
					window.syncModeRefresh(CamerasSharedData.ASYNC_MODE);
					if (monitor1.hasImage()) {
						window.refreshCamera1(monitor1.popUnpackedImage());
					}
					if (monitor2.hasImage()) {
						window.refreshCamera2(monitor2.popUnpackedImage());
					}
				} else {
					window.syncModeRefresh(CamerasSharedData.SYNC_MODE);
					DataFrame dataFrame1 = monitor1.popUnpackedImage();
					DataFrame dataFrame2 = monitor2.popUnpackedImage();
					if (Math.abs(dataFrame1.getTimeStamp() - dataFrame2.getTimeStamp()) > 20) {
						if (outOfSync > 5) {
							camerasMonitor.setDisplayMode(CamerasSharedData.ASYNC_MODE);
						} else {
							window.refreshCamera1(dataFrame1);
							window.refreshCamera2(dataFrame2);
							outOfSync++;
						}
					} else {
						window.refreshCamera1(dataFrame1);
						window.refreshCamera2(dataFrame2);
						outOfSync = 0;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}