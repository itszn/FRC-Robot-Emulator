package emulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class JoystickGUI extends JPanel implements MouseListener,MouseMotionListener{
	public int port;
	public int x=0, y=0;
	public JoystickGUI(int port) {
		this.setPreferredSize(new Dimension(100,100));
		this.setSize(new Dimension(100,100));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setBackground(Color.WHITE);
	}
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
		g.drawLine(0, 50, 100, 50);
		g.drawLine(50, 0, 50, 100);
		g.setColor(Color.BLACK);
		g.drawOval(x+45, y+45, 10, 10);
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
		if (x>0 && x<100)
			x = evn.getX()-50;
		if (y>0 && y<100)
			y = evn.getY()-50;
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		x=0;
		y=0;
		
	}
	@Override
	public void mouseDragged(MouseEvent evn) {
		if (x>0 && x<100)
			x = evn.getX()-50;
		if (y>0 && y<100)
		y = evn.getY()-50;
		
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
