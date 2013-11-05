package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MotorConnector {
	public Part owner;
	
	boolean justChangedMotor = false;
	boolean connected = false;
	public MotorPart motorParent = null;
	boolean motorConnecting = false;
	JLabel motorConnectionDisplay;
	public Point motorInPoint = new Point(0,0);
	
	public MotorConnector(Part owner) {
		this.owner = owner;
		motorConnectionDisplay = new JLabel("Not Connected");
	}
	
	public void update() {
		connected = false;
		if (motorParent != null) {
			if (!motorParent.exists)
				motorParent = null;
			else
				connected = true;
		}
	}
	
	public void updateProperties() {}
	
	public void getProperties(JPanel p) {
		JPanel n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("Motor"));
			motorConnectionDisplay = new JLabel((motorParent==null?"Not ":"")+"Connected");
			n.add(motorConnectionDisplay);
		p.add(n);
		n = new JPanel();
			JButton b;
			if (motorConnecting) {
				b = new JButton("Cancel Connect");
				b.setActionCommand("stopConnect");
				Window.setButtonTriggerKey(b, KeyEvent.VK_ESCAPE);
			}
			else {
				b = new JButton("Connect Motor");
				b.setActionCommand("connectTo");
			}
			b.addActionListener(owner);
			n.add(b);
		p.add(n);
	}
	
	public void actionPerformed(ActionEvent evn) {
		if (evn.getActionCommand().equals("close")) {
			motorConnecting = false;
		}
		else if (evn.getActionCommand().equals("save")) {
			motorConnecting = false;
		}
		else if (evn.getActionCommand().equals("delete")) {
			motorConnecting = false;
		}
		else if (evn.getActionCommand().equals("connectTo")) {
			RobotEmulator.window.partConnecting = owner;
			motorConnecting = true;
			JPanel n = new JPanel();
			JPanel pa = new JPanel();
			owner.getProperties(pa);
			n.add(pa);
			RobotEmulator.window.showPref(n);
		}
		else if(evn.getActionCommand().equals("stopConnect")) {
			RobotEmulator.window.partConnecting = null;
			motorConnecting = false;
			JPanel n = new JPanel();
			JPanel pa = new JPanel();
			owner.getProperties(pa);
			n.add(pa);
			RobotEmulator.window.showPref(n);
		}
	}
	
	public void setMotorConnectParent(MotorPart p) {
		if (motorConnecting) {
			motorParent = p;
			motorConnecting = false;
			JPanel n = new JPanel();
			JPanel pa = new JPanel();
			owner.getProperties(pa);
			n.add(pa);
			RobotEmulator.window.showPref(n);
		}
	}
	
	public void paint(Graphics g) {
		if (motorConnecting) {
			Point p = RobotEmulator.window.draw.getMousePosition();
			if (p!=null) {
				g.setColor(Color.red);
				g.drawLine(p.x,p.y, owner.x+motorInPoint.x, owner.y+motorInPoint.y);
				g.setColor(Color.black);
			}
		}
		else if (motorParent!=null) {
			g.setColor(Color.red);
			g.drawLine(motorParent.x+75, motorParent.y+25, owner.x+motorInPoint.x, owner.y+motorInPoint.y);
			g.setColor(Color.black);
		}
	}
}
