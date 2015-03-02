package StevenDimDoors.mod_pocketDim.util;

import java.util.ArrayList;
import java.util.HashMap;

public class DDLogger
{
	private static DDLogger instance;
	private StringBuilder log;
	private HashMap<String,DDTimer> timers = new HashMap<String,DDTimer>();
	
	private DDLogger()
	{
		this.log = new StringBuilder();
		this.log.append("Logger started.\n");
	}
	
	// SenseiKiwi: I changed this to private to guarantee that the logger isn't being used anywhere.
	private static DDLogger logger()
	{
		if( instance == null)
		{
			instance = new DDLogger();
		}
		return instance;
	}
	
	private class DDTimer
	{
		final String description;
		Long startTime;
		Long endTime;
		boolean stopped = false;
		
		private DDTimer(String description)
		{
			this.description=description;
		}
		private void start()
		{
			this.startTime=System.nanoTime();
		}
		private void stop(long endTime)
		{
			this.endTime=endTime;
			if(!this.stopped)
			{
				this.stopped=true;
				log.append(this.description+" took "+this.getDuration()+" seconds to execute.\n");
			}
		}
		/**
		 * @return the duration in seconds, returns -1 if it still running
		 */
		public double getDuration()
		{
			if(this.stopped)
			{
				return (this.endTime-this.startTime)/1000000000D;
			}
			return -1;
		}
	}
	
	/**
	 * Creates and starts a timer.
	 * 
	 * @param description: The string used to identify the timer
	 * @return
	 */
	public static void startTimer(String description)
	{
		DDTimer timer = logger().new DDTimer(description);
		logger().timers.put(description, timer);
		timer.start();
	}
	
	/**Stops and records a timer to the log
	 * 
	 * @param description
	 * @return
	 */
	public static double stopTimer(String description)
	{
		long endTime = System.nanoTime();
		DDTimer timer = logger().timers.get(description);
		if(timer==null)
		{
			return -1;
		}
		timer.stop(endTime);
		logger().timers.remove(description);
		return timer.getDuration();
	}
	
	public String printLog()
	{
		return this.log.toString();
	}
	
	public void clearLog()
	{
		this.log = new StringBuilder();
	}
	
}
