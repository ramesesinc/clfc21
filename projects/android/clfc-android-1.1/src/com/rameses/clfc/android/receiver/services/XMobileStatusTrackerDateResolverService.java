package com.rameses.clfc.android.receiver.services;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.StatusDB;
import com.rameses.clfc.android.db.MobileStatusTrackerDB;
import com.rameses.client.android.Platform;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;

public class XMobileStatusTrackerDateResolverService extends Service {

//	private final String DBNAME = "clfcstatus.db";
	private Timer timer;
	private TimerTask timerTask;
	private boolean serviceStarted = false;
	private ApplicationImpl app;
	private AppSettingsImpl settings;
	
	private MobileStatusTrackerDB mobilestatustrackerdb = new MobileStatusTrackerDB();
	
	public XMobileStatusTrackerDateResolverService() {
		app = (ApplicationImpl) Platform.getApplication();
		settings = (AppSettingsImpl) app.getAppSettings();
	}
	
	@Override
	public int onStartCommand( Intent intent, int flags, int startId ) {
		super.onStartCommand(intent, flags, startId);
		
		startTimer();
		
		return START_STICKY;
	}
	
	public boolean getIsServiceStarted() { return serviceStarted; }
	
	public void startTimer() {
		
		if (serviceStarted == false) {
			if (timer == null) timer = new Timer();
			
			int timeout = settings.getTrackerTimeout() * 1000;
		
			initializeTimerTask();
			
			timer.schedule( timerTask, 1000, 1000 );
		
			serviceStarted = true;
		} 
		
	}
	
	public void restartTimer() {
		if (serviceStarted == true) {
			stopTimer();
		}
		startTimer();
	}

	private void initializeTimerTask() {
		timerTask = new TimerTask() {
			
			public void run() {
				if (app.getIsDateSync() == false) {
					stopTimer();
					return;
				}
				
				try {
					runImpl();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			/*
			public void xrun() {
				println("status date resolver service running");
				println("date sync: " + app.getIsDateSync());
				if (app.getIsDateSync() == false) {
					stopTimer();
					return;
				}

				boolean flag = false;
				synchronized (StatusDB.LOCK) {
					SQLTransaction statusdb = new SQLTransaction( DBNAME );
					
					try {
						statusdb.beginTransaction();
						runImpl( statusdb );
						statusdb.commit();
					} catch (Throwable t) {
						t.printStackTrace();
					} finally {
						statusdb.endTransaction();
					}

					DBContext context = new DBContext( DBNAME );
					DBMobileStatusTracker statusTracker = new DBMobileStatusTracker();
					statusTracker.setDBContext( context );
					try {
						flag = statusTracker.hasTrackerForDateResolving();
					} catch (Throwable t) {
						t.printStackTrace();
						flag = false;
					}
					
				}
				
				//start broadcast status service
				println("start status broadcast service");
				MobileStatusTrackerBroadcastService service = app.getMobileStatusTrackerBroadcastServiceInstance();
				if (service.getIsServiceStarted() == false) {
					app.startService( new Intent( app, service.getClass() ) );
				}
				
				if (flag == false) {
					stopTimer();
					return;
				}
			}
			*/
			private void runImpl() throws Exception {
				StringBuilder sb = new StringBuilder();
				
				String trackerid = settings.getTrackerid();
				List<Map> list = mobilestatustrackerdb.getTrackersForDateResolving();
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
					String sql = "UPDATE mobile_status";
					sql += " SET dtposted='" + timestamp + "',";
					sql += " trackerid='" + trackerid + "',";
					sql += " txndate='" + timestamp + "',";
					sql += " forupload=1";
					sql += " WHERE objid='" + m.get("objid").toString() + "'";

					sb.append( sql );
				} 
				
				if (!sb.toString().trim().equals("")) {
					SQLiteDatabase statusdb = ApplicationDatabase.getStatusWritableDB();
					try {
						statusdb.beginTransaction();
						statusdb.execSQL( sb.toString() );
						statusdb.setTransactionSuccessful();
					} catch (Exception e) {
						throw e;
					} finally {
						statusdb.endTransaction();
					}
				}

				//start broadcast status service
				println("start status broadcast service");
//				MobileStatusTrackerBroadcastService service = app.getMobileStatusTrackerBroadcastServiceInstance();
//				if (service.getIsServiceStarted() == false) {
//					app.startService( new Intent( app, service.getClass() ) );
//				}

				boolean flag = mobilestatustrackerdb.hasTrackerForDateResolving();				
				if (flag == false) {
					stopTimer();
				}
			}
			/*
			private void runImpl( SQLTransaction statusdb ) throws Exception {
				DBMobileStatusTracker statusTracker = new DBMobileStatusTracker();
				statusTracker.setDBContext( statusdb.getContext() );
				statusTracker.setCloseable( false );
				
				String trackerid = settings.getTrackerid();
				List<Map> list = statusTracker.getTrackersForDateResolving();
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
					String sql = "UPDATE mobile_status";
					sql += " SET dtposted='" + timestamp + "',";
					sql += " trackerid='" + trackerid + "',";
					sql += " txndate='" + timestamp + "',";
					sql += " forupload=1";
					sql += " WHERE objid='" + m.get("objid").toString() + "'";

					statusdb.execute( sql ); 
					
				} 
			}
			*/
		};
	}
	
	private void stopTimer() {
		if (serviceStarted == true) {
			if (timer != null) {
				timer.cancel();
				timer = null;
				stopSelf();
			}
			serviceStarted = false;
		}
 	}	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void println( Object msg ) {
		ApplicationUtil.println("MobileStatusTrackerDateResolverService", msg.toString());
	}
}
