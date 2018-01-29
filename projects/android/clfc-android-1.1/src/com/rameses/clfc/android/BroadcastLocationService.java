package com.rameses.clfc.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;

import com.rameses.clfc.android.db.DBLocationTracker;
import com.rameses.clfc.android.services.LoanLocationService;
import com.rameses.client.android.Platform;
import com.rameses.client.android.Task;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MapProxy;

public class BroadcastLocationService 
{
	private final int SIZE = 6;
	
	private ApplicationImpl app;
	private Handler handler;
	private SQLTransaction trackerdb;
	private DBLocationTracker locationTracker = new DBLocationTracker();
	private MapProxy proxy;
	private Map params;
	private Map response = new HashMap();
	private int listSize;
//	private LoanLocationService svc = new LoanLocationService();
	private boolean hasLocationTrackers = false;
	private Task actionTask;
	
	public boolean serviceStarted = false;
	
	public BroadcastLocationService(ApplicationImpl app) {
		this.app = app;
	}
	
	public void start() { 
		if (handler == null) { 
			handler = new Handler();
//			new RunnableImpl().run(); 
		}  

		ApplicationUtil.println("BroadcastLocationService", "service started " + serviceStarted);
		
		if (serviceStarted == false) {
			serviceStarted = true;
			createTask();
			Platform.getTaskManager().schedule(actionTask, 1000, 1000);
			ApplicationUtil.println("BroadcastLocationService", "start");
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
	
	
	private void createTask() {
		actionTask = new Task() {
			public void run() {
				List<Map> list = new ArrayList<Map>();
				DBContext ctx = null;
				synchronized (TrackerDB.LOCK) {
//					trackerdb = new SQLTransaction("clfctracker.db")
					ctx = new DBContext("clfctracker.db");
					locationTracker.setDBContext(ctx);
					locationTracker.setCloseable(false);
					try { 
//						trackerdb.beginTransaction();
//						list = locationTracker.getLocationTrackers(SIZE);	
						list = locationTracker.getForUploadLocationTrackers(SIZE);
						ApplicationUtil.println("BroadcastLocationLService", "list " + list);
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
					ctx = new DBContext("clfctracker.db");
					locationTracker.setDBContext(ctx);
					locationTracker.setCloseable(false);
					try {
//						trackerdb.beginTransaction();
//						list = locationTracker.getLocationTrackers(1);
						list = locationTracker.getForUploadLocationTrackers(1);
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
					int networkStatus = 0;
					for (int i=0; i<listSize; i++) {
						networkStatus = app.getNetworkStatus();
						if (networkStatus == 3) {
							break;
						}
						
						proxy  = new MapProxy((Map) list.get(i));

//						params.clear();
						params = new HashMap();
						params.put("objid", proxy.getString("objid"));
						params.put("trackerid", proxy.getString("trackerid"));
						params.put("txndate", proxy.getString("txndate"));
//						params.put("lng", proxy.getDouble("lng"));
//						params.put("lat", proxy.getDouble("lat"));
						params.put("lng", Double.parseDouble(proxy.getString("lng")));
						params.put("lat", Double.parseDouble(proxy.getString("lat")));
						params.put("state", 1);
						
						Map param = new HashMap();
						String enc = new Base64Cipher().encode(params);
						param.put("encrypted", enc);
						
						if (response == null) response = new HashMap();
						response.clear();
						LoanLocationService service;
						for (int j=0; j<10; j++) {
							try {
								service = new LoanLocationService();
								response = service.postLocationEncrypt(param);
								break;
							} catch (Throwable e) {;}
						}
						 
						if (response != null && response.containsKey("response")) {
							String str = response.get("response").toString();
							if (str.toLowerCase().equals("success")) {
								trackerdb = new SQLTransaction("clfctracker.db");
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
						
//						if (response.containsKey("response") && response.get("response").toString().toLowerCase().equals("success")) {
//							synchronized (TrackerDB.LOCK) {
//								trackerdb = new SQLTransaction("clfctracker.db");
//								locationTracker.setDBContext(trackerdb.getContext());
//								try {
//									trackerdb.beginTransaction();
//									trackerdb.delete("location_tracker", "objid=?", new Object[]{proxy.getString("objid")});
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
		};
	}
	
}
