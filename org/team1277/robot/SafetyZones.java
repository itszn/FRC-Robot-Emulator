package org.team1277.robot;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/*
 * Determine if it is safe to move an arm into a given position
 */
public class SafetyZones
{
    public static final String RACK_LOCATION_KEY = "rackLocation";
    public static final double RACK_LENGTH = 49.0; //TODO: CHANGE ME

    private static void debugMessage(double length, double angle, Point rackEnd)
    {
	String msg = "Length: " + String.valueOf(length) + ", Angle: " + String.valueOf(angle) + "Point: (" + rackEnd.x + ", " + rackEnd.y + ")";
	MainRobot.server.putString("safetyDebug", msg);
    }
    
    private static class Point
    {
        public double x;
        public double y;
    }
    
    /**
     * How high above the base of the robot the pivot is
     */
    private static final double PIVOT_HEIGHT = 14.0;
    
    /**
     * Check to see if the given position is safe for the rack to be in.
     * @param extended how far the rack is extended
     * @param angle the angle the rack is pivoted to
     * @return {@code true} if the rack can move to the position, {@code false} otherwise
     */
    public static boolean checkAllowed(double extended, double angle)
    {
        return isAllowed(RACK_LENGTH-(extended+4), angle); //add 4 to extended because the potentiometer measures from the end of the white plastic
    }
    
    /**
     * Determine if it is possible for the rack to move to a certain position
     * @param extended the length of the rack (the side without the hook)
     * @param angle the angle of the rack (relative to the vertical line perpendicular to the base
     * of the robot and passing through the center of the pivot)
     * @return {@code true} if the rack can move to the position, {@code false} otherwise
     */
    private static boolean isAllowed(double length, double angle)
    {
        Point rackEnd = toPoint(length, angle);
	debugMessage(length, angle, rackEnd);
        if(testFront(rackEnd) || testBack(rackEnd))
            return false;
        return true;
    }
    
    // the end with the router
    private static boolean testFront(Point point)
    {
        double expected = -0.252*point.x*point.x + 5.5678*point.x-25.836;
	return point.y <= expected;
    }
    
    //the end with the big motor
    private static boolean testBack(Point point)
    {
       double expected = -0.2295*(point.x*point.x) - 4.758*point.x - 15.938;
       if(point.y >= expected)
	   return true;
       return false;
    }
    
    /**
     * Convert the length and angle of the rack to a coordinate
     * @param length see {@link #isAllowed(double, double)}
     * @param angle see {@link #isAllowed(double, double)}
     * @return a point containing the location of the non-hook end of the rack
     */
    private static Point toPoint(double length, double angle)
    {
        double radialCoordinate = polarLength(angle, length);
        double angularCoordinate = polarAngle(radialCoordinate, length, angle);
        double x = radialCoordinate * Math.cos(angularCoordinate);
        double y = radialCoordinate * Math.sin(angularCoordinate);
        Point pt = new Point();
        pt.x = x;
        pt.y = y;
        return pt;
    }
    
    /**
     * Use the law of cosines to determine the length of the polar coordinate given the angle and length as defined
     * by {@link #isAllowed(double, double)}.
     * @param angle see {@link #isAllowed(double, double)}
     * @param length see {@link #isAllowed(double, double)}
     * @return a length that can be used as a polar coordinate
     */
    private static double polarLength(double angle, double length)
    {
        // c^2 = a^2 + b^2 - 2ab*cos(C)
        double a2 = PIVOT_HEIGHT * PIVOT_HEIGHT; // a squared
        double b2 = length * length; // b squared
        double lastPart = 2 * PIVOT_HEIGHT * length * Math.cos(deg2Rad(angle)); // not sure what else to call it (2ab*cos(C))
        return Math.sqrt(a2 + b2 - lastPart);
    }
    
    /**
     * Calculate the polar coordinate angle (r in radians)
     * @param polarLength equal to {@link #polarLength(double, double)} using the same length and angle as those passed into this method
     * @param length see {@link #isAllowed(double, double)}
     * @param angle see {@link #isAllowed(double, double)}
     * @return 
     */
    private static double polarAngle(double polarLength, double length, double angle)
    {
        // use law of sines
        // given b, c, and B: solve for C
        //double b = polarLength;
        //double c = length;
        //double B = deg2Rad(angle);
        return MathUtils.asin((length * Math.sin(deg2Rad(angle))) / polarLength);
    }
    
    /**
     * Convert angle in degrees to angle in radians
     * @param deg angle in degrees
     * @return angle in radians
     */
    private static double deg2Rad(double deg)
    {
        return Math.PI * deg / 180.0;
    }
    
    /**
     * Convert angle in radians to angle in degrees
     * @param rad angle in radians
     * @return angle in degrees
     */
    private static double rad2Deg(double rad)
    {
        return rad * (180 / Math.PI);
    }
    
    /**
     * Send information to the SmartDashboard so it can draw the rack position widget
     * @param server 
     */
    public static void updateDisplay(NetworkTable server)
    {
        //TODO: since these numbers use the opposite end of the rack, do I need to convert them?
        
        Climber.findPosition(); // do I need this?
        double length = Climber.rackExtention;
        double angle = Climber.rackPivot;
        Point point = toPoint(length, angle);
        String pointText = String.valueOf(point.x).concat(",").concat(String.valueOf(point.y));
        server.putString(RACK_LOCATION_KEY, pointText);
    }
}