package com.rameses.clfc.android.receiver;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.PrevLocationDB;
import com.rameses.client.android.AppSettings;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIApplication;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.MapProxy;

public class PhoneStatusListener extends BroadcastReceiver {
	
	private PrevLocationDB prevlocationdb = new PrevLocationDB();

	public void onReceive(Context ctx, Intent intent) {
//		UserProfile profile = SessionContext.getProfile();
//		AppSettingsImpl settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
		
		println("intent " + intent.getAction());
		
		if (intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")) {
			println("phone shutdown ");
//			addLog("Phone Shutdown");
		} 
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Toast.makeText(ctx, "Phone Boot Completed", Toast.LENGTH_LONG);
			println("phone boot completed "); 
//			DBContext clfcdb = new DBContext("clfc.db");
//			println("dbcontext-> " + clfcdb);
//			
//			if (clfcdb != null) {
//				println("sys var list-> " + clfcdb.getList("select * from sys_var", new Object[]{}));
//			}
//			
//			if (profile != null) {
//				println("boot completed-> " + profile.getFullName());
//			}
//			if (settings != null) {
//				settings.put("boot_completed", "boot completed-> " + (profile != null? profile.getFullName() : "no profile"));
//			} 
		} 
		if (intent.getAction().equals("rameses.clfc.APPLICATION_START")) {
//			addLog("Application Started");
		}    
	}
	
	private void addLog( String statusText ) {
		
		UIApplication app = Platform.getApplication();
		AppSettingsImpl settings = null;
		if (app != null) {
			settings = (AppSettingsImpl) app.getAppSettings();
		}
		
		String trackerid = "";
		if (settings != null) {
//			trackerid = settings.getTrackerid();
			Map settingsMap = settings.getAll();
			if (settingsMap != null && settingsMap.containsKey("collector_trackerid")) {
				trackerid = settingsMap.get("collector_trackerid").toString();
			}
			println("get trackerid " + settings.getTrackerid());
		}
		println("trackerid " + trackerid);  
		
		if (trackerid == null || trackerid.trim().length() == 0) return;
		
		UserProfile profile = SessionContext.getProfile();
		if (profile == null) return; 
		
		try {
			addLogImpl(app, trackerid, profile.getUserId(), statusText);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	
//		synchronized(TrackerDB.LOCK) {
//			SQLTransaction trackerdb = new SQLTransaction("clfctracker.db");
//			try {
//				trackerdb.beginTransaction();
//				addLogImpl(trackerdb, app, trackerid, profile.getUserId(), statusText);
//				trackerdb.commit();
//			} catch (Throwable t) {
//				t.printStackTrace();
//			} finally {
//				trackerdb.endTransaction();
//			}
//		}
		
		
	}
	
	private void addLogImpl( UIApplication app, String trackerid, String collectorid,
			String statusText ) throws Exception {


		Location location = NetworkLocationProvider.getLocation();
		String val1 = "0", val2 = "0";
		if (location != null) {
			val1 = location.getLongitude() + "";
			val2 = location.getLatitude() + "";
		}
		BigDecimal lng = new BigDecimal( val1 );
		BigDecimal lat = new BigDecimal( val2 );
//		double lng = (location == null? 0.0: location.getLongitude());
//		double lat = (location == null? 0.0: location.getLatitude());
		
		Map data = prevlocationdb.getPrevLocation( trackerid );
		
		MapProxy prevlocation = new MapProxy( data );
		val1 = "0";
		val2 = "0";
		if (prevlocation != null && !prevlocation.isEmpty()) {
			val1 = prevlocation.getString("lng");
			val2 = prevlocation.getString("lat");
//			prevlng = prevlocation.getDouble("lng");
//			prevlat = prevlocation.getDouble("lat");
//			strPrevLng = prevlocation.getString("lng");
//			strPrevLat = prevlocation.getString("lat");
		}
		BigDecimal prevlng = new BigDecimal( val1 );
		BigDecimal prevlat = new BigDecimal( val2 );
		
		Map params = new HashMap();
		params.put("objid", "TRCK" + UUID.randomUUID());
		params.put("trackerid", trackerid);
		params.put("collectorid", collectorid);
		params.put("txndate", app.getServerDate().toString());
		params.put("lng", lng); 
		params.put("lat", lat);
		params.put("prevlng", prevlng);
		params.put("prevlat", prevlat);
		params.put("phonestatus", statusText);
//		params.put("lng", strLng);
//		params.put("lat", strLat);
//		params.put("prevlng", strPrevLng);
//		params.put("prevlat", strPrevLat);
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
		
//			Date phonedate = java.sql.Timestamp.valueOf(DATE_FORMAT.format(cal.getTime()));
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
		
		SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
		try {
			trackerdb.beginTransaction();
			trackerdb.execSQL( sql );
			trackerdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			trackerdb.endTransaction();
		}
//		trackerdb.insert("mobile_status", params); 
	}
	
	private void addLogImpl( SQLTransaction trackerdb, UIApplication app, String trackerid, String collectorid, 
			String statusText ) throws Exception {

		Location location = NetworkLocationProvider.getLocation();
		String val1 = "0", val2 = "0";
		if (location != null) {
			val1 = location.getLongitude() + "";
			val2 = location.getLatitude() + "";
		}
		BigDecimal lng = new BigDecimal( val1 );
		BigDecimal lat = new BigDecimal( val2 );
//		double lng = (location == null? 0.0: location.getLongitude());
//		double lat = (location == null? 0.0: location.getLatitude());
		
		Map data = prevlocationdb.getPrevLocation( trackerid );
		
		MapProxy prevlocation = new MapProxy( data );
		val1 = "0";
		val2 = "0";
		if (prevlocation != null && !prevlocation.isEmpty()) {
			val1 = prevlocation.getString("lng");
			val2 = prevlocation.getString("lat");
//			prevlng = prevlocation.getDouble("lng");
//			prevlat = prevlocation.getDouble("lat");
//			strPrevLng = prevlocation.getString("lng");
//			strPrevLat = prevlocation.getString("lat");
		}
		BigDecimal prevlng = new BigDecimal( val1 );
		BigDecimal prevlat = new BigDecimal( val2 );
		
		Map params = new HashMap();
		params.put("objid", "TRCK" + UUID.randomUUID());
		params.put("trackerid", trackerid);
		params.put("collectorid", collectorid);
		params.put("txndate", app.getServerDate().toString());
		params.put("lng", lng); 
		params.put("lat", lat);
		params.put("prevlng", prevlng);
		params.put("prevlat", prevlat);
		params.put("phonestatus", statusText);
//		params.put("lng", strLng);
//		params.put("lat", strLat);
//		params.put("prevlng", strPrevLng);
//		params.put("prevlat", strPrevLat);
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
		
//			Date phonedate = java.sql.Timestamp.valueOf(DATE_FORMAT.format(cal.getTime()));
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
	}

	private void println( Object msg ) {
		ApplicationUtil.println("PhoneStatusListener", msg.toString());
	}

}
