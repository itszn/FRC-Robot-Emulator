package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import edu.wpi.first.wpilibj.DigitalModule;

public class Motor extends Part {
	int channel=0;
	double maxSpeed = 1;
	double speed = 0;
	public Motor(int x, int y, int width, int height) {
		super(x, y, width, height);
		name = "Motor";
		props = new JComponent[2];
		String[] items = new String[DigitalModule.kDigitalChannels+1];
		items[0]="None";
		for (int i=1; i<=DigitalModule.kDigitalChannels;i++)
			items[i]=String.valueOf(i);
		JComboBox<String> channel = new JComboBox<String>(items);
		channel.setSelectedIndex(0);
		props[0] = channel;
		//JTextField f = new JTextField("1.00",4);
		SpinnerNumberModel numberModel = new SpinnerNumberModel(1.0,0,30,.1);
		JSpinner s = new JSpinner(numberModel);
		
		props[1] = s;
	}
	
	public Motor(int x, int y) {
		this(x, y, 100,50);
	}

	@Override
	public void update() {
		if (channel >0 && DigitalModule.DIOChannels[channel-1]>=4) {
			speed = ((DigitalModule.DIOChannels[channel-1]-128d)/125d)*maxSpeed;
		}
		else
			speed = 0.0;
	}

	@Override
	public void paint(Graphics g) {
		g.drawRect(x, y, width, height);
		if (channel != 0) {
			g.drawLine((channel>9?20:10), 11*(channel+1)-5, x, y);
		}
		g.drawString("Speed: "+speed, x, y+height+11);
	}

	@Override
	public void updateProperties() {
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
			n.add(new JLabel("Port"));
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
	public void drawSelected(Graphics g) {
		super.drawSelected(g);
		g.setColor(Color.black);
	}


}
