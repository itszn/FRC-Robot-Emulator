package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

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
	}
	
	public RelayPart(int x, int y) {
		this(x,y,50,50);
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
		if (channel != 0) {
			g.drawLine((channel>9?20:10), 200+11*(channel+1)-5, x, y);
		}
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
