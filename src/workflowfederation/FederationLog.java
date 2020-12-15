package workflowfederation;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

public class FederationLog extends Log{
	private static boolean debug = true;

	public static void disable()
	{
		debug = false;
	}
	
	public static void print(Object message)
	{
		println(String.valueOf(message));
	}
	
	public static void print(String message)
	{
		debugLog(message);
	}
	
	public static void println(String message)
	{
		debugLog(message + "\n");
	}
	
	public static void setDebug(boolean flag)
	{
		debug = flag;
	}
	
	public static void debugLog(String message)
	{
		if (debug)
			printLine("[SmartFed] "+ message);
	}
	
	public static void timeLog(String message)
	{
		if (debug)
			printLine("[SmartFed "+getSimTime()+"] "+ message);
	}
	
	public static void timeLogDebug(String message)
	{
		if (debug)
			printLine("[SmartFed "+getSimTime()+"] "+ message);
	}
	
	private static String getSimTime()
	{
	    DecimalFormat df = new DecimalFormat("0.###");
	    df.setRoundingMode(RoundingMode.DOWN);
	    return df.format(CloudSim.clock());
	}
}
