/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team1277.robot;

/**
 *
 * @author roboclub
 */
public class CameraMotor {
    
    public static double angleY = 0;
    public static double angleX = 0;
    public static double speed = 1d;
    
    public static void setAngle(double inAngleY, double inAngleX) {
        
        if (inAngleY>=0 && inAngleY<=90) {
            angleY=inAngleY;
        }
        else if(inAngleY<0) {
            angleY=0;
        }
        else if(inAngleY>90) {
            angleY=90;
        }
        if (inAngleX>=0 && inAngleX<=180) {
            angleX=inAngleX;
        }
        else if(inAngleX<0) {
            angleX=0;
        }
        else if(inAngleX>180) {
            angleX=90;
        }
        MainRobot.cameraServoX.setAngle(angleX);
        MainRobot.cameraServoY.setAngle(angleY);
    }
    
    public static void updateAngle() { 
        speed = MainRobot.leftStick.getThrottle() + 1d;
        if(angleY > 0) {
            if(MainRobot.leftStick.getRawButton(MainRobot.BUTTON_CAMERA_SERVO_DOWN))
            {
                angleY-=speed;
                MainRobot.cameraServoY.setAngle(angleY);
            }
        }
        if(angleY < 90) {
            if(MainRobot.leftStick.getRawButton(MainRobot.BUTTON_CAMERA_SERVO_UP))
            {
                angleY+=speed;
                MainRobot.cameraServoY.setAngle(angleY);
            }
        }
        if(angleX > 0) {
            if(MainRobot.leftStick.getRawButton(MainRobot.BUTTON_CAMERA_SERVO_LEFT))
            {
                angleX-=speed;
                MainRobot.cameraServoX.setAngle(angleX);
            }
        }
        if(angleX < 180) {
            if(MainRobot.leftStick.getRawButton(MainRobot.BUTTON_CAMERA_SERVO_RIGHT))
            {
                angleX+=speed;
                MainRobot.cameraServoX.setAngle(angleX);
            }
        }
    }
        
}