package com.rameses.clfc.android.receiver.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.db.LocationTrackerDB;
import com.rameses.clfc.android.services.LoanLocationService;
import com.rameses.client.android.Platform;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MapProxy;

public class XLocationTrackerBroadcastService extends Service {

//	private final String DBNAME = "clfctracker.db";
	private final int SIZE = 6;
	
	private Timer timer;
	private TimerTask timerTask;
	private boolean serviceStarted = false;
	private ApplicationImpl app;
	
	private LocationTrackerDB locationtrackerdb = new LocationTrackerDB();
	
	@Override
	public void onCreate() {
		app = (ApplicationImpl) Platform.getApplication();
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
				try {
					runImpl();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			/*
			public void xrun() {
				println("tracker service broadcast running");
				List<Map> list = new ArrayList<Map>();
				
				synchronized (TrackerDB.LOCK) {
					DBContext ctx = new DBContext( DBNAME );
					locationTracker.setDBContext( ctx );
					
					try {
						list = locationTracker.getForUploadLocationTrackers( SIZE );
					} catch (Throwable t) {
						t.printStackTrace();
						list = new ArrayList<Map>();
					}
				}
				
				runImpl( list );
				
				boolean flag = true;
				synchronized (TrackerDB.LOCK) {
					DBContext ctx = new DBContext( DBNAME );
					locationTracker.setDBContext( ctx );
					try {
						list = locationTracker.getForUploadLocationTrackers( 1 );
						if (list.isEmpty() || list.size() == 0) {
							flag = false;
						}
					} catch (Throwable t) {
						t.printStackTrace();
						flag = false;
					}
				}
				
				if (flag == false) {
					stopTimer();
				}
			}
			*/
			private void runImpl() throws Exception {
				StringBuilder sb = new StringBuilder();
				
				List<Map> list = locationtrackerdb.getForUploadLocationTrackers( SIZE );

				int size = (list.size() < SIZE - 1? list.size() : SIZE - 1);
				int networkStatus = 0;
				for (int i=0; i<size; i++) {
					MapProxy proxy = new MapProxy((Map) list.get(i));
					String objid = proxy.getString("objid");
					
					networkStatus = app.getNetworkStatus();
					if (networkStatus == 3) break;
					
					Map params = new HashMap();
					params.put("objid", objid);
					params.put("trackerid", proxy.getString("trackerid"));
					params.put("txndate", proxy.getString("txndate"));
					params.put("lng", new BigDecimal(proxy.getString("lng")));
					params.put("lat", new BigDecimal(proxy.getString("lat")));
					params.put("state", 1);
					
					Map param = new HashMap();
					String enc = new Base64Cipher().encode(params);
					param.put("encrypted", enc);
					
					Map response = new HashMap();
					
					LoanLocationService service;
					for (int j=0; j<10; j++) {
						try {
							service = new LoanLocationService();
							response = service.postLocationEncrypt( param );
						} catch (Throwable t) {;}
					}
					
					if (response != null && response.containsKey("response")) {
						String str = response.get("response").toString();
						if (str.toLowerCase().equals("success")) {
							
							String sql = "delete from location_tracker where objid='" + objid + "';";
							sb.append( sql );
						}
					}
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
				
				boolean flag = true;
				list = locationtrackerdb.getForUploadLocationTrackers( 1 );
				if (list == null || list.isEmpty()) {
					flag = true;
				}
				
				if (flag == false) {
					stopTimer();
				}
			}
			
			/*
			private void xrunImpl( List<Map> list ) {
				if (!list.isEmpty()) {
					int listSize = (list.size() < SIZE - 1? list.size() : SIZE - 1);
					int networkStatus = 0;
					for (Map m : list) {
						networkStatus = app.getNetworkStatus();
						if (networkStatus == 3) break;
						
						MapProxy proxy = new MapProxy( m );
						
						Map params = new HashMap();
						params.put("objid", proxy.getString("objid"));
						params.put("trackerid", proxy.getString("trackerid"));
						params.put("txndate", proxy.getString("txndate"));
//						params.put("lng", proxy.getDouble("lng"));
//						params.put("lat", proxy.getDouble("lat"));
//						params.put("lng", Double.parseDouble(proxy.getString("lng")));
//						params.put("lat", Double.parseDouble(proxy.getString("lat")));
						params.put("lng", new BigDecimal(proxy.getString("lng")));
						params.put("lat", new BigDecimal(proxy.getString("lat")));
						params.put("state", 1);
						
						Map param = new HashMap();
						String enc = new Base64Cipher().encode(params);
						param.put("encrypted", enc);
						
						Map response = new HashMap();
						
						LoanLocationService service;
						for (int i=0; i<10; i++) {
							try {
								service = new LoanLocationService();
								response = service.postLocationEncrypt( param );
							} catch (Throwable t) {;}
						}
						
						if (response != null && response.containsKey("response")) {
							String str = response.get("response").toString();
							if (str.toLowerCase().equals("success")) {
								SQLTransaction trackerdb = new SQLTransaction( DBNAME );
								locationTracker.setDBContext(trackerdb.getContext());
								try {
									trackerdb.beginTransaction();
									trackerdb.delete("location_tracker", "objid=?", new Object[]{proxy.getString("objid")});
									trackerdb.commit();
								} catch (Throwable t) {
									t.printStackTrace();
								} finally {
									trackerdb.endTransaction();
								}
							}
						}
					}
					
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
		ApplicationUtil.println("LocationTrackerBroadcastService", msg.toString());
	}
}
