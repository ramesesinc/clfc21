package com.rameses.clfc.android.receiver.services;

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
import android.os.IBinder;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.XMainDB;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.CollectionGroupDB;
import com.rameses.clfc.android.db.LocationTrackerDB;
import com.rameses.clfc.android.services.LoanLocationService;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MapProxy;

public class LocationTrackerService extends Service {
	
	private boolean serviceStarted = false;
	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(5);
	private ScheduledFuture<?> trackerHandle;
	
	private LocationTrackerDB locationtrackerdb = new LocationTrackerDB();
	private CollectionGroupDB collectiongroupdb = new CollectionGroupDB();
	
	private Thread locationTrackerThread = new Thread() {
		
		UserProfile profile;				
		public void run() {
			println("tracker service running");
			profile = SessionContext.getProfile();
			if (profile == null) return;
			
			try {
				saveLocation();
				trackerDateResolver();
				broadcastTracker();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		private void saveLocation() throws Exception {
			String trackerid = settings.getTrackerid();
			if (trackerid == null || "".equals( trackerid )) return; 
			
			String collectorid = profile.getUserId();
			if (collectorid == null || "".equals( collectorid )) return;
			
			String date = ApplicationUtil.formatDate(Platform.getApplication().getServerDate(), "yyyy-MM-dd");
			
			boolean flag = collectiongroupdb.hasCollectionGroupByCollectorAndDate(collectorid, date);
			if (!flag) return;

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
			
//			scheduleTrackerDateResolverThread();
		}
		
		private void trackerDateResolver() throws Exception {
			if (app.getIsDateSync() == false) {
				return;
			}
			
//			StringBuilder sb = new StringBuilder();
			List<Map> sqlParams = new ArrayList<Map>();
			Map data = new HashMap();
			
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
				
				data = new HashMap();
				data.put("type", "update");
				data.put("sql", sql);
				
				sqlParams.add( data );
				
//				sb.append( sql );
				
			} 
			
			if (sqlParams.size() > 0) {
				SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
				try {
					trackerdb.beginTransaction();
					ApplicationDBUtil.executeSQL( trackerdb, sqlParams );
//					trackerdb.execSQL( sb.toString() );
					trackerdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					trackerdb.endTransaction();
				}
			}
		}
		
		private void broadcastTracker() throws Exception {
			if (app.getIsDateSync() == false) {
				return;
			}
			
//			StringBuilder sb = new StringBuilder();
			List<Map> sqlParams = new ArrayList<Map>();
			Map data = new HashMap();
			
			List<Map> list = locationtrackerdb.getForUploadLocationTrackers();

//			int size = (list.size() < SIZE - 1? list.size() : SIZE - 1);
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
						
						data = new HashMap();
						data.put("type", "delete");
						data.put("sql", sql);
						
						sqlParams.add( data );
						
//						sb.append( sql );
						
					}
				}
			}
			
			if (sqlParams.size() > 0) {
				SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
				try {
					trackerdb.beginTransaction();
					ApplicationDBUtil.executeSQL( trackerdb, sqlParams );
//					trackerdb.execSQL( sb.toString() );
					trackerdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					trackerdb.endTransaction();
				}
			}
		}
	};
	
	@Override
	public void onCreate() {
		app = (ApplicationImpl) Platform.getApplication();
		settings = (AppSettingsImpl) app.getAppSettings();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		start();
		
		return START_STICKY;
	}
	
	public synchronized boolean getIsServiceStarted() { return serviceStarted; }
	
	public synchronized void start() {
		if (serviceStarted==false) {
						
			if (locationTrackerThread.getState().equals( Thread.State.NEW ) || 
					locationTrackerThread.getState().equals( Thread.State.TERMINATED )) {
				
				int timeout = settings.getTrackerTimeout();
				trackerHandle = scheduledPool.scheduleWithFixedDelay( locationTrackerThread, 1, timeout, TimeUnit.SECONDS );
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
		// TODO Auto-generated method stub
		return null;
	}
	
	private void println( Object msg ) {
		ApplicationUtil.println( "LocationTrackerService", msg.toString() );
	}
	
}
