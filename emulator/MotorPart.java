package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import edu.wpi.first.wpilibj.DigitalModule;

public class MotorPart extends Part {
	int channel=0;
	double maxSpeed = 1;
	double speed = 0;
	long lastFrame = -1;
	double rot = 0;
	
	public MotorPart(int x, int y, int width, int height) {
		super(x, y, width, height);
		name = "Motor";
		props = new JComponent[3];
		String[] items = new String[DigitalModule.kPwmChannels+1];
		items[0]="None";
		for (int i=1; i<=DigitalModule.kPwmChannels;i++)
			items[i]=String.valueOf(i);
		JComboBox<String> channel = new JComboBox<String>(items);
		channel.setSelectedIndex(0);
		props[0] = channel;
		//JTextField f = new JTextField("1.00",4);
		SpinnerNumberModel numberModel = new SpinnerNumberModel(1.0,0,30,.1);
		JSpinner s = new JSpinner(numberModel);
		props[1] = s;
		String[] types = {"Automatic","Manual"};
		JComboBox<String> power = new JComboBox<String>(types);
		power.setSelectedIndex(0);
		props[2]=power;
		powerInPoint = new Point(7,15);
		pwmPoint = new Point(13,44);
		powerOutPoint = new Point(43,15);
		
	}
	
	public MotorPart(int x, int y) {
		this(x, y, 100,50);
	}

	@Override
	public void update() {
		super.update();
			
		if (channel >0 && DigitalModule.PWMChannels[channel-1]>=4) {
			speed = ((DigitalModule.PWMChannels[channel-1]-128d)/125d)*maxSpeed*powered;
		}
		else
			speed = 0.0;
	}

	@Override
	public void paint(Graphics g) {
		
		//AffineTransform tx = new AffineTransform();
		//tx.translate(x+75, y+25);
		//tx.rotate(speed);
		//Graphics2D ourG = (Graphics2D) g;
		//ourG.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
		//		RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
		//ourG.setTransform(tx);
		BufferedImage i = null;
		try {
			i = ImageIO.read(ClassLoader.getSystemResource("res/jaguar.png"));
			g.drawImage(i, x, y, new Color(1,1,1,0), RobotEmulator.window);
			long dif = 0;
			if (lastFrame!=-1)
				dif = System.currentTimeMillis() - lastFrame;
			lastFrame = System.currentTimeMillis();
			rot+=((double)dif)/1000d * speed * 180d;
			g.setColor(Color.red);
			g.drawLine(x+75, y+25, x+powerOutPoint.x, y+powerOutPoint.y);
			g.setColor(Color.black);
			i = rotate(ImageIO.read(ClassLoader.getSystemResource("res/wheel.png")),Math.toRadians(rot));
			g.drawImage(i, x+50, y, new Color(1,1,1,0), RobotEmulator.window);
			
		}
		catch (IOException e) {}
		//ourG.setTransform(new AffineTransform());
		g.drawRect(x, y, width, height);
		if (channel != 0) {
			g.drawLine((channel>9?20:10), 11*(channel+1)-5, x+pwmPoint.x, y+pwmPoint.y);
		}
		if (connecting) {
			Point p = RobotEmulator.window.draw.getMousePosition();
			if (p!=null) {
				g.setColor(Color.red);
				g.drawLine(p.x,p.y, x+powerInPoint.x, y+powerInPoint.y);
				g.setColor(Color.black);
			}
		}
		else if (parent!=null) {
			g.setColor(Color.red);
			g.drawLine(parent.x+parent.powerOutPoint.x, parent.y+parent.powerOutPoint.y, x+powerInPoint.x, y+powerInPoint.y);
			g.setColor(Color.black);
		}
		g.drawString("Speed: "+speed+" rps", x, y+height+11);
	}
	
	public static BufferedImage rotate(BufferedImage image, double angle) {
	    double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
	    int w = image.getWidth(), h = image.getHeight();
	    int neww = (int)Math.floor(w*cos+h*sin), newh = (int)Math.floor(h*cos+w*sin);
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
	    GraphicsConfiguration gc = gd.getDefaultConfiguration();
	    BufferedImage result = gc.createCompatibleImage(/*neww, newh*/50,50, Transparency.TRANSLUCENT);
	    Graphics2D g = result.createGraphics();
	    g.translate((/*neww*/50-w)/2, (/*newh*/50-h)/2);
	    g.rotate(angle, w/2, h/2);
	    g.drawRenderedImage(image, null);
	    g.dispose();
	    return result;
	}

	@Override
	public void updateProperties() {
		super.updateProperties();
		channel = ((JComboBox)props[0]).getSelectedIndex();
		try {
			maxSpeed = Double.valueOf(((JSpinner)props[1]).getValue().toString());
		} catch (Exception e) {
			System.out.println("t");
		}
	}
	
	@Override
	public void getProperties(JPanel p) {
		p.add(new JLabel(name));
		JPanel n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("PWM Port"));
			((JComboBox)props[0]).setSelectedIndex(channel);
			n.add(props[0]);
		p.add(n);
		n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("Top Speed"));
			((JSpinner)props[1]).setValue(maxSpeed);
			n.add(props[1]);
		p.add(n);
		super.getProperties(p);
	}

	@Override
	public void actionPerformed(ActionEvent evn) {
		super.actionPerformed(evn);
		
	}

	@Override
	public int outPuttingPower(Part p) {
		return 0;
	}


}
