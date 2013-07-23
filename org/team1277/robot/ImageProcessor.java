/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team1277.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.TableKeyNotDefinedException;


/**
 *
 * @author roboclub
 */
public class ImageProcessor {
    
    //public static NetworkTable server;
    public static double x = 0;
    public static double y = 0;
    public static double targetX = 339;
    public static double targetY = 241;
    public static double rangeX = 60;
    public static double rangeY = 80;
    public static boolean turnCamera = false;
    public static boolean turnRobot = true;
    public static double width = 0;
    public static double distance = 0;
    
    
    
    public static void Process(NetworkTable server) {
        
        
        try
        {
            x=0;
            y=0;
            width = 0;
            if(server.containsKey("X") && server.containsKey("Y"))
            {
                
                x = server.getNumber("X");
                y = server.getNumber("Y");
            }
            if(server.containsKey("width"))
            {
                width = server.getNumber("width");
                distance = (-3.0D/250.0D)*width+(48.0D/5.0D);
                System.out.println(width);
                System.out.println(distance);
            }
            
        }
        catch (TableKeyNotDefinedException ex)
        {
            
            ex.printStackTrace();
        }
    }
    
    public static void Track() {
        if (x!=0&&y!=0)
        {
            if (Math.abs(x-targetX)>rangeX)
            {
                if (x-targetX>0)
                {
                    if (turnCamera&&!turnRobot)
                    {
                        CameraMotor.setAngle(CameraMotor.angleY, CameraMotor.angleX+1);
                    }
                    if (turnRobot&&!turnCamera)
                    {
                        MainRobot.rightDrive.set(0);
                        MainRobot.leftDrive.set(.2);
                    }
                }
                else
                {
                    if (turnCamera&&!turnRobot)
                    {
                        CameraMotor.setAngle(CameraMotor.angleY, CameraMotor.angleX-1);
                    }
                    if (turnRobot&&!turnCamera)
                    {
                        MainRobot.rightDrive.set(-.2);
                        MainRobot.leftDrive.set(0);
                    }
                }
                
            }else if (turnRobot) {
                MainRobot.rightDrive.set(0);
                MainRobot.leftDrive.set(0);
            }
            if (Math.abs(y-targetY)>rangeY)
            {
                if (y-targetY>0)
                {
                    if (true)//turnCamera)
                    {
                        CameraMotor.setAngle(CameraMotor.angleY+1, CameraMotor.angleX);
                    }
                }
                else
                {
                    if (true)//turnCamera)
                    {
                        CameraMotor.setAngle(CameraMotor.angleY-1, CameraMotor.angleX);
                    }
                }
            }
            
        }
        else if(turnRobot) {
            MainRobot.rightDrive.set(0);
            MainRobot.leftDrive.set(0);
        }
    }

}
