/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team1277.robot.step;

import java.util.Vector;
import org.team1277.robot.step.StepParser.Step;

/**
 *
 * @author ben
 */
public class StepIterator
{
    private Vector steps;
    private int index;
    
    public StepIterator(Vector steps)
    {
        this.steps = steps;
        this.index = 0;
    }
    
    public boolean hasNext()
    {
        return index < steps.size();
    }
    
    public Step nextStep()
    {
        return (Step) steps.elementAt(index);
    }
}
