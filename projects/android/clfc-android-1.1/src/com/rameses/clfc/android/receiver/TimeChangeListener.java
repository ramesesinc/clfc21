package com.rameses.clfc.android.receiver;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rameses.clfc.android.AppRunningTimeUtil;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.client.android.AbstractActionBarActivity;
import com.rameses.client.android.AppSettings;
import com.rameses.client.android.Platform;

public class TimeChangeListener extends BroadcastReceiver {

//	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public void onReceive(Context context, Intent intent) {
		
		if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {

			final ApplicationImpl app = (ApplicationImpl) Platform.getApplication();
			AbstractActionBarActivity aa = Platform.getCurrentActionBarActivity();
			if (aa == null) aa = Platform.getActionBarMainActivity();
			synchronized (ApplicationImpl.LOCK) {
				aa.getHandler().postDelayed(new Runnable() {
					public void run() {
						app.paymentDateResolverSvc.stop();
						app.captureDateResolverSvc.stop();
						app.remarksDateResolverSvc.stop();
					}
				}, 1);
			}
			
			Calendar cal = Calendar.getInstance();
			Calendar xcal = Calendar.getInstance();
			
			AppSettings settings = Platform.getApplication().getAppSettings();
			Map map = settings.getAll();
			if (map.containsKey("phonedate")) {
//				println("server date " + settings.getString("serverdate"));
				cal.setTime(java.sql.Timestamp.valueOf(settings.getString("phonedate")));
			}
			 
			long timemillis = cal.getTimeInMillis();
			
			AppRunningTimeUtil instance = AppRunningTimeUtil.getInstance();
			long elapsedtimemillis = instance.getElapsedTimeInMillis();
			
			
			timemillis += elapsedtimemillis; 
			
            long xtimemillis = xcal.getTimeInMillis();
            
            long diff = timemillis - xtimemillis;
            long timediff = diff;
            
            if (map.containsKey("timedifference")) {
            	timediff = settings.getLong("timedifference");
            }
            
            long newtimediff = timediff + diff; 
//            println("saved phonetime-> " + new Timestamp(cal.getTimeInMillis()) + " current phonetime-> " + new Timestamp(xcal.getTimeInMillis()) + " previous timediff-> " + timediff + " current timediff-> " + newtimediff + " elapsed timemillis-> " + elapsedtimemillis);
            
            Timestamp currenttime = new Timestamp(xcal.getTimeInMillis());
            settings.put("phonedate", currenttime);
            settings.put("timedifference", newtimediff);
            
			instance.reset();
            
			ApplicationUtil.resolvePaymentTimedifference(newtimediff);
			synchronized (ApplicationImpl.LOCK) {
				if (Platform.getApplication().getIsDateSync() == true) {

					aa.getHandler().postDelayed(new Runnable() {
		 				public void run() {
							boolean flag = app.paymentDateResolverSvc.getServiceStarted();
							if (flag == true) {
								app.paymentDateResolverSvc.restart(); 
							} else {
								app.paymentDateResolverSvc.start();
							}

							flag = app.captureDateResolverSvc.getServiceStarted();
							if (flag == true) {
								app.captureDateResolverSvc.restart();
							} else {
								app.captureDateResolverSvc.start();
							}
							
							flag = app.remarksDateResolverSvc.getServiceStarted();
							if (flag == true) {
								app.remarksDateResolverSvc.restart();
							} else {
								app.remarksDateResolverSvc.start();
							}
						}
					}, 1);   
				}
			}

		}
		
	}
	
	private void println(Object msg) {
		Log.i("TimeChangeListener", msg.toString());
	}

}
