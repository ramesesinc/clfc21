package com.rameses.clfc.android.receiver.services;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.CollectionGroupDB;
import com.rameses.clfc.android.db.MobileStatusTrackerDB;
import com.rameses.clfc.android.services.MobileStatusService;
import com.rameses.client.android.AppSettings;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MapProxy;

public class MobileStatusTrackerService extends Service {

	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(5);	
	private ScheduledFuture<?> trackerHandle;

	private boolean serviceStarted = false;
	
	private MobileStatusTrackerDB mobilestatustrackerdb = new MobileStatusTrackerDB();
	private CollectionGroupDB collectiongroupdb = new CollectionGroupDB();
	
	private Thread statusTrackerThread = new Thread() {

		UserProfile profile;
		
		public void run() {
			profile = SessionContext.getProfile();
			if (profile == null) return;
			
			try {
				saveStatus();
				statusDateResolver();
				broadcastStatus();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		private void saveStatus() throws Exception {			
			String trackerid = settings.getTrackerid();
			if (trackerid == null || "".equals( trackerid )) return;
			
			String collectorid = profile.getUserId();
			if (collectorid == null || "".equals( collectorid )) return;
			
			String date = ApplicationUtil.formatDate(Platform.getApplication().getServerDate(), "yyyy-MM-dd");
			
			boolean flag = collectiongroupdb.hasCollectionGroupByCollectorAndDate(collectorid, date);
			if (!flag) return;
			
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

//			scheduleTrackerDateResolverThread();
//			scheduleStatusTrackerDateResolverTask();
		}
		
		private void statusDateResolver() throws Exception {
			if (app.getIsDateSync() == false) {
				return;
			}
			
//			StringBuilder sb = new StringBuilder();
			List<Map> sqlParams = new ArrayList<Map>();
			Map data = new HashMap();
			
			String trackerid = settings.getTrackerid();
			if (trackerid == null) trackerid = "";
			
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
				sql += " WHERE objid='" + m.get("objid").toString() + "';";

				data = new HashMap();
				data.put("type", "update");
				data.put("sql", sql);
				
				sqlParams.add( data );
				
//				sb.append( sql );
			} 
			
			if (sqlParams.size() > 0) {
				SQLiteDatabase statusdb = ApplicationDatabase.getStatusWritableDB();
				try {
					statusdb.beginTransaction();
					ApplicationDBUtil.executeSQL( statusdb, sqlParams );
//					statusdb.execSQL( sb.toString() );
					statusdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					statusdb.endTransaction();
				}
			}
		}
		
		private void broadcastStatus() throws Exception {
			if (app.getIsDateSync() == false) {
				return;
			}
			
//			StringBuilder sb = new StringBuilder();
			List<Map> sqlParams = new ArrayList<Map>();
			Map data = new HashMap();
			
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
						
						data = new HashMap();
						data.put("type", "delete");
						data.put("sql", sql);
						
						sqlParams.add( data );
						
//						sb.append( sql );
					}
				}
			}
			
			if (sqlParams.size() > 0) {
				SQLiteDatabase statusdb = ApplicationDatabase.getStatusWritableDB();
				try {
					statusdb.beginTransaction();
					ApplicationDBUtil.executeSQL( statusdb, sqlParams );
//					statusdb.execSQL( sb.toString() );
					statusdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					statusdb.endTransaction();
				}
			}
		}
	};
	
	public MobileStatusTrackerService() {
		app = (ApplicationImpl) Platform.getApplication();
		settings = (AppSettingsImpl) app.getAppSettings();
	}
	
	@Override
	public int onStartCommand( Intent intent, int flags, int startId ) {
		super.onStartCommand(intent, flags, startId);
		
		start();
		
		return START_STICKY;
	}
	
	public synchronized boolean getIsServiceStarted() { return serviceStarted; }
	
	public synchronized void start() {
		if (serviceStarted==false) {
			
			println("thread state " + statusTrackerThread.getState());
			if (statusTrackerThread.getState().equals( Thread.State.NEW ) || 
					statusTrackerThread.getState().equals( Thread.State.TERMINATED )) {
				
				int timeout = settings.getTrackerTimeout();
				trackerHandle = scheduledPool.scheduleWithFixedDelay( statusTrackerThread, 1, timeout, TimeUnit.SECONDS );
			}
			serviceStarted = true;
		}
	}
	
	public synchronized void restart() {
		stop();
		start();
	} 
	
	public synchronized void stop() {
		if (serviceStarted==true) {
			if (trackerHandle != null) {
				trackerHandle.cancel( true );
			}
			serviceStarted = false;
		}
	}
	
	public synchronized void shutdown() {
		if (scheduledPool != null) {
			scheduledPool.shutdown();
		}
	}
	
	@Override
	public void onDestroy() {
		stop();
		shutdown();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void println( Object msg ) {
		ApplicationUtil.println("MobileStatusTrackerService", msg.toString());
	}

}
