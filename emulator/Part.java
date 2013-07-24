package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class Part implements ActionListener{
	String name = "Part";
	public JComponent[] props;
	public Rectangle bound;
	protected int x=0, y=0, width=100, height=100;
	public Part(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		bound = new Rectangle(x,y,width,height);
		if (!RobotEmulator.parts.contains(this)) {
			RobotEmulator.parts.add(this);
		}
	}
	
	public abstract void update();
	public abstract void paint(Graphics g);
	public abstract void updateProperties();
	public void getProperties(JPanel p) {
		BoxLayout layout = new BoxLayout(p, BoxLayout.Y_AXIS);
		p.setLayout(layout);
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
	}
	
	@Override
	public void actionPerformed(ActionEvent evn) {
		if (evn.getActionCommand().equals("close")) {
			RobotEmulator.window.closePref();
		}
		else if (evn.getActionCommand().equals("save")) {
			RobotEmulator.window.closePref();
			this.updateProperties();
		}
		else if (evn.getActionCommand().equals("delete")) {
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
}
