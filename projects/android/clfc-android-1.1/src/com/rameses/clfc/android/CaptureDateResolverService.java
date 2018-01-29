package com.rameses.clfc.android;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.rameses.clfc.android.db.DBCapturePayment;
import com.rameses.client.android.Platform;
import com.rameses.client.android.Task;
import com.rameses.db.android.SQLTransaction;

public class CaptureDateResolverService {

	private ApplicationImpl app;
	private DBCapturePayment captureSvc = new DBCapturePayment();
	private boolean serviceStarted = false;
	private Task actionTask;
	
	public CaptureDateResolverService(ApplicationImpl app) {
		this.app = app;
	}
	
	public void start() {
		if (serviceStarted == false) {
			createTask();
			Platform.getTaskManager().schedule(actionTask, 1000, 1000);
			serviceStarted = true;
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
		Log.i("CaptureDateResolverService", msg.toString());
	}
	
	private void createTask() {
		actionTask = new Task() {
			public void run() {
				synchronized (ApplicationImpl.LOCK) {
					boolean isDateSync = Platform.getApplication().getIsDateSync();
					if (isDateSync == false) {
						this.cancel();
						serviceStarted = false;
						return;
			 		}
				}
				
//				println("capture date resolver");
				
				synchronized (CaptureDB.LOCK) {
					SQLTransaction capturedb = new SQLTransaction("clfccapture.db");
					try {
						capturedb.beginTransaction();
						runImpl(capturedb);
						capturedb.commit();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						capturedb.endTransaction();
					}
				}
			}
			
			private void runImpl(SQLTransaction capturedb) throws Exception {
				captureSvc.setDBContext(capturedb.getContext());
				captureSvc.setCloseable(false);

				List<Map> list = captureSvc.getPaymentsForDateResolving();
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
					timemillis -= timedifference;
					
					Timestamp timestamp = new Timestamp(timemillis);
					String sql = "UPDATE capture_payment SET dtposted='" + timestamp + "', dtpaid='" + timestamp + "', forupload=1 WHERE objid='" + m.get("objid").toString() + "'";
					capturedb.execute(sql);
					
				}

				ActionBarActivity activity = Platform.getCurrentActionBarActivity();
				if (activity != null) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							app.captureSvc.start();
						}
					});
				}
			}
		};
	}
}
