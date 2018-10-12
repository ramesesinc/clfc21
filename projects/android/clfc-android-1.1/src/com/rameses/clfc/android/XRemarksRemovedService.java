package com.rameses.clfc.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.rameses.clfc.android.db.RemarksRemovedDB;
import com.rameses.clfc.android.services.LoanPostingService;
import com.rameses.client.android.Platform;
import com.rameses.client.android.Task;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.MapProxy;

public class XRemarksRemovedService 
{
	private final int SIZE = 5;
	
	private ApplicationImpl app;
	private AppSettingsImpl appSettings;
	private Handler handler;
//	private SQLTransaction remarksremoveddb;
//	private DBRemarksRemoved remarksRemoved = new DBRemarksRemoved();
//	private LoanPostingService svc = new LoanPostingService();
//	private MapProxy proxy;
//	private Map params = new HashMap();
//	private Map response = new HashMap();
//	private int delay;
//	private int size;
//	private boolean hasPendingRemarksRemoved = false;
	private Task actionTask;
	
	private static boolean serviceStarted = false;
	private RemarksRemovedDB remarksremoveddb = new RemarksRemovedDB();
	
	public XRemarksRemovedService(ApplicationImpl app) {
		this.app = app;
		appSettings = (AppSettingsImpl) app.getAppSettings();
	}
	
	public void start() {
		if (handler == null) { 
			handler = new Handler();
//			new RunnableImpl().run();
		}  
		if (serviceStarted == false) {
			serviceStarted = true;
			int delay = appSettings.getUploadTimeout()*1000;
			createTask();
			Platform.getTaskManager().schedule(actionTask, 1000, delay);
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
				try {
					runImpl();
				} catch (Throwable t) {
					t.printStackTrace();
				}
				
			}
			
			private void runImpl() throws Exception {
				StringBuilder sb = new StringBuilder();
				
				List<Map> list = remarksremoveddb.getPendingRemarksRemoved( SIZE );
				for (Map item : list) {
					MapProxy proxy = new MapProxy( item );
					String objid = proxy.getString("objid");

					int networkStatus = app.getNetworkStatus();
					if (networkStatus == 3) {
						break;
					}
					
					Map params = new HashMap();
					params.put("detailid", proxy.getString("objid"));
					
					Map response = new HashMap();
					for (int j=0; j<10; j++) {
						try {
							LoanPostingService svc = new LoanPostingService();
							response = svc.removeRemarks(params);
//							removeRemarks(response, proxy.getString("objid"));
							break;
						} catch (Throwable e) {;}
					}
					
					if (response != null || !response.isEmpty()) {
						if (response.containsKey("")) {
							String str = response.get("response").toString().toLowerCase();
							if ("success".equals(str)) {
								
								String sql = "delete from remarks_removed where objid='" + objid + "';";
							}
						}
					}
				}
				
				if (!sb.toString().trim().equals("")) {
					SQLiteDatabase remarksremoveddb = ApplicationDatabase.getRemarksRemovedWritableDB();
					try {
						remarksremoveddb.beginTransaction();
						remarksremoveddb.execSQL( sb.toString() );
						remarksremoveddb.setTransactionSuccessful();
					} catch (Exception e) {
						throw e;
					} finally {
						remarksremoveddb.endTransaction();
					}
				}
				
				boolean hasPendingRemarksRemoved = remarksremoveddb.hasPendingRemarksRemoved();
				if (hasPendingRemarksRemoved == false) {
					serviceStarted = false;
					this.cancel();
				}
			}
			/*
			public void xrun() {
				List<Map> list = new ArrayList<Map>();
				DBContext ctx = null;
				synchronized (RemarksRemovedDB.LOCK) {
//					remarksremoveddb = new SQLTransaction("clfcremarksremoved.db");
					ctx = new DBContext("clfcremarksremoved.db");
					remarksRemoved.setDBContext(ctx);
					remarksRemoved.setCloseable(false);
					try {
//						remarksremoveddb.beginTransaction();
						list = remarksRemoved.getPendingRemarksRemoved(SIZE);
//						remarksremoveddb.commit();
					} catch (Throwable t) {
						t.printStackTrace();
					} finally {
						ctx.close();
//						remarksremoveddb.endTransaction();
					}
				}
				
				try {
					execRemarksRemoved(list);
				} catch (Throwable t) {
					t.printStackTrace();
				}
				
				hasPendingRemarksRemoved = true;
				synchronized (RemarksRemovedDB.LOCK) {
					ctx = new DBContext("clfcremarksremoved.db");
					remarksRemoved.setDBContext(ctx);
					try {
						hasPendingRemarksRemoved = remarksRemoved.hasPendingRemarksRemoved();
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
//				hasPendingRemarksRemoved = false;
//				if (list.size() == SIZE) {
//					hasPendingRemarksRemoved = true;
//				}
				
				if (hasPendingRemarksRemoved == false) {
					serviceStarted = false;
					this.cancel();
				}
//				synchronized (RemarksRemovedDB.LOCK) {
//					DBContext ctx = new DBContext("clfcremarksremoved.db");
//					remarksRemoved.setDBContext(ctx);
//					try {
//						hasPendingRemarksRemoved = remarksRemoved.hasPendingRemarksRemoved();
//					} catch (Throwable t) {
//						t.printStackTrace();
//					}
//				}

//				if (hasPendingRemarksRemoved == true) {
//					Platform.getTaskManager().schedule(runnableImpl, delay*1000);	
//				} else if (hasPendingRemarksRemoved == false) {
//					serviceStarted = false;
//				}
			}
			*/
			/*
			private void execRemarksRemoved(List<Map> list) {
					
				if (!list.isEmpty()) {
					size = (list.size() < SIZE-1? list.size() : SIZE-1);
					int networkStatus = 0;
					for (int i=0; i<size; i++) {
						proxy = new MapProxy((Map) list.get(i));

						networkStatus = app.getNetworkStatus();
						if (networkStatus == 3) {
							break;
						}
						
						params.clear();
						params.put("detailid", proxy.getString("objid"));
						
						if (response == null) response = new HashMap();
						response.clear();
						for (int j=0; i<10; j++) {
							try {
								LoanPostingService svc = new LoanPostingService();
								response = svc.removeRemarks(params);
								removeRemarks(response, proxy.getString("objid"));
								break;
							} catch (Throwable e) {;}
						}
					}
				}
			}
			*/
			/*
			private void removeRemarks(Map response, String objid) throws Exception {
				String str = "";
				if (response.containsKey("response")) {
					str = response.get("response").toString().toLowerCase();
				}
				if ("success".equals(str)) {
					synchronized (RemarksRemovedDB.LOCK) {
						remarksremoveddb = new SQLTransaction("clfcremarksremoved.db");
						remarksRemoved.setDBContext(remarksremoveddb.getContext());
						try {
							remarksremoveddb.beginTransaction();
							remarksremoveddb.delete("remarks_removed", "objid=?", new Object[]{objid});
							remarksremoveddb.commit();
						} catch (Throwable t) {
							t.printStackTrace();
						} finally {
							remarksremoveddb.endTransaction();
						}
					}
				}
			}
			*/
		};
	}
}
