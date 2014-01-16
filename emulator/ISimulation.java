package emulator;

import java.util.List;

import javax.swing.JFrame;

public interface ISimulation {
	
	/**
	 * Gets the JFrame with the canvas. Used for paiting
	 * @return JFrame with the simulation canvas in its contentpane
	 */
	public JFrame getSimulationFrame();

	/**
	 * Update the simulation
	 * @param delta Number of milliseconds since last update
	 */
	public void update (int delta);
	
	/**
	 * Gets list of avalible parts for the simulation.
	 * Parts should have their String id set
	 * @return List of Parts
	 */
	public List<Part> getSimulationParts();
	
	//public 
}
