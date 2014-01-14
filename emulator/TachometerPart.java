package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import edu.wpi.first.wpilibj.DigitalModule;

public class TachometerPart extends Part implements IMotorConnector{

	MotorConnector motorConnector;
	int channel = 0;
	long lastFrame = -1;
	double currentPos = 0;
	int count = 0;
	boolean just0 = true;
	
	public TachometerPart(int x, int y, int width, int height) {
		super(x, y, width, height);
		motorConnector = new MotorConnector(this);
		name = "Tachometer";
		props = new JComponent[2];
		String[] items = new String[DigitalModule.kDigitalChannels+1];
		items[0] = "None";
		for (int i=1; i<=DigitalModule.kDigitalChannels;i++) 
			items[i] = String.valueOf(i);
		JComboBox<String> channel = new JComboBox<String>(items);
		channel.setSelectedIndex(0);
		props[0] = channel;
//		SpinnerNumberModel countModel = new SpinnerNumberModel(0,(int)Double.MIN_VALUE,0,1);
//		JSpinner count = new JSpinner(countModel);
//		props[1] = count;

		motorConnector.motorInPoint = new Point(7,5);
	}
	
	public TachometerPart(int x,int y) {
		this(x,y,50,75);
	}

	@Override
	public MotorConnector getMotorConnector() {
		return motorConnector;
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawRect(x, y, width, height);
		try {
			BufferedImage i = ImageIO.read(ClassLoader.getSystemResource("res/tachometer.png"));
			g.drawImage(i, x, y, new Color(1,1,1,0), RobotEmulator.window);
		} catch(Exception e){};
		g.setColor(Color.black);
		g.drawString("Count: "+count, x, y+height+11);
		if (channel != 0) {
			g.drawLine((channel>9?20:10), 220+11*(channel+1)-5, x+pwmPoint.x, y+pwmPoint.y);
		}
		super.paint(g);
	}
	
	@Override
	public void getProperties(JPanel p) {
		p.add(new JLabel(name));
		JPanel n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("Digital I/O Port"));
			((JComboBox)props[0]).setSelectedIndex(channel);
			n.add(props[0]);
		p.add(n);
//		n = new JPanel();
//			n.setLayout(new FlowLayout());
//			n.add(new JLabel("Current Count"));
//			((JSpinner)props[1]).setValue(count);
//			n.add(props[1]);
//		p.add(n);
		super.getProperties(p);
	}
	
	@Override 
	public void update() {
		super.update();
		long dif = 0;
		if (lastFrame!=-1)
			dif = System.currentTimeMillis()-lastFrame;
		lastFrame = System.currentTimeMillis();
		double oldPos = currentPos;
		if (motorConnector.motorParent!=null) {
			currentPos += (motorConnector.motorParent.speed * ((double)dif)/1000d);
		}
		if ((int)oldPos!=(int)currentPos) {
			count+=Math.abs((int)oldPos-(int)currentPos);
		}
		if (channel!=0) {
			if (DigitalModule.DIOChannels[channel-1]==0&&!just0) {
				count = 0;
				just0=true;
			}
			else {
				DigitalModule.DIOChannels[channel-1]=count;
				if (count!=0)
					just0=false;
			}
				
		}
	}
	
	@Override
	public void updateProperties() {
		super.updateProperties();
		channel = ((JComboBox)props[0]).getSelectedIndex();
		//count = Double.valueOf(((JSpinner)props[1]).getValue().toString()).intValue();
	}
	
	

}
