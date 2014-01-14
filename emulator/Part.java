package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public abstract class Part implements ActionListener/*, Serializable*/{
	
	private static final long serialVersionUID = 4753975558253506016L;
	public UUID uuid = null; 
	public UUID tempParentUUID = null;
	static String name = "Part";
	//boolean connecting = false;
	//boolean connectionOption = false;
	//public int maxChildren = 0;
	//public Part parent = null;
	//public int powered = 0;
	//public ArrayList<Part> children = new ArrayList<Part>();
	//boolean autoPower = true;
	//boolean usePower = true;
	//boolean justChangedPower = false;
	public boolean exists = true;
	public JComponent[] props;
	public Rectangle bound;
	public Rectangle interactBound;
	//JComboBox<String> powerChoice;
	protected int x=0, y=0, width=100, height=100;
	//public Point powerInPoint = new Point(0,0);
	//public Point powerOutPoint = new Point(0,0);
	public Point pwmPoint = new Point(0,0);
	
	public Part(int x, int y, int width, int height) {
		this.uuid = UUID.randomUUID();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		bound = new Rectangle(x,y,width,height);
		interactBound = new Rectangle(x,y,width,height);
		//TODO fix Sync
		if (!RobotEmulator.parts.contains(this)) {
			RobotEmulator.parts.add(this);
		}
	}
	
	public void update() {
		if (this instanceof IPowerConnector && ((IPowerConnector)this).getPowerConnector()!=null) {
			((IPowerConnector)this).getPowerConnector().update();
		}
		if (this instanceof IMotorConnector && ((IMotorConnector)this).getMotorConnector()!=null) {
			((IMotorConnector)this).getMotorConnector().update();
		}
	}
	public void paint(Graphics g) {
		if (this instanceof IPowerConnector) {
			((IPowerConnector)this).getPowerConnector().paint(g);
		}
		if (this instanceof IMotorConnector) {
			((IMotorConnector)this).getMotorConnector().paint(g);
		}
	}
	public void updateProperties() {
		if (this instanceof IPowerConnector) {
			((IPowerConnector)this).getPowerConnector().updateProperties();
		}
		if (this instanceof IMotorConnector) {
			((IMotorConnector)this).getMotorConnector().updateProperties();
		}
	}
	public void getProperties(JPanel p) {
		BoxLayout layout = new BoxLayout(p, BoxLayout.Y_AXIS);
		p.setLayout(layout);
		if (this instanceof IPowerConnector) {
			((IPowerConnector)this).getPowerConnector().getProperties(p);
		}
		if (this instanceof IMotorConnector) {
			((IMotorConnector)this).getMotorConnector().getProperties(p);
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
			if (this instanceof IPowerConnector && !((IPowerConnector)this).getPowerConnector().powerConnecting)
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
		if (this instanceof IPowerConnector) {
			((IPowerConnector)this).getPowerConnector().actionPerformed(evn);
		}
		if (this instanceof IMotorConnector) {
			((IMotorConnector)this).getMotorConnector().actionPerformed(evn);
		}
		if (evn.getActionCommand().equals("close")) {
			RobotEmulator.window.closePref();
		}
		else if (evn.getActionCommand().equals("save")) {
			Window.changes = true;
			RobotEmulator.window.closePref();
			this.updateProperties();
		}
		else if (evn.getActionCommand().equals("delete")) {
			Window.changes = true;
			RobotEmulator.window.closePref();
			this.remove();
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
		interactBound.setLocation(x+interactBound.x-bound.x, y+interactBound.y-bound.y);
		bound = new Rectangle(x,y,width,height);
	}
	public void setY(int y){
		this.y = y;
		interactBound.setLocation(x+interactBound.x-bound.x, y+interactBound.y-bound.y);
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
	
	public void interactMouseClicked(MouseEvent evn) {};
	public void interactMousePressed(MouseEvent evn) {};
	public void interactMouseReleased(MouseEvent evn) {};
	public void interactMouseDragged(MouseEvent evn) {};
	public void interactMoveMouse(MouseEvent evn) {};
	
 	public String toString() {
		return this.getClass()+" ("+this.x+","+this.y+") UUID: "+this.uuid;
	}
}
