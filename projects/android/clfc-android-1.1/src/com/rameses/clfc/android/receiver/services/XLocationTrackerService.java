package com.rameses.clfc.android.receiver.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.IBinder;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.ApplicationTimer;
import com.rameses.clfc.android.ApplicationTimerTask;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.LocationTrackerDB;
import com.rameses.clfc.android.services.LoanLocationService;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MapProxy;

public class XLocationTrackerService extends Service {

	private final int SIZE = 5;
	
	private ApplicationTimer timer;
	private ApplicationTimerTask locationTrackerTask, locationTrackerDateResolverTask, locationTrackerBroadcastTask;
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
			println("timer-> " + timer);
			if (timer == null) timer = new ApplicationTimer();
			
			int timeout = settings.getTrackerTimeout() * 1000;
		 
//			initializeTimerTask();
			initializeTrackerTasks();  
			
			if (locationTrackerTask.getIsScheduled() == false) {
				timer.purge();
				timer.schedule( locationTrackerTask, 1000, timeout );
			}
			
//			timer.schedule( locationTrackerTask, 1000, 1000 );
//			timer.schedule( timerTask1, 0, 1000 );
//			timer.schedule( timerTask2, 0, 2000 );
		
			serviceStarted = true;
		}  
		
	}
	
	public void restartTimer() {
		if (serviceStarted == true) {
			stopTimer();
		}
		startTimer();
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
	
	private void initializeTrackerTasks() {
		
		initializeLocationTrackerTask();
		initializeLocationTrackerDateResolverTask();
		initializeLocationTrackerBroadcastTask();
	}
	
	
	private void initializeLocationTrackerTask() {
		if (locationTrackerTask == null) {
			locationTrackerTask = new ApplicationTimerTask() {
				
				UserProfile profile;				
				public void run() {
					println("tracker service running");
					profile = SessionContext.getProfile();
					if (profile == null) return;
					
					try {
						runImpl();
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				
				private void runImpl() throws Exception {
					String trackerid = settings.getTrackerid();
					if (trackerid == null || "".equals( trackerid )) return; 
					
					String collectorid = profile.getUserId();
					if (collectorid == null || "".equals( collectorid )) return;

					Location location = NetworkLocationProvider.getLocation();
					String val1 = "0", val2 = "0";
					if (location != null) {
						val1 = location.getLongitude() + "";
						val2 = location.getLatitude() + "";
					}
					BigDecimal lng = new BigDecimal( val1 );
					BigDecimal lat = new BigDecimal( val2 );
					
					int seqno = locationtrackerdb.getLastSeqnoByCollectorid( collectorid );
					
					Map params = new HashMap();
					params.put("objid", "TRCK" + UUID.randomUUID());
					params.put("seqno", seqno+1);
					params.put("txndate", app.getServerDate().toString());
					params.put("trackerid", trackerid);
					params.put("collectorid", collectorid);
					params.put("lng", lng.toString());
					params.put("lat", lat.toString());
					params.put("forupload", 0);
					
					Map map = settings.getAll();
					
					long timedifference = 0L;
					if (map.containsKey("timedifference")) {
						timedifference = settings.getLong("timedifference");
					}
					params.put("timedifference", timedifference);
					
					Calendar cal = Calendar.getInstance();				
					Date phonedate = new Timestamp(cal.getTimeInMillis());
					params.put("dtsaved", phonedate.toString());
					
					String sql = ApplicationDBUtil.createInsertSQLStatement("location_tracker", params);
										
					SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
					try {
						trackerdb.beginTransaction();
						trackerdb.execSQL( sql );
						trackerdb.setTransactionSuccessful();
					} catch (Throwable t) {
						t.printStackTrace();
					} finally {
						trackerdb.endTransaction();
					}
					
					scheduleTrackerDateResolverTask();
				}
			};
		}
	}
	
	private void initializeLocationTrackerDateResolverTask() {
		if (locationTrackerDateResolverTask == null) {
			locationTrackerDateResolverTask = new ApplicationTimerTask() {
				
				ApplicationTimerTask self = this;
				
				public void run() {
					println("location tracker data resolver running");
					if (app.getIsDateSync() == false) {
						self.cancel();
						self.setIsScheduled( false );
						return;
					}
					
					try {					
						runImpl();	
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				
				private void runImpl() throws Exception {
					StringBuilder sb = new StringBuilder();
					
					String trackerid = settings.getTrackerid();
					if (trackerid == null || "".equals( trackerid )) return;
					
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
//					LocationTrackerBroadcastService service = app.getLocationTrackerBroadcastServiceInstance();
//					if (service.getIsServiceStarted() == false) {
//						app.startService( new Intent( app, service.getClass() ) );
//					}
					scheduleTrackerBroadcastTask();

					boolean flag = locationtrackerdb.hasTrackerForDateResolving();
					if (flag == false) {
						self.cancel();
						self.setIsScheduled( false );
//						stopTimer();
					}
				}
				
				
			};
		}
	}
	
	private void initializeLocationTrackerBroadcastTask() {
		if (locationTrackerBroadcastTask == null) {
			locationTrackerBroadcastTask = new ApplicationTimerTask() {
				
				ApplicationTimerTask self = this;
				
				public void run() {
					println("location tracker broadcast running");
					if (app.getIsDateSync() == false) {
						self.cancel();
						self.setIsScheduled( false );
						return;
					}
					
					try {					
						runImpl();	
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				
				private void runImpl() throws Exception {

					StringBuilder sb = new StringBuilder();
					
					List<Map> list = locationtrackerdb.getForUploadLocationTrackers();

//					int size = (list.size() < SIZE - 1? list.size() : SIZE - 1);
					for (Map map : list) {
						MapProxy proxy = new MapProxy( map );
						String objid = proxy.getString("objid");
						
						int networkStatus = app.getNetworkStatus();
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
							String str = response.get("response").toString().toLowerCase();
							if (str.equals("success")) {
								
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
						self.cancel();
						self.setIsScheduled( false );
					}
				}
				/*
				private void xrunImpl() throws Exception {
					StringBuilder sb = new StringBuilder();
					
					String trackerid = settings.getTrackerid();
					if (trackerid == null || "".equals( trackerid )) return;
					
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
//					LocationTrackerBroadcastService service = app.getLocationTrackerBroadcastServiceInstance();
//					if (service.getIsServiceStarted() == false) {
//						app.startService( new Intent( app, service.getClass() ) );
//					}

					boolean flag = locationtrackerdb.hasTrackerForDateResolving();
					if (flag == false) {
						self.cancel();
						self.setIsScheduled( false );
//						stopTimer();
					}
				}
				*/
				
			};
		}
	}
	
	private void scheduleTrackerDateResolverTask() {
		initializeLocationTrackerDateResolverTask();
		println("tracker date resolver scheduled: " + locationTrackerDateResolverTask.getIsScheduled());
		if (locationTrackerDateResolverTask.getIsScheduled() == false) {
			timer.purge();
			timer.schedule( locationTrackerDateResolverTask, 1000 );
		}
	}
	
	private void scheduleTrackerBroadcastTask() {
		initializeLocationTrackerBroadcastTask();
		println("tracker broadcast scheduled: " + locationTrackerBroadcastTask.getIsScheduled());
		if (locationTrackerBroadcastTask.getIsScheduled() == false) {
			timer.purge();
			timer.schedule( locationTrackerBroadcastTask, 1000 );
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void println( Object msg ) {
		ApplicationUtil.println( "LocationTrackerService", msg.toString() );
	}
	
}
