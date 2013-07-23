/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team1277.robot;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import org.team1277.robot.step.StepSystem;

/**
 *
 * @author roboclub
 */
public class Climber {
    //Rack Constants
    public static final double RACK_RETRACTED_INCHES = 0;
    public static final double RACK_RETRACTED_VOLTS = .13;
    public static final double RACK_EXTENDED_INCHES = 40;
    public static final double RACK_EXTENDED_VOLTS = 4.08;
    
    //Rack Pivot Constants
    public static final double PIVOT_BACK_DEGREES = -90;
    public static final double PIVOT_BACK_VOLTS = 1.10;
    public static final double PIVOT_FORWARD_DEGREES = 90;
    public static final double PIVOT_FORWARD_VOLTS = 3.23;
    
    //Plumbob Pivot Constants
    public static final double PLUMB_BACK_DEGREES = 0;
    public static final double PLUMB_BACK_VOLTS = 0;
    public static final double PLUMB_FORWARD_DEGREES = 1;
    public static final double PLUMB_FORWARD_VOLTS = 1;
    
    //PWMs and Channels
    public static final int RACK_EXTENDER_PWM = 5;
    public static final int RACK_PIVOTER_PWM = 6;
    public static final int RACK_EXTEND_POT_CHNL = 1;
    public static final int RACK_PIVOT_POT_CHNL = 2;
    public static final int PLUMB_POT_CHNL = 3;
    
    public static final double OUTER_LEFT_ANGLE_LIMIT = 61;
    public static final double INNER_LEFT_ANGLE_LIMIT = 28;
    public static final double INNER_RIGHT_ANGLE_LIMIT = -28;
    public static final double OUTER_RIGHT_ANGLE_LIMIT = -52;
    public static final double LEFT_EXTEND_LIMIT = 33;
    public static final double RIGHT_EXTEND_LIMIT = 33;
    
    public static final double START_EXTEND = 28.7;
    public static final double START_ANGLE = -11;
    
    public static final boolean SLOW_AT_END = false;
    
    public static boolean LEARNING_MODE = true;
    
    public static boolean climbing = false;
    public static boolean correctToGoal = true;
    public static int stepNum = 0;
    
    public static boolean manual = false;
    
    public static double rackExtention = 0;
    public static double rackPivot = 0;
    public static double plumbPivot = 0;
    
    //POTs
    public static AnalogChannel rackExtendPOT;
    public static AnalogChannel rackPivotPOT;
    public static AnalogChannel plumbPivotPOT;
    
    
            
    //Motors
    public static Jaguar rackExtender;
    public static Jaguar rackPivoter;
    
    public static boolean atPosition = false;
    public static boolean atLength = false;
    public static boolean atAngle = false;
    
    public static boolean rackRunning = true;
    
    private static boolean buttonDown;
    private static boolean buttonDown2;
    private static boolean buttonDown3;
    
    public static boolean inBounds;
    
    public static Relay eBreak;
    
    public static void loadClimber()
    {
        eBreak = new Relay(1);
        rackExtendPOT = new AnalogChannel(RACK_EXTEND_POT_CHNL);
        rackPivotPOT = new AnalogChannel(RACK_PIVOT_POT_CHNL);
        //plumbPivotPOT = new AnalogChannel(PLUMB_POT_CHNL);
        
        rackExtender = new Jaguar(RACK_EXTENDER_PWM);
        rackPivoter = new Jaguar(RACK_PIVOTER_PWM);
        
        
        
        //StepSystem.loadSteps();
    }
    
    public static void stop()
    {
        rackExtender.set(0);
        rackPivoter.set(0);
        climbing = false;
        if (LEARNING_MODE)
        {
            if (StepSystem.inTraining())
            {
                //StepSystem.stopTraining();
                //StepSystem.loadSteps();
            }
            
        }
    }
    
    public static void reset()
    {
        
        climbing = false;
        stepNum = 0;
        manual = true;
        if (LEARNING_MODE)
        {
            
            StepSystem.startTraining();
        }
    }
    
    public static void start()
    {
        if (MainRobot.server.getBoolean("AUTO_RUN", false))
        {
            climbing = true;
            manual = false;
        }
        else
        {
            climbing = false;
            manual = true;
        }
        LEARNING_MODE=false;
        if (DriverStation.getInstance().isAutonomous())
            LEARNING_MODE=false;
        
        StepSystem.loadSteps();
        MainRobot.server.putNumber("Received Length", 0);
    }
    
    public static void setClimbing(boolean state)
    {
        climbing = state;
    }
    
    public static void setCorrectToGoal(boolean state)
    {
        correctToGoal = state;
    }
    
    public static void update()
    {
        if (LEARNING_MODE)
        {
            manual = true;
            climbing = false;
        }
        DisplayUpdater.update();
        findPosition();
        inBounds  = SafetyZones.checkAllowed(rackExtention, rackPivot);
        //SafetyZones.updateDisplay(MainRobot.server);
        MainRobot.server.putBoolean("Safety", inBounds);
        
        MainRobot.server.putBoolean("Learning", LEARNING_MODE);
        
        
        MainRobot.server.putBoolean("Manual Override", manual);
        if (rackExtention < -1.5)
        {
            eBreak.set(Value.kForward);
            MainRobot.server.putBoolean("Emergancy Break", true);
        }
        else
        {
            if (!MainRobot.rightStick.getRawButton(6))
            {
                MainRobot.server.putBoolean("Emergency Break", false);
                eBreak.set(Value.kReverse);
            }
            else
            {
                eBreak.set(Value.kForward);
                MainRobot.server.putBoolean("Emergency Break", true);
            }
        
        
        }
        
       
        
        if (MainRobot.leftStick.getRawButton(7))
            {
                if (!buttonDown3)
                {
                    buttonDown3 = true;
                    stop();
                    reset();
                    LEARNING_MODE = !LEARNING_MODE;
                    manual = false;
                    climbing = false;
                }
            }
            else
            {
                buttonDown3 = false;
            }
        if(MainRobot.rightStick.getRawButton(7))
        {
            correct(START_EXTEND, START_ANGLE);
        }
        else if (!manual)
        {
            if (MainRobot.leftStick.getRawButton(10))
            {
                if (!buttonDown)
                {
                    buttonDown = true;
                    climbing = !climbing;
                }
            }
            else
            {
                buttonDown = false;
            }
            
            if (MainRobot.rightStick.getRawButton(1)||MainRobot.leftStick.getRawButton(1))
            {
                if (!buttonDown2)
                {
                    buttonDown2 = true;
                    manual = true;
                    climbing = false;
                }
            }
            else
            {
                buttonDown2 = false;
            }
            
            
            /*if (MainRobot.leftStick.getRawButton(11))
            {
                if (!buttonDown2)
                {
                    buttonDown2 = true;
                    stepNum++;
                }
            }
            else
            {
                buttonDown2 = false;
            }*/
            
            MainRobot.server.putBoolean("Climbing", climbing);
            MainRobot.server.putNumber("StepNum", stepNum);
            
            
            if (climbing)
            {

                if (correctToGoal)
                {
                    atLength = false;
                    atAngle = false;
                    //System.out.print("Got: "+getLength(stepNum)+" in | "+getAngle(stepNum)+ " deg |Reading: "+rackExtention+" in | "+rackPivot+" deg |SpeedExtend: ");
                    
                    double extendGoal = getLength(stepNum);
                    double angleGoal = getAngle(stepNum);
                    
                    /*if (rackPivot <= OUTER_LEFT_ANGLE_LIMIT && rackPivot >= INNER_LEFT_ANGLE_LIMIT)
                    {
                        if (rackExtention < LEFT_EXTEND_LIMIT)
                            angleGoal = rackPivot;
                        extendGoal = LEFT_EXTEND_LIMIT+1;
                    }
                    
                    if (rackPivot >= OUTER_RIGHT_ANGLE_LIMIT && rackPivot <= INNER_RIGHT_ANGLE_LIMIT)
                    {
                        if (rackExtention < RIGHT_EXTEND_LIMIT)
                            angleGoal = rackPivot;
                        extendGoal = RIGHT_EXTEND_LIMIT+1;
                    }*/
                    if (MainRobot.leftStick.getRawButton(2) || MainRobot.rightStick.getRawButton(2))
                    {
                        extendGoal = rackExtention;
                        angleGoal = rackPivot;
                    }
                    
                    MainRobot.server.putNumber("Received Length", extendGoal);
                    correct(extendGoal, angleGoal);
                    
                    //System.out.println((rackPivot > angleGoal - 3 && rackPivot < angleGoal + 3) + " "+(rackExtention > extendGoal - .5 && rackExtention < extendGoal+.5));
                    if (rackPivot > angleGoal - 3 && rackPivot < angleGoal + 3)
                    {
                        
                        if (angleGoal==getAngle(stepNum))
                            atAngle = true;
                    }
                    
                    if (rackExtention > extendGoal - .5 && rackExtention < extendGoal+.5)
                    {
                        if (extendGoal==getLength(stepNum))
                            atLength = true;
                    }
                    

                    /*
                     * Add angle PID;
                     */
                    
                    if (atAngle && atLength)
                    {
                        atPosition = true;
                    }
                    else
                    {
                        atPosition = false;
                    }
                    
                    if(atPosition) {
                        stepNum++;
                        atPosition = false;
                    }
                }
                else
                {
                    rackExtender.set(0);
                    rackPivoter.set(0);
                }
            }
            else
            {
               rackExtender.set(0);
               rackPivoter.set(0);
            }
        }
        else if (manual)
        {
            if (MainRobot.rightStick.getRawButton(1))
            {
                if (!buttonDown2)
                {
                    buttonDown2 = true;
                    rackRunning = !rackRunning;
                }
            }
            else
            {
                buttonDown2 = false;
            }
            
            
            if (rackRunning)
            {
                double rightJoyY = MainRobot.rightStick.getY();
                double leftJoyY = MainRobot.leftStick.getY();
                
                MainRobot.server.putBoolean("drive1",leftJoyY > 0 );
                MainRobot.server.putBoolean("drive2", false);
                if (!(leftJoyY > 0 && rackPivot<=OUTER_LEFT_ANGLE_LIMIT && rackPivot>=INNER_LEFT_ANGLE_LIMIT+4&&rackExtention<LEFT_EXTEND_LIMIT)) 
                {
                    if (!(leftJoyY > 0 && rackPivot>=OUTER_RIGHT_ANGLE_LIMIT && rackPivot<=INNER_RIGHT_ANGLE_LIMIT+4&&rackExtention<RIGHT_EXTEND_LIMIT)) 
                    {
                        if (!(leftJoyY < 0 && rackPivot<=OUTER_LEFT_ANGLE_LIMIT-4 && rackPivot>=INNER_LEFT_ANGLE_LIMIT&&rackExtention<LEFT_EXTEND_LIMIT)) 
                        {
                            if (!(leftJoyY < 0 && rackPivot>=OUTER_RIGHT_ANGLE_LIMIT-4 && rackPivot<=INNER_RIGHT_ANGLE_LIMIT&&rackExtention<RIGHT_EXTEND_LIMIT)) 
                            {
                                
                                MainRobot.server.putBoolean("drive2", true);
                                rackPivoter.set(-leftJoyY);
                            }
                            else
                            {
                                rackPivoter.set(0);
                            }
                        }
                        else
                        {
                            rackPivoter.set(0);
                        }
                    }
                    else
                    {
                        rackPivoter.set(0);
                    }
                }
                else
                {
                    rackPivoter.set(0);
                }
                if (!(rightJoyY<0 && rackExtention > 39) && !(rightJoyY>0 && rackExtention<1))
                {
                    rackExtender.set(-rightJoyY);
                }
                else
                {
                    rackExtender.set(0);
                }
                
            }
            if (LEARNING_MODE&&MainRobot.leftStick.getRawButton(1))
            {
                if (!buttonDown)
                {
                    buttonDown = true;
                    StepSystem.update(rackExtention, rackPivot);
                    System.out.println("Sent: "+rackExtention+" in | " + rackPivot+" deg");
                }
            }
            else
            {
                buttonDown = false;
            }
            
            //double rightJoyY = MainRobot.rightStick.getY();
            //double leftJoyY = MainRobot.leftStick.getY();
            //rackExtender.set(rightJoyY*.5);//m_rightDrive.set(rightJoyY*MainRobot.driveSpeed);
            //rackPivoter.set(-leftJoyY*.5);
            /*if (MainRobot.leftStick.getRawButton(3))
            {
                if (MainRobot.leftStick.getRawButton(1))
                {
                    rackPivoter.set(.7);
                }
                else
                {
                    rackPivoter.set(.4);
                }  
            }
            else if (MainRobot.leftStick.getRawButton(2))
            {
                if (MainRobot.leftStick.getRawButton(1))
                {
                    rackPivoter.set(-.7);
                }
                else
                {
                    rackPivoter.set(-.4);
                }  
            }
            else
            {
                rackPivoter.set(0);
            }
            
            if (MainRobot.rightStick.getRawButton(3))
            {
                if (MainRobot.rightStick.getRawButton(1))
                {
                    rackExtender.set(.9);
                }
                else
                {
                    rackExtender.set(.6);
                }  
            }
            else if (MainRobot.rightStick.getRawButton(2))
            {
                if (MainRobot.rightStick.getRawButton(1))
                {
                    rackExtender.set(-.9);
                }
                else
                {
                    rackExtender.set(-.6);
                }  
            }
            else
            {
                rackExtender.set(0);
            }*/
        
        }
    }
    
    private static void correct(double extendGoal, double angleGoal)
    {
     
        double value = 1;  
        if (SLOW_AT_END)
        {
            
            if(DriverStation.getInstance().getMatchTime()>133)
            {
                value = .4;
            }
        }
        if (rackExtention < extendGoal - 3)//3
        {
            rackExtender.set(1*value);
        }
        else if (rackExtention < extendGoal - .2)
        {
            rackExtender.set(.7*value);
            //System.out.print(.7);
        }
        /*else if (rackExtention < extendGoal - .2)
        {
            rackExtender.set(.4*value);
            //System.out.print(.4);
        }*/
        else if (rackExtention > extendGoal + 3)//3
        {
            rackExtender.set(-1*value);
            //System.out.print(-1);
        }
        else if (rackExtention > extendGoal + .2)
        {
            rackExtender.set(-.7*value);
            //System.out.print(-.7);
        }/*
        else if (rackExtention > extendGoal + .2)
        {
            rackExtender.set(-.4*value);
            //System.out.print(-.4);
        }*/
        else
        {
            rackExtender.set(0);

            //System.out.print(0);
        }

        //System.out.print(" | SpeedPivot: ");

        if (rackPivot < angleGoal - 10)
        {
            rackPivoter.set(.7*value);
            //System.out.println(.5);
        }
        else if (rackPivot < angleGoal - 1.5)
        {
            rackPivoter.set(.6*value);
            //System.out.println(.3);
        }
        /*else if (rackPivot < angleGoal - 2)
        {
            rackPivoter.set(.1*value);
            //System.out.println(.1);
        }*/
        else if (rackPivot > angleGoal + 10)
        {
            rackPivoter.set(-.7*value);
            //System.out.println(-.5);
        }
        else if (rackPivot > angleGoal + 1.5)
        {
            rackPivoter.set(-.6*value);
            //System.out.println(-.2);
        }
        else
        {
            rackPivoter.set(0);

        }
    }
    
    private static double getAngle(int step)
    {
        return StepSystem.getStep(step).rackAngle;
    }
    
    private static double getLength(int step)
    {
        return StepSystem.getStep(step).rackPosition;
    }
    
    public static void findPosition()
    {
        double rackExtendVolts = rackExtendPOT.getVoltage();
        double rackPivotVolts = rackPivotPOT.getVoltage();
        //double plumbPivotVolts = plumbPivotPOT.getVoltage();
        
        MainRobot.server.putNumber("Pot1", rackExtendVolts);
        MainRobot.server.putNumber("Pot2", rackPivotVolts);
        
        //System.out.println(rackExtendVolts + " # " + rackPivotVolts);
        rackExtention = RACK_RETRACTED_INCHES + (RACK_EXTENDED_INCHES - RACK_RETRACTED_INCHES) * ((rackExtendVolts - RACK_RETRACTED_VOLTS) / (RACK_EXTENDED_VOLTS - RACK_RETRACTED_VOLTS));
        rackPivot = PIVOT_BACK_DEGREES + (PIVOT_FORWARD_DEGREES - PIVOT_BACK_DEGREES) * ((rackPivotVolts - PIVOT_BACK_VOLTS) / (PIVOT_FORWARD_VOLTS - PIVOT_BACK_VOLTS));
        //plumbPivot = PLUMB_BACK_DEGREES + (PLUMB_FORWARD_DEGREES - PLUMB_BACK_DEGREES) * ((plumbPivotVolts - PLUMB_BACK_VOLTS) / (PLUMB_FORWARD_VOLTS - PLUMB_BACK_VOLTS));
        MainRobot.server.putNumber("Extention", rackExtention);
        MainRobot.server.putNumber("Pivot", rackPivot);
    }
}
