package com.rameses.clfc.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;

import com.rameses.clfc.android.db.DBRemarksService;
import com.rameses.clfc.android.services.LoanPostingService;
import com.rameses.client.android.Platform;
import com.rameses.client.android.Task;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.MapProxy;

public class RemarksService 
{
	private final int SIZE = 6;
	
	private ApplicationImpl app;
	private AppSettingsImpl appSettings;
	private Handler handler;
	private SQLTransaction remarksdb;
	private DBRemarksService remarksSvc = new DBRemarksService();
	private String mode = "";
	private MapProxy proxy;
	private Map params = new HashMap();
	private Map loanapp = new HashMap();
	private Map borrower = new HashMap();
	private Map response = new HashMap();
	private Map collector = new HashMap();
	private Map collectionSheet = new HashMap();
	private int networkStatus = 0;
	private int delay;
	private int size;
	private LoanPostingService svc = new LoanPostingService();
	private boolean hasUnpostedRemarks = false;
	private Task actionTask;
	
	public static boolean serviceStarted = false;
	
	public RemarksService(ApplicationImpl app) {
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
			delay = appSettings.getUploadTimeout()*1000;
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
				List<Map> list = new ArrayList<Map>();
				synchronized (RemarksDB.LOCK) {
					remarksdb = new SQLTransaction("clfcremarks.db");
					remarksSvc.setDBContext(remarksdb.getContext());
					try {
						remarksdb.beginTransaction();
						list = remarksSvc.getPendingRemarks(SIZE);					
						remarksdb.commit();
					} catch (Throwable t) {
						t.printStackTrace();
					} finally {
						remarksdb.endTransaction();
					}
				}
				
				try {
					execRemarks(list);
				} catch (Throwable t) {
					t.printStackTrace();
				}

				hasUnpostedRemarks = false;
				synchronized (RemarksDB.LOCK) {
					DBContext ctx = new DBContext("clfcremarks.db");
					remarksSvc.setDBContext(ctx);
					try {
						hasUnpostedRemarks = remarksSvc.hasUnpostedRemarks();
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				
				if (hasUnpostedRemarks == false) {
					serviceStarted = false;
					this.cancel();
				}
			}

			private void execRemarks(List<Map> list) {
				if (!list.isEmpty()) {
					size = (list.size() < SIZE-1? list.size() : SIZE-1);
					String loanappid = "";
					for (int i=0; i<size; i++) {
						proxy = new MapProxy((Map) list.get(i));
						
						mode = "ONLINE_WIFI";
						networkStatus = app.getNetworkStatus();
						if (networkStatus == 1) {
							mode = "ONLINE_MOBILE";
						}
						
						collector.put("objid", proxy.getString("collectorid"));
						collector.put("name", proxy.getString("collectorname"));
						
//						mCollectionSheet = dbCollectionSheet.findCollectionSheetByLoanappid(map.get("loanappid").toString());
						collectionSheet.clear();
//						collectionSheet.put("detailid", mCollectionSheet.get("detailid").toString());
						collectionSheet.put("detailid", proxy.getString("detailid"));
						
						loanapp.clear();
						loanappid = proxy.getString("loanappid");
//						loanapp.put("objid", mCollectionSheet.get("loanappid").toString());
//						loanapp.put("appno", mCollectionSheet.get("appno").toString());
						loanapp.put("objid", loanappid);
						loanapp.put("appno", proxy.getString("appno"));
						collectionSheet.put("loanapp", loanapp);
						
						borrower.clear();
//						borrower.put("objid", mCollectionSheet.get("acctid").toString());
//						borrower.put("name", mCollectionSheet.get("acctname").toString());
						borrower.put("objid", proxy.getString("borrowerid"));
						borrower.put("name", proxy.getString("borrowername"));
						collectionSheet.put("borrower", borrower);
						
						params.clear();
						params.put("sessionid", proxy.getString("sessionid"));
						params.put("routecode", proxy.getString("routecode"));
						params.put("mode", mode);
						params.put("trackerid", proxy.getString("trackerid"));
						params.put("longitude", proxy.getDouble("longitude"));
						params.put("latitude", proxy.getDouble("latitude"));
						params.put("collector", collector);
						params.put("collectionsheet", collectionSheet);
						params.put("remarks", proxy.getString("remarks"));
						params.put("type", proxy.getString("cstype"));
						
						response.clear();
						for (int j=0; j<10; j++) {
							try {
								response = svc.updateRemarks(params);
								break;
							} catch (Throwable e) {;}
						}
						if (response.containsKey("response") && response.get("response").toString().toLowerCase().equals("success")) {
							synchronized (RemarksDB.LOCK) {
								remarksdb = new SQLTransaction("clfcremarks.db");
								remarksSvc.setDBContext(remarksdb.getContext());
								try {
									remarksdb.beginTransaction();
									remarksSvc.approveRemarksByLoanappid(loanappid);
									remarksdb.commit();
								} catch (Throwable t) {
									t.printStackTrace();
								} finally {
									remarksdb.endTransaction();
								}
							}
						}
					}
				}
			}
		};
	}
}
