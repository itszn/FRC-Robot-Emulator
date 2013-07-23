/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team1277.robot;

/**
 *
 * @author roboclub
 */
public class DriveTrain {
    /**
     * This function updates the jaguars based on the position of the joysticks
     */
        
    public static void updateDrive(int state) {
        if (state == 1) {
            updateTank();
        }
        else if (state == 2) {
            updateArcade();
        }
        //MainRobot.m_leftDrive.set(.5);
    }
    
    /**
     * Updates for the tank drive
     */
    public static void updateTank() {
        double rightJoyY = MainRobot.rightStick.getY();
        double leftJoyY = MainRobot.leftStick.getY();
        MainRobot.rightDrive.set(rightJoyY*MainRobot.driveSpeed);//m_rightDrive.set(rightJoyY*MainRobot.driveSpeed);
        MainRobot.leftDrive.set(-leftJoyY*MainRobot.driveSpeed);
        
    }
    
    /**
     * Updates for the arcade drive
     */
    public static void updateArcade() {
        double joyY, joyX;
        if (MainRobot.ARCADE_JOYSTICK == 1) {
            joyY = MainRobot.leftStick.getY();
            joyX = MainRobot.leftStick.getX();
        } else if (MainRobot.ARCADE_JOYSTICK == 2) {
            joyY = MainRobot.rightStick.getY();
            joyX = MainRobot.rightStick.getX();
        }
        if (joyX<0) {
            MainRobot.leftDrive.set(-joyY*(1+joyX)*MainRobot.driveSpeed);
            MainRobot.rightDrive.set(joyY*MainRobot.driveSpeed);
        }
        if (joyX>0) {
            MainRobot.rightDrive.set(joyY*(1-joyX)*MainRobot.driveSpeed);
            MainRobot.leftDrive.set(-joyY*MainRobot.driveSpeed);
        }
        if (joyX==0) {
            MainRobot.rightDrive.set(joyY*MainRobot.driveSpeed);
            MainRobot.leftDrive.set(-joyY*MainRobot.driveSpeed);
        }
    }
    
}
