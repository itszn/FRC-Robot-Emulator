package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PowerConnector{
	public Part owner;

	public int maxPowerChildren;
	boolean justChangedPower = false;
	boolean usePower = true;
	boolean autoPower = true;
	public int powered = 0;
	public Part powerParent = null;
	public ArrayList<Part> powerChildren = new ArrayList<Part>();
	boolean powerConnecting = false;
	boolean powerConnectionOption = false;
	JComboBox<String> powerChoice;
	public Point powerInPoint = new Point(0,0);
	public Point powerOutPoint = new Point(0,0);
	
	public PowerConnector(Part owner) {
		this.owner = owner;
		String[] types = {"Automatic","Manual"};
		powerChoice = new JComboBox<String>(types);
		powerChoice.setSelectedIndex(0);
		powerChoice.addActionListener(owner);
		powerChoice.setActionCommand("changePower");
	}
	
	public void update() {
		//System.out.println("update");
		powered = 0;
		if (powerParent != null) {
			if (!powerParent.exists)
				powerParent=null;
			else
				powered = ((IPowerConnector)powerParent).outPuttingPower(owner);
		}
		if (autoPower)
			powered = 1;
		ArrayList<Part> toRemove = new ArrayList<Part>();
		for (Part p: powerChildren) {
			if (!p.exists||((IPowerConnector)p).getPowerConnector().autoPower)
				toRemove.add(p);
		}
		for (Part p: toRemove) {
			powerChildren.remove(p);
		}
	}
	
	public void updateProperties() {
		if (usePower) {
			autoPower = powerChoice.getSelectedIndex()==0;
			if (autoPower) {
				powerParent = null;
			}
		}
	}
	
	public void getProperties(JPanel p) {
		if (usePower) {
			JPanel n = new JPanel();
				n.setLayout(new FlowLayout());
				n.add(new JLabel("Power"));
				if (!justChangedPower) {
					String[] types = {"Automatic","Manual"};
					powerChoice = new JComboBox<String>(types);
					powerChoice.setSelectedIndex(autoPower?0:1);
					if (!autoPower)
						powerConnectionOption = true;
					powerChoice.addActionListener(owner);
					powerChoice.setActionCommand("changePower");
				}
				n.add(powerChoice);
			p.add(n);
			if (powerConnectionOption) {
				n = new JPanel();
					JButton b;
					if (powerConnecting) {
						b = new JButton("Cancel Connect");
						b.setActionCommand("stopConnect");
						Window.setButtonTriggerKey(b, KeyEvent.VK_ESCAPE);
					}
					else {
						b = new JButton("Connect Power");
						b.setActionCommand("connectTo");
					}
					b.addActionListener(owner);
					n.add(b);
				p.add(n);
			}
		}
	}
	

	public void actionPerformed(ActionEvent evn) {
		if (evn.getActionCommand().equals("close")) {
			powerConnecting = false;
			justChangedPower = false;
			powerConnectionOption = false;
		}
		else if (evn.getActionCommand().equals("save")) {
			
			powerConnecting = false;
			justChangedPower = false;
			powerConnectionOption = false;
		}
		else if (evn.getActionCommand().equals("delete")) {
			powerConnecting = false;
			justChangedPower = false;
			powerConnectionOption = false;
		}
		else if (evn.getActionCommand().equals("changePower")) {
			Window.changes = true;
			justChangedPower = true;
			int i = powerChoice.getSelectedIndex();
			if (i==0) {
				powerConnectionOption = false;
			}
			if (i==1) {
				powerConnectionOption = true;
			}
			JPanel n = new JPanel();
			JPanel pa = new JPanel();
			owner.getProperties(pa);
			n.add(pa);
			RobotEmulator.window.showPref(n);
		}
		else if (evn.getActionCommand().equals("connectTo")) {
			RobotEmulator.window.partConnecting = owner;
			justChangedPower = true;
			powerConnecting = true;
			JPanel n = new JPanel();
			JPanel pa = new JPanel();
			owner.getProperties(pa);
			n.add(pa);
			RobotEmulator.window.showPref(n);
		}
		else if (evn.getActionCommand().equals("stopConnect")) {
			RobotEmulator.window.partConnecting=null;
			justChangedPower = true;
			powerConnecting = false;
			JPanel n = new JPanel();
			JPanel pa = new JPanel();
			owner.getProperties(pa);
			n.add(pa);
			RobotEmulator.window.showPref(n);
		}
	}
	
	
	public void setPowerConnectParent(Part p) {
		if (powerConnecting) {
			powerParent = p;
			justChangedPower = true;
			powerConnecting = false;
			JPanel n = new JPanel();
			JPanel pa = new JPanel();
			owner.getProperties(pa);
			n.add(pa);
			RobotEmulator.window.showPref(n);
		}
	}
	
	public void paint(Graphics g) {
		if (powerConnecting) {
			Point p = RobotEmulator.window.draw.getMousePosition();
			if (p!=null) {
				g.setColor(Color.red);
				g.drawLine(p.x,p.y, owner.x+powerInPoint.x, owner.y+powerInPoint.y);
				g.setColor(Color.black);
			}
		}
		else if (powerParent!=null) {
			g.setColor(Color.red);
			g.drawLine(powerParent.x+((IPowerConnector)powerParent).getPowerConnector().powerOutPoint.x, powerParent.y+((IPowerConnector)powerParent).getPowerConnector().powerOutPoint.y, owner.x+powerInPoint.x, owner.y+powerInPoint.y);
			g.setColor(Color.black);
		}
	}
}
