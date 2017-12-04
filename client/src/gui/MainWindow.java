package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import client.CamerasSharedData;
import client.DataFrame;
import client.SharedData;

public class MainWindow {
	SharedData monitor1;
	SharedData monitor2;
	CamerasSharedData cameraMonitor;
	JLabel frame1;
	JLabel frame2;
	JLabel modeDisplay;
	JLabel syncModeDisplay;
	JButton idleB;
	JButton movieB;
	JButton autoB;
	JButton syncB;
	JButton asyncB;
	JButton connectButton1;
	JButton connectButton2;
	JTextArea console;
	JTextField serverHost1;
	JTextField serverHost2;
	JTextField serverWritePort1;
	JTextField serverWritePort2;
	JTextField serverReadPort1;
	JTextField serverReadPort2;
	JLabel currentDelay1;
	JLabel currentDelay2;
	JLabel causedMoveMode1;
	JLabel causedMoveMode2;

	public MainWindow(SharedData monitor1, SharedData monitor2, CamerasSharedData cameraMonitor) {
		this.monitor1 = monitor1;
		this.monitor2 = monitor2;
		this.cameraMonitor = cameraMonitor;

		// Setup: add frame, panels, text
		JFrame window = new JFrame();
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel innerPanel1 = new JPanel();
		JPanel innerPanel2 = new JPanel();
		JPanel textPanel = new JPanel();

		serverHost1 = new JTextField("argus-5.student.lth.se");
		serverWritePort1 = new JTextField("19999");
		serverReadPort1 = new JTextField("22000");

		serverHost2 = new JTextField("argus-7.student.lth.se");
		serverWritePort2 = new JTextField("19999");
		serverReadPort2 = new JTextField("22000");

		connectButton1 = new JButton("Connect");
		connectButton2 = new JButton("Connect");
		JPanel leftPanel = new JPanel(new GridLayout(1, 3));
		JPanel rightPanel = new JPanel(new GridLayout(1, 3));
		JPanel southPanel = new JPanel(new BorderLayout());
		currentDelay1 = new JLabel();
		currentDelay2 = new JLabel();
		JLabel divisor = new JLabel(" | ");
		console = new JTextArea("");
		JScrollPane scrollis = new JScrollPane(console);

		leftPanel.add(serverHost1);
		leftPanel.add(serverWritePort1);
		leftPanel.add(serverReadPort1);
		leftPanel.add(connectButton1);
		rightPanel.add(serverHost2);
		rightPanel.add(serverWritePort2);
		rightPanel.add(serverReadPort2);
		rightPanel.add(connectButton2);

		console.setEditable(false);
		console.setMaximumSize(new Dimension(150, 150));
		console.setMinimumSize(new Dimension(100, 100));

		frame1 = new JLabel();
		frame2 = new JLabel();
		causedMoveMode1 = new JLabel();
		causedMoveMode2 = new JLabel();
		JPanel buttonPanel = new JPanel();
		modeDisplay = new JLabel();
		syncModeDisplay = new JLabel();
		window.setLayout(new BorderLayout());
		window.setTitle("Camera surveillance system");
		window.setSize(new Dimension(1300, 700)); // default size is 0,0
		window.setLocation(10, 200); // default is 0,0 (top left corner)
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// add Buttons
		idleB = new JButton("Idle");
		movieB = new JButton("Movie");
		autoB = new JButton("Auto");
		syncB = new JButton("Sync");
		asyncB = new JButton("Async");

		// Add components: images to labels
		ImageIcon icon = new ImageIcon();
		ImageIcon icon2 = new ImageIcon();
		frame1.setIcon(icon);
		frame2.setIcon(icon2);

		// Add components: Buttons to buttonpanel
		buttonPanel.add(idleB);
		buttonPanel.add(movieB);
		buttonPanel.add(autoB);
		buttonPanel.add(syncB);
		buttonPanel.add(asyncB);
		// Add eventlisteners to buttons
		listeners();

		panel1.setLayout(new BorderLayout());
		panel2.setLayout(new BorderLayout());
		// Add components: items to panels
		textPanel.add(modeDisplay, BorderLayout.WEST);
		textPanel.add(divisor, BorderLayout.CENTER);
		textPanel.add(syncModeDisplay, BorderLayout.EAST);

		panel1.add(frame1, BorderLayout.CENTER);
		panel1.add(leftPanel, BorderLayout.NORTH);
		innerPanel1.add(currentDelay1, BorderLayout.SOUTH);
		innerPanel1.add(causedMoveMode1, BorderLayout.NORTH);
		panel2.add(frame2, BorderLayout.CENTER);
		panel2.add(rightPanel, BorderLayout.NORTH);
		innerPanel2.add(currentDelay2, BorderLayout.SOUTH);
		innerPanel2.add(causedMoveMode2, BorderLayout.NORTH);

		panel1.add(innerPanel1, BorderLayout.SOUTH);
		panel2.add(innerPanel2, BorderLayout.SOUTH);

		southPanel.add(buttonPanel, BorderLayout.NORTH);
		southPanel.add(scrollis, BorderLayout.CENTER);

		// Add components: panels to frame
		window.add(textPanel, BorderLayout.NORTH);
		window.add(panel1, BorderLayout.WEST);
		window.add(panel2, BorderLayout.EAST);
		window.add(southPanel, BorderLayout.PAGE_END);
		// f.pack();
		window.setVisible(true);
	}
	
	public void setCameraCausedMoveMode(int id) {
		if (id == 1 && causedMoveMode1.getText() == null)
			causedMoveMode1.setText("CAUSED MOVIE MODE");
		else if (id == 2 && causedMoveMode2.getText() == null)
			causedMoveMode2.setText("CAUSED MOVIE MODE");
	}

	public void refreshCamera1(DataFrame dataFrame) {
		byte[] image = dataFrame.getFrames();
		String timeStamp = parseTimeStamp(dataFrame.getTimeStamp());
		if (image != null && timeStamp != null) {
			ImageIcon icon = new ImageIcon(image);
			frame1.setIcon(icon);
			currentDelay1.setText("CURRENT DELAY: " + timeStamp + " ms");
		}
	}

	public void refreshCamera2(DataFrame dataFrame) {
		byte[] image = dataFrame.getFrames();
		String timeStamp = parseTimeStamp(dataFrame.getTimeStamp());
		if (image != null && timeStamp != null) {
			ImageIcon icon = new ImageIcon(image);
			frame2.setIcon(icon);
			currentDelay2.setText("CURRENT DELAY: " + timeStamp + " ms");
		}
	}

	public void modeStatusRefresh(int mode) {
		if (mode == CamerasSharedData.IDLE_MODE) {
			modeDisplay.setText("Idle mode");
			causedMoveMode1.setText("");
			causedMoveMode1.setText("");
		} else {
			modeDisplay.setText("Movie mode");
		}
	}
	
	public void syncModeRefresh(int syncMode) {
		if (syncMode == CamerasSharedData.SYNC_MODE) {
			syncModeDisplay.setText("Syncronous");
		} else 
			syncModeDisplay.setText("Asyncronous");
	}
	
	public void setErrorMessage(String error, int cameraID) {
		console.setText(error);
	}

	private String parseTimeStamp(long cameraTime) {
		long currentTime = System.currentTimeMillis();
		long delay = currentTime - cameraTime;
		return delay + "";
	}

	private void listeners() {
		idleB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cameraMonitor.forceSetMode(CamerasSharedData.IDLE_MODE);
			}
		});

		movieB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cameraMonitor.forceSetMode(CamerasSharedData.MOVIE_MODE);
			}
		});

		autoB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cameraMonitor.exitForceMode();
			}
		});

		syncB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cameraMonitor.forceSetDisplayMode(CamerasSharedData.SYNC_MODE);
			}
		});
		asyncB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cameraMonitor.forceSetDisplayMode(CamerasSharedData.ASYNC_MODE);
			}
		});

		connectButton1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String buttonLabel = connectButton1.getText();
				// TODO: Kolla så att vi bara skickar in int
				String host = serverHost1.getText();
				String serverReadPort = serverReadPort1.getText();
				String serverWritePort = serverWritePort1.getText();
				if (buttonLabel == "Connect") {
					monitor1.connect(host, serverReadPort, serverWritePort);
					connectButton1.setText("Disconnect");
				} else {
					monitor1.disconnect();
					connectButton1.setText("Connect");
					frame1.setIcon(null);
				}

			}
		});

		connectButton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = connectButton2.getText();
				// TODO: Kolla så att vi bara skickar in int
				String host = serverHost2.getText();
				String serverReadPort = serverReadPort2.getText();
				String serverWritePort = serverWritePort2.getText();
				if (text == "Connect") {
					monitor2.connect(host, serverReadPort, serverWritePort);
					connectButton2.setText("Disconnect");
				} else {
					monitor2.disconnect();
					connectButton2.setText("Connect");
					frame2.setIcon(null);
				}

			}
		});
	}
}
