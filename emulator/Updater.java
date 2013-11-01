package emulator;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class Updater {
	private static double version = 1.0;
	//private static boolean eclipseFlag = false;
	//private static boolean netbeansFlag = false;
	//private static boolean updateFound = true;
	
	public static void checkUpdate(boolean isAuto) {
		System.out.println("Checking for an update");
		String vers = "https://dl.dropboxusercontent.com/u/54680365/frcEmulator/versions.list";
		String dl = "https://dl.dropboxusercontent.com/u/54680365/frcEmulator/frcEmulator.jar";
		boolean doUpdate = false;
		String totalPatch = "";
		double newVers = 0;
		try {
			BufferedReader vIn = new BufferedReader(new InputStreamReader( new URL(vers).openStream()));
			String l = vIn.readLine();
			while(l!=null) {
				if(l.startsWith("version ")) {
					double tempVer = Double.valueOf(l.substring(8));
					if (tempVer > version) {
						if (tempVer>newVers)
							newVers = tempVer;
						doUpdate = true;
						totalPatch+="\nVersion "+tempVer+"\n";
						l = vIn.readLine();
						String notes = "";
						if (l!=null && l.startsWith("patch ")) {
							notes = l.substring(6);
							totalPatch+=notes+"\n";
						} else if (l!=null) {
							continue;
						}
					}
				}
				l = vIn.readLine();
			}
			vIn.close();
		} catch (Exception e) {
			System.err.println("Could not connect to update server.");
		}
		
		if (doUpdate) {
			JPanel update = new JPanel();
			update.setSize(200, 300);
			update.setLayout(new BoxLayout(update,BoxLayout.Y_AXIS));
			update.add(new JLabel("A new update is avalible to Version "+newVers));
			update.add(new JLabel("patch notes:"));
				JTextArea notes = new JTextArea();
					notes.setText(totalPatch);
				JScrollPane scrollNotes = new JScrollPane(notes);
				scrollNotes.setPreferredSize(new Dimension(200,200));
				scrollNotes.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				scrollNotes.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			update.add(scrollNotes);
			if (isAuto) {
				update.add(new JLabel("(You can turn off automatic update checks in the update menu)"));
			}
			update.add(new JLabel("Do you want to update?"));
			int val = JOptionPane.showConfirmDialog(null,update,"An Update Is Avalible",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE);
			if (val == 0) {
			totalPatch = totalPatch.replace("\\n", "\n");
			System.out.println("Update avalible:\n"+totalPatch);
			String path = "";
			boolean eclipseFlag = false;
			boolean netbeansFlag = false;
			try {
				File classpath = new File(".classpath");
				BufferedReader in =  new BufferedReader(new FileReader(classpath));
				eclipseFlag = true;
				System.out.println("Eclipse Detected");
				String l = in.readLine();
				while (l!=null) {
					if (l.matches("\t<classpathentry kind=\"lib\" path\".*?frcEmulator.jar\"/?>")) {
						if (l.charAt(l.length()-2)=='/')
							path = l.substring(34,l.length()-3);
						else
							path = l.substring(34,l.length()-2);
						System.out.println("Found path of emulator jar at "+path);
						break;
					}
					l = in.readLine();
				}
				in.close();
			} catch (FileNotFoundException e) {
				eclipseFlag = false;
				//e.printStackTrace();
			} catch (IOException e) {
				eclipseFlag = false;
				System.err.println("Unable to read eclipse .classpath file");
				//e.printStackTrace();
			}
			
			if (!eclipseFlag) {
				try {
					File project = new File("nbproject/project.properties");
					BufferedReader in =  new BufferedReader(new FileReader(project));
					netbeansFlag = true;
					System.out.println("Netbeans Detected");
					String l = in.readLine();
					while (l!=null) {
						if (l.matches("file.reference.frcEmulator.jar=.*?frcEmulator.jar")) {
							path = l.substring(31);
							System.out.println("Found path of emulator jar at "+path);
							break;
						}
						l = in.readLine();
					}
					in.close();
				} catch (FileNotFoundException e) {
					netbeansFlag = false;
					//e.printStackTrace();
				} catch (IOException e) {
					netbeansFlag = false;
					System.err.println("Unable to read netbeans project.properties file");
					//e.printStackTrace();
				}	
			}
			
			if (!path.equals("") && (netbeansFlag || eclipseFlag)) {
				
					try {
						System.out.println("Downloading Update");
						URL website = new URL(dl);
						ReadableByteChannel rbc = Channels.newChannel(website.openStream());
						FileOutputStream fos = new FileOutputStream(path);
						fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
						JOptionPane.showMessageDialog(null, "The update has been download.\nPlease reboot the emulator.", "Download Successful", JOptionPane.INFORMATION_MESSAGE);
						
					} catch (IOException e) {
						System.err.println("Unable to connect to the update servers");
						e.printStackTrace();
					}
				
			}
			else {
				System.err.println("Unable to find frcEmulator.jar, cannot update. Please redownload the jar.");
				JOptionPane.showMessageDialog(null, "The update failed to download.\nCheck your internet connection and your project settings.", "Download Failed", JOptionPane.ERROR_MESSAGE);
				
			}
		}
		}
		
	}
}
