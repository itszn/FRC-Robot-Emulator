package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class Part implements ActionListener, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4753975558253506016L;
	public UUID uuid = null; 
	public UUID tempParentUUID = null;
	static String name = "Part";
	boolean connecting = false;
	boolean connectionOption = false;
	public int maxChildren = 0;
	public Part parent = null;
	public int powered = 0;
	public ArrayList<Part> children = new ArrayList<Part>();
	boolean autoPower = true;
	boolean usePower = true;
	boolean justChangedPower = false;
	public boolean exists = true;
	public JComponent[] props;
	public Rectangle bound;
	JComboBox<String> powerChoice;
	protected int x=0, y=0, width=100, height=100;
	public Part(int x, int y, int width, int height) {
		this.uuid = UUID.randomUUID();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		bound = new Rectangle(x,y,width,height);
		if (!RobotEmulator.parts.contains(this)) {
			RobotEmulator.parts.add(this);
		}
		String[] types = {"Automatic","Manual"};
		powerChoice = new JComboBox<String>(types);
		powerChoice.setSelectedIndex(0);
		powerChoice.addActionListener(this);
		powerChoice.setActionCommand("changePower");
	}
	
	public void update() {
		powered = 0;
		if (parent != null) {
			if (!parent.exists)
				parent=null;
			else 
				powered = parent.outPuttingPower(this);
		}
		if (autoPower)
			powered = 1;
		ArrayList<Part> toRemove = new ArrayList<Part>();
		for (Part p: children) {
			if (!p.exists)
				toRemove.add(p);
		}
		for (Part p: toRemove) {
			children.remove(p);
		}
	}
	public abstract void paint(Graphics g);
	public void updateProperties() {
		if (usePower) {
			autoPower = powerChoice.getSelectedIndex()==0;
		}
	}
	public void getProperties(JPanel p) {
		BoxLayout layout = new BoxLayout(p, BoxLayout.Y_AXIS);
		p.setLayout(layout);
		if (usePower) {

			JPanel n = new JPanel();
				n.setLayout(new FlowLayout());
				n.add(new JLabel("Power"));
				if (!justChangedPower) {
					//powerChoice.setSelectedIndex(autoPower?0:1);
					String[] types = {"Automatic","Manual"};
					powerChoice = new JComboBox<String>(types);
					powerChoice.setSelectedIndex(autoPower?0:1);
					if (!autoPower)
						connectionOption = true;
					powerChoice.addActionListener(this);
					powerChoice.setActionCommand("changePower");
				}
				n.add(powerChoice);
			p.add(n);
			if (connectionOption) {
				n = new JPanel();
					
					JButton b;
					if (connecting) {
						b = new JButton("Cancel Connect");
						b.setActionCommand("stopConnect");
						Window.setButtonTriggerKey(b, KeyEvent.VK_ESCAPE);
					}
					else  {
						b = new JButton("Connect Power");
						b.setActionCommand("connectTo");
					}
					b.addActionListener(this);
					n.add(b);
				p.add(n);
			}
		}
		JPanel n = new JPanel();
			JButton b = new JButton("Delete Part");
			b.addActionListener(this);
			b.setActionCommand("delete");
			Window.setButtonTriggerKey(b, KeyEvent.VK_DELETE);
			n.add(b);
		p.add(n);
		n = new JPanel();
			n.setLayout(new FlowLayout());
			b = new JButton("Close");
			b.addActionListener(this);
			if (!connecting)
				Window.setButtonTriggerKey(b, KeyEvent.VK_ESCAPE);
			b.setActionCommand("close");
			n.add(b);
			
			b = new JButton("Save");
			b.addActionListener(this);
			b.setActionCommand("save");
			Window.setButtonTriggerKey(b, KeyEvent.VK_ENTER);
			n.add(b);
		p.add(n);
	}
	
	public void remove() {
		RobotEmulator.parts.remove(this);
		if (Window.selectedPart.equals(this))
			Window.selectedPart=null;
		exists = false;
	}
	
	@Override
	public void actionPerformed(ActionEvent evn) {
		if (evn.getActionCommand().equals("close")) {
			RobotEmulator.window.closePref();
			connecting = false;
			justChangedPower = false;
			connectionOption = false;
		}
		else if (evn.getActionCommand().equals("save")) {
			RobotEmulator.window.closePref();
			this.updateProperties();
			connecting = false;
			justChangedPower = false;
			connectionOption = false;
		}
		else if (evn.getActionCommand().equals("delete")) {
			RobotEmulator.window.closePref();
			this.remove();
			connecting = false;
			justChangedPower = false;
			connectionOption = false;
		}
		else if (evn.getActionCommand().equals("changePower")) {
			justChangedPower = true;
			int i = powerChoice.getSelectedIndex();
			if (i==0) {
				connectionOption = false;
			}
			if (i==1) {
				connectionOption = true;
			}
			JPanel n = new JPanel();
			JPanel pa = new JPanel();
			getProperties(pa);
			n.add(pa);
			RobotEmulator.window.showPref(n);
		}
		else if (evn.getActionCommand().equals("connectTo")) {
			RobotEmulator.window.partConnecting = this;
			justChangedPower = true;
			connecting = true;
			JPanel n = new JPanel();
			JPanel pa = new JPanel();
			getProperties(pa);
			n.add(pa);
			RobotEmulator.window.showPref(n);
		}
		else if (evn.getActionCommand().equals("stopConnect")) {
			RobotEmulator.window.partConnecting=null;
			justChangedPower = true;
			connecting = false;
			JPanel n = new JPanel();
			JPanel pa = new JPanel();
			getProperties(pa);
			n.add(pa);
			RobotEmulator.window.showPref(n);
		}
	}
	
	public int getX(){return x;}
	public int getY(){return y;}
	public int getHeight(){return height;}
	public int getWidth(){return width;}
	
	public void drawSelected(Graphics g) {
		g.setColor(Color.GREEN);
		g.drawRect(x, y, width, height);
	}
	
	public void setX(int x){
		this.x = x;
		bound = new Rectangle(x,y,width,height);
	}
	public void setY(int y){
		this.y = y;
		bound = new Rectangle(x,y,width,height);
	}
	public void setWidth(int width){
		this.width = width;
		bound = new Rectangle(x,y,width,height);
	}
	public void setHeight(int height){
		this.height = height;
		bound = new Rectangle(x,y,width,height);
	}
	public abstract int outPuttingPower(Part p);
	public void setConnectParent(Part p) {
		if (connecting) {
			parent = p;
			justChangedPower = true;
			connecting = false;
			JPanel n = new JPanel();
			JPanel pa = new JPanel();
			getProperties(pa);
			n.add(pa);
			RobotEmulator.window.showPref(n);
		}
	}
	
	public String toString() {
		return this.getClass()+" ("+this.x+","+this.y+") UUID: "+this.uuid;
	}
}
