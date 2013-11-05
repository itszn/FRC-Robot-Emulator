package emulator;

public class Utils {
	public static double truncate(double num, int dec) {
		return ((int)(num*Math.pow(10, dec))/Math.pow(10, dec));
	}
	
	public static double truncate(double num) {
		return ((int)(num*Math.pow(10, RobotEmulator.maxDec))/Math.pow(10, RobotEmulator.maxDec));
	}
}
