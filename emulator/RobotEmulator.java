package emulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;

import javax.microedition.midlet.MIDletStateChangeException;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;

public class RobotEmulator implements Runnable{
	public static RobotBase robot;
	public static RobotEmulator instance;
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
		// TODO Start GUI for Emulator
		instance = new RobotEmulator();
		try {
			robot.startApp();
		} catch (MIDletStateChangeException e) {
			e.printStackTrace();
		}
	}
	
	private Thread t;
	
	private RobotEmulator() {
		t = new Thread(this, "Emulator");
		t.start();
	}
	long lastPeriod;
	@Override
	public void run() {
		Scanner scan = new Scanner(System.in);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		lastPeriod = System.currentTimeMillis();
		while (t.isAlive()) {
			String in = "";
			try {
				if (br.ready()) {
					in = br.readLine();
					if (in.equals("teleop")) {
						DriverStation.instance.InOperatorControl(true);
						DriverStation.instance.InDisabled(true);
						System.out.println("Switched to teleop, Disabled");
					}
					if (in.equals("enable")) {
						DriverStation.instance.InDisabled(false);
						System.out.println("Enabling");
					}
					if (in.equals("disable")) {
						DriverStation.instance.InDisabled(true);
						System.out.println("Disabling");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (System.currentTimeMillis()-lastPeriod>20) {
				DriverStation.instance.setNewControlData(true);
				lastPeriod = System.currentTimeMillis();
			}
		}
	}
	
}
