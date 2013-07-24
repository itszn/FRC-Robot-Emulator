package emulator;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;

public class TestBot extends IterativeRobot{
	Jaguar motor;
	double speed = 0;
	public TestBot() {
		motor = new Jaguar(1);
		this.teleopInit();
	}
	
	public void teleopInit() {
		
	}
	
	public void teleopPeriodic() {
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
