package emulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.JOptionPane;

import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class RobotEmulator implements Runnable{
	public static RobotBase robot;
	public static RobotEmulator instance;
	public static ArrayList<Part> parts = new ArrayList<Part>();
	public static int maxDec = 2;
	
	public static boolean autoUpdate = true;
	public static String defaultBot = "";
	
	public static String curClassPath = "";
	
	public static void main (String[] args) {
		ConfigManager.loadConfig();
		while (robot == null) {
			if (args.length == 0) {
				//robot = new TestBot();
				//System.err.println("No class given, defaulting to TestBot");
				args = new String[1];
				args[0] = JOptionPane.showInputDialog(null, "Please enter the classpath for the Main Robot Class:", defaultBot);
				if (args[0]==null) {
					System.exit(1);
				}
			} else {
				try {
					robot = (RobotBase) Class.forName(args[0]).newInstance();
				} catch (ClassNotFoundException e) {
					//throw new RuntimeException("Robot class not found");
					JOptionPane.showMessageDialog(null, "Robot class provided could not be found.", "ClassPath Error", JOptionPane.ERROR_MESSAGE);
				} catch (InstantiationException e) {
					//throw new RuntimeException("Invalid robot constructor. Must contain No parameters");
					JOptionPane.showMessageDialog(null, "Invalid robot constructor. Must contain no parameters.", "ClassPath Constructor Error", JOptionPane.ERROR_MESSAGE);
				} catch (ClassCastException e) {
					//throw new RuntimeException("Robot Class not subclass of edu.wpi.first.wpilibj.RobotBase");
					JOptionPane.showMessageDialog(null, "Robot Class not subclass of edu.wpi.first.wpilibj.RobotBase.", "ClassPath Subclass Error", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "An error was encountered using that classpath", "ClassPath Error", JOptionPane.ERROR_MESSAGE);
				}
				if (robot == null) {
					args = new String[0];
				}
			}
		}
		curClassPath = args[0];
		System.out.println("Starting robot code at "+args[0]);
		System.out.println("Started Emulator Version " + Updater.version);
		SaveManager.initSave();
		instance = new RobotEmulator();
		//window = new Window();
		//new PotentiometerPart(25,25,100,50).channel=1;
		new MotorPart(25,25,100,50).channel=1;
		try {
			robot.startApp();
		} catch (Exception e) {
			e.printStackTrace();
			instance.t = null;
			System.exit(1);
		}
		
		
	}
	
	private Thread t;
	
	private RobotEmulator() {
		t = new Thread(this, "Emulator");
		t.start();
	}
	long lastPeriod;
	public static Window window;
	@Override
	public void run() {
		window = new Window();
		if (autoUpdate)
			Updater.checkUpdate(true);
		Scanner scan = new Scanner(System.in);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		lastPeriod = System.currentTimeMillis();
		Thread thisThread = Thread.currentThread();
		while (thisThread.equals(t)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			for (Part p: parts){
				try {
					p.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			window.update();
			try {
				window.repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String in = "";
			try {
				if (br.ready()) {
					in = br.readLine();
					if (in.equals("teleop")) {
						DriverStation.instance.InOperatorControl(true);
						DriverStation.instance.InDisabled(true);
						System.out.println("Switched to teleop, Disabled");
					}
					if (in.equals("auto")) {
						DriverStation.instance.InOperatorControl(true);
						DriverStation.instance.InDisabled(true);
						System.out.println("Switched to auto, Disabled");
					}
					if (in.equals("enable")) {
						DriverStation.instance.InDisabled(false);
						System.out.println("Enabling");
					}
					if (in.equals("disable")) {
						DriverStation.instance.InDisabled(true);
						System.out.println("Disabling");
					}
					if (in.startsWith("putNumber ")) {
						NetworkTable.table.put(in.split(" ")[1], Double.valueOf(in.split(" ")[2]));
						System.out.println("Set "+in.split(" ")[1]+" to value "+Double.valueOf(in.split(" ")[2]));
					}
					if (in.startsWith("putString ")) {
						NetworkTable.table.put(in.split(" ")[1], in.split(" ")[2]);
						System.out.println("Set "+in.split(" ")[1]+" to value "+in.split(" ")[2]);
					}
					if (in.startsWith("putBoolean ")) {
						NetworkTable.table.put(in.split(" ")[1], Boolean.valueOf(in.split(" ")[2]));
						System.out.println("Set "+in.split(" ")[1]+" to value "+Boolean.valueOf(in.split(" ")[2]));
					}
					if (in.startsWith("get ")) {
						System.out.println(in.split(" ")[1]+": "+NetworkTable.table.get(in.split(" ")[1]));
					}
					if (in.startsWith("pwm ")) {
						System.out.println("PWM "+in.split(" ")[1]+": "+DigitalModule.PWMChannels[Integer.valueOf(in.split(" ")[1])-1]);
					}
					if (in.startsWith("dio ")) {
						System.out.println("DIO "+in.split(" ")[1]+": "+DigitalModule.getInstance(1).getDIO(Integer.valueOf(in.split(" ")[1])));
					}
					if (in.startsWith("relay ")) {
						DigitalModule.RelayInfo rel = DigitalModule.relayChannels[Integer.valueOf(in.split(" ")[1])-1];
						System.out.println("Relay "+in.split(" ")[1]+": "+(rel.fwd?"Forward":(rel.rev?"Reverse":"Off")));
					}
					if (in.startsWith("setm ")) {
						((TestBot) robot).setMotor(Double.valueOf(in.split(" ")[1]));
						System.out.println("Set Motor to "+in.split(" ")[1]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (System.currentTimeMillis()-lastPeriod>20) {
				DriverStation.instance.setNewControlData(true);
				lastPeriod = System.currentTimeMillis();
			}
		}
		System.exit(1);
	}
	
}
