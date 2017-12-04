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
				//only one connected
				if (!monitor1.isConnected() && !monitor2.isConnected()) {
					System.out.println("HEj0");
				} else if (monitor1.isConnected() && !monitor2.isConnected()) {
					System.out.println("HEj1");
					if(monitor1.hasImage())
						window.refreshCamera1(monitor1.popUnpackedImage());
				} else if (!monitor1.isConnected() && monitor2.isConnected()) {
					System.out.println("HEj2");
					if(monitor2.hasImage())
						window.refreshCamera2(monitor2.popUnpackedImage());
				//async mode
				} else if (camerasMonitor.getDisplayMode() == CamerasSharedData.ASYNC_MODE) {
					window.syncModeRefresh(CamerasSharedData.ASYNC_MODE);
					DataFrame dataFromMonitor1 = null;
					DataFrame dataFromMonitor2 = null;
					if (monitor1.hasImage()) {
						dataFromMonitor1 = monitor1.popUnpackedImage();
						window.refreshCamera1(dataFromMonitor1);
					}
					if (monitor2.hasImage()) {
						dataFromMonitor2 = monitor2.popUnpackedImage();
						window.refreshCamera2(dataFromMonitor2);
					}
					if (dataFromMonitor1 != null && dataFromMonitor2 != null) {
						if (Math.abs(dataFromMonitor1.getTimeStamp() - dataFromMonitor2.getTimeStamp()) < 20) {
							outOfSync = 0;
							camerasMonitor.trySetDisplayMode(CamerasSharedData.SYNC_MODE);
						}
					}
				//sync mode
				} else {
					window.syncModeRefresh(CamerasSharedData.SYNC_MODE);
					DataFrame dataFrame1 = monitor1.popUnpackedImage();
					DataFrame dataFrame2 = monitor2.popUnpackedImage();
					if (Math.abs(dataFrame1.getTimeStamp() - dataFrame2.getTimeStamp()) > 20) {
						if (outOfSync > 5) {
							if (!camerasMonitor.trySetDisplayMode(CamerasSharedData.ASYNC_MODE)) {
								window.refreshCamera1(dataFrame1);
								window.refreshCamera2(dataFrame2);
							}
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