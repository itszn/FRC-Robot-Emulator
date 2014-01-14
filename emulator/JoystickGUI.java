package emulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.wpi.first.wpilibj.Joystick;

public class JoystickGUI extends JPanel implements MouseListener,MouseMotionListener,KeyListener{
	public int port;
	public double x=0, y=0;
	JCheckBox useKeys;
	boolean[] dirKeys = {false,false,false,false};
	
	boolean[] buttonKeys = new boolean[20];
	boolean mouse = false;
	public JoystickGUI(int port) {
		for (int i=0; i<20; i++)
			buttonKeys[i]=false;
		this.setPreferredSize(new Dimension(100,100));
		this.setSize(new Dimension(100,100));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		this.setBackground(Color.WHITE);
		useKeys = new JCheckBox("Use Keyboard");
		this.port = port;
	}
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
		g.drawLine(0, 50, 100, 50);
		g.drawLine(50, 0, 50, 100);
		g.setColor(Color.BLACK);
		g.drawOval((int)(45-x), (int)(45-y), 10, 10);
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent evn) {
		if (evn.getID()==MouseEvent.MOUSE_PRESSED) {
			mouse = true;
			if (evn.getX()>0 && evn.getX()<100)
				x = 50-evn.getX();
			if (evn.getY()>0 && evn.getY()<100)
				y = 50-evn.getY();
			RobotEmulator.window.repaint();
		}
		update();
	}
	@Override
	public void mouseReleased(MouseEvent evn) {
		if (evn.getID()==MouseEvent.MOUSE_RELEASED) {
			mouse = false;
			x=0;
			y=0;
		}
		update();
	}
	@Override
	public void mouseDragged(MouseEvent evn) {
		if (evn.getX()>0 && evn.getX()<100)
			x = 50-evn.getX();
		if (evn.getY()>0 && evn.getY()<100)
			y = 50-evn.getY();
		update();
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void update() {
		if (!mouse) {
			if (dirKeys[0]&&!dirKeys[1])
				if (y<50)
					y+=.5;
				else
					y=50;
			else if (dirKeys[1]&&!dirKeys[0])
				if (y>-50)
					y-=.5;
				else
					y=-50;
			else if (Math.abs(y)>10)
				y+= y>0?-1:1;
			else
				y=0;
			
			if (dirKeys[2]&&!dirKeys[3])
				if (x<50)
					x+=.5;
				else
					x=50;
			else if (dirKeys[3]&&!dirKeys[2])
				if (x>-50)
					x-=.5;
				else
					x=-50;
			else if (Math.abs(x)>10)
				x+= x>0?-1:1;
			else
				x=0;
		}
		//System.out.println(y);
		Joystick.axises[port][Joystick.kDefaultXAxis] = -x/50d;
		Joystick.axises[port][Joystick.kDefaultYAxis] = y/50d;
		for (int i=0; i<20; i++) {
			Joystick.buttons[port][i] = buttonKeys[i];
		}
		
	}
	@Override
	public void keyPressed(KeyEvent evn) {
		if (evn.getID() != KeyEvent.KEY_PRESSED)
			return;
		if (useKeys.isSelected()) {
			if (evn.getKeyCode()==KeyEvent.VK_UP) {
				dirKeys[0]=true;
			}
			else if (evn.getKeyCode()==KeyEvent.VK_DOWN) {
				dirKeys[1]=true;
			}
			else if (evn.getKeyCode()==KeyEvent.VK_LEFT) {
				dirKeys[2]=true;
			}
			else if (evn.getKeyCode()==KeyEvent.VK_RIGHT) {
				dirKeys[3]=true;
			}
			//System.out.println(evn.getKeyCode());
			if (evn.getKeyCode()>=48 && evn.getKeyCode()<=58) {
				int i = evn.getKeyCode()-49+1;
				//System.out.println(evn.getKeyCode()-49+1);
				if (i==0)
					i=10;
				buttonKeys[i-1]=true;
				//System.out.println(buttonKeys[2-1]);
			}
			
			update();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent evn) {
		if (evn.getID() != KeyEvent.KEY_RELEASED)
			return;
		if (evn.getKeyCode()==KeyEvent.VK_UP) {
			dirKeys[0]=false;
		}
		else if (evn.getKeyCode()==KeyEvent.VK_DOWN) {
			dirKeys[1]=false;
		}
		else if (evn.getKeyCode()==KeyEvent.VK_LEFT) {
			dirKeys[2]=false;
		}
		else if (evn.getKeyCode()==KeyEvent.VK_RIGHT) {
			dirKeys[3]=false;
		}
		if (evn.getKeyCode()>=48 && evn.getKeyCode()<=58) {
			int i = evn.getKeyCode()-49+1;
			if (i==0)
				i=10;
			buttonKeys[i-1]=false;
		}
		update();
	}
	@Override
	public void keyTyped(KeyEvent evn) {
		
	}
}
