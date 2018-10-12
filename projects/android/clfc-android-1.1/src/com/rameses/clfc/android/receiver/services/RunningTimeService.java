package com.rameses.clfc.android.receiver.services;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.client.android.Platform;

public class RunningTimeService extends Service {

	private RunningTimeService root = this;
	
	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(5);
	private ScheduledFuture<?> runningTimeHandle;

	private Calendar calendar;
	private boolean serviceStarted = false;
	
	private int elapsedTimeInSecond = 0;
	
	private Thread runningTimeThread = new Thread() {
		public void run() {
			//runningCal.add( Calendar.SECOND, 1 );
			elapsedTimeInSecond++;
		}
	};
	
	@Override
	public void onCreate() {
		app = (ApplicationImpl) Platform.getApplication();
		settings = (AppSettingsImpl) app.getAppSettings();
		calendar = Calendar.getInstance();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		start();
		
		return START_STICKY;
	}
	
	public synchronized boolean getIsServiceStarted() { return serviceStarted; }
	
	public synchronized void reset() {
		elapsedTimeInSecond = 0;
	}

	public synchronized long getElapsedTime() {
		long elapsedtime = 0L;
		
		long t = calendar.getTimeInMillis();
		calendar.add( Calendar.SECOND, elapsedTimeInSecond );
		
		elapsedtime = calendar.getTimeInMillis() - t;
		
		calendar.setTime(new Timestamp( t ));
		
		return elapsedtime;
	}
	
	public synchronized void setTime( Date date ) {
		calendar.setTime( date );
		/*
		long timemillis = calendar.getTimeInMillis();
		long xtimemillis = runningCal.getTimeInMillis();
		long diff = timemillis - xtimemillis;
		
		calendar.setTime( date );
		runningCal.setTime( date );
		timemillis = runningCal.getTimeInMillis();
		timemillis += diff;
		runningCal.setTime(new Timestamp(timemillis));
		*/
//		timemillis = calendar.getTimeInMillis();
//		timemillis += diff;
//		calendar.setTime(new Timestamp(timemillis));
		
	}
	
	public synchronized void start() {
		if (serviceStarted==false) {
			if (runningTimeThread.getState().equals( Thread.State.NEW ) || 
					runningTimeThread.getState().equals( Thread.State.TERMINATED )) {

				elapsedTimeInSecond = 0;
				runningTimeHandle = scheduledPool.scheduleWithFixedDelay( runningTimeThread, 0, 1, TimeUnit.SECONDS );
			}
			serviceStarted = true;
		}
	}
	
	public synchronized void restart() {
		stop();
		start();
	}
	
	public synchronized void stop() {
		if (serviceStarted==true) {
			if (runningTimeHandle != null) {
				runningTimeHandle.cancel( true );
			}
			serviceStarted = false;
		}
	}
	
	public synchronized void shutdown() {
		if (scheduledPool != null) {
			scheduledPool.shutdown();
		}
	}
	
	@Override
	public void onDestroy() {
		stop();
		shutdown();
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
