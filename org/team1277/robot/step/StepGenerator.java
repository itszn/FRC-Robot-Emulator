/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team1277.robot.step;

import com.sun.squawk.microedition.io.FileConnection;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.microedition.io.Connector;
import org.team1277.robot.Climber;

/**
 *
 * @author ben
 */
public class StepGenerator
{
    private FileConnection conn;
    private OutputStreamWriter writer;
    public boolean fileOpen = false;
    
    public void writeStep(double rackPosition, double rackAngle) throws IOException
    {
        writer.write(Double.toString(rackPosition));
        writer.write(' ');
        writer.write(Double.toString(rackAngle));
        writer.write('\n');
    }
    
    public void update()
    {
        try
        {
            this.writeStep(getRackPosition(), getRackAngle());
        }
        catch(IOException e)
        {
            System.err.println("ERROR!!!");
            e.printStackTrace();
        }
    }
    
    public double getRackPosition()
    {
        return 42.0;
    }
    
    public double getRackAngle()
    {
        return 42.0;
    }
    
    public boolean open(String file)
    {
        try
        {
            conn = (FileConnection) Connector.open("file://" + file);
            if(Climber.LEARNING_MODE)
            {
                if (conn.exists())
                {
                    conn.delete();
                    conn.create();
                }
                else
                {

                    conn.create();
                }
            }
            else
            {
                if (!conn.exists())
                {
                    conn.create();
                }
            }
            writer = new OutputStreamWriter(conn.openOutputStream());
            
            fileOpen = true;
            return true;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            
            return false;
        }
    }
    
    public boolean close()
    {
        try
        {
            writer.flush();
            writer.close();
            
            fileOpen = false;
            return true;
        }
        catch(IOException e)
        {
            return false;
        }
    }
}
