package de.htw.thesis.ba_ai.cryptovalid;

interface TimerEvents {
	public void timerStart();
	public void timerStop();
}

public abstract class Timer {

	/// VARIABLES ///
	private static long oldTime;
	
	public static void startTimer()
	{
		oldTime = System.nanoTime();
	}
	
	public static long stopTimer()
	{
		return (System.nanoTime() - oldTime) / 1000000;
	}
}
