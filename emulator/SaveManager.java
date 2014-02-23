package emulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import edu.wpi.first.wpilibj.DriverStation;

public class SaveManager{
	public static SaveManager instance = new SaveManager();
	public static String currentFile = "";
	
	public static void initSave() {
		System.out.println("Save Manager Loaded");
	}
	
	public boolean callSaveData() {
		if (Window.saveFile==null)
			return callSaveDataAs();
		else
			return saveToFile(Window.saveFile);
	}
	
	public boolean callSaveDataAs() {
		JFileChooser cho = new JFileChooser();
		cho.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter emu = new EmulatorFileFilter();
		cho.addChoosableFileFilter(emu);
		cho.setFileFilter(emu);
		cho.setSelectedFile(new File("*.emu"));
		//cho.setAcceptAllFileFilterUsed(false);
		int choice = cho.showSaveDialog(RobotEmulator.window);
		System.out.println(choice);
		if (choice == JFileChooser.APPROVE_OPTION) {
			File f = cho.getSelectedFile();
			String ext = getExtension(f);
			if (ext==null||!ext.equals("emu"))
				f = new File(f.getAbsolutePath()+".emu");
			
			return saveToFile(f);
		}
		return false;
	}
	
	public void saveToFileSer(File f) {
		try {
			FileOutputStream fo = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fo);
			for (Part p: RobotEmulator.parts) {
				out.writeObject(p);
			}
			out.close();
			fo.flush();
			fo.close();
			System.out.println("Saved to "+f);
			Window.saveFile = f;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean saveToFile(File f) {
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(f));
			pw.println("<version "+Updater.version+">");
			for (Part p: RobotEmulator.parts) {
				pw.println("<newPart>");
				UUID id =p.uuid;
				pw.println(id.getMostSignificantBits());
				pw.println(id.getLeastSignificantBits());
				pw.println(p.getClass());
				if (p instanceof IPowerConnector) {
					if (((IPowerConnector)p).getPowerConnector().powerParent==null){
						pw.println(false);
					}
					else {
						pw.println(true);
						pw.println(((IPowerConnector)p).getPowerConnector().powerParent.uuid.getMostSignificantBits());
						pw.println(((IPowerConnector)p).getPowerConnector().powerParent.uuid.getLeastSignificantBits());
					}
					pw.println(((IPowerConnector)p).getPowerConnector().autoPower);
				}
				if (p instanceof IMotorConnector) {
					if (((IMotorConnector)p).getMotorConnector().motorParent==null){
						pw.println(false);
					}
					else {
						pw.println(true);
						pw.println(((IMotorConnector)p).getMotorConnector().motorParent.uuid.getMostSignificantBits());
						pw.println(((IMotorConnector)p).getMotorConnector().motorParent.uuid.getLeastSignificantBits());
					}
				}
				pw.println(p.x+"\n"+p.y+"\n"+p.width+"\n"+p.height);
				
				if (p instanceof MotorPart) {
					pw.println(((MotorPart) p).channel);
					pw.println(((MotorPart) p).maxSpeed);
				}
				if (p instanceof RelayPart) {
					pw.println(((RelayPart) p).channel);
				}
				if (p instanceof LimitSwitchPart) {
					pw.println(((LimitSwitchPart) p).channel);
					pw.println(((LimitSwitchPart) p).activated);
				}
				if (p instanceof PotentiometerPart) {
					pw.println(((PotentiometerPart) p).channel);
					pw.println(((PotentiometerPart) p).currentPos);
					pw.println(((PotentiometerPart) p).gearRatio);
					pw.println(((PotentiometerPart) p).turns);
				}
				if (p instanceof TachometerPart) {
					pw.println(((TachometerPart) p).channel);
				}
				if (p instanceof RangeFinderPart) {
					pw.println(((RangeFinderPart) p).channel);
					pw.println(((RangeFinderPart) p).distance);
				}
				pw.println(p.id);
			}
			pw.flush();
			pw.close();
			System.out.println("Saved to "+f);
			Window.saveFile = f;
			Window.changes = false;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Unable to save to "+f);
			return false;
		}
		
		
	}
	
	public boolean callLoadData() {
		JFileChooser cho = new JFileChooser();
		cho.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter emu = new EmulatorFileFilter();
		cho.addChoosableFileFilter(emu);
		cho.setFileFilter(emu);
		cho.setSelectedFile(new File("*.emu"));
		cho.setAcceptAllFileFilterUsed(false);
		int choice = cho.showOpenDialog(RobotEmulator.window);
		System.out.println(choice);
		if (choice == JFileChooser.APPROVE_OPTION) {
			File f = cho.getSelectedFile();
			String ext = getExtension(f);
			if (ext!=null&&ext.equals("emu")) {
				RobotEmulator.window.clearAll();
				return openFile(f);
			}
		}
		return false;
	}
	
	public boolean openFile(File f) {
		try {
			currentFile = f.getAbsolutePath();
			BufferedReader br = new BufferedReader(new FileReader(f));
			ArrayList<Part> tempParts = new ArrayList<Part>();
			String ln=" ";
			double version = 0;
			boolean skip = false;
			while(ln!=null) {
				if (!skip)
					ln = br.readLine();
				if (ln==null)
					break;
				skip = false;
				if (ln.startsWith("<version ")) {
					version = Double.valueOf(ln.substring(9,ln.length()-1));
				}
				if (ln.equals("<newPart>")) {
					Part p = null;
					long id1 = Long.valueOf(br.readLine());
					long id2 = Long.valueOf(br.readLine());
					UUID id = new UUID(id1,id2);
					Class c = Class.forName(br.readLine().split(" ")[1]);
					UUID parentId = null;
					boolean autoPower = false;
					System.out.println(IPowerConnector.class.isAssignableFrom(c));
					if ((IPowerConnector.class.isAssignableFrom(c))) {
						if(Boolean.valueOf(br.readLine())){
							id1 = Long.valueOf(br.readLine());
							id2 = Long.valueOf(br.readLine());
							parentId = new UUID(id1,id2);
						}
						autoPower = Boolean.valueOf(br.readLine());
					}
					if ((IMotorConnector.class.isAssignableFrom(c))) {
						if (Boolean.valueOf(br.readLine())) {
							id1 = Long.valueOf(br.readLine());
							id2 = Long.valueOf(br.readLine());
							parentId = new UUID(id1,id2);
						}
					}
					int x = Integer.valueOf(br.readLine());
					int y = Integer.valueOf(br.readLine());
					int width = Integer.valueOf(br.readLine());
					int height = Integer.valueOf(br.readLine());
					if (c.equals(MotorPart.class)) {
						p = new MotorPart(x,y/*,width,height*/);
						((MotorPart)p).channel = Integer.valueOf(br.readLine());
						((MotorPart)p).maxSpeed = Double.valueOf(br.readLine());
					}
					if (c.equals(RelayPart.class)) {
						p = new RelayPart(x,y/*,width,height*/);
						((RelayPart)p).channel = Integer.valueOf(br.readLine());
					}
					if (c.equals(LightPart.class)) {
						p = new LightPart(x,y/*,width,height*/);
					}
					if (c.equals(LimitSwitchPart.class)) {
						p = new LimitSwitchPart(x,y/*,width,height*/);
						((LimitSwitchPart)p).channel = Integer.valueOf(br.readLine());
						((LimitSwitchPart)p).activated = Boolean.valueOf(br.readLine());
					}
					if (c.equals(PotentiometerPart.class)) {
						p = new PotentiometerPart(x,y);
						((PotentiometerPart)p).channel = Integer.valueOf(br.readLine());
						((PotentiometerPart)p).currentPos = Double.valueOf(br.readLine());
						((PotentiometerPart)p).gearRatio = Double.valueOf(br.readLine());
						((PotentiometerPart)p).turns = Integer.valueOf(br.readLine());
					}
					if (c.equals(TachometerPart.class)) {
						p = new TachometerPart(x,y);
						((TachometerPart)p).channel = Integer.valueOf(br.readLine());
					}
					if (c.equals(RangeFinderPart.class)) {
						p = new RangeFinderPart(x,y);
						((RangeFinderPart)p).channel = Integer.valueOf(br.readLine());
						((RangeFinderPart)p).distance = Double.valueOf(br.readLine());
					}
					p.uuid = id;
					p.tempParentUUID = parentId;
					
					//1.40 versioning:
					if (version>=1.40) {
						p.id = br.readLine();
					}
					
					if (p instanceof IPowerConnector)
					((IPowerConnector)p).getPowerConnector().autoPower = autoPower;
					tempParts.add(p);
				}
			}
			br.close();
			for (Part p: tempParts) {
				Part cparent = null;
				if (p instanceof IPowerConnector || p instanceof IMotorConnector) {
					if (p.tempParentUUID==null)
						cparent=null;
					else {
						Part parent = null;
						for (Part pa: tempParts) {
							if (pa.uuid.equals(p.tempParentUUID)) {
								parent = pa;
								if (pa instanceof IPowerConnector && p instanceof IPowerConnector)
									((IPowerConnector)pa).getPowerConnector().powerChildren.add(p);
							}
						}
						if (parent==null){
							System.err.println("Warning: Could not find specified parent "+p.tempParentUUID+" for part "+p);
							cparent=null;
						}
						else {
							cparent=parent;
						}
					}
					if (p instanceof IPowerConnector) {
						((IPowerConnector)p).getPowerConnector().powerParent = cparent;
					}
					else if (p instanceof IMotorConnector && cparent instanceof MotorPart) {
						((IMotorConnector) p).getMotorConnector().motorParent = (MotorPart)cparent;
					}
				}
			}
			RobotEmulator.parts = tempParts;
			Window.changes = false;
			RobotEmulator.window.partConnecting = null;
			RobotEmulator.window.closePref();
			Window.selectedPart = null;
			DriverStation.instance.InDisabled(true);
			DriverStation.instance.InOperatorControl(true);
			System.out.println("Loaded from "+f);
			Window.saveFile = f;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not read file "+f);
			return false;
		}
	}
	
	public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
 
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
	
	private class EmulatorFileFilter extends FileFilter {
		
		
		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			String ext = getExtension(f);
			if (ext != null) {
				if (ext.equals("emu"))
					return true;
				else
					return false;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "*.emu FRC Robot Emulator Files";
		}
		
	}
}
