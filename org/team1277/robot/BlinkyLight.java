/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team1277.robot;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;

/**
 *
 * @author roboclub
 */
public class BlinkyLight {
    
    private int pattern;
    private int speed;
    public Relay light;
    
    public BlinkyLight(int port, int pattern) {
        this(port,pattern,20);
    }
    
    /**
     * 
     * @param pattern Pattern of the blink (Use LightPatterns)
     * @param speed Speed of the blink in loops per change
     */
    public BlinkyLight(int port, int pattern, int speed) {
        this.pattern = pattern;
        this.speed = speed;
        this.light = new Relay(port);
    }
    
    public BlinkyLight(int port)
    {
        this(port,1,20);
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public void setPattern(int pattern) {
        this.pattern = pattern;
    }
    
    public int getSpeed() {
        return this.speed;
    }
    
    public int getPattern() {
        return this.pattern;
    }
    
    public void update(int loops) {
        switch(pattern){
            case LightPatterns.REGULAR_BLINK:
                if(loops%speed==0)
                    this.light.set((this.light.get().equals(Value.kOff))?Value.kOn:Value.kOff);
                break;
            case LightPatterns.DOUBLE_BLINK:
                if(this.light.get().equals(Value.kOff)&&loops%5==0)
                    this.light.set(Value.kOn);
                else if(loops%speed==0)
                    this.light.set(Value.kOff);
                break;
        }
    }
}
