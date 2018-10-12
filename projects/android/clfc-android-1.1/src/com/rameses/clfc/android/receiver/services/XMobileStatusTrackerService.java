package com.rameses.clfc.android.receiver.services;

import java.lang.reflect.Method;
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
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.ApplicationTimer;
import com.rameses.clfc.android.ApplicationTimerTask;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.MobileStatusTrackerDB;
import com.rameses.clfc.android.services.MobileStatusService;
import com.rameses.client.android.AppSettings;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MapProxy;

public class XMobileStatusTrackerService extends Service {

//	private final int SIZE = 5;
	
	private ApplicationTimer timer;
//	private ApplicationTimerTask timerTask;
	private ApplicationTimerTask statusTrackerTask, statusTrackerDateResolverTask, statusTrackerBroadcastTask;
	private ApplicationImpl app;
	private AppSettingsImpl settings;

	private boolean serviceStarted = false;
	private MobileStatusTrackerDB mobilestatustrackerdb = new MobileStatusTrackerDB();
		
	
	public XMobileStatusTrackerService() {
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
			if (timer == null) timer = new ApplicationTimer();
			
			int timeout = settings.getTrackerTimeout() * 1000;
		
			initializeTrackerTask();
			
			if (statusTrackerTask.getIsScheduled() == false) {
				timer.purge();
				timer.schedule( statusTrackerTask, 1000, timeout );
			}
			
//			timer.schedule( timerTask, 1000, 1000 );
		
			serviceStarted = true;
		} 
		
	}
	
	public void restartTimer() {
		if (serviceStarted == true) {
			stopTimer();
		}
		startTimer();
	}

	private void initializeTrackerTask() {
		initializeStatusTrackerTask();
		initializeStatusTrackerDateResolverTask();
		initializeStatusTrackerBroadcastTask();
	}
	
	private void initializeStatusTrackerTask() {
		if (statusTrackerTask == null) {
			statusTrackerTask = new ApplicationTimerTask() {
				
				UserProfile profile;
				
				public void run() {

					profile = SessionContext.getProfile();
					if (profile == null) return;
					
					try {
						runImpl();
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				
				private void runImpl() throws Exception {
					String collectorid = profile.getUserId();
					if (collectorid == null || "".equals( collectorid )) return;
					
					String trackerid = settings.getTrackerid();
					if (trackerid == null || "".equals( trackerid )) return;
					
					String val1 = "0", val2 = "0";
					Location location = NetworkLocationProvider.getLocation();
					if (location != null) {
						val1 = location.getLongitude() + "";
						val2 = location.getLatitude() + "";
					}
					
					BigDecimal lng = new BigDecimal( val1 );
					BigDecimal lat = new BigDecimal( val2 );
					
					Map params = new HashMap();
					params.put("objid", "TRCK" + UUID.randomUUID());
					params.put("trackerid", trackerid);
					params.put("collectorid", collectorid);
					params.put("txndate", app.getServerDate().toString());
					params.put("lng", lng.toString());
					params.put("lat", lat.toString());
					params.put("prevlng", lng.toString());
					params.put("prevlat", lat.toString());
					params.put("forupload", 0);

					boolean isWifiEnabled = ApplicationUtil.getWifiManager().isWifiEnabled();
					params.put("wifi", (isWifiEnabled == true? 1 : 0));
					
					LocationManager locationmngr = ApplicationUtil.getLocationManager();
					params.put("gps", (locationmngr.isProviderEnabled(LocationManager.GPS_PROVIDER) == true? 1 : 0));
					params.put("network", (locationmngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true? 1 : 0));
					
					boolean mobileDataEnabled = false; // Assume disabled
					ConnectivityManager cm = ApplicationUtil.getConnectivityManager();
					try {
					    Class cmClass = Class.forName(cm.getClass().getName());
					    Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
					    method.setAccessible( true ); // Make the method callable
					// get the setting for "mobile data"
					    mobileDataEnabled = (Boolean) method.invoke(cm);
					} catch (Exception e) {
					    // Some problem accessible private API
					// TODO do whatever error handling you want here
					}
					
					params.put("mobiledata", (mobileDataEnabled == true? 1 : 0));
					
					Calendar cal = Calendar.getInstance();
					Date phonedate = new Timestamp(cal.getTimeInMillis());
					params.put("dtsaved", phonedate.toString());
					
					AppSettings settings = Platform.getApplication().getAppSettings();
					Map map = settings.getAll();
					
					long timedifference = 0L;
					if (map.containsKey("timedifference")) {
						timedifference = settings.getLong("timedifference");
					}
					params.put("timedifference", timedifference);
					
					String sql = ApplicationDBUtil.createInsertSQLStatement("mobile_status", params);
					
					SQLiteDatabase statusdb = ApplicationDatabase.getStatusWritableDB();
					try {
						statusdb.beginTransaction();
						statusdb.execSQL( sql );
						statusdb.setTransactionSuccessful();
					} catch (Exception e) {
						throw e;
					} finally  {
						statusdb.endTransaction();
					}

					scheduleStatusTrackerDateResolverTask();
//					MobileStatusTrackerDateResolverService service = app.getMobileStatusTrackerDateResolverServiceInstance();
//					if (service.getIsServiceStarted() == false) {
//						app.startService( new Intent( app, service.getClass() ) );
//					}
				}
				
				/*
				public void xrun() {
					println("status tracker running");
					
					profile = SessionContext.getProfile();
					if (profile == null) return;
					
					try {
						runImpl();
					} catch (Throwable t) {
						t.printStackTrace();
					}
					
//					runImpl();
//					MobileStatusTrackerDateResolverService service = app.getMobileStatusTrackerDateResolverServiceInstance();
//					if (service.getIsServiceStarted() == false) {
//						app.startService( new Intent( app, service.getClass() ) );
//					}
				}
				*/
				/*
				private void xrunImpl() throws Exception {
					String collectorid = profile.getUserId();
					String trackerid = settings.getTrackerid();				
					
					String val1 = "0", val2 = "0";
					Location location = NetworkLocationProvider.getLocation();
					if (location != null) {
						val1 = location.getLongitude() + "";
						val2 = location.getLatitude() + "";
					}
					
					BigDecimal lng = new BigDecimal( val1 );
					BigDecimal lat = new BigDecimal( val2 );
					
					Map params = new HashMap();
					params.put("objid", "TRCK" + UUID.randomUUID());
					params.put("trackerid", trackerid);
					params.put("collectorid", collectorid);
					params.put("txndate", app.getServerDate().toString());
					params.put("lng", lng.toString());
					params.put("lat", lat.toString());
					params.put("prevlng", lng.toString());
					params.put("prevlat", lat.toString());
					params.put("forupload", 0);

					boolean isWifiEnabled = ApplicationUtil.getWifiManager().isWifiEnabled();
					params.put("wifi", (isWifiEnabled == true? 1 : 0));
					
					LocationManager locationmngr = ApplicationUtil.getLocationManager();
					params.put("gps", (locationmngr.isProviderEnabled(LocationManager.GPS_PROVIDER) == true? 1 : 0));
					params.put("network", (locationmngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true? 1 : 0));
					
					boolean mobileDataEnabled = false; // Assume disabled
					ConnectivityManager cm = ApplicationUtil.getConnectivityManager();
					try {
					    Class cmClass = Class.forName(cm.getClass().getName());
					    Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
					    method.setAccessible( true ); // Make the method callable
					// get the setting for "mobile data"
					    mobileDataEnabled = (Boolean) method.invoke(cm);
					} catch (Exception e) {
					    // Some problem accessible private API
					// TODO do whatever error handling you want here
					}
					
					params.put("mobiledata", (mobileDataEnabled == true? 1 : 0));
					
					Calendar cal = Calendar.getInstance();
					Date phonedate = new Timestamp(cal.getTimeInMillis());
					params.put("dtsaved", phonedate.toString());
					
					AppSettings settings = Platform.getApplication().getAppSettings();
					Map map = settings.getAll();
					
					long timedifference = 0L;
					if (map.containsKey("timedifference")) {
						timedifference = settings.getLong("timedifference");
					}
					params.put("timedifference", timedifference);
					
					String sql = ApplicationDBUtil.createInsertSQLStatement("mobile_status", params);
					
					SQLiteDatabase statusdb = ApplicationDatabase.getStatusWritableDB();
					try {
						statusdb.beginTransaction();
						statusdb.execSQL( sql );
						statusdb.setTransactionSuccessful();
					} catch (Exception e) {
						throw e;
					} finally  {
						statusdb.endTransaction();
					}

					MobileStatusTrackerDateResolverService service = app.getMobileStatusTrackerDateResolverServiceInstance();
					if (service.getIsServiceStarted() == false) {
						app.startService( new Intent( app, service.getClass() ) );
					}
				}
				*/
			};
		}
	}
	
	private void initializeStatusTrackerDateResolverTask() {
		if (statusTrackerDateResolverTask == null) {
			statusTrackerDateResolverTask = new ApplicationTimerTask() {
				
				ApplicationTimerTask self = this;
				
				public void run() {
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
					scheduleStatusTrackerBroadcastTask();
					/*
					MobileStatusTrackerBroadcastService service = app.getMobileStatusTrackerBroadcastServiceInstance();
					if (service.getIsServiceStarted() == false) {
						app.startService( new Intent( app, service.getClass() ) );
					}
					*/

//					boolean flag = mobilestatustrackerdb.hasTrackerForDateResolving();				
//					if (flag == false) {
//						self.cancel();
//						self.setIsScheduled( false );
//					}
					self.setIsScheduled( false );
				}
			};
		}
	}
	
	private void initializeStatusTrackerBroadcastTask() {
		if (statusTrackerBroadcastTask == null) {
			statusTrackerBroadcastTask = new ApplicationTimerTask() {
				
				ApplicationTimerTask self = this;
				
				public void run() {
					try {
						runImpl();
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				
				private void runImpl() throws Exception {

					StringBuilder sb = new StringBuilder();
					
//					List<Map> list = mobilestatustrackerdb.getForUploadLocationTrackers( SIZE );
					List<Map> list = mobilestatustrackerdb.getForUploadLocationTrackers();

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
					
//					boolean flag = true;
//					list = mobilestatustrackerdb.getForUploadLocationTrackers( 1 );
//					if (list == null || list.isEmpty()) {
//						flag = false;
//					}
//					
//					if (flag == false) {
//						self.cancel();
//						self.setIsScheduled( false );
//					}
					self.setIsScheduled( false );
				}
				
			};
		}
	}
	
	private void scheduleStatusTrackerDateResolverTask() {
		initializeStatusTrackerDateResolverTask();
		if (statusTrackerDateResolverTask.getIsScheduled() == false) {
			timer.purge();
			timer.schedule( statusTrackerDateResolverTask, 1000 );
		}
	}
	
	private void scheduleStatusTrackerBroadcastTask() {
		initializeStatusTrackerBroadcastTask();
		if (statusTrackerBroadcastTask.getIsScheduled() == false) {
			timer.purge();
			timer.schedule( statusTrackerBroadcastTask, 1000 );
		}
	}
	
	/*
	private void xinitializeTimerTask() {
		timerTask = new TimerTask() {
			
			UserProfile profile;
			
			public void run() {
				println("status tracker running");
				
				profile = SessionContext.getProfile();
				if (profile == null) return;
				
				try {
					runImpl();
				} catch (Throwable t) {
					t.printStackTrace();
				}
				
//				runImpl();
//				MobileStatusTrackerDateResolverService service = app.getMobileStatusTrackerDateResolverServiceInstance();
//				if (service.getIsServiceStarted() == false) {
//					app.startService( new Intent( app, service.getClass() ) );
//				}
			}
			
			private void runImpl() throws Exception {
				String collectorid = profile.getUserId();
				String trackerid = settings.getTrackerid();				
				
				String val1 = "0", val2 = "0";
				Location location = NetworkLocationProvider.getLocation();
				if (location != null) {
					val1 = location.getLongitude() + "";
					val2 = location.getLatitude() + "";
				}
				
				BigDecimal lng = new BigDecimal( val1 );
				BigDecimal lat = new BigDecimal( val2 );
				
				Map params = new HashMap();
				params.put("objid", "TRCK" + UUID.randomUUID());
				params.put("trackerid", trackerid);
				params.put("collectorid", collectorid);
				params.put("txndate", app.getServerDate().toString());
				params.put("lng", lng.toString());
				params.put("lat", lat.toString());
				params.put("prevlng", lng.toString());
				params.put("prevlat", lat.toString());
				params.put("forupload", 0);

				boolean isWifiEnabled = ApplicationUtil.getWifiManager().isWifiEnabled();
				params.put("wifi", (isWifiEnabled == true? 1 : 0));
				
				LocationManager locationmngr = ApplicationUtil.getLocationManager();
				params.put("gps", (locationmngr.isProviderEnabled(LocationManager.GPS_PROVIDER) == true? 1 : 0));
				params.put("network", (locationmngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true? 1 : 0));
				
				boolean mobileDataEnabled = false; // Assume disabled
				ConnectivityManager cm = ApplicationUtil.getConnectivityManager();
				try {
				    Class cmClass = Class.forName(cm.getClass().getName());
				    Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
				    method.setAccessible( true ); // Make the method callable
				// get the setting for "mobile data"
				    mobileDataEnabled = (Boolean) method.invoke(cm);
				} catch (Exception e) {
				    // Some problem accessible private API
				// TODO do whatever error handling you want here
				}
				
				params.put("mobiledata", (mobileDataEnabled == true? 1 : 0));
				
				Calendar cal = Calendar.getInstance();
				Date phonedate = new Timestamp(cal.getTimeInMillis());
				params.put("dtsaved", phonedate.toString());
				
				AppSettings settings = Platform.getApplication().getAppSettings();
				Map map = settings.getAll();
				
				long timedifference = 0L;
				if (map.containsKey("timedifference")) {
					timedifference = settings.getLong("timedifference");
				}
				params.put("timedifference", timedifference);
				
				String sql = ApplicationDBUtil.createInsertSQLStatement("mobile_status", params);
				
				SQLiteDatabase statusdb = ApplicationDatabase.getStatusWritableDB();
				try {
					statusdb.beginTransaction();
					statusdb.execSQL( sql );
					statusdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally  {
					statusdb.endTransaction();
				}

				MobileStatusTrackerDateResolverService service = app.getMobileStatusTrackerDateResolverServiceInstance();
				if (service.getIsServiceStarted() == false) {
					app.startService( new Intent( app, service.getClass() ) );
				}
			}

//			private void xrunImpl() {
//				
//				String collectorid = profile.getUserId();
//				String date = ApplicationUtil.formatDate(app.getServerDate(), "yyyy-MM-dd");
//				
////				boolean flag = false;
////				synchronized (MainDB.LOCK) {
////					DBContext clfcdb = new DBContext("clfc.db");
////					DBCollectionGroup collectionGroup = new DBCollectionGroup();
////					collectionGroup.setDBContext( clfcdb );
////					try {
////						flag = collectionGroup.hasCollectionGroupByCollectorAndDate( collectorid, date );
////					} catch (Throwable t) {
////						t.printStackTrace();
////						flag = false;
////					}
////				}
////				
////				if (!flag) return;
//
//				String trackerid = settings.getTrackerid();
//				
//				synchronized (StatusDB.LOCK) {
//					SQLTransaction statusdb = new SQLTransaction("clfcstatus.db");
//					try {
//						statusdb.beginTransaction();
//						execTracker( statusdb, trackerid, collectorid );
//						statusdb.commit();
//					} catch (Throwable t) {
//						t.printStackTrace();
//					} finally {
//						statusdb.endTransaction();
//					}					
//				}
//				
//			}

//			private void execTracker( SQLTransaction statusdb, String trackerid, String collectorid ) throws Exception {
//
//				String val1 = "0", val2 = "0";
//				Location location = NetworkLocationProvider.getLocation();
//				if (location != null) {
//					val1 = location.getLongitude() + "";
//					val2 = location.getLatitude() + "";
//				}
//				
//				BigDecimal lng = new BigDecimal( val1 );
//				BigDecimal lat = new BigDecimal( val2 );
//				
//				Map params = new HashMap();
//
//				params.put("objid", "TRCK" + UUID.randomUUID());
//				params.put("trackerid", trackerid);
//				params.put("collectorid", collectorid);
//				params.put("txndate", app.getServerDate().toString());
////				params.put("lng", lng);
////				params.put("lat", lat);
////				params.put("prevlng", prevlng);
////				params.put("prevlat", prevlat);
//				params.put("lng", lng.toString());
//				params.put("lat", lat.toString());
//				params.put("prevlng", lng.toString());
//				params.put("prevlat", lat.toString());
//				params.put("forupload", 0);
//
//				boolean isWifiEnabled = ApplicationUtil.getWifiManager().isWifiEnabled();
//				params.put("wifi", (isWifiEnabled == true? 1 : 0));
//				
//				LocationManager locationmngr = ApplicationUtil.getLocationManager();
//				params.put("gps", (locationmngr.isProviderEnabled(LocationManager.GPS_PROVIDER) == true? 1 : 0));
//				params.put("network", (locationmngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true? 1 : 0));
//				
//				boolean mobileDataEnabled = false; // Assume disabled
//				ConnectivityManager cm = ApplicationUtil.getConnectivityManager();
//				try {
//				    Class cmClass = Class.forName(cm.getClass().getName());
//				    Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
//				    method.setAccessible(true); // Make the method callable
//				// get the setting for "mobile data"
//				    mobileDataEnabled = (Boolean) method.invoke(cm);
//				} catch (Exception e) {
//				    // Some problem accessible private API
//				// TODO do whatever error handling you want here
//				}
//				
//				params.put("mobiledata", (mobileDataEnabled == true? 1 : 0));
//				
//				Calendar cal = Calendar.getInstance();
//				
////					Date phonedate = java.sql.Timestamp.valueOf(DATE_FORMAT.format(cal.getTime()));
//				Date phonedate = new Timestamp(cal.getTimeInMillis());
//				params.put("dtsaved", phonedate.toString());
//				
//				AppSettings settings = Platform.getApplication().getAppSettings();
//				Map map = settings.getAll();
//				
//				long timedifference = 0L;
//				if (map.containsKey("timedifference")) {
//					timedifference = settings.getLong("timedifference");
//				}
//				params.put("timedifference", timedifference);
//				statusdb.insert("mobile_status", params); 
//			}

			
		};
	}
	*/
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
		ApplicationUtil.println("MobileStatusTrackerService", msg.toString());
	}

}
