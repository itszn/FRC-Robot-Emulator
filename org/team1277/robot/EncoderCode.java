/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team1277.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 * @author roboclub
 */
public class EncoderCode extends Encoder{
    
    
    /**
     * 
     * @param port1 First digital IO port for the encoder
     * @param port2 Second digital IO port for the encoder
     */
    public EncoderCode(int port1, int port2) {
        super(port1,port2);
    }
    
    
    
    /**
     * 
     * @param server NetworkTable server for smartDashboard
     * @param key NetworkTable key for smartDashboard
     */
    public void putDistance(NetworkTable server, String key) {
        try
        {
            server.putNumber(key, this.getDistance());
        }catch(Exception e) {}
    }
    
    
    /**
     * 
     * @param server NetworkTable server for smartDashboard
     * @param key NetworkTable key for smartDashboard
     */
    public void putCount(NetworkTable server, String key) {
        try
        {
            server.putNumber(key, this.get());
        }catch(Exception e) {}
    }
    
    
}
