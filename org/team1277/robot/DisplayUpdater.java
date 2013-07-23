package org.team1277.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class DisplayUpdater
{
	public static void update(NetworkTable table, double length, double angle)
	{
		table.putString("armPosition", String.valueOf(length).concat(",").concat(String.valueOf(-angle)));
	}
	
	public static void update()
	{
		update(MainRobot.server, Climber.rackExtention, Climber.rackPivot);
	}
}