package com.rameses.clfc.android;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import com.rameses.clfc.android.db.DBCollectionGroup;
import com.rameses.clfc.android.db.DBLocationTracker;
import com.rameses.clfc.android.db.DBPrevLocation;
import com.rameses.clfc.android.db.DBSystemService;
import com.rameses.client.android.AppSettings;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.Task;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.MapProxy;

class MobileStatusTrackerService 
{
	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private Handler handler;
//	private List tables;
	private SQLTransaction trackerdb;
	private int timeout;
	private String collectorid;
	private Location location;
	private UserProfile profile;
	private double lng;
	private double lat;
	private Map params = new HashMap();
	private Task actionTask;
	
	private boolean serviceStarted = false;
	
	public MobileStatusTrackerService(ApplicationImpl app) {
		this.app = app;
		settings = (AppSettingsImpl) app.getAppSettings();
	}
	
	public void start() {
		if (handler == null) { 
			handler = new Handler();
//			new RunnableImpl().run(); 
		} 
		
		if (serviceStarted == false) {
			serviceStarted = true;
			timeout = settings.getTrackerTimeout()*1000;
			createTask();
			Platform.getTaskManager().schedule(actionTask, 1000, timeout);
			println("start");
		}
	}
	
	public void restart() {
		if (serviceStarted == true) {
			actionTask.cancel();
			actionTask = null;
			serviceStarted = false;
		}
		start();
	}
	
	private void println(String msg) {
		Log.i("MobileStatusTrackerService", msg);
	}
	
	private void createTask() {
		actionTask = new Task() {
			public void run() {
				runImpl();
				Platform.getActionBarMainActivity().getHandler().post(new Runnable() {
					public void run() {
//						app.locationTrackerDateResolverSvc.start();
						app.mobileStatusTrackerDateResolverSvc.start();
					}
				});	
			}
			
			public void runImpl() {
				String trackerid = settings.getTrackerid();
				//_("location: " + xl);
				
//				double lng = xl != null? xl.getLongitude() : 0.0000000;
//				double lat = xl != null? xl.getLatitude() : 0.0000000;
//				_("lng: " + lng + " lat: " + lat);
//				Log.i("LocationTrackerService", "trackerid " + trackerid);
				if (trackerid == null || "".equals(trackerid)) return;

				boolean flag = false;
 
				DBContext ctx = new DBContext("clfc.db");
				DBCollectionGroup dbcg = new DBCollectionGroup();
				dbcg.setDBContext(ctx);
//				DBSystemService sysSvc = new DBSystemService();
//				sysSvc.setDBContext(ctx);
				

				UserProfile profile = SessionContext.getProfile();
				String collectorid = (profile == null? null : profile.getUserId());
				String date = ApplicationUtil.formatDate(app.getServerDate(), "yyyy-MM-dd");
				
				synchronized (MainDB.LOCK) {
					try {
						flag = dbcg.hasCollectionGroupByCollectorAndDate(collectorid, date);
//						flag = sysSvc.hasBillingid(collectorid, date);
					} catch (Throwable t) {
						t.printStackTrace();
//						UIDialog.showMessage(t, RouteListActivity.this);
					}
				} 
//				 println("has billingid " + flag); 
				 
				if (!flag) return;
				
				synchronized (TrackerDB.LOCK) {
					trackerdb = new SQLTransaction("clfctracker.db");
					try {
						trackerdb.beginTransaction();
						execTracker(trackerdb, trackerid);
						trackerdb.commit();
					} catch (Throwable t) {
						t.printStackTrace();
					} finally {
						trackerdb.endTransaction(); 
					}
				}
			} 
					 
			private void execTracker(SQLTransaction trackerdb, String trackerid) throws Exception {

				location = NetworkLocationProvider.getLocation();
				lng = (location == null? 0.0: location.getLongitude());
				lat = (location == null? 0.0: location.getLatitude());

				profile = SessionContext.getProfile();
				collectorid = (profile == null? null : profile.getUserId());
				if (collectorid != null) {
					
					DBPrevLocation prevLocation = new DBPrevLocation();
					prevLocation.setDBContext(trackerdb.getContext());
					prevLocation.setCloseable(false);
					
					MapProxy prevlocation = new MapProxy(prevLocation.getPrevLocation(trackerid));
					
//					double prevlng = 0.0;
//					double prevlat = 0.0;
					String strPrevLng = "0", strPrevLat = "0";
					String strLng = String.valueOf(lng), strLat = String.valueOf(lat);
					if (prevlocation != null && !prevlocation.isEmpty()) {
//						prevlng = prevlocation.getDouble("lng");
//						prevlat = prevlocation.getDouble("lat");
						strPrevLng = prevlocation.getString("lng");
						strPrevLat = prevlocation.getString("lat");
					}
					
					params = new HashMap();
					params.put("objid", "TRCK" + UUID.randomUUID());
					params.put("trackerid", trackerid);
					params.put("collectorid", collectorid);
					params.put("txndate", app.getServerDate().toString());
//					params.put("lng", lng);
//					params.put("lat", lat);
//					params.put("prevlng", prevlng);
//					params.put("prevlat", prevlat);
					params.put("lng", strLng);
					params.put("lat", strLat);
					params.put("prevlng", strPrevLng);
					params.put("prevlat", strPrevLat);
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
					    method.setAccessible(true); // Make the method callable
					// get the setting for "mobile data"
					    mobileDataEnabled = (Boolean) method.invoke(cm);
					} catch (Exception e) {
					    // Some problem accessible private API
					// TODO do whatever error handling you want here
					}
					
					params.put("mobiledata", (mobileDataEnabled == true? 1 : 0));
					
					Calendar cal = Calendar.getInstance();
					
//						Date phonedate = java.sql.Timestamp.valueOf(DATE_FORMAT.format(cal.getTime()));
					Date phonedate = new Timestamp(cal.getTimeInMillis());
					params.put("dtsaved", phonedate.toString());
					
					AppSettings settings = Platform.getApplication().getAppSettings();
					Map map = settings.getAll();
					
					long timedifference = 0L;
					if (map.containsKey("timedifference")) {
						timedifference = settings.getLong("timedifference");
					}
					params.put("timedifference", timedifference);
					trackerdb.insert("mobile_status", params); 
   
					
					/*Platform.getMainActivity().getHandler().post(new Runnable() {
						public void run() {
							app.broadcastLocationSvc.start();
						}
					});*/
				}
			}
		};
	}
}

