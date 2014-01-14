package emulator;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.microedition.io.Connector;

import com.sun.squawk.microedition.io.FileConnection;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.DriverStationLCD.Line;
import edu.wpi.first.wpilibj.Relay.Value;

public class TestBot extends IterativeRobot{
	Jaguar motorL;
	Jaguar motorR;
	Joystick joy;
	Relay relay;
	DigitalInput limitSwitch;
	Counter tach;
	AnalogChannel anlo;
	AnalogChannel rangeFind;
	double speed = 0;
	public TestBot() {
		motorL = new Jaguar(1);
		motorR = new Jaguar(2);
		relay = new Relay(1);
		limitSwitch = new DigitalInput(1);
		tach = new Counter(1);
		anlo = new AnalogChannel(1);
		joy = new Joystick(1);
		rangeFind = new AnalogChannel(2);
		//this.teleopInit();
	}
	
	public void disabledInit() {
		super.disabledInit();
		motorL.set(0);
		motorR.set(0);
		relay.set(Value.kOff);
		speed = 0;
		
	}
	
	public void teleopInit() {
		super.teleopInit();
		System.out.println("Trying to run");
		motorL.set(0);
		motorR.set(0);
		relay.set(Value.kReverse);
		FileConnection test = (FileConnection) Connector.open("file://" + "src/res/stest.txt");
		
		try {
			test.create();
			OutputStreamWriter writer = new OutputStreamWriter(test.openOutputStream());
			writer.write("test");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void teleopPeriodic() {
		super.teleopPeriodic();
		//System.out.println(joy.getY());
		motorL.set(joy.getX()*speed);
		motorR.set(-speed);
		if (!limitSwitch.get()) {
			if (speed<=1)
				speed += 1d/50d;
			else
				speed=1;
		}
		else
			speed=0;
		//System.out.println(DriverStation.getInstance().getMatchTime());
		//System.out.println(joy.buttons[0][2-1]);
		DriverStationLCD.getInstance().println(Line.kUser1, 1, "1: "+joy.getRawButton(2));
		DriverStationLCD.getInstance().updateLCD();
		//RobotEmulator.window.outputBox.setText("qwertyuiop");
		//RobotEmulator.window.outputBox.setEditable(true);
	}
	
	public void setMotor(double speed) {
		motorL.set(speed);
		motorR.set(-speed);
	}
}
