package com.rameses.clfc.android.main;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.MainDB;
import com.rameses.clfc.android.db.DBCollectionSheet;
import com.rameses.clfc.android.db.DBSpecialCollection;
import com.rameses.clfc.android.db.DBSystemService;
import com.rameses.clfc.android.services.LoanBillingService;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.TerminalManager;
import com.rameses.client.android.UIActionBarActivity;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.MapProxy;

public class DownloadSpecialCollectionRequestController 
{
//	private UIActivity activity;
	private UIActionBarActivity activity;
	private ProgressDialog progressDialog;
	private Map specialcollection;
	private AppSettingsImpl settings;
	
	DownloadSpecialCollectionRequestController(UIActionBarActivity activity, ProgressDialog progressDialog, Map specialcollection) {
		this.activity = activity;
		this.progressDialog = progressDialog;
		this.specialcollection = specialcollection;
		this.settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
	}
	
	void execute() throws Exception {
		progressDialog.setMessage("Downloading special collection..");
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (!progressDialog.isShowing()) progressDialog.show();
			}
		});
		
		Platform.runAsync(new DownloadActionProcess());
	}
	
	private void println(String msg) {
		Log.i("DownloadSpecialCollectionRequestController", msg);
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
			activity.getHandler().post(new Runnable() {
				public void run() {
					((SpecialCollectionActivity) activity).loadRequests();
				}
			});
			if (progressDialog.isShowing()) progressDialog.dismiss();
			ApplicationUtil.showShortMsg("Successfully downloaded billing!", activity);
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

				UserProfile prof = SessionContext.getProfile();
				String userid = (prof != null? prof.getUserId() : "");
				
				String trackerid = settings.getTrackerid();
//				String trackerid = "";
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
				
				String specialcollectionid = MapProxy.getString(specialcollection, "objid");
				
				Map params = new HashMap();
				params.put("objid", specialcollectionid);
				params.put("trackerid", trackerid);
				params.put("terminalid", TerminalManager.getTerminalId());
				params.put("lng", lng);
				params.put("lat", lat);
				params.put("userid", userid);
				params.put("type", "REQUEST");
				
				LoanBillingService svc = new LoanBillingService();
				Map response = svc.downloadSpecialCollection(params);
								
				saveSpecialCollection(response);
				
				boolean flag = ApplicationUtil.isCollectionCreated(specialcollectionid);
				if (flag == false) {
					throw new RuntimeException("Collection not created.");
				}
				
				params = new HashMap();
				params.put("objid", specialcollectionid);
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
			SQLTransaction clfcdb = new SQLTransaction("clfc.db");
//			SQLTransaction paymentdb = new SQLTransaction("clfcpayment.db");
//			SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");
			try {
				//System.out.println("Special collection id: " + specialcollection.get("objid").toString());
				clfcdb.beginTransaction();
//				paymentdb.beginTransaction();
//				remarksdb.beginTransaction();
								
				saveSpecialCollectionImpl(clfcdb, map);
				
				//if (1==1) throw new RuntimeException("stopping");
				clfcdb.commit();
//				paymentdb.commit();
//				remarksdb.commit();
			} catch(Exception e) {
				throw e;
			} finally {
				clfcdb.endTransaction();
//				paymentdb.endTransaction();
//				remarksdb.endTransaction();
			}
		}
		
		private void saveSpecialCollectionImpl(SQLTransaction clfcdb, Map map) throws Exception {
			DBSpecialCollection specialCollection = new DBSpecialCollection();
			specialCollection.setDBContext(clfcdb.getContext());
			
			DBSystemService systemSvc = new DBSystemService();
			systemSvc.setDBContext(clfcdb.getContext());
			
			DBCollectionSheet collectionSheet = new DBCollectionSheet();
			collectionSheet.setDBContext(clfcdb.getContext());
						
			//Map request = (Map) map.get("request");
			MapProxy proxy = new MapProxy((Map) map.get("item"));
			
			UserProfile prof = SessionContext.getProfile();
			String collectorid = (prof != null? prof.getUserId() : "");
			
			if (collectorid == null || "".equals(collectorid)) {
				throw new RuntimeException("Collector not specified.");
			}

//			println("billdate " + proxy.getString("billdate"));
//			String billdate = MapProxy.getString(specialcollection, "billdate");
			String billdate = proxy.getString("billdate");
			if (billdate == null || "".equals(billdate)) {
				throw new RuntimeException("Billdate is required.");
			}

			String billingid = MapProxy.getString(map, "billingid");
			synchronized (MainDB.LOCK) {
				boolean flag = systemSvc.hasBillingid(collectorid, billdate);
//				billingid = systemSvc.getBillingid(collectorid, billdate);
				if (flag == false) {
					Map params = new HashMap();
					params.put("name", collectorid + "-" + billdate);
					params.put("value", billingid);
					clfcdb.insert("sys_var", params);
				}	
			}	
			
			Map params = new HashMap();
//			params.put("objid", specialcollection.get("objid").toString());
			params.put("objid", MapProxy.getString(specialcollection, "objid"));
			params.put("state", "DOWNLOADED");
//			System.out.println("params -> "+params);
			synchronized (MainDB.LOCK) {
				specialCollection.changeStateById(params);
			}
			
			if (!proxy.isEmpty()) {
				Map item = new HashMap();
				item.put("objid", proxy.getString("objid"));
				item.put("state", "ACTIVE");
				item.put("description", MapProxy.getString(specialcollection, "name"));
				item.put("billingid", proxy.getString("parentid"));
				item.put("billdate", billdate);
				item.put("collectorid",collectorid);
				item.put("type", "special");
				synchronized (MainDB.LOCK) {
					clfcdb.insert("collection_group", item);
				}
			}
			
			List<Map> billings = (List<Map>) map.get("list");
			if (!billings.isEmpty()) {	
				params = new HashMap();
				Map item = new HashMap();
				MapProxy m, offer;
				List list;
				int s, size = billings.size();
				BigDecimal amountdue = new BigDecimal("0").setScale(2);
				BigDecimal amount = amountdue;
				for (int i=0; i<size; i++) {
					proxy = new MapProxy((Map) billings.get(i));
					
					item = clfcdb.find("SELECT objid FROM collectionsheet WHERE objid=? LIMIT 1", new Object[]{proxy.getString("objid")});
					if (item != null) {
						System.out.println("item " + item);
					} else {
											
						amountdue = new BigDecimal(proxy.getDouble("amountdue")+"").setScale(2);
	//					params.clear();
						params = new HashMap();
						params.put("objid", proxy.getString("objid"));
						params.put("billingid", proxy.getString("billingid"));
						params.put("itemid", proxy.getString("parentid"));
						params.put("seqno", proxy.getInteger("seqno"));
						params.put("borrower_objid", proxy.getString("acctid"));
						params.put("borrower_name", proxy.getString("acctname"));
						params.put("loanapp_objid", proxy.getString("loanappid"));
						params.put("loanapp_appno", proxy.getString("appno"));
						params.put("loanapp_loanamount", proxy.getDouble("loanamount"));
						params.put("amountdue", proxy.getDouble("amountdue"));
						params.put("overpaymentamount", proxy.getDouble("overpaymentamount"));
						params.put("refno", proxy.getString("refno"));
						params.put("routecode", proxy.getString("routecode"));
						params.put("term", proxy.getInteger("term"));
						params.put("releasedate", proxy.getString("dtreleased"));
						params.put("maturitydate", proxy.getString("dtmatured"));
						params.put("dailydue", proxy.getDouble("dailydue"));
						params.put("balance", proxy.getDouble("balance"));
						params.put("interest", proxy.getDouble("interest"));
						params.put("penalty", proxy.getDouble("penalty"));
						params.put("others", proxy.getDouble("others"));
						params.put("homeaddress", proxy.getString("homeaddress"));
						params.put("collectionaddress", proxy.getString("collectionaddress"));
						params.put("type", "SPECIAL");
						params.put("paymentmethod", proxy.getString("paymentmethod"));
						params.put("isfirstbill", proxy.getString("isfirstbill"));
						params.put("totaldays", proxy.getInteger("totaldays"));
						synchronized (MainDB.LOCK) {
	//						params.put("seqno", collectionSheet.getCountByRoutecode(m.get("routecode").toString()));
							clfcdb.insert("collectionsheet", params);
						}
						
						if (proxy.containsKey("notes")) {
							list = (List) proxy.get("notes");
							s = list.size();
							String txndate;
							for (int j=0; j<s; j++) {
								m = new MapProxy((Map) list.get(j));
								
								txndate = new SimpleDateFormat("yyyy-MM-dd").parse(m.getString("dtcreated")).toString();
	//							txndate = ApplicationUtil.formatDate(new SimpleDateFormat("yyyy-MM-dd").parse(m.getString("dtcreated")), "yyyy-MM-dd");
								
	//							params.clear();
								params = new HashMap();
								params.put("objid", m.getString("objid"));
								params.put("parentid", proxy.getString("objid"));
								params.put("itemid", proxy.getString("parentid"));
								params.put("txndate", txndate);
								params.put("dtcreated", m.getString("dtcreated"));
								params.put("remarks", m.getString("remarks"));
								synchronized (MainDB.LOCK) {
									clfcdb.insert("notes", params);
								}
							}
						}	
						
						if (proxy.containsKey("payments")) {
							list = (List) proxy.get("payments");
							s = list.size();
							String option;
							for (int j=0; j<s; j++) {
								m = new MapProxy((Map) list.get(j));
	
								amount = new BigDecimal(m.getDouble("amount")+"");
								String posttype = "Schedule";
								int val = amount.compareTo(amountdue);
								if (val < 0) {
									posttype = "Underpayment";
								} else if (val > 0) {
									posttype = "Overpayment";
								}
	//							params.clear();
								params = new HashMap();
								params.put("objid", m.getString("objid"));
								params.put("parentid", m.getString("parentid"));
	//							params.put("state", m.getString("state"));
								params.put("itemid", m.getString("itemid"));
								params.put("billingid", m.getString("billingid"));
	//							params.put("trackerid", m.getString("trackerid"));
								params.put("collector_objid", (prof != null? prof.getUserId() : ""));
	//							params.put("collector_name", SessionContext.getProfile().getFullName());
	//							params.put("borrower_objid", m.getString("borrower_objid"));
	//							params.put("borrower_name", m.getString("borrower_name"));
	//							params.put("loanapp_objid", m.getString("loanapp_objid"));
	//							params.put("loanapp_appno", m.getString("loanapp_appno"));
	//							params.put("routecode", m.getString("routecode"));
								params.put("txndate", m.getString("txndate"));
								params.put("refno", m.getString("refno"));
								params.put("posttype", posttype);
								params.put("paytype", m.getString("paytype"));
								params.put("amount", m.getDouble("amount"));
								params.put("paidby", m.getString("paidby"));
	//							params.put("isfirstbill", m.getString("isfirstbill"));
	//							params.put("lng", m.getDouble("lng"));
	//							params.put("lat", m.getDouble("lat"));
	//							params.put("type", m.getString("type"));
								
								option = m.getString("payoption");
								params.put("payoption", option);
								
								if ("check".equals(option)) {
									params.put("bank_objid", m.getString("bank_objid"));
									params.put("bank_name", m.getString("bank_name"));
									params.put("check_no", m.getString("check_no"));
									params.put("check_date", m.getString("check_date"));
								}
								
								synchronized (MainDB.LOCK) {
									clfcdb.insert("payment", params);
								}
							}
						}
						
						if (proxy.containsKey("remarkslist")) {
							list = (List) proxy.get("remarkslist");
							s = list.size();
							int isfollowup = 0;
							for (int j=0; j<s; j++) {
								m = new MapProxy((Map) list.get(j));
								
								isfollowup = m.getInteger("isfollowup");
	//							params.clear();
								params = new HashMap();
								params.put("objid", "REM" + UUID.randomUUID().toString());
								params.put("parentid", proxy.getString("objid"));
								params.put("txndate", m.getString("txndate"));
								params.put("collectorname", m.getString("collectorname"));
								params.put("remarks", m.getString("remarks"));
								
								synchronized (MainDB.LOCK) {
									if (isfollowup == 0) {
										clfcdb.insert("collector_remarks", params);
									} else if (isfollowup == 1) {
										clfcdb.insert("followup_remarks", params);
									}
								}
							}
						}
						
						if (proxy.containsKey("remarks") && proxy.get("remarks") != null) {
							params = new HashMap();
							params.put("objid", proxy.getString("objid"));
							params.put("billingid", proxy.getString("billingid"));
							params.put("itemid", proxy.getString("parentid"));
	//						params.put("state", "CLOSED");
	//						params.put("trackerid", "UPLOADED");
	//						params.put("txndate", Platform.getApplication().getServerDate().toString());
	//						params.put("borrower_objid", proxy.getString("acctid"));
	//						params.put("borrower_name", proxy.getString("acctname"));
	//						params.put("loanapp_objid", proxy.getString("loanappid"));
	//						params.put("loanapp_appno", proxy.getString("appno"));
							params.put("collector_objid", (prof != null? prof.getUserId() : ""));
							params.put("collector_name", (prof != null? prof.getFullName() : ""));
	//						params.put("routecode", proxy.getString("routecode"));
							params.put("remarks", proxy.getString("remarks"));
	//						params.put("lng", 0.00);
	//						params.put("lat", 0.00);
	//						params.put("type", "SYSTEM");
							synchronized (MainDB.LOCK) {
								clfcdb.insert("remarks", params);
							}
						}
						
						if (proxy.containsKey("amnesty") && proxy.get("amnesty") != null) {
							m = new MapProxy((Map) proxy.get("amnesty"));
							if (!m.isEmpty()) {
	//							params.clear();
								params = new HashMap();
								params.put("objid", m.getString("objid"));
								params.put("parentid", proxy.getString("objid"));
								params.put("refno", m.getString("refno"));
								params.put("dtstarted", m.getString("dtstarted"));
								params.put("dtended", m.getString("dtended"));
								params.put("amnestyoption", m.getString("amnestyoption"));						
								params.put("iswaivepenalty", m.getInteger("iswaivepenalty"));
								params.put("iswaiveinterest", m.getInteger("iswaiveinterest"));
								offer = new MapProxy((Map) m.get("grantedoffer"));
								params.put("grantedoffer_amount", offer.getDouble("amount"));
								params.put("grantedoffer_days", offer.getInteger("days"));
								params.put("grantedoffer_months", offer.getInteger("months"));
								params.put("grantedoffer_isspotcash", offer.getInteger("isspotcash"));
								params.put("grantedoffer_date", offer.getString("date"));
								synchronized (MainDB.LOCK) {
									clfcdb.insert("amnesty", params);
								}
							}
						}
						
						if (proxy.containsKey("segregation")) {
							list = (List) proxy.get("segregation");
							s = list.size();
							//Log.i("CollectionSheetListFragment", "name " + proxy.getString("acctname") + " segregation: " + list);
							for (int j=0; j<s; j++) {
								m = new MapProxy((Map) list.get(j));
								params = new HashMap();
								params.put("collectionsheetid", proxy.getString("objid"));
								params.put("segregationtypeid", m.getString("segregationid"));
								synchronized (MainDB.LOCK) {
									clfcdb.insert("collectionsheet_segregation", params);
								}
							}
						}
					}
				}
			}
		}
	}
}
