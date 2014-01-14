package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.wpi.first.wpilibj.DigitalModule;

public class LimitSwitchPart extends Part{
	int channel=0;
	boolean activated = false;
	
	public LimitSwitchPart(int x, int y, int width, int height) {
		super(x, y, width, height);
		name = "Limit Switch";
		props = new JComponent[2];
		String[] items = new String[DigitalModule.kDigitalChannels+1];
		items[0]="None";
		for (int i=1;i<=DigitalModule.kDigitalChannels;i++)
			items[i]=String.valueOf(i);
		JComboBox<String> channel = new JComboBox<String>(items);
		channel.setSelectedIndex(0);
		props[0]=channel;
		JCheckBox on = new JCheckBox("Pressed");
		on.setSelected(false);
		props[1] = on;
		pwmPoint = new Point(10,16);
	}
	
	public LimitSwitchPart(int x, int y) {
		this(x,y,100,50);
	}

	@Override
	public void update() {
		super.update();
		if (channel != 0)
			DigitalModule.DIOChannels[channel-1] = (activated?1:0);
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawRect(x, y, width, height);
		try {
			BufferedImage i;
			if (activated)
				i = ImageIO.read(ClassLoader.getSystemResource("res/limitOn.png"));
			else
				i = ImageIO.read(ClassLoader.getSystemResource("res/limitOff.png"));
			g.drawImage(i, x, y, new Color(1,1,1,0), RobotEmulator.window);
		} catch(IOException e){};
		if (channel != 0) {
			g.drawLine((channel>9?20:10), 220+11*(channel+1)-5, x+pwmPoint.x, y+pwmPoint.y);
		}
		g.drawString(activated?"Pushed":"Released", x, y+height+11);
	}

	@Override
	public void updateProperties() {
		super.updateProperties();
		channel = ((JComboBox)props[0]).getSelectedIndex();
		activated = ((JCheckBox)props[1]).isSelected();
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
		n = new JPanel();
			n.setLayout(new FlowLayout());
			((JCheckBox)props[1]).setSelected(activated);
			n.add(props[1]);
		p.add(n);
		super.getProperties(p);
	}
	
	@Override
	public void interactMousePressed(MouseEvent evn) {
		if (evn.getID()==evn.MOUSE_PRESSED && evn.getButton()==1) {
			activated = !activated;
		}
	}
	
	@Override
	public void interactMouseReleased(MouseEvent evn) {
		if (evn.getID()==evn.MOUSE_RELEASED && evn.getButton()==1 && !RobotEmulator.window.retainMode) {
			activated = false;
		}
	}

	
}
