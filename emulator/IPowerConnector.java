package emulator;

public interface IPowerConnector {
	public PowerConnector getPowerConnector();
	public PowerConnector getParentPowerConnector();
	
	public int outPuttingPower(Part p);
}
