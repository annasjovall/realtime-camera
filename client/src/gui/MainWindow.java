package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.*;

import client.ClientMonitor;

public class MainWindow {
	ClientMonitor cm1;
	ClientMonitor cm2;
	JLabel l;
	JLabel l2;
	JLabel motionText;
	JLabel connectionText;
	JButton idleB;
	JButton movieB;
	JButton autoB;
	JButton syncB;
	JButton connectB1;
	JButton connectB2;
	JTextArea console;
	
	public MainWindow(ClientMonitor cm1, ClientMonitor cm2) {
		this.cm1 = cm1;
		this.cm2 = cm2;

		//Setup: add frame, panels, text
		JFrame f = new JFrame();
		JPanel p = new JPanel();
		JPanel p2 = new JPanel();
		JPanel textPanel = new JPanel();
		
		JTextField tf1 = new JTextField("Host");
		JTextField port1 = new JTextField("Read port");
		JTextField port2 = new JTextField("Write port");

		JTextField tf2 = new JTextField("Host");
		JTextField port3 = new JTextField("Read port");
		JTextField port4 = new JTextField("Write port");
		
		connectB1 = new JButton("Connect");
		connectB2 = new JButton("Connect");
		JPanel leftPanel = new JPanel(new GridLayout(1,3));
		JPanel rightPanel = new JPanel(new GridLayout(1,3));
		JPanel southPanel = new JPanel(new BorderLayout());
		JLabel leftSyncMsg = new JLabel("CM1 Sync Error: ");
		JLabel rightSyncMsg = new JLabel("CM2 Sync Error: ");
		JLabel stupidLabel = new JLabel(" | ");
		console = new JTextArea("");
		JScrollPane scrollis = new JScrollPane(console);
		
		leftPanel.add(tf1);
		leftPanel.add(port1);
		leftPanel.add(port2);
		leftPanel.add(connectB1);
		rightPanel.add(tf2);
		rightPanel.add(port3);
		rightPanel.add(port4);
		rightPanel.add(connectB2);
		
		console.setEditable(false);
		console.setMaximumSize(new Dimension(150, 150));
		console.setMinimumSize(new Dimension(100, 100));
		
		l = new JLabel();
		l2 = new JLabel();
		JPanel buttonPanel = new JPanel();
		motionText = new JLabel();
		connectionText = new JLabel();
		motionText.setText("Motion server disconnected...");
		connectionText.setText("Capture server not connected...");
		f.setLayout(new BorderLayout());
		f.setTitle("Camera surveillance system");
		f.setSize(new Dimension(1800, 700)); // default size is 0,0
		f.setLocation(10, 200); // default is 0,0 (top left corner)
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//add Buttons
		idleB = new JButton("Idle");
		movieB = new JButton("Movie");
		autoB = new JButton("Auto");
		syncB = new JButton("Sync");

		//Add components: images to labels
		ImageIcon icon = new ImageIcon();
		ImageIcon icon2 = new ImageIcon();
		l.setIcon(icon);
		l2.setIcon(icon2);

		//Add components: Buttons to buttonpanel
		buttonPanel.add(idleB);
		buttonPanel.add(movieB);
		buttonPanel.add(autoB);
		buttonPanel.add(syncB);
		//Add eventlisteners to buttons
		listeners();
		
		p.setLayout(new BorderLayout());
		p2.setLayout(new BorderLayout());
		//Add components: items to panels
		textPanel.add(connectionText,BorderLayout.WEST);
		textPanel.add(stupidLabel, BorderLayout.CENTER);
		textPanel.add(motionText, BorderLayout.EAST);
		
		
		
		p.add(l,BorderLayout.CENTER);
		p.add(leftPanel,BorderLayout.NORTH);
		p.add(leftSyncMsg, BorderLayout.SOUTH);
		p2.add(l2,BorderLayout.CENTER);
		p2.add(rightPanel, BorderLayout.NORTH);
		p2.add(rightSyncMsg, BorderLayout.SOUTH);
		
		southPanel.add(buttonPanel,BorderLayout.NORTH);
		southPanel.add(scrollis,BorderLayout.CENTER);
		
		//Add components: panels to frame
		f.add(textPanel, BorderLayout.NORTH);
		f.add(p, BorderLayout.WEST);
		f.add(p2, BorderLayout.EAST);
		f.add(southPanel, BorderLayout.PAGE_END);
		//f.pack();
		f.setVisible(true);
	}

	public void refreshCamera1(byte[] img, int camera) {
		if (img == null)
			return;
		ImageIcon icon = new ImageIcon(img);
		l.setIcon(icon);
		
	}
	
	public void refreshCamera2(byte[] img, int camera){
		if (img == null)
			return;
		ImageIcon icon = new ImageIcon(img);
		l2.setIcon(icon);
	}

	public void statusRefresh(boolean idle) {
		if (idle) {
			motionText.setText("Idle mode");
		} else {
			motionText.setText("Movie mode");
		}
	}
//	
//	public void printToConsole(String text){
//		console.setText(console.getText() + text + "\n");
//	}
//	
//	public void setActiveCapture(boolean active){
//		if(active){
//			connectionText.setText("Connected");
//		} else {
//			connectionText.setText("Capture server not connected");
//		}
//	}

	private void listeners() {
		idleB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//cm1.forceSetMode(true);
				//cm2.forceSetMode(true);
			}
		});
		
		movieB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//cm1.forceSetMode(false);
				//cm2.forceSetMode(false);
			}
		});

		autoB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//cm1.closeForceMode();
				//cm2.closeForceMode();
			}
		});

		syncB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Hej Synkat");
			}
		});
		

		connectB1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = connectB1.getText();
				if (text == "Connect") {
					cm1.buttonConnection(true);
					connectB1.setText("Disconnect");
				} else {
					cm1.buttonConnection(false);
					connectB1.setText("Connect");
				}
				
			}
		});
		
		connectB2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = connectB2.getText();
				if (text == "Connect") {
					cm2.buttonConnection(true);
					connectB2.setText("Disconnect");
				} else {
					cm2.buttonConnection(false);
					connectB2.setText("Connect");
				}
				
			}
		});

	}
}
