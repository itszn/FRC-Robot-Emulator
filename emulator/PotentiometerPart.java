package emulator;

import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import edu.wpi.first.wpilibj.AnalogModule;

public class PotentiometerPart extends Part {
	int channel = 0;
	public PotentiometerPart(int x, int y, int width, int height) {
		super(x,y,width,height);
		name = "Potentiometer";
		props = new JComponent[5];
		String[] items = new String[AnalogModule.kAnalogChannels+1];
		items[0]="None";
		for (int i=1; i<=AnalogModule.kAnalogChannels;i++)
			items[i]=String.valueOf(i);
		JComboBox<String> channel = new JComboBox<String>(items);
		channel.setSelectedIndex(0);
		props[0] = channel;
		SpinnerNumberModel turnModel = new SpinnerNumberModel(1.0,1,20,1);
		JSpinner turns = new JSpinner(turnModel);
		props[1] = turns;
		SpinnerNumberModel gearModel = new SpinnerNumberModel(1.0,0,100,.5);
		JSpinner gears = new JSpinner(gearModel);
		props[2] = gears;
		
		
	}
	
	public PotentiometerPart(int x, int y) {
		this(x,y,50,50);
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawRect(x, y, width, height);

	}
	
	@Override
	public void getProperties(JPanel p) {
		/*p.add(new JLabel(name));
		JPanel n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("Analog Port"));
			((JComboBox)props[0]).setSelectedIndex(channel);
			n.add(props[0]);
		p.add(n);
		n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("Top Speed"));
			((JSpinner)props[1]).setValue(maxSpeed);
			n.add(props[1]);
		p.add(n);
		super.getProperties(p);*/
	}

	@Override
	public int outPuttingPower(Part p) {
		// TODO Auto-generated method stub
		return 0;
	}

}
