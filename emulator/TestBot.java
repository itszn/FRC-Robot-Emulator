package emulator;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.microedition.io.Connector;

import com.sun.squawk.microedition.io.FileConnection;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;

public class TestBot extends IterativeRobot{
	Jaguar motorL;
	Jaguar motorR;
	Relay relay;
	DigitalInput limitSwitch;
	double speed = 0;
	public TestBot() {
		motorL = new Jaguar(1);
		motorR = new Jaguar(2);
		relay = new Relay(1);
		limitSwitch = new DigitalInput(1);
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
		motorL.set(speed);
		motorR.set(-speed);
		if (!limitSwitch.get()) {
			if (speed<=1)
				speed -= 1d/50d;
			else
				speed=-1;
		}
		else
			speed=0;
	}
	
	public void setMotor(double speed) {
		motorL.set(speed);
		motorR.set(-speed);
	}
}
