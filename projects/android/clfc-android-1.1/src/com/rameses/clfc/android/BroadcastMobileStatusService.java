package com.rameses.clfc.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.util.Log;

import com.rameses.clfc.android.db.DBMobileStatusTracker;
import com.rameses.clfc.android.services.MobileStatusService;
import com.rameses.client.android.Platform;
import com.rameses.client.android.Task;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MapProxy;

public class BroadcastMobileStatusService {
	
	private final int SIZE = 6;
	private final String DBNAME = "clfctracker.db";
	
	private ApplicationImpl app;
	private Handler handler;
	private SQLTransaction trackerdb;
	private DBMobileStatusTracker trackerSvc = new DBMobileStatusTracker();
//	private MobileStatusService svc = new MobileStatusService();
	private MapProxy proxy;
	private Map params;
	private Map response = new HashMap();
	private int listSize;
//	private LoanLocationService svc = new LoanLocationService();
	private boolean hasLocationTrackers = false;
	private Task actionTask;
	
	public boolean serviceStarted = false;
	
	public BroadcastMobileStatusService(ApplicationImpl app) {
		this.app = app;
	}
	
	public void start() { 
		if (handler == null) { 
			handler = new Handler();
//			new RunnableImpl().run(); 
		}  

		ApplicationUtil.println("BroadcastMobileStatusService", "service started " + serviceStarted);
		
		if (serviceStarted == false) {
			serviceStarted = true;
			createTask();
			Platform.getTaskManager().schedule(actionTask, 1000, 1000);
			ApplicationUtil.println("BroadcastMobileStatusService", "start");
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
	
	private void println(Object msg) {
		Log.i("BroadcastMobileStatusService", msg.toString());
	}
	
	private void createTask() {
		actionTask = new Task() {
			public void run() {
				List<Map> list = new ArrayList<Map>();
				DBContext ctx = null;
				synchronized (TrackerDB.LOCK) {
//					trackerdb = new SQLTransaction("clfctracker.db")
					ctx = new DBContext(DBNAME);
					trackerSvc.setDBContext(ctx);
					trackerSvc.setCloseable(false);
					try { 
//						trackerdb.beginTransaction();
//						list = locationTracker.getLocationTrackers(SIZE);	
						list = trackerSvc.getForUploadLocationTrackers(SIZE);
						ApplicationUtil.println("BroadcastMobileStatusService", "list " + list);
//						trackerdb.commit(); 
					} catch (Throwable t) {
						t.printStackTrace();
					} finally { 
//						trackerdb.endTransaction();
						ctx.close();
					}
				}
				
				execTracker(list);
				
				hasLocationTrackers = true;
				synchronized (TrackerDB.LOCK) {
//					trackerdb = new SQLTransaction("clfctracker.db")
					ctx = new DBContext(DBNAME);
					trackerSvc.setDBContext(ctx);
					trackerSvc.setCloseable(false);
					try {
//						trackerdb.beginTransaction();
//						list = locationTracker.getLocationTrackers(1);
						list = trackerSvc.getForUploadLocationTrackers(1);
						if (list.isEmpty() || list.size() == 0) {
							hasLocationTrackers = false;
						}
//						trackerdb.commit();
					} catch (Throwable t) {
						t.printStackTrace();
					} finally { 
//						trackerdb.endTransaction();
						ctx.close();
					}
				}
//				if (list.size() == SIZE) {
//					hasLocationTrackers = true;
//				}
				
				if (hasLocationTrackers == false) {
					serviceStarted = false;
					this.cancel();
				}
				
//				synchronized (TrackerDB.LOCK) {
//					DBContext ctx = new DBContext("clfctracker.db");
//					locationTracker.setDBContext(ctx);
//					try {
//						hasLocationTrackers = locationTracker.hasLocationTrackers();
//						
//					} catch (Throwable t) {
//						t.printStackTrace();
//					}
//				}

//				if (hasLocationTrackers == true) {
//					Platform.getTaskManager().schedule(runnableImpl, 5000);
//				} else if (hasLocationTrackers == false) {
//					serviceStarted = false;
//				}
			}

			private void execTracker(List<Map> list) {			
				if (!list.isEmpty()) {
					listSize = (list.size() < SIZE-1? list.size() : SIZE-1);
					println("execute tracker");
					for (int i=0; i<listSize; i++) {						
						proxy  = new MapProxy((Map) list.get(i));

						params = new HashMap();
						params.put("objid", proxy.getString("objid"));
						params.put("trackerid", proxy.getString("trackerid"));
						params.put("txndate", proxy.getString("txndate"));
//						params.put("lng", proxy.getDouble("lng"));
//						params.put("lat", proxy.getDouble("lat"));
						params.put("lng", Double.parseDouble(proxy.getString("lng")));
						params.put("lat", Double.parseDouble(proxy.getString("lat")));
						params.put("state", 1);
						params.put("wifi", proxy.getInteger("wifi"));
						params.put("mobiledata", proxy.getInteger("mobiledata"));
						params.put("gps", proxy.getInteger("gps"));
						params.put("network", proxy.getInteger("network"));
//						params.put("prevlng", proxy.getDouble("prevlng"));
//						params.put("prevlat", proxy.getDouble("prevlat"));
						params.put("prevlng", Double.parseDouble(proxy.getString("prevlng")));
						params.put("prevlat", Double.parseDouble(proxy.getString("prevlat")));
						
						Map param = new HashMap();
						String enc = new Base64Cipher().encode(params);
						param.put("encrypted", enc);
						
						if (response == null) response = new HashMap();
						response.clear();
						MobileStatusService service;
						for (int j=0; j<10; j++) {
							try {
//								println("before sending");
								service = new MobileStatusService();
								response = service.postMobileStatusEncrypt(param);
//								println("after sending");
						 		break;
							} catch (Throwable e) { e.printStackTrace();}
						}
						
						if (response != null && response.containsKey("response")) {
							String str = response.get("response").toString();
							if (str.toLowerCase().equals("success")) {
								trackerdb = new SQLTransaction(DBNAME);
								try {
									trackerdb.beginTransaction();
									trackerdb.delete("mobile_status", "objid=?", new Object[]{proxy.getString("objid")});
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
		};
	}
	
}
