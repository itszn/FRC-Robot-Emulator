package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

public class LightPart extends Part{
	boolean on = false;
	public LightPart(int x, int y, int width, int height) {
		super(x, y, width, height);
		autoPower = false;
		name="Light";
	}
	
	public LightPart(int x, int y) {
		this(x,y,50,50);
	}

	@Override
	public void paint(Graphics g) {
		if (on) {
			g.setColor(Color.green);
			g.fillOval(x, y, width, height);
			
		}
		g.setColor(Color.black);
			g.drawOval(x, y, width, height);
		if (connecting) {
			Point p = RobotEmulator.window.draw.getMousePosition();
			if (p!=null) {
				g.setColor(Color.red);
				g.drawLine(p.x,p.y, x, y);
				g.setColor(Color.black);
			}
		}
		else if (parent!=null) {
			g.setColor(Color.red);
			g.drawLine(parent.x, parent.y, x, y);
			g.setColor(Color.black);
		}
		g.drawString((on?"On":"Off"), x, y+height+11);
	}
	
	public void update() {
		super.update();
		on = powered != 0;
	}
	
	@Override
	public void getProperties(JPanel p) {
		p.add(new JLabel(name));
		super.getProperties(p);
	}

	@Override
	public int outPuttingPower(Part p) {
		return 0;
	}

}
