package com.rameses.clfc.android;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class ApplicationTimer extends Timer {

	private void println( Object msg ) {
		Log.i("ApplicationTimer", msg.toString());
	}
	
	private void setTimerTaskAsScheduled( TimerTask task ) {
		println("instancof ApplicationTimerTask " + (task instanceof ApplicationTimerTask));
		if (task instanceof ApplicationTimerTask) {
			ApplicationTimerTask t = (ApplicationTimerTask) task;
			println("is scheduled-> " + t.getIsScheduled());
			if (t.getIsScheduled() == false) {
				t.setIsScheduled( true );
			}
		}
	}
	
	public void schedule( TimerTask task, long delay, long period ) {
		super.schedule( task, delay, period );
		setTimerTaskAsScheduled( task );
	}
	
	public void schedule( TimerTask task, Date time ) {
		super.schedule( task, time );
		setTimerTaskAsScheduled( task );
	}
	
	public void schedule( TimerTask task, Date firstTime, long period ) {
		super.schedule( task, firstTime, period );
		setTimerTaskAsScheduled( task ); 
	}
	
	public void schedule( TimerTask task, long delay ) {
		super.schedule( task, delay );
		setTimerTaskAsScheduled( task );
	}
	
	public void scheduleAtFixedRate( TimerTask task, long delay, long period ) {
		super.scheduleAtFixedRate( task, delay, period );
		setTimerTaskAsScheduled( task );
	}
	
	public void scheduleAtFixedRate( TimerTask task, Date firstTime, long period ) {
		super.scheduleAtFixedRate(task, firstTime, period);
		setTimerTaskAsScheduled( task );
	}
	
}
