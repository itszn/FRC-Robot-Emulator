package emulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.microedition.midlet.MIDletStateChangeException;

import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class RobotEmulator implements Runnable{
	public static RobotBase robot;
	public static RobotEmulator instance;
	public static ArrayList<Part> parts = new ArrayList<Part>();
	
	public static boolean autoUpdate = true;
	
	public static void main (String[] args) {
		if (args.length == 0) {
			throw new RuntimeException("No robot class provided as argument");
		}
		try {
			robot = (RobotBase) Class.forName(args[0]).newInstance();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Robot class not found");
		} catch (InstantiationException e) {
			throw new RuntimeException("Invalid robot constructor. Must contain No parameters");
		} catch (ClassCastException e) {
			throw new RuntimeException("Robot Class not subclass of edu.wpi.first.wpilibj.RobotBase");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Starting robot code at "+args[0]);
		instance = new RobotEmulator();
		//window = new Window();
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
			for (Part p: parts)
				p.update();
			window.repaint();
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
