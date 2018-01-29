package com.rameses.clfc.android;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.rameses.clfc.android.db.DBLocationTracker;
import com.rameses.client.android.AppSettings;
import com.rameses.client.android.Platform;
import com.rameses.client.android.Task;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;

public class LocationTrackerDateResolverService {

	private final String DBNAME = "clfctracker.db"; 
	private ApplicationImpl app;
	private DBLocationTracker trackerSvc = new DBLocationTracker();
	private boolean serviceStarted = false;
	private Task actionTask;
	
	public LocationTrackerDateResolverService(ApplicationImpl app) {
		this.app = app;
	}
	
	public void start() {
		if (serviceStarted == false) {
			createTask();
			Platform.getTaskManager().schedule(actionTask, 1000, 1000);
			serviceStarted = true;
			ApplicationUtil.println("LocationTrackerDateResolverService", "start");
		}
	}
	
	public void restart() {
		if (serviceStarted == true) {
			if (actionTask != null) {
				actionTask.cancel();
				actionTask = null;
			}
			serviceStarted = false;
		}
		start();
	}
	
	public void stop() {
		if (serviceStarted == true) {
			if (actionTask != null) {
				actionTask.cancel();
				actionTask = null;
			}
			serviceStarted = false;
		}
	}
	
	public boolean getServiceStarted() { return this.serviceStarted; }
	
	private void println(Object msg) {
		Log.i("LocationTrackerDateResolverService", msg.toString());
	}
	
	private void createTask() {
		actionTask = new Task() {
			
			public void run() {				
				synchronized (ApplicationImpl.LOCK) {
					boolean isDateSync = Platform.getApplication().getIsDateSync();
					println("is date sync " + isDateSync);
					if (isDateSync == false) {
						this.cancel();
						serviceStarted = false;
						return;
					}
				}
				
				println("tracker date resolver");
				
				synchronized (TrackerDB.LOCK) {
//					DBContext ctx = new DBContext("clfcpayment.db");
					SQLTransaction trackerdb = new SQLTransaction(DBNAME);
					
					try {
						trackerdb.beginTransaction();
						runImpl(trackerdb);
						trackerdb.commit();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						trackerdb.endTransaction();
					}
					
					boolean flag = false;
					DBContext ctx = new DBContext(DBNAME);
					trackerSvc.setDBContext(ctx);
					try {
						flag = trackerSvc.hasTrackerForDateResolving();
					} catch (Exception e) {
						flag = false;
					}
					
					if (flag == false) {
						this.cancel();
						serviceStarted = false;
					}
//					List<Map> list = paymentSvc.getPaymentsForDateResolving();
				}
			}
			
			private void runImpl(SQLTransaction paymentdb) throws Exception {
				trackerSvc.setDBContext(paymentdb.getContext());
				trackerSvc.setCloseable(false);
				
				AppSettings settings = Platform.getApplication().getAppSettings();
//				println("server time-> " + settings.getString("serverdate") + " phone time-> " + settings.getString("phonedate"));
				
				List<Map> list = trackerSvc.getTrackersForDateResolving();
//				println("payments for resolving " + list.size());
				for (Map m : list) {

				 	Calendar cal = Calendar.getInstance();
					if (m.containsKey("dtsaved")) {
						cal.setTime(java.sql.Timestamp.valueOf(m.get("dtsaved").toString()));
					}
					
			 		long timedifference = 0L;
					if (m.containsKey("timedifference")) {
						timedifference = Long.parseLong(m.get("timedifference").toString());
					}
					
					long timemillis = cal.getTimeInMillis();
					timemillis += timedifference;
					
					Timestamp timestamp = new Timestamp(timemillis);
					String sql = "UPDATE location_tracker SET dtposted='" + timestamp + "', txndate='" + timestamp + "', forupload=1 WHERE objid='" + m.get("objid").toString() + "'";
//					println("dtsaved-> " + m.get("dtsaved").toString() + " timedifference-> " + m.get("timedifference").toString() + " dtposted-> " + timestamp);
					paymentdb.execute(sql); 
					
				} 
				
				ActionBarActivity activity = Platform.getCurrentActionBarActivity();
				ApplicationUtil.println("LocationTrackerDateResolverService", "activity " + activity);
				if (activity != null) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							app.broadcastLocationSvc.start();
						}
					});
				}
			}
			
		};
	}
}
