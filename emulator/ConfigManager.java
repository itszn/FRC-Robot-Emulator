package emulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ConfigManager {
	public static boolean saveConfig() {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(new File("emulator.cfg")));
			out.println("autoUpdate: "+RobotEmulator.autoUpdate);
			out.println("defaultClass: "+RobotEmulator.defaultBot);
			out.println("defaultLoadFile: "+RobotEmulator.defaultLoadFile);
			
			out.close();
			System.out.println("Saved config");
		} catch (IOException e) {
			System.err.println("Could not save to config file.");
			return false;
			//e.printStackTrace();
		}
		return true;
	}
	
	public static boolean loadConfig() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File("emulator.cfg")));
			String l = in.readLine();
			try {
				while (l!=null && !l.equals("")) {
					if (l.startsWith("autoUpdate: ")) {
						RobotEmulator.autoUpdate = Boolean.valueOf(l.split(" ")[1]);
					} else if(l.startsWith("defaultClass: ")) {
						RobotEmulator.defaultBot = l.split(" ")[1];
					}
					else if (l.startsWith("defaultLoadFile: ")) {
						RobotEmulator.defaultLoadFile = l.substring(17);
					}
					l = in.readLine();
				}
			} catch(Exception e) {
				in.close();
				if (e instanceof FileNotFoundException) {
					saveConfig();
					System.err.println("No config existed, creating one.");
				}
				else {
					System.err.println("Error when reading config, using defaults");
				}
				return false;
			}
			in.close();
			System.out.println("Loaded config");
		
		} catch (IOException e) {
			System.err.println("No config existed, creating one.");
			saveConfig();
			return false;
			//e.printStackTrace();
		}
		return true;
	}
}
