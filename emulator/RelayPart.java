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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.wpi.first.wpilibj.DigitalModule;

public class RelayPart extends Part{
	int channel=0;
	int powerOut=0;
	
	public RelayPart(int x, int y, int width, int height) {
		super(x, y, width, height);
		name = "Relay";
		props = new JComponent[1];
		String[] items = new String[DigitalModule.kRelayChannels+1];
		items[0]="None";
		for (int i=1;i<=DigitalModule.kRelayChannels;i++)
			items[i]=String.valueOf(i);
		JComboBox<String> channel = new JComboBox<String>(items);
		channel.setSelectedIndex(0);
		props[0]=channel;
		maxChildren = 1;
		powerInPoint = new Point(0,9);
		pwmPoint = new Point(10,16);
		powerOutPoint = new Point(75,9);
	}
	
	public RelayPart(int x, int y) {
		this(x,y,75,50);
	}

	@Override
	public void update() {
		super.update();
		if (channel>0) {
			DigitalModule.RelayInfo rel = DigitalModule.relayChannels[channel-1];
			int newPow = (rel.fwd?1:(rel.rev?-1:0));
			powerOut = powered*newPow;
		}
		else
			powerOut = 0;

	}
	
	@Override
	public void paint(Graphics g) {
		g.drawRect(x, y, width, height);
		try {
			BufferedImage i = ImageIO.read(ClassLoader.getSystemResource("res/spike.png"));
			g.drawImage(i, x, y, new Color(1,1,1,0), RobotEmulator.window);
		} catch(IOException e){};
		if (channel == 0)
			g.setColor(Color.red);
		else {
			if (powerOut<0) {
				g.setColor(Color.orange);
			}
			else if(powerOut>0)
				g.setColor(Color.green);
			else if (powerOut==0)
				g.setColor(Color.yellow);
		}
		if (powered!=0)
			g.fillOval(x+53, y+38, 6, 6);
		g.setColor(Color.black);
		if (channel != 0) {
			g.drawLine((channel>9?20:10), 121+11*(channel+1)-5, x+pwmPoint.x, y+pwmPoint.y);
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
		g.drawString((powerOut==1?"Forward":(powerOut==0?"Off":"Reverse")), x, y+height+11);
	}

	@Override
	public void updateProperties() {
		super.updateProperties();
		channel = ((JComboBox)props[0]).getSelectedIndex();
	}
	
	@Override
	public void getProperties(JPanel p) {
		p.add(new JLabel(name));
		JPanel n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("Relay Port"));
			((JComboBox)props[0]).setSelectedIndex(channel);
			n.add(props[0]);
		p.add(n);
		super.getProperties(p);
	}

	@Override
	public int outPuttingPower(Part p) {
		return powerOut;
	}
	

}
