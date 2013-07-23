/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team1277.robot.step;

import edu.wpi.first.wpilibj.DriverStation;
import java.io.IOException;
import java.util.Vector;
import org.team1277.robot.step.StepParser.Step;

/**
 *
 * @author ben
 */
public class StepSystem
{
    public static final String STEP_FILE = "steps.txt";
    public static final String STEP_FILE_AUTO = "stepsAuto.txt";
    
    private static StepGenerator generator;
    private static Vector steps;
    private static boolean trainingRunning = false;
    
    public static void startTraining()
    {
        generator = new StepGenerator();
        trainingRunning=true;
        
    }
    
    public static void stopTraining()
    {
        
        trainingRunning=false;
        if(generator == null)
        {
            throw new IllegalStateException("Training was never started");
        }
        if (generator.fileOpen)
        {
            if(!generator.close())
            {
                throw new RuntimeException("Can't save " + STEP_FILE);
            }
        }
    }
    
    public static Vector loadSteps()
    {
        if (generator.fileOpen)
        {
            if(!generator.close())
            {
                throw new RuntimeException("Can't save " + STEP_FILE);
            }
        }
        try
        {
            if(DriverStation.getInstance().isAutonomous())
                steps = StepParser.load(STEP_FILE_AUTO);
            else
                steps = StepParser.load(STEP_FILE);
            return steps;
        }
        catch(IOException e)
        {
            throw new RuntimeException("Can't load steps: " + e);
        }
    }
    
    public static Step getStep(int index)
    {
        if (index<steps.size())
            return (Step) steps.elementAt(index);
        return (Step) steps.elementAt(steps.size()-1);
    }
    
    public static StepIterator steps()
    {
        return new StepIterator(steps);
    }
    
    public static boolean inTraining()
    {
        return trainingRunning;
    }
    
    public static void update(double rackPosition, double rackAngle)
    {
        try {
            if (!generator.fileOpen)
            {
                if(!generator.open(STEP_FILE))
                {
                throw new RuntimeException("Can't open " + STEP_FILE);
                }
            }
            generator.writeStep(rackPosition, rackAngle);
        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }
}
