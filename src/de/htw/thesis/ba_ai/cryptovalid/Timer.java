package de.htw.thesis.ba_ai.cryptovalid;

import java.util.ArrayList;
import java.util.List;

interface TimerEvents {
	public void timerStart();
	public void timerStop();
}

public class Timer {

	/// EVENTS ///
	List<TimerEvents> listeners = new ArrayList<TimerEvents>();
	public void addTimerListener(TimerEvents toAdd) 
	{
		listeners.add(toAdd);
	}

	/// VARIABLES ///
	private long oldTime;
	
	public void startTimer()
	{
		oldTime = System.nanoTime();
	}
	
	public long stopTimer()
	{
		return (System.nanoTime() - oldTime) / 1000000;
	}
}
