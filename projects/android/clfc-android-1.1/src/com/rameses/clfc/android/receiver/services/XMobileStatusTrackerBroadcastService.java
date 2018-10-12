package com.rameses.clfc.android.receiver.services;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.rameses.clfc.android.StatusDB;
import com.rameses.clfc.android.db.MobileStatusTrackerDB;
import com.rameses.clfc.android.services.MobileStatusService;
import com.rameses.client.android.Platform;
import com.rameses.db.android.DBContext;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MapProxy;

public class XMobileStatusTrackerBroadcastService extends Service {

//	private final String DBNAME = "clfcstatus.db";
	private final int SIZE = 5;
	
	private Timer timer;
	private TimerTask timerTask;
	private boolean serviceStarted = false;
	private ApplicationImpl app;
	
	private MobileStatusTrackerDB mobilestatustrackerdb = new MobileStatusTrackerDB();
	
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
				println("status tracker service broadcast running");
				List<Map> list = new ArrayList<Map>();
				
				synchronized (StatusDB.LOCK) {
					DBContext ctx = new DBContext( DBNAME );
					statusTracker.setDBContext( ctx );
					 
					try {
						list = statusTracker.getForUploadLocationTrackers( SIZE );
					} catch (Throwable t) {
						t.printStackTrace();
						list = new ArrayList<Map>();
					}
				}
				
				runImpl( list );
				
				boolean flag = true;
				synchronized (StatusDB.LOCK) {
					DBContext ctx = new DBContext( DBNAME );
					statusTracker.setDBContext( ctx );
					try {
						list = statusTracker.getForUploadLocationTrackers( 1 );
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
				
				List<Map> list = mobilestatustrackerdb.getForUploadLocationTrackers( SIZE );

				for (Map m : list) {
					MapProxy proxy = new MapProxy( m );
					String objid = proxy.getString("objid");
					
					int networkStatus = app.getNetworkStatus();
					if (networkStatus == 3) break;
					
					Map params = new HashMap();
					params.put("objid", objid);
					params.put("trackerid", proxy.getString("trackerid"));
					params.put("txndate", proxy.getString("txndate"));
					params.put("lng", new BigDecimal( proxy.getString("lng") ));
					params.put("lat", new BigDecimal( proxy.getString("lat") ));
					params.put("state", 1);
					params.put("wifi", proxy.getInteger("wifi"));
					params.put("mobiledata", proxy.getInteger("mobiledata"));
					params.put("gps", proxy.getInteger("gps"));
					params.put("network", proxy.getInteger("network"));
					params.put("statustext", proxy.getString("phonestatus"));
					params.put("prevlng", new BigDecimal( proxy.getString("prevlng") ));
					params.put("prevlat", new BigDecimal( proxy.getString("prevlat") ));
										
					Map param = new HashMap();
					String enc = new Base64Cipher().encode(params);
					param.put("encrypted", enc);
					
					Map response = new HashMap();
					MobileStatusService service;
					for (int j=0; j<10; j++) {
						try {
							service = new MobileStatusService();
							response = service.postMobileStatusEncrypt( param );
					 		break;
						} catch (Throwable e) { e.printStackTrace();}
					} 
					 
					if (response != null && response.containsKey("response")) {
						String str = response.get("response").toString().toLowerCase();
						if (str.equals("success")) {
							
							String sql = "delete from mobile_status ";
							sql += "where objid='" + objid + "';";
							
							sb.append( sql );
						}
					}
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
				
				boolean flag = true;
				list = mobilestatustrackerdb.getForUploadLocationTrackers( 1 );
				if (list == null || list.isEmpty()) {
					flag = false;
				}
				
				if (flag == false) {
					stopTimer();
				}
			}
			/*
			private void runImpl( List<Map> list ) {
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
						params.put("lng", new BigDecimal( proxy.getString("lng") ));
						params.put("lat", new BigDecimal( proxy.getString("lat") ));
						params.put("state", 1);
						params.put("wifi", proxy.getInteger("wifi"));
						params.put("mobiledata", proxy.getInteger("mobiledata"));
						params.put("gps", proxy.getInteger("gps"));
						params.put("network", proxy.getInteger("network"));
						params.put("statustext", proxy.getString("phonestatus"));
//						params.put("prevlng", proxy.getDouble("prevlng"));
//						params.put("prevlat", proxy.getDouble("prevlat"));
//						params.put("prevlng", Double.parseDouble(proxy.getString("prevlng")));
//						params.put("prevlat", Double.parseDouble(proxy.getString("prevlat")));
						params.put("prevlng", new BigDecimal( proxy.getString("prevlng") ));
						params.put("prevlat", new BigDecimal( proxy.getString("prevlat") ));
						
						
						Map param = new HashMap();
						String enc = new Base64Cipher().encode(params);
						param.put("encrypted", enc);
						
						Map response = new HashMap();
						MobileStatusService service;
						for (int j=0; j<10; j++) {
							try {
//								println("before sending");
								service = new MobileStatusService();
								response = service.postMobileStatusEncrypt( param );
//								println("after sending");
						 		break;
							} catch (Throwable e) { e.printStackTrace();}
						} 
						 
//						if (response != null && response.containsKey("response")) {
//							String str = response.get("response").toString();
//							println("response " + str);
//							if (str.toLowerCase().equals("success")) {
//								SQLTransaction trackerdb = new SQLTransaction(DBNAME);
//								try { 
//									trackerdb.beginTransaction();
//									trackerdb.delete("mobile_status", "objid=?", new Object[]{proxy.getString("objid")});
//									trackerdb.commit();
//								} catch (Throwable t) {
//									t.printStackTrace();
//								} finally {
//									trackerdb.endTransaction();
//								}
//							}
//						}
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
		ApplicationUtil.println("MobileStatusTrackerBroadcastService", msg.toString());
	}

}
