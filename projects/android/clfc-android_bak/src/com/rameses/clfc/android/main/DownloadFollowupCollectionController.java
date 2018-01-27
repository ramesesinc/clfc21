package com.rameses.clfc.android.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.MainDB;
import com.rameses.clfc.android.db.DBCollectionSheet;
import com.rameses.clfc.android.db.DBSystemService;
import com.rameses.clfc.android.services.LoanBillingService;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.TerminalManager;
import com.rameses.client.android.UIActivity;
import com.rameses.db.android.SQLTransaction;

public class DownloadFollowupCollectionController 
{
	private UIActivity activity;
	private ProgressDialog progressDialog;
	private Map followupcollection;
	private AppSettingsImpl settings;
	
	DownloadFollowupCollectionController(UIActivity activity, ProgressDialog progressDialog, Map followupcollection) {
		this.activity = activity;
		this.progressDialog = progressDialog;
		this.followupcollection = followupcollection;
		this.settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
	}

	void execute() throws Exception {
		progressDialog.setMessage("processing...");
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (!progressDialog.isShowing()) progressDialog.show();
			}
		});
		
		Platform.runAsync(new DownloadActionProcess());
	}
	
	private Handler errorhandler = new Handler() {  
		@Override
		public void handleMessage(Message msg) {
			if (progressDialog.isShowing()) progressDialog.dismiss();
			
			Bundle data = msg.getData();			
			Object o = data.getSerializable("response"); 
			if (o instanceof Throwable) {
				Throwable t = (Throwable)o;
				ApplicationUtil.showShortMsg("[ERROR] " + t.getMessage());		
			} else {
				ApplicationUtil.showShortMsg("[ERROR] " + o);	
			} 
		} 
	}; 
	
	private Handler successhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			followupcollection.put("downloaded", 1);
			activity.getHandler().post(new Runnable() {
				public void run() {
					((RouteListActivity) activity).loadFollowupCollections();
				}
			});
			if (progressDialog.isShowing()) progressDialog.dismiss();
			ApplicationUtil.showShortMsg("Successfully downloaded follow-up collection!", activity);
		}
	};	
	
	private class DownloadActionProcess implements Runnable {
		public void run() {
			Bundle data = new Bundle();
			Message message = null;
			Handler handler = null;
			try {
				Location location = NetworkLocationProvider.getLocation();
				double lng = (location == null? 0.0 : location.getLongitude());
				double lat = (location == null? 0.0 : location.getLatitude());
				
				String trackerid = settings.getTrackerid();
//				synchronized (MainDB.LOCK) {
//					DBContext ctx = new DBContext("clfc.db");
//					DBSystemService systemSvc = new DBSystemService();
//					systemSvc.setDBContext(ctx);
//					try {
//						trackerid = systemSvc.getTrackerid();
//					} catch (Throwable t) {
//						throw t;
//					}
//				}
				 
				String followupcollectionid = followupcollection.get("objid").toString();
				
				Map params = new HashMap();
				params.put("trackerid", trackerid);
				params.put("objid", followupcollectionid);
				params.put("terminalid", TerminalManager.getTerminalId());
				params.put("longitude", lng);
				params.put("latitude", lat);
				params.put("userid", SessionContext.getProfile().getUserId());
				
				LoanBillingService svc = new LoanBillingService();
				Map response = svc.downloadSpecialCollection(params);
				
				String mTrackerid = response.get("trackerid").toString();
				
				if (trackerid == null || "".equals(trackerid)) {
					settings.put("trackerid", response.get("trackerid"));
					trackerid = settings.getTrackerid();
					
				} else if (!trackerid.equals(mTrackerid)) {
					params = new HashMap();
					params.put("trackerid", mTrackerid);
					for (int i=0; i<10; i++) {
						try {
							svc.removeTracker(params);
							break;
						} catch (Exception ex) {
							throw ex;
						}
					}
				}
				saveSpecialCollection(response);

				params = new HashMap();
				params.put("objid", followupcollectionid);
				params.put("trackerid", trackerid);
				for (int i=0; i<10; i++) {
					try {
						svc.notifySpecialCollectionDownloaded(params);
						break;
					} catch (Exception ex) {
						throw ex;
					}
				}
				
				data.putString("response", "success");
				message = successhandler.obtainMessage();
				handler = successhandler;
			} catch (Throwable t) {
				t.printStackTrace();
				Platform.getLogger().log(t);
				data.putSerializable("response", t);
				message = errorhandler.obtainMessage();
				handler = errorhandler;
			}
			
			message.setData(data);
			handler.sendMessage(message);
		}
		
		private void saveSpecialCollection(Map map) throws Exception {
			SQLTransaction txn = new SQLTransaction("clfc.db");
			try {
				txn.beginTransaction();
				saveSpecialCollectionImpl(txn, map);
				txn.commit();
			} catch(Exception e) {
				throw e;
			} finally {
				txn.endTransaction();
			}
		}
		
		private void saveSpecialCollectionImpl(SQLTransaction txn, Map map) throws Exception {
			DBSystemService systemSvc = new DBSystemService();
			systemSvc.setDBContext(txn.getContext());
			systemSvc.setCloseable(false);
			
			DBCollectionSheet collectionSheet = new DBCollectionSheet();
			collectionSheet.setDBContext(txn.getContext());
			collectionSheet.setCloseable(false);

			Map request = (Map) map.get("request");
			
//			synchronized (MainDB.LOCK) {
//				String trackerid = systemSvc.getTrackerid();
//				System.out.println("[DownloadFollowupCollectionController.saveSpecialCollectionImpl] trackerid -> "+trackerid);
//				if (trackerid == null || "".equals(trackerid)) {
//					Map mParams = new HashMap();
//					mParams.put("name", "trackerid");
//					mParams.put("value", map.get("trackerid").toString());
//					txn.insert("sys_var", mParams);
//				}
//			}
			
			
			String billingid = "";
			synchronized (MainDB.LOCK) {
				billingid = systemSvc.getBillingid();			
				if (billingid == null || "".equals(billingid)) {
					billingid = request.get("billingid").toString();
					Map mParams = new HashMap();
					mParams.put("name", "billingid");
					mParams.put("value", billingid);
					txn.insert("sys_var", mParams);
				}
			}
			
			String collectorid = SessionContext.getProfile().getUserId();			
			
			Map params = new HashMap();
			List<Map> list = (List<Map>) map.get("routes");

			if (!list.isEmpty()) {
				Map m;
				boolean flag;
				for (int i=0; i<list.size(); i++) {
					m = (Map) list.get(i);
					
					flag = true;
					synchronized (MainDB.LOCK) {
						Map t = txn.find("SELECT routecode FROM route WHERE routecode=?", new Object[]{m.get("code").toString()});
						if (t != null) flag = false;
					}
					
					if (flag == true) {
						params = new HashMap();
						params.put("routecode", m.get("code").toString());
						params.put("routedescription", m.get("description").toString());
						params.put("routearea", m.get("area").toString());
						params.put("state", "ACTIVE");
						params.put("sessionid", billingid);
						params.put("collectorid", collectorid);
						synchronized (MainDB.LOCK) {
							txn.insert("route", params);	
						}
					}
				}
			}
			
			list = (List<Map>) map.get("list");
			
			if (!list.isEmpty()) {				
				Map m;
				for (int i=0; i<list.size(); i++) {
					m = (Map) list.get(i);
										
					params = new HashMap();
					params.put("loanappid", m.get("loanappid").toString());
					params.put("detailid", m.get("objid").toString());
					params.put("appno", m.get("appno").toString());
					params.put("acctid", m.get("acctid").toString());
					params.put("acctname", m.get("acctname").toString());
					params.put("loanamount", Double.parseDouble(m.get("loanamount").toString()));
					params.put("term", Integer.parseInt(m.get("term").toString()));
					params.put("balance", Double.parseDouble(m.get("balance").toString()));
					params.put("dailydue", Double.parseDouble(m.get("dailydue").toString()));
					params.put("amountdue", Double.parseDouble(m.get("amountdue").toString()));
					params.put("interest" ,Double.parseDouble(m.get("interest").toString()));
					params.put("penalty", Double.parseDouble(m.get("penalty").toString()));
					params.put("others", Double.parseDouble(m.get("others").toString()));
					params.put("overpaymentamount", Double.parseDouble(m.get("overpaymentamount").toString()));
					params.put("refno", m.get("refno").toString());
					params.put("routecode", m.get("routecode").toString());
					params.put("duedate", m.get("dtmatured").toString());
					params.put("isfirstbill", Integer.parseInt(m.get("isfirstbill").toString()));
					params.put("paymentmethod", m.get("paymentmethod").toString());
					System.out.println("payment method = " + m.get("paymentmethod").toString());
					params.put("homeaddress", m.get("homeaddress").toString());
					params.put("collectionaddress", m.get("collectionaddress").toString());
					params.put("sessionid", m.get("sessionid").toString());
					params.put("type", "FOLLOWUP");
					synchronized (MainDB.LOCK) {
						params.put("seqno", collectionSheet.getCountByRoutecode(m.get("routecode").toString()));
						txn.insert("collectionsheet", params);
					}
				}
			}
		}
	}
}
