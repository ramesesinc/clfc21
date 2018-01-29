package com.rameses.clfc.android;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import com.rameses.client.android.Platform;
import com.rameses.client.android.Task;

public class AppRunningTimeUtil {

	private static AppRunningTimeUtil instance;
	private Task actionTask;
	private boolean isStarted = false;
	private Calendar calendar = Calendar.getInstance();
	private Calendar runningCal = Calendar.getInstance();
//	private int elapsedSeconds = 0;
	
	public static AppRunningTimeUtil getInstance() {
		if (instance == null) {
			instance = new AppRunningTimeUtil();
		}
		
		return instance;
	}
	
	public boolean getIsStarted() { return isStarted; }
	
	public void start() {
		if (isStarted == false) {
			createTask();
//			runningCal.setTime(calendar.getTime());
			Platform.getTaskManager().schedule(actionTask, 0, 1000);
			isStarted = true;
		}
	}

	public void reset() {
		runningCal.setTime(calendar.getTime());
	}
	
	public void setTime(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		setTime(cal.getTime());
	}
	
	public void setTime(java.sql.Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		setTime(cal.getTime());
	}
	
	public void setTime(Date date) {
		stop();
		if (calendar == null) calendar = Calendar.getInstance();
		if (runningCal == null) {
			runningCal = Calendar.getInstance();
			runningCal.setTime(calendar.getTime());
		}
		long timemillis = calendar.getTimeInMillis();
		long xtimemillis = runningCal.getTimeInMillis();
		long diff = timemillis - xtimemillis;
		
		calendar.setTime(date);
		runningCal.setTime(date);
		timemillis = runningCal.getTimeInMillis();
		timemillis += diff;
		runningCal.setTime(new Timestamp(timemillis));
//		timemillis = calendar.getTimeInMillis();
//		timemillis += diff;
//		calendar.setTime(new Timestamp(timemillis));
		
	}
	
	public long getElapsedTimeInMillis() {
		long timemillis = calendar.getTimeInMillis();
		long xtimemillis = runningCal.getTimeInMillis();
		return (xtimemillis - timemillis);
	}
	
	public void restart() {
		if (isStarted == true) {
			if (actionTask != null) {
				actionTask.cancel();
				actionTask = null;
			}
			isStarted = false;
		}
		start();
	}
	
	public void stop() {
		if (isStarted == true) {
			if (actionTask != null) {
				actionTask.cancel();
				actionTask = null;
			}
			isStarted = false;
		}
	}
	
	private void createTask() {
		actionTask = new Task() {
			
//			AppRunningTimeUtil root = AppRunningTimeUtil.this;
			public void run() {
				runningCal.add(Calendar.SECOND, 1);
//				elapsedSeconds += 1;
			}
		};
	}
	
}
