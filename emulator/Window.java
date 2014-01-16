package emulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;

import edu.wpi.first.wpilibj.AnalogModule;
import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.DriverStation;

public class Window extends JFrame implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
	//JFrame frame;
	public static File saveFile = null;
	public static boolean changes = false;
	Canvas draw;
	JMenuBar menuBar;
	JMenu fileMenu;
	JMenu updateMenu;
	JMenu configMenu;
	JPanel info;
	JPanel shownInfo;
	JComboBox<String> partTypes;
	ButtonGroup enableGroup, modeGroup;
	JRadioButton disable, enable, tele, auto;
	JCheckBoxMenuItem autoUpdateOption;
	JCheckBox interactModeBox;
	JCheckBox retainStateBox;
	JButton addPartButton;
	JoystickGUI joy1;
	JoystickGUI joy2;
	JPanel simOption;
	
	ArrayList<JFrame> simTabs = new ArrayList<JFrame>();
	
	boolean editMode = true;
	boolean retainMode = false;
	static Part selectedPart = null;
	public Part partConnecting = null;
	public JTextArea outputBox;
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
		menuBar.add(fileMenu);
		updateMenu = new JMenu("Update");
		i = new JMenuItem("Check For Update");
		i.addActionListener(this);
		i.setActionCommand("checkUpdate");
		updateMenu.add(i);
		autoUpdateOption = new JCheckBoxMenuItem("Preform Update Check On Startup");
		autoUpdateOption.setState(RobotEmulator.autoUpdate);
		autoUpdateOption.addActionListener(this);
		autoUpdateOption.setActionCommand("updateCheckbox");
		updateMenu.add(autoUpdateOption);
		menuBar.add(updateMenu);
		configMenu = new JMenu("Config");
		i = new JMenuItem("Set Current Classpath As Default");
		i.addActionListener(this);
		i.setActionCommand("setClassDef");
		configMenu.add(i);
		i = new JMenuItem("Set Current File To Open On Launch");
		i.addActionListener(this);
		i.setActionCommand("setFileDef");
		configMenu.add(i);
		i = new JMenuItem("Set New File To Open On Launch");
		i.addActionListener(this);
		i.setActionCommand("clearFileDef");
		configMenu.add(i);
		menuBar.add(configMenu);
		this.setJMenuBar(menuBar);
		JFrame mainTab = new JFrame();
		this.addKeyListener(this);
		//this.addKeyListener(joy2);
		
		info = new JPanel();
			JPanel n = new JPanel();
				BoxLayout layout = new BoxLayout(n, BoxLayout.Y_AXIS);
				
				n.setLayout(layout);
				String[] types = {"Motor","Relay","Light","Limit Switch","Potentiometer","Tachometer","Range Finder"};
				partTypes = new JComboBox<String>(types);
					partTypes.setSelectedIndex(0);
					partTypes.addActionListener(this);
				n.add(partTypes);
				addPartButton = new JButton("Add Selected");
					addPartButton.addActionListener(this);
					addPartButton.setActionCommand("addPart");
				n.add(addPartButton);
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

				n2 = new JPanel();
					n2.setLayout(new FlowLayout());
					interactModeBox = new JCheckBox("Interact Mode");
					interactModeBox.addActionListener(this);
					interactModeBox.setActionCommand("switchMode");
					n2.add(interactModeBox);
				
					retainStateBox = new JCheckBox("Retain State");
					retainStateBox.addActionListener(this);
					retainStateBox.setActionCommand("retainSwitch");
					retainStateBox.setEnabled(false);
					n2.add(retainStateBox);
					n2.setBorder(BorderFactory.createEtchedBorder());
				n.add(n2);
				n2 = new JPanel();
					n2.setLayout(new FlowLayout());
					n2.setBorder(BorderFactory.createRaisedBevelBorder());
					joy1 = new JoystickGUI(0);
					joy2 = new JoystickGUI(1);
					JPanel n3 = new JPanel();
						n3.add(joy1);
						n3.add(joy1.useKeys);
						joy1.useKeys.addKeyListener(this);
						BoxLayout layout1 = new BoxLayout(n3, BoxLayout.Y_AXIS);
						//joy1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
						//joy1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
						//joy1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
						//joy1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
						//joy1.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, joy1.getInputMap());
						n3.setLayout(layout1);
					n2.add(n3);
					n3 = new JPanel();
						n3.add(joy2);
						n3.add(joy2.useKeys);
						joy2.useKeys.addKeyListener(this);
						BoxLayout layout2 = new BoxLayout(n3, BoxLayout.Y_AXIS);
						n3.setLayout(layout2);
				n2.add(n3);
				n.add(n2);
					outputBox = new JTextArea(6,26);
					outputBox.setEditable(false);
					outputBox.setFocusable(false);
					outputBox.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				
				//n.add(new JLabel("Driver Station LCD"));
				n.add(outputBox);
				info.add(n);
		add(info,BorderLayout.EAST);
		
		draw = new Canvas();
			draw.addMouseListener(this);
			draw.addMouseMotionListener(this);
			draw.setSize(100, 100);
		mainTab.getContentPane().add(draw,BorderLayout.CENTER);
		
		//TODO Search for moduals and add the name and JFrame
		
		JFrame simTab = new JFrame();
		JPanel simOptions = new JPanel();
		
		
		
		JTabbedPane tabs = new JTabbedPane();
		
		tabs.addTab("LayOut", mainTab.getContentPane());
		
		
		
		
		add(tabs,BorderLayout.CENTER);
		setVisible(true);
		
		shownInfo=info;
		//SaveManager.instance.callSaveData();

		//this.setFocusableWindowState(true);
	}
	
	class Canvas extends JPanel{
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.black);
			this.setBackground(Color.white);
			try {
				for(Part p: RobotEmulator.parts){
					p.paint(g);
					g.setColor(Color.black);
				}
			} catch (Exception e) {
			}
			g.drawString("PWM", 1, 11);
			for (int i=1; i<=DigitalModule.kPwmChannels;i++)
				g.drawString(String.valueOf(i), 1, 11+11*i);
			g.drawString("Relay", 1, 132);
			for (int i=1; i<=DigitalModule.kRelayChannels;i++)
				g.drawString(String.valueOf(i), 1, 132+11*i);
			g.drawString("Digital I/O", 1, 231);
			for (int i=1; i<=DigitalModule.kDigitalChannels;i++)
				g.drawString(String.valueOf(i), 1, 231+11*i);
			g.drawString("Analog", 1, 396);
			for (int i=1; i<=AnalogModule.kAnalogChannels;i++)
				g.drawString(String.valueOf(i), 1, 396+11*i);
			
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
		//revalidate();
		invalidate();
		validate();
		
		
		repaint();
	}
	
	public void showPref(JPanel panel) {
		this.remove(shownInfo);
		this.add(panel,BorderLayout.EAST);
		shownInfo = panel;
		//revalidate();
		invalidate();
		validate();
		repaint();
	}
	private Part partDragged;
	private int relX, relY;
	@Override
	public void mouseClicked(MouseEvent evn) {
		if (editMode) {
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
				if (partConnecting instanceof IPowerConnector && ((IPowerConnector)partConnecting).getPowerConnector().powerConnecting) {
					boolean flag = true;
					for (Part p: RobotEmulator.parts) {
						if (p.bound.contains(evn.getPoint())) {
							if (p instanceof IPowerConnector) {
								if (((IPowerConnector)p).getPowerConnector().powerChildren.size()<((IPowerConnector)p).getPowerConnector().maxPowerChildren&&((IPowerConnector)p).getPowerConnector().usePower) {
									((IPowerConnector)partConnecting).getPowerConnector().setPowerConnectParent(p);
									((IPowerConnector)p).getPowerConnector().powerChildren.add(partConnecting);
									flag = false;
									break;
								}
							}
						}
					}
					if (flag) {
						((IPowerConnector)partConnecting).getPowerConnector().setPowerConnectParent(null);
					}
					Window.changes = true;
					partConnecting = null;
				}
				else if (partConnecting instanceof IMotorConnector && ((IMotorConnector)partConnecting).getMotorConnector().motorConnecting) {
					boolean flag = true;
					for (Part p: RobotEmulator.parts) {
						if (p.bound.contains(evn.getPoint())) {
							if (p instanceof MotorPart) {
								((IMotorConnector)partConnecting).getMotorConnector().setMotorConnectParent((MotorPart)p);
								flag = false;
								break;
							}
						}
					}
					if (flag) {
						((IMotorConnector)partConnecting).getMotorConnector().setMotorConnectParent(null);
					}
					Window.changes = true;
					partConnecting = null;
				}
			}
		}
		else  if (evn.getID() == evn.MOUSE_CLICKED){
			for (Part p: RobotEmulator.parts) {
				if (p.interactBound.contains(evn.getPoint())) {
					p.interactMousePressed(evn);
					break;
				}
			}
		}
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent evn) {
		
	}

	@Override
	public void mouseExited(MouseEvent evn) {
		
	}

	@Override
	public void mousePressed(MouseEvent evn) {
		this.setFocusable(true);
		this.requestFocusInWindow();
		if (editMode) {
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
		else  if (evn.getID() == evn.MOUSE_PRESSED){
			for (Part p: RobotEmulator.parts) {
				if (p.interactBound.contains(evn.getPoint())) {
					p.interactMousePressed(evn);

					break;
				}
			}
		}
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent evn) {
		if (editMode) {
			if (evn.getButton()==1) {
				partDragged = null;
			}
		} else  if (evn.getID() == evn.MOUSE_RELEASED){
			for (Part p: RobotEmulator.parts) {
				if (p.interactBound.contains(evn.getPoint())) {
					p.interactMouseReleased(evn);
					break;
				}
			}
		}
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent evn) {
		if (editMode) {
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
		} else if (evn.getID() == evn.MOUSE_DRAGGED){
			for (Part p: RobotEmulator.parts) {
				if (p.interactBound.contains(evn.getPoint())) {
					p.interactMouseDragged(evn);
					break;
				}
			}
		}
		
		repaint();
	}
	
	public static void setButtonTriggerKey (JButton b, int key) {
		b.registerKeyboardAction(b.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),KeyStroke.getKeyStroke(key, 0, false), JComponent.WHEN_IN_FOCUSED_WINDOW);
		b.registerKeyboardAction(b.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),KeyStroke.getKeyStroke(key, 0, true), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	@Override
	public void mouseMoved(MouseEvent evn) {
		if (!editMode && evn.getID() == evn.MOUSE_MOVED) {
			for (Part p: RobotEmulator.parts) {
				if (p.interactBound.contains(evn.getPoint())) {
					p.interactMoveMouse(evn);
					break;
				}
			}
		}
		repaint();
	}

	
	public synchronized void addPart(int i) {
		if (i==0) {
			new MotorPart(addX,addX);
		}
		if (i==1) {
			new RelayPart(addX,addX);
		}
		if (i==2) {
			new LightPart(addX,addX);
		}
		if (i==3) {
			new LimitSwitchPart(addX,addX);
		}
		if (i==4) {
			new PotentiometerPart(addX,addX);
		}
		if (i==5) {
			new TachometerPart(addX,addX);
		}
		if (i==6) {
			new RangeFinderPart(addX,addX);
		}
		changes = true;
		addX+=10;
		if (addX>100)
			addX=30;
	}
	
	private int addX=30;
	@Override
	public void actionPerformed(ActionEvent evn) {
		if (evn.getActionCommand().equals("addPart")) {
			addPart(partTypes.getSelectedIndex());
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
			DriverStation.instance.InOperatorControl(false);
			//TODO Fix switch to auto
			DriverStation.instance.InDisabled(true);
			disable.setSelected(true);
			System.out.println("Switched to auto, Disabled");
		}
		else if (evn.getActionCommand().equals("setClassDef")) {
			RobotEmulator.defaultBot = RobotEmulator.curClassPath;
			ConfigManager.saveConfig();
		}
		else if (evn.getActionCommand().equals("switchMode")) {
			editMode=!interactModeBox.isSelected();
			retainStateBox.setEnabled(!editMode);
			addPartButton.setEnabled(editMode);
		}
		else if (evn.getActionCommand().equals("retainSwitch")) {
			retainMode = retainStateBox.isSelected();
			System.out.println(retainMode);
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
		else if (evn.getActionCommand().equals("checkUpdate")) {
			Updater.checkUpdate(false);
		}
		else if (evn.getActionCommand().equals("updateCheckbox")) {
			RobotEmulator.autoUpdate = autoUpdateOption.getState();
			ConfigManager.saveConfig();
		}
		else if (evn.getActionCommand().equals("setFileDef")) {
			RobotEmulator.defaultLoadFile = SaveManager.currentFile;
			ConfigManager.saveConfig();
			System.out.println("Test");
		}
		else if (evn.getActionCommand().equals("clearFileDef")) {
			RobotEmulator.defaultLoadFile = "";
			ConfigManager.saveConfig();
		}
		//System.out.println(evn.getActionCommand());
		repaint();
	}
	
	public void closeWindow() {
		boolean flag = !changes;
		if (changes) {
			Object[] opts = {"Save and Close","Close Without Saving","Cancel"};
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
	
	public void clearAll() {
		RobotEmulator.parts = new ArrayList<Part>();
		RobotEmulator.window.partConnecting = null;
		RobotEmulator.window.closePref();
		DriverStation.instance.InDisabled(true);
		DriverStation.instance.InOperatorControl(true);
		Window.selectedPart = null;
		changes = false;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		joy1.keyPressed(arg0);
		joy2.keyPressed(arg0);

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		joy1.keyReleased(arg0);
		joy2.keyReleased(arg0);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		joy1.keyTyped(arg0);
		joy2.keyTyped(arg0);
	}
	
	public void update() {
		joy1.update();
		joy2.update();
	}
}
