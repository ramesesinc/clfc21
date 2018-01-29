package com.rameses.clfc.android;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.rameses.clfc.android.db.DBCollectionGroup;
import com.rameses.clfc.android.db.DBLocationTracker;
import com.rameses.clfc.android.db.DBPrevLocation;
import com.rameses.client.android.AppSettings;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.Task;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.MapProxy;

class LocationTrackerService 
{
	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private Handler handler;
//	private List tables;
	private SQLTransaction trackerdb;
	private DBLocationTracker locationTracker = new DBLocationTracker();
	private DBPrevLocation prevLocation = new DBPrevLocation();
	private int seqno;
	private int timeout;
	private String collectorid;
	private Location location;
	private UserProfile profile;
	private double lng;
	private double lat;
	private double prevlng = 0.0;
	private double prevlat = 0.0;
	private Map params = new HashMap();
	private MapProxy prevlocation;	
	private Task actionTask;
	
	private boolean serviceStarted = false;
	
	public LocationTrackerService(ApplicationImpl app) {
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
//			ApplicationUtil.println("LocationTrackerService", "start");
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
		Log.i("LocationTrackerService", msg);
	}
	
	void _(String msg) {
		Log.i("[LocationTrackerService]", msg);
	}
		
	private void createTask() {
		actionTask = new Task() {
			public void run() {
				runImpl();
				Platform.getActionBarMainActivity().getHandler().post(new Runnable() {
					public void run() {
//						app.broadcastLocationSvc.start();
//						app.location
						app.locationTrackerDateResolverSvc.start();
					}
				});	
			}
			
			public void runImpl() {
//				System.out.println("[LocationTrackerService.run]");
				String trackerid = settings.getTrackerid();
				if (trackerid == null || "".equals(trackerid)) return;
				
				boolean flag = false;
 
				DBContext ctx = new DBContext("clfc.db");
				DBCollectionGroup dbcg = new DBCollectionGroup();
				dbcg.setDBContext(ctx);
//				DBSystemService sysSvc = new DBSystemService();
//				sysSvc.setDBContext(ctx);
				

				UserProfile profile = SessionContext.getProfile();
				String collectorid = (profile == null? null : profile.getUserId());
				String date = ApplicationUtil.formatDate(Platform.getApplication().getServerDate(), "yyyy-MM-dd");
				
				synchronized (MainDB.LOCK) {
					try {
						flag = dbcg.hasCollectionGroupByCollectorAndDate(collectorid, date);
//						flag = sysSvc.hasBillingid(collectorid, date);
					} catch (Throwable t) {
						t.printStackTrace();
//						UIDialog.showMessage(t, RouteListActivity.this);
					}
				} 
				ApplicationUtil.println("LocationTrackerService", "has billingid " + flag); 
				 
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

				prevLocation.setDBContext(trackerdb.getContext());
				prevLocation.setCloseable(false);			
				prevlocation = new MapProxy(prevLocation.getPrevLocation(trackerid));
				prevlng = 0.0;
				prevlat = 0.0;
				if (prevlocation != null && !prevlocation.isEmpty()) {
//					prevlng = prevlocation.getDouble("lng");
//					prevlat = prevlocation.getDouble("lat");
					prevlng = Double.parseDouble(prevlocation.getString("lng"));
					prevlat = Double.parseDouble(prevlocation.getString("lat"));
				}
//				lat += 1;
//				System.out.println("lng->"+lng+", lat->"+lat+", prevlng->"+prevlng+", prevlat->"+prevlat);
				
				ApplicationUtil.println("LocationTrackerService", "lng: " + lng + " prevlng: " + prevlng + " lat: " + lat + " prevlat: " + prevlat);
				if ((lng > 0 && prevlng != lng) || (lat > 0 && prevlat != lat)) {				
					profile = SessionContext.getProfile();
					collectorid = (profile == null? null : profile.getUserId());
					if (collectorid != null) {					
						locationTracker.setDBContext(trackerdb.getContext());
						seqno = locationTracker.getLastSeqnoByCollectorid(collectorid);	
													
						String strLng = String.valueOf(lng);
						String strLat = String.valueOf(lat);
						
						params = new HashMap();
						params.put("objid", "TRCK" + UUID.randomUUID());
						params.put("seqno",  seqno+1);
						params.put("trackerid", trackerid);
						params.put("collectorid", collectorid);
						params.put("txndate", app.getServerDate().toString());
//						params.put("lng", lng);
//						params.put("lat", lat);
						params.put("lng", strLng);
						params.put("lat", strLat);
						params.put("forupload", 0);
						Calendar cal = Calendar.getInstance();
						
//						Date phonedate = java.sql.Timestamp.valueOf(DATE_FORMAT.format(cal.getTime()));
						Date phonedate = new Timestamp(cal.getTimeInMillis());
						params.put("dtsaved", phonedate.toString());
						
						AppSettings settings = app.getAppSettings();
						Map map = settings.getAll();
						
						long timedifference = 0L;
						if (map.containsKey("timedifference")) {
							timedifference = settings.getLong("timedifference");
						}
						params.put("timedifference", timedifference);
						trackerdb.insert("location_tracker", params);
						
//						Map xmap = trackerdb.find("select * from location_tracker where objid='" + params.get("objid").toString() + "'");
//						ApplicationUtil.println("LocationTrackerService", "tracker-> " + xmap);
						
						
						params = new HashMap();
//						params.put("lng", lng);
//						params.put("lat", lat);
						params.put("lng", strLng);
						params.put("lat", strLat);
						ApplicationUtil.println("LocationTrackerService", "params-> " + params);
						if (prevlocation == null || prevlocation.isEmpty()) {
							params.put("objid", "PL" + UUID.randomUUID().toString());
							params.put("trackerid", trackerid);

							Date dt = Platform.getApplication().getServerDate();
							String date = ApplicationUtil.formatDate(dt, "yyyy-MM-dd");
							params.put("date", date);

//							ApplicationUtil.println("LocationTrackerService", "params1-> " + params);
							trackerdb.insert("prev_location", params);
						} else if (prevlocation != null && !prevlocation.isEmpty()) {
							trackerdb.update("prev_location", "objid='" + prevlocation.getString("objid") + "'", params);
						} 
						
//						prevlocation = new MapProxy(prevLocation.getPrevLocation(trackerid));
//						ApplicationUtil.println("LocationTrackerService", "prevlocation-> " + prevlocation.entrySet()); 
						
						
//						if (1==1) {
//							throw new RuntimeException("stopping");
//						}
						/*Platform.getMainActivity().getHandler().post(new Runnable() {
							public void run() {
								app.broadcastLocationSvc.start();
							}
						});*/ 
					}
				}
			}
		};
	}
}

