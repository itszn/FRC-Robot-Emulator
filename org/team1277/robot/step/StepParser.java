/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team1277.robot.step;

import com.sun.squawk.io.BufferedReader;
import com.sun.squawk.util.ArgsUtilities;
import com.sun.squawk.util.StringTokenizer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 *
 * @author ben
 */
public class StepParser
{
    public static class Step
    {
        public double rackPosition;
        public double rackAngle;
    }
    
    public static Vector load(String file) throws IOException
    {
        BufferedReader reader = null;
        try
        {
            Vector results = new Vector();
            
            try{
                reader = new BufferedReader(new InputStreamReader(Connector.openInputStream("file://" + file)));
                
            } catch (Exception e)
            {
                return new Vector();
            }
            String line;
            while((line = reader.readLine()) != null)
            {
                String[] parts = cut(line);
                if(parts.length != 2)
                {
                    throw new IllegalStateException("Bad line: \"" + line + "\"");
                }
                Step step = new Step();
                step.rackPosition = Double.parseDouble(parts[0]);
                step.rackAngle = Double.parseDouble(parts[1]);
                results.addElement(step);
            }
            return results;
            
        }
        finally
        {
            if(reader != null)
            {
                reader.close();
            }
        }
    }
    

    public static String[] cut(String in)
    {
        StringTokenizer st = new StringTokenizer(in);
        
        Vector strings = new Vector();
        
        
        
        
        while (st.hasMoreTokens())
        {
            strings.addElement(st.nextToken());
        }
        
        /*int lastIndex  = 0;
        int index = 1;
        int i = 0;
        String[] strings = new String[1];
        while (index >= 1)
        {
            index = in.indexOf(' ');
            if (index < 1)
                break;
            strings[i] = in.substring(0, index);
            
            i++;
            String[] strings2 = new String[i+1];
            for (int j=0;j<i;j++)
            {
                strings2[j]=strings[j];
            }
            strings = strings2;
            lastIndex = index+1;
            in = in.substring(index+1);
        }
        String[] strings2 = new String[strings.length-1];
            for (int j=0;j<i;j++)
            {
                strings2[j]=strings[j];
            }
            strings = strings2;
        
        return strings;*/
        String[] strang = new String[strings.size()];
        for (int i=0; i< strings.size(); i++)
        {
            strang[i] = (String)strings.elementAt(i);
        }
        return strang;
    }
}
