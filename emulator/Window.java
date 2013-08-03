package emulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.DriverStation;

public class Window extends JFrame implements ActionListener, MouseListener, MouseMotionListener{
	//JFrame frame;
	public static File saveFile = null;
	public static boolean changes = false; 
	Canvas draw;
	JMenuBar menuBar;
	JMenu fileMenu;
	JPanel info;
	JPanel shownInfo;
	JComboBox<String> partTypes;
	ButtonGroup enableGroup, modeGroup;
	JRadioButton disable, enable, tele, auto;
	static Part selectedPart = null;
	public Part partConnecting = null;
	public Window() {
		super("Robot Emulator");
		setSize(800,600);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});
		setLayout(new BorderLayout(5,5));
		
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem i = new JMenuItem("New");
		i.addActionListener(this);
		i.setActionCommand("new");
		fileMenu.add(i);
		i = new JMenuItem("Open...");
		i.addActionListener(this);
		i.setActionCommand("open");
		fileMenu.add(i);
		fileMenu.addSeparator();
		i = new JMenuItem("Save");
		i.addActionListener(this);
		i.setActionCommand("save");
		fileMenu.add(i);
		i = new JMenuItem("Save As...");
		i.setActionCommand("saveAs");
		fileMenu.add(i);
		i.addActionListener(this);
		fileMenu.addSeparator();
		i = new JMenuItem("Close");
		i.addActionListener(this);
		i.setActionCommand("close");
		fileMenu.add(i);
		this.setJMenuBar(menuBar);
		
		
		
		info = new JPanel();
			JPanel n = new JPanel();
				BoxLayout layout = new BoxLayout(n, BoxLayout.Y_AXIS);
				
				n.setLayout(layout);
				String[] types = {"Motor","Relay","Light","Limit Switch"};
				partTypes = new JComboBox<String>(types);
					partTypes.setSelectedIndex(0);
					partTypes.addActionListener(this);
				n.add(partTypes);
				JButton b = new JButton("Add Selected");
					b.addActionListener(this);
					b.setActionCommand("addPart");
				n.add(b);
				JPanel n2 = new JPanel();
					n2.setLayout(new FlowLayout());
					enableGroup = new ButtonGroup();
					disable = new JRadioButton("Disable",true);
						disable.addActionListener(this);
						disable.setActionCommand("disableRobot");
						enableGroup.add(disable);
					n2.add(disable);
					enable = new JRadioButton("Enable",false);
						enable.addActionListener(this);
						enable.setActionCommand("enableRobot");
						enableGroup.add(enable);
					n2.add(enable);
					n2.setBorder(BorderFactory.createLoweredBevelBorder());
				n.add(n2);
				n2 = new JPanel();
					n2.setLayout(new FlowLayout());
					modeGroup = new ButtonGroup();
					tele = new JRadioButton("Tele-op",true);
						tele.addActionListener(this);
						tele.setActionCommand("teleMode");
						modeGroup.add(tele);
					n2.add(tele);
					auto = new JRadioButton("Autonomous", false);
						auto.addActionListener(this);
						auto.setActionCommand("autoMode");
						modeGroup.add(auto);
					n2.add(auto);
					n2.setBorder(BorderFactory.createLoweredBevelBorder());
				n.add(n2);
			info.add(n);
		add(info,BorderLayout.EAST);
		
		draw = new Canvas();
			draw.addMouseListener(this);
			draw.addMouseMotionListener(this);
		getContentPane().add(draw,BorderLayout.CENTER);
		setVisible(true);
		
		shownInfo=info;
		//SaveManager.instance.callSaveData();
	}
	
	class Canvas extends JPanel{
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.black);
			this.setBackground(Color.white);
			for(Part p: RobotEmulator.parts)
				p.paint(g);
			g.drawString("PWM", 1, 11);
			for (int i=1; i<=DigitalModule.kPwmChannels;i++)
				g.drawString(String.valueOf(i), 1, 11+11*i);
			g.drawString("Relay", 1, 132);
			for (int i=1; i<=DigitalModule.kRelayChannels;i++)
				g.drawString(String.valueOf(i), 1, 132+11*i);
			g.drawString("Digital I/O", 1, 231);
			for (int i=1; i<=DigitalModule.kDigitalChannels;i++)
				g.drawString(String.valueOf(i), 1, 231+11*i);
			if (selectedPart!=null) {
				selectedPart.drawSelected(g);
			}
		}
	}
	
	public void closePref() {
		this.remove(shownInfo);
		this.add(info,BorderLayout.EAST);
		partConnecting = null;
		shownInfo = info;
		revalidate();
		repaint();
	}
	
	public void showPref(JPanel panel) {
		this.remove(shownInfo);
		this.add(panel,BorderLayout.EAST);
		shownInfo = panel;
		revalidate();
		repaint();
	}
	private Part partDragged;
	private int relX, relY;
	@Override
	public void mouseClicked(MouseEvent evn) {
		if (info.equals(shownInfo)) {
			if (evn.getButton()==3) {
				for (Part p: RobotEmulator.parts) {
					//System.out.println(p.bound);
					if (p.bound.contains(evn.getPoint())) {
						System.out.println(evn.getPoint());
						JPanel n = new JPanel();
						JPanel pa = new JPanel();
						p.getProperties(pa);
						n.add(pa);
						showPref(n);
						selectedPart = p;
						break;
					}
				}
				repaint();
			}
			else if (evn.getButton()==1) {
				boolean flag = true;
				for (Part p: RobotEmulator.parts) {
					if (p.bound.contains(evn.getPoint())) {
						selectedPart = p;
						flag = false;
						break;
					}
				}
				if (flag)
					selectedPart=null;
				repaint();
			}
		}
		else if (partConnecting!=null && evn.getButton()==1) {
			boolean flag = true;
			for (Part p: RobotEmulator.parts) {
				if (p.bound.contains(evn.getPoint())) {
					if (p.children.size()<p.maxChildren&&p.usePower) {
						partConnecting.setConnectParent(p);
						p.children.add(partConnecting);
						flag = false;
						break;
					}
				}
			}
			if (flag) {
				partConnecting.setConnectParent(null);
			}
			Window.changes = true;
			partConnecting = null;
		}
	}

	@Override
	public void mouseEntered(MouseEvent evn) {
		
	}

	@Override
	public void mouseExited(MouseEvent evn) {
		
	}

	@Override
	public void mousePressed(MouseEvent evn) {
		if (evn.getButton()==1) {
			for (Part p: RobotEmulator.parts) {
				if (p.bound.contains(evn.getPoint())) {
					partDragged = p;
					relX = evn.getPoint().x-p.getX();
					relY = evn.getPoint().y-p.getY();
					break;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent evn) {
		if (evn.getButton()==1) {
			partDragged = null;
		}
	}

	@Override
	public void mouseDragged(MouseEvent evn) {
		if (partDragged!=null) {
			Window.changes = true;
			partDragged.setX(evn.getPoint().x - relX);
			partDragged.setY(evn.getPoint().y - relY);
			if (partDragged.getX()+partDragged.getWidth()+1>draw.getSize().width)
				partDragged.setX(draw.getSize().width-partDragged.getWidth()-1);
			if (partDragged.getY()+partDragged.getHeight()+1>draw.getSize().height)
				partDragged.setY(draw.getSize().height-partDragged.getHeight()-1);
			if (partDragged.getX()+1<0) 
				partDragged.setX(1);
			if (partDragged.getY()+1<0) 
				partDragged.setY(1);
			
		}

		repaint();
	}
	
	public static void setButtonTriggerKey (JButton b, int key) {
		b.registerKeyboardAction(b.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),KeyStroke.getKeyStroke(key, 0, false), JComponent.WHEN_IN_FOCUSED_WINDOW);
		b.registerKeyboardAction(b.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),KeyStroke.getKeyStroke(key, 0, true), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
	}

	@Override
	public void mouseMoved(MouseEvent evn) {
		repaint();
	}

	private int addX=30;
	@Override
	public void actionPerformed(ActionEvent evn) {
		if (evn.getActionCommand().equals("addPart")) {
			if (partTypes.getSelectedIndex()==0) {
				new MotorPart(addX,addX);
			}
			if (partTypes.getSelectedIndex()==1) {
				new RelayPart(addX,addX);
			}
			if (partTypes.getSelectedIndex()==2) {
				new LightPart(addX,addX);
			}
			if (partTypes.getSelectedIndex()==3) {
				new LimitSwitchPart(addX,addX);
			}
			changes = true;
			addX+=10;
			if (addX>100)
				addX=30;
		}
		else if (evn.getActionCommand().equals("enableRobot")) {
			DriverStation.instance.InDisabled(false);
			System.out.println("Enabling");
		}
		else if (evn.getActionCommand().equals("disableRobot")) {
			DriverStation.instance.InDisabled(true);
			System.out.println("Disabling");
		}
		else if (evn.getActionCommand().equals("teleMode")) {
			DriverStation.instance.InOperatorControl(true);
			DriverStation.instance.InDisabled(true);
			disable.setSelected(true);
			System.out.println("Switched to teleop, Disabled");
		}
		else if (evn.getActionCommand().equals("autoMode")) {
			DriverStation.instance.InOperatorControl(true);
			DriverStation.instance.InDisabled(true);
			disable.setSelected(true);
			System.out.println("Switched to auto, Disabled");
		}
		else if (evn.getActionCommand().equals("save"))
			SaveManager.instance.callSaveData();
		else if (evn.getActionCommand().equals("saveAs"))
			SaveManager.instance.callSaveDataAs();
		else if (evn.getActionCommand().equals("open"))
			openFile();
		else if (evn.getActionCommand().equals("close")) {
			closeWindow();
		}
		else if (evn.getActionCommand().equals("new")) {
			openNew();
		}
		//System.out.println(evn.getActionCommand());
		repaint();
	}
	
	public void closeWindow() {
		boolean flag = !changes;
		if (changes) {
			Object[] opts = {"Save and Close","Close Without Saving","Cancle"};
			int save = JOptionPane.showOptionDialog(this, "Do you want to save before exiting?","Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
			if (save==0) {
				if (SaveManager.instance.callSaveData()){
					flag = true;
				}
			}
			else if (save==1) {
				flag = true;
			}
		}
		if (flag) {
			this.dispose();
			System.exit(0);
		}
	}
	
	public void openFile() {
		boolean flag = !changes;
		if (changes) {
			Object[] opts = {"Save and Open File","Open File Without Saving","Cancle"};
			int save = JOptionPane.showOptionDialog(this, "Do you want to save before opening a file?","Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
			if (save==0) {
				if (SaveManager.instance.callSaveData()){
					flag = true;
				}
			}
			else if (save==1) {
				flag = true;
			}
		}
		
		if (flag) {
			SaveManager.instance.callLoadData();
		}
	}
	
	public void openNew() {
		boolean flag = !changes;
		if (changes) {
			Object[] opts = {"Save and Open New","Open New Without Saving","Cancle"};
			int save = JOptionPane.showOptionDialog(this, "Do you want to save before opening a new file?","Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
			if (save==0) {
				if (SaveManager.instance.callSaveData()){
					flag = true;
				}
			}
			else if (save==1) {
				flag = true;
			}
		}
		if (flag) {
			RobotEmulator.parts = new ArrayList<Part>();
			RobotEmulator.window.partConnecting = null;
			RobotEmulator.window.closePref();
			DriverStation.instance.InDisabled(true);
			DriverStation.instance.InOperatorControl(true);
			Window.selectedPart = null;
			changes = false;
		}
	}
}
