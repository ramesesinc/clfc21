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
import com.rameses.clfc.android.TrackerDB;
import com.rameses.clfc.android.db.LocationTrackerDB;
import com.rameses.client.android.Platform;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;

public class XLocationTrackerDateResolverService extends Service {

//	private final String DBNAME = "clfctracker.db";
	private Timer timer;
	private TimerTask timerTask;
	private boolean serviceStarted = false;
	private ApplicationImpl app;
	private AppSettingsImpl settings;
	
	private LocationTrackerDB locationtrackerdb = new LocationTrackerDB();
	
	@Override
	public void onCreate() {
		app = (ApplicationImpl) Platform.getApplication();
		settings = (AppSettingsImpl) app.getAppSettings();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		startTimer();
		
		return START_STICKY;
	}
	
	public boolean getIsServiceStarted() { return serviceStarted; }
	
	public void startTimer() {
		
		if (serviceStarted == false) {
			if (timer == null) timer = new Timer();
					
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
				println("tracker date resolver running");
				if (app.getIsDateSync() == false) {
					stopTimer();
					return;
				}

				boolean flag = false;
				synchronized (TrackerDB.LOCK) {
					SQLTransaction trackerdb = new SQLTransaction( DBNAME );
					
					try {
						trackerdb.beginTransaction();
						runImpl( trackerdb );
						trackerdb.commit();
					} catch (Throwable t) {
						t.printStackTrace();
					} finally {
						trackerdb.endTransaction();
					}

					DBContext context = new DBContext( DBNAME );
					DBLocationTracker locationTracker = new DBLocationTracker();
					locationTracker.setDBContext( context );
					try {
						flag = locationTracker.hasTrackerForDateResolving();
					} catch (Throwable t) {
						t.printStackTrace();
						flag = false;
					}
				}

				
				//start brodcast location service
				LocationTrackerBroadcastService service = app.getLocationTrackerBroadcastServiceInstance();
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
				List<Map> list = locationtrackerdb.getTrackersForDateResolving();
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
					
					Timestamp timestamp = new Timestamp( timemillis );
					String sql = "UPDATE location_tracker";
					sql += " SET dtposted='" + timestamp + "',";
					sql += " trackerid='" + trackerid + "',";
					sql += " txndate='" + timestamp + "',";
					sql += " forupload=1";
					sql += " WHERE objid='" + m.get("objid").toString() + "';";
					
					sb.append( sql );
					
//					trackerdb.execute( sql ); 
					
				} 
				
				if (!sb.toString().trim().equals("")) {
					SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
					try {
						trackerdb.beginTransaction();
						trackerdb.execSQL( sb.toString() );
						trackerdb.setTransactionSuccessful();
					} catch (Exception e) {
						throw e;
					} finally {
						trackerdb.endTransaction();
					}
				}
				
				//start broadcast location service
//				LocationTrackerBroadcastService service = app.getLocationTrackerBroadcastServiceInstance();
//				if (service.getIsServiceStarted() == false) {
//					app.startService( new Intent( app, service.getClass() ) );
//				}

				boolean flag = locationtrackerdb.hasTrackerForDateResolving();
				if (flag == false) {
					stopTimer();
				}
				
			}
			/*
			private void runImpl( SQLTransaction trackerdb ) throws Exception {
				DBLocationTracker locationTracker = new DBLocationTracker();
				locationTracker.setDBContext( trackerdb.getContext() );
				locationTracker.setCloseable( false );
				
				String trackerid = settings.getTrackerid();
				List<Map> list = locationTracker.getTrackersForDateResolving();
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
					
					Timestamp timestamp = new Timestamp( timemillis );
					String sql = "UPDATE location_tracker";
					sql += " SET dtposted='" + timestamp + "',";
					sql += " trackerid='" + trackerid + "',";
					sql += " txndate='" + timestamp + "',";
					sql += " forupload=1";
					sql += " WHERE objid='" + m.get("objid").toString() + "'";
					
					trackerdb.execute( sql ); 
					
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
		ApplicationUtil.println("LocationTrackerDateResolverService", msg.toString());
	}

}
