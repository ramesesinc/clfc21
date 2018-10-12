package com.rameses.clfc.android;

import java.util.TimerTask;

public abstract class ApplicationTimerTask extends TimerTask {

	private boolean isScheduled = false;
	
	public void setIsScheduled( boolean isScheduled ) {
		this.isScheduled = isScheduled;
	}
	
	public boolean getIsScheduled() { return isScheduled; }
	
}
