package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

public class LightPart extends Part implements IPowerConnector{
	boolean on = false;
	PowerConnector powerConnector;
	public LightPart(int x, int y, int width, int height) {
		super(x, y, width, height);
		powerConnector = new PowerConnector(this);
		powerConnector.autoPower = false;
		name="Light";
		powerConnector.powerInPoint = new Point(26,71);
	}
	
	public LightPart(int x, int y) {
		this(x,y,50,75);
	}

	@Override
	public void paint(Graphics g) {
		/*if (on) {
			g.setColor(Color.green);
			g.fillOval(x, y, width, height);
			
		}*/
		g.drawRect(x, y, width, height);
		try {
			BufferedImage i;
			if (on)
				i= ImageIO.read(ClassLoader.getSystemResource("res/lightOn.png"));
			else
				i= ImageIO.read(ClassLoader.getSystemResource("res/lightOff.png"));
			g.drawImage(i, x, y, new Color(1,1,1,0), RobotEmulator.window);
		} catch(IOException e){};
		//g.setColor(Color.black);
			//g.drawOval(x, y, width, height);
		
		g.drawString((on?"On":"Off"), x, y+height+11);
		super.paint(g);
	}
	
	public void update() {
		super.update();
		on = powerConnector.powered != 0;
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

	@Override
	public PowerConnector getPowerConnector() {
		return powerConnector;
	}
	
	@Override
	public PowerConnector getParentPowerConnector() {
		return ((IPowerConnector)powerConnector.powerParent).getPowerConnector();
	}

}
