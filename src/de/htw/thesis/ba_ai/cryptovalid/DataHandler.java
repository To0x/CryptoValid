package de.htw.thesis.ba_ai.cryptovalid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryManager;

public class DataHandler {

	
	private float temperature;
	private float batteryPct;
		
//		manager = (SensorManager)c.getSystemService(Context.SENSOR_SERVICE);
//		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//			tempSensor = manager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
//		else
//			tempSensor = manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
//		
//		if (manager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_FASTEST))
//		{
//			throw new Exception("passt");
//		}
//		else
//		{
//			throw new Exception("bla");
//		}
	
	public void Start()
	{
		Timer.startTimer();
	}
	
	public String getData(Context c){
		
	     Intent intent = c.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	     temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
	     int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	     int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	     batteryPct = (float)(currentLevel * 100) / (float)scale;
	     
		return String.format("time=%d ; acc=%f ; temp=%f", Timer.stopTimer(), batteryPct, (temperature/10));
	}

}
