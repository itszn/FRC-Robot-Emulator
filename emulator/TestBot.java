package emulator;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;

public class TestBot extends IterativeRobot{
	Jaguar motor;
	Relay relay;
	double speed = 0;
	public TestBot() {
		motor = new Jaguar(1);
		relay = new Relay(1);
		this.teleopInit();
	}
	
	public void disabledInit() {
		super.disabledInit();
		motor.set(0);
		relay.set(Value.kOff);
		speed = 0;
	}
	
	public void teleopInit() {
		super.teleopInit();
		motor.set(0);
		relay.set(Value.kReverse);
	}
	
	public void teleopPeriodic() {
		super.teleopPeriodic();
		motor.set(speed);
		if (speed<=1)
			speed += 1d/50d;
		else
			speed=1;
	}
	
	public void setMotor(double speed) {
		motor.set(speed);
	}
}
