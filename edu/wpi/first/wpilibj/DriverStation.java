/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2012. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.DriverStationOrig.Alliance;
import edu.wpi.first.wpilibj.communication.*;
import edu.wpi.first.wpilibj.parsing.IInputOutput;

/**
 * Provide access to the network communication data to / from the Driver Station.
 */
public class DriverStation{
	/**
     * The size of the user control data
     */
    public static final int USER_CONTROL_DATA_SIZE = FRCControl.USER_CONTROL_DATA_SIZE;
    /**
     * The size of the user status data
     */
    public static final int USER_STATUS_DATA_SIZE = FRCControl.USER_STATUS_DATA_SIZE;
    /**
     * Slot for the analog module to read the battery
     */
    public static final int kBatterySlot = 1;
    /**
     * Analog channel to read the battery
     */
    public static final int kBatteryChannel = 8;
    /**
     * Number of Joystick Ports
     */
    public static final int kJoystickPorts = 4;
    /**
     * Number of Joystick Axes
     */
    public static final int kJoystickAxes = 6;
    /**
     * Convert from raw values to volts
     */
    public static final double kDSAnalogInScaling = 5.0 / 1023.0;

    /**
     * The robot alliance that the robot is a part of
     */
    public static class Alliance {

        /** The integer value representing this enumeration. */
        public final int value;
        /** The Alliance name. */
        public final String name;
        public static final int kRed_val = 0;
        public static final int kBlue_val = 1;
        public static final int kInvalid_val = 2;
        /** alliance: Red */
        public static final Alliance kRed = new Alliance(kRed_val, "Red");
        /** alliance: Blue */
        public static final Alliance kBlue = new Alliance(kBlue_val, "Blue");
        /** alliance: Invalid */
        public static final Alliance kInvalid = new Alliance(kInvalid_val, "invalid");

        private Alliance(int value, String name) {
            this.value = value;
            this.name = name;
        }
    } /* Alliance */
    
    
    private boolean m_userInDisabled = false;
    private boolean m_userInAutonomous = false;
    private boolean m_userInTeleop = false;
    private boolean m_userInTest = false;
    private boolean m_inDisabled = true;
    private boolean m_inAutonomous = false;
    private boolean m_inTeleop = false;
    private boolean m_inTest = false;
    private boolean m_newControlData;
	private boolean m_userInTestMode;
	
	private long timeStamp=-1;
	
	
	
	public static DriverStation instance = new DriverStation();
    
    /**
     * Gets a value indicating whether the Driver Station requires the
     * robot to be enabled.
     *
     * @return True if the robot is enabled, false otherwise.
     */
	
	public static DriverStation getInstance() {
		return instance;
	}
	
	/**
     * Return the approximate match time
     * The FMS does not currently send the official match time to the robots
     * This returns the time since the enable signal sent from the Driver Station
     * At the beginning of autonomous, the time is reset to 0.0 seconds
     * At the beginning of teleop, the time is reset to +15.0 seconds
     * If the robot is disabled, this returns 0.0 seconds
     * Warning: This is not an official time (so it cannot be used to argue with referees)
     * @return Match time in seconds since the beginning of autonomous
     */
    public double getMatchTime() {
        if (timeStamp ==-1) {
            return 0;
        }
        return ((float)(System.currentTimeMillis()-timeStamp))/1000f;
    }
	
    public boolean isEnabled() {
        return !m_inDisabled;
    }

    public boolean isNewControlData() {
    	if (m_newControlData) {
    		m_newControlData = false;
    		return true;
    	}
    	return false;
    }
    
    public void setNewControlData(boolean data) {
    	m_newControlData = data;
    }
    
    /**
     * Gets a value indicating whether the Driver Station requires the
     * robot to be disabled.
     *
     * @return True if the robot should be disabled, false otherwise.
     */
    public boolean isDisabled() {
        return m_inDisabled;
    }

    /**
     * Gets a value indicating whether the Driver Station requires the
     * robot to be running in autonomous mode.
     *
     * @return True if autonomous mode should be enabled, false otherwise.
     */
    public boolean isAutonomous() {
        return m_inAutonomous;
    }
    
    /**
     * Gets a value indicating whether the Driver Station requires the
     * robot to be running in test mode.
     * @return True if test mode should be enabled, false otherwise.
     */
    public boolean isTest() {
        return m_inTest;
    }
    
    public boolean isOperatorControl() {
        return !(isAutonomous() || isTest());
    }
    
    
    /** Only to be used to tell the Driver Station what code you claim to be executing
	 *   for diagnostic purposes only
	 * @param entering If true, starting disabled code; if false, leaving disabled code */
	public void InDisabled(boolean entering) {m_inDisabled=entering;}

        /** Only to be used to tell the Driver Station what code you claim to be executing
	 *   for diagnostic purposes only
	 * @param entering If true, starting autonomous code; if false, leaving autonomous code */
	public void InAutonomous(boolean entering) {m_inAutonomous=entering;}
	
        /** Only to be used to tell the Driver Station what code you claim to be executing
	 *   for diagnostic purposes only
	 * @param entering If true, starting teleop code; if false, leaving teleop code */
	public void InOperatorControl(boolean entering) {m_inTeleop=entering;}
        
        /** Only to be used to tell the Driver Station what code you claim to be executing
         *   for diagnostic purposes only
         * @param entering If true, starting test code; if false, leaving test code */
        public void InTest(boolean entering) {m_inTeleop = entering; }
    
}
