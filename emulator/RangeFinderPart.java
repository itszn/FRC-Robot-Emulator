package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.wpi.first.wpilibj.AnalogModule;

class RangeFinderPart extends Part {

	int channel = 0;
	double distance= 10;
	double analogValue= 0 ;
	
	private final double vcc = 5.0; //supplied voltage
    private final double vi = vcc / 512; //volts per inch
    
    
        // vm / vi = ri where vm = measured voltage, vi is as above, and ri = range in inches
        //return getVoltage()/vi;
	
	
	protected RangeFinderPart(int x, int y, int width, int height) {
		super(x, y, width, height);
		name = "RangeFinder";
		props = new JComponent[5];
		String[] items = new String[AnalogModule.kAnalogChannels+1];
		items[0]="None";
		for (int i=1; i<=AnalogModule.kAnalogChannels;i++)
			items[i]=String.valueOf(i);
		JComboBox<String> channel = new JComboBox<String>(items);
		channel.setSelectedIndex(0);
		props[0] = channel;
		pwmPoint = new Point(20,25);
		// TODO Auto-generated constructor stub
	}
	
	protected RangeFinderPart(int x, int y) {
		this(x,y,50,50);
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawRect(x, y, width, height);
		try {
			BufferedImage i = ImageIO.read(ClassLoader.getSystemResource("res/rangeFinder.png"));
			g.drawImage(i, x, y, new Color(1,1,1,0), RobotEmulator.window);
		} catch(Exception e){};
		g.setColor(Color.GRAY);
		g.fillRect((int) (x+50+distance*40.0/12.0), y, 15, 50);
		g.setColor(Color.black);
		g.drawRect((int) (x+50+distance*40.0/12.0), y, 15, 50);
		
		g.drawString("Analog Value: "+Utils.truncate(analogValue), x, y+height+11);
		g.drawString("Distance: "+Utils.truncate(distance)+" Inches", x, y+height+22);
		
		if (channel != 0) {
			g.drawLine((channel>9?20:10), 396+11*(channel)-5, x+pwmPoint.x, y+pwmPoint.y);
		}
		super.paint(g);
	}
	
	@Override
	public void getProperties(JPanel p) {
		p.add(new JLabel(name));
		JPanel n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("Analog Port"));
			((JComboBox)props[0]).setSelectedIndex(channel);
			n.add(props[0]);
		p.add(n);
		super.getProperties(p);
	}
	
	@Override
	public void update() {
		super.update();
		this.interactBound.width = (int) (this.width+distance*40.0/12.0+20);
		analogValue = distance * vi;
		if (channel!=0)
			AnalogModule.AnalogChannels[channel-1]=analogValue;
	}
	
	@Override
	public void updateProperties() {
		super.updateProperties();
		channel = ((JComboBox)props[0]).getSelectedIndex();		
	}
	
	private double mouseXOff = 0;
	boolean mouseDown = false;
	@Override
	public void interactMousePressed(MouseEvent evn) {
		if (evn.getID()==evn.MOUSE_PRESSED) {
			//g.fillRect((int) (x+50+distance*50.0/12.0), y, 10, 50);

			int dist = (int) (this.x+50+distance*40.0/12.0);
			int x = evn.getX();
			//System.out.println(x+" "+dist);
			if (x>=dist-5 && x<dist+20) {
				mouseXOff = x-dist;
				mouseDown = true;

				//System.out.println(x+" "+dist);
			}
		}
	}
	
	@Override
	public void interactMouseDragged(MouseEvent evn) {
		if (evn.getID()==evn.MOUSE_DRAGGED) {
			if (mouseDown) {
				distance = (evn.getX()-50-x-mouseXOff)*12.0/40.0;
				if (distance<0) {
					distance = 0;
				}
			}
		}
	}
	
	@Override
	public void interactMouseReleased(MouseEvent evn) {
		if (evn.getID()==evn.MOUSE_RELEASED) {
			mouseDown = false;
		}
	}
	
}
