package com.rameses.clfc.android.main;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.CollectionSheetDB;
import com.rameses.clfc.android.db.SystemDB;
import com.rameses.clfc.android.services.LoanBillingService;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.TerminalManager;
import com.rameses.client.android.UIActionBarActivity;
import com.rameses.client.android.UIDialog;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.util.MapProxy;

public class DownloadBillingController 
{
//	private UIActivity activity;
	private UIActionBarActivity activity;
	private ProgressDialog progressDialog;
	private Map route;
	private LoanBillingService svc;
	private AppSettingsImpl settings;
	
	private SystemDB systemdb = new SystemDB();
	private CollectionSheetDB collectionsheetdb = new CollectionSheetDB();

	DownloadBillingController(UIActionBarActivity activity, ProgressDialog progressDialog, Map route) {
		this.activity = activity;
		this.progressDialog = progressDialog;
		this.route = route;
		this.settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
		svc = new LoanBillingService();
	}
	
	void execute() throws Exception {
		progressDialog.setMessage("Downloading route "+route.get("description").toString()+" - "+route.get("area").toString());
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (!progressDialog.isShowing()) progressDialog.show();
			}
		});
		
		Platform.runAsync(new ActionProcess());
	}
	
	private Handler errorhandler = new Handler() {  
		@Override
		public void handleMessage(Message msg) {
			if (progressDialog.isShowing()) progressDialog.dismiss();
			
			Bundle data = msg.getData();			
			Object o = data.getSerializable("response"); 
			if (o instanceof Throwable) {
				Throwable t = (Throwable)o;
				UIDialog.showMessage(t.getMessage(), activity);
//				ApplicationUtil.showShortMsg("[ERROR] " + t.getMessage());		
			} else {
				UIDialog.showMessage(o, activity);
//				ApplicationUtil.showShortMsg("[ERROR] " + o);
			} 
		} 
	}; 
	
	private Handler successhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
//			route.put("downloaded", 1);			
			if (route != null) {
				route.put("allowdownload", false);
				route.put("itemstate", "DOWNLOADED");
			}
			activity.getHandler().post(new Runnable() {
				public void run() {
					((RouteListActivity) activity).loadRoutes();
				}
			});
			if (progressDialog.isShowing()) progressDialog.dismiss();
			ApplicationUtil.showShortMsg("Successfully downloaded billing!", activity);
		}
	};	
	
	private void println(String msg) {
		Log.i("DownloadBillingController", msg);
	}
	
	private class ActionProcess implements Runnable 
	{
		public void run() {
			Bundle data = new Bundle();			
			Handler handler = null;
			Message message = null;
			try {
				String billingid = MapProxy.getString(route, "billingid");//route.get("billingid").toString();
				String routecode = MapProxy.getString(route, "code");//route.get("code").toString();
				String itemid = MapProxy.getString(route, "itemid");
				String billdate = MapProxy.getString(route, "billdate");
				
				UserProfile prof = SessionContext.getProfile();
				String userid = (prof != null? prof.getUserId() : "");
				
				Map params = new HashMap();
				params.put("billingid", billingid);
				params.put("billdate", billdate);
//				params.put("billdate", route.get("billdate").toString());
//				params.put("billdate", MapProxy.getString(route, "billdate"));
				params.put("routecode", routecode);
//				params.put("itemid", route.get("itemid").toString());
				params.put("itemid", itemid);
				params.put("terminalid", TerminalManager.getTerminalId());
				params.put("userid", userid);

				
				Location location = NetworkLocationProvider.getLocation();
				params.put("lng", (location != null? location.getLongitude() : 0.00));
				params.put("lat", (location != null? location.getLatitude() : 0.00));

				Date date = ApplicationUtil.parseDate(billdate);
//				String trackerid = settings.getTrackerid();
				String trackerid = settings.getTrackerid(date);
				if (trackerid == null || "".equals(trackerid)) {
					Map map = ApplicationUtil.renewTracker(date);
					if (map != null && !map.isEmpty()) {
						settings.putAll(map);
						trackerid = settings.getTrackerid(date);
					}
				}
				
				String tracker_owner = settings.getTrackerOwner(date);
				params.put("trackerid", trackerid);

				Map map = svc.downloadBilling( params );

//				String mTrackerid = map.get("trackerid").toString();
//				String userid = SessionContext.getProfile().getUserId();
//				
//				if (trackerid == null || "".equals(trackerid)) {
//					settings.put("trackerid", mTrackerid);
//					settings.put("tracker_owner", userid);
//					trackerid = settings.getTrackerid();
//					
//				} else if (!trackerid.equals(mTrackerid)) {
//					if (!userid.equals(tracker_owner)) {
//						settings.put("trackerid", mTrackerid);
//						settings.put("tracker_owner", userid);
//						trackerid = settings.getTrackerid();
//					} else {
//						params = new HashMap();
//						params.put("trackerid", mTrackerid);
//						for (int i=0; i<10; i++) {
//							try {
//								svc.removeTracker(params);
//								break;
//							} catch (Exception ex) {
//								throw ex;
//							}
//						}
//					}
//				}
//				String trackerid = settings.getTrackerid();
//				if (trackerid == null || "".equals(trackerid)) {
//				settings.put("trackerid", map.get("trackerid").toString());
//				}
				
				Platform.getLogger().log("[DownloadBillingController.ActionProcess.run] map-> "+map);
				runImpl( map );

				boolean flag = ApplicationUtil.isCollectionCreated( itemid );
				if (flag == false) {
					throw new RuntimeException("Collection not created.");
				}
				
				MapProxy proxy = new MapProxy((Map) map.get("item"));
				params = new HashMap();
				params.put("billingid", billingid);
				params.put("trackerid", trackerid);
				params.put("routecode", routecode); 
				params.put("itemid", proxy.getString("objid"));
				for (int i=0; i<10; i++) {
					try {
						svc.notifyBillingDownloaded(params);
						break;
					} catch (Exception ex) {
						throw ex;
					}
				}
				
				data.putString("response", "success");
				handler = successhandler;
				message = handler.obtainMessage();
				
			} catch(Throwable t) { 
				data.putSerializable("response", t);
				handler = errorhandler;
				message = handler.obtainMessage();				
				t.printStackTrace();
			} 
			
			message.setData(data);
			handler.sendMessage(message); 
		}
		
		private void runImpl( Map data ) throws Exception {
			
			UserProfile profile = SessionContext.getProfile();
			if (profile == null) {
				throw new RuntimeException("Collector not specified.");
			}
			
			String collectorid = profile.getUserId();
			
			String billdate = MapProxy.getString(route, "billdate");
			if (billdate == null || "".equals(billdate)) {
				throw new RuntimeException("Billdate is required.");
			}
			
//			StringBuilder appdbsb = new StringBuilder();
			List<Map> sqlParams = new ArrayList<Map>();
			Map xdata = new HashMap();
			
			MapProxy proxy = new MapProxy((Map) data.get("item"));
			String billingid = proxy.getString("parentid");
			
			boolean flag = systemdb.hasBillingid(collectorid, billdate);
			if (flag == false) {
//				String sql = "insert or ignore into sys_var(`name`,`value`) values('" + collectorid + "-" + billdate + "','" + billingid  + "');";
				Map params = new HashMap();
				params.put("name",  collectorid + "-" + billdate);
				params.put("value", billingid);
				String sql = ApplicationDBUtil.createInsertSQLStatement("sys_var", params);
//				appdbsb.append( sql );
				
				xdata = new HashMap();
				xdata.put("type", "insert");
				xdata.put("sql", sql);
				
				sqlParams.add( xdata );
			}

			if (!proxy.isEmpty()) {
				
				/*
				String sql = "insert or ignore into collection_group(`objid`,`state`,`description`,`area`,`billingid`,`billdate`,`collectorid`,`type`)";
				sql += " values(";
				
				//objid
				sql += "'" + proxy.getString("objid") + "'";
				
				//state
				sql += ",'ACTIVE'";

				//description
				sql += ",'" + MapProxy.getString(route, "description") + "'";
				
				//area
				sql += ",'" + MapProxy.getString(route, "area") + "'";

				//billingid
				sql += ",'" + proxy.getString("parentid") + "'";
				
				//billdate
				sql += ",'" + billdate + "'";

				//collectorid
				sql += ",'" + collectorid + "'";
				
				//type
				sql += ",'route'";
				sql += ");";
				*/
				Map params = new HashMap();
				params.put("objid", proxy.getString("objid"));
				params.put("state", "ACTIVE");
				params.put("description", MapProxy.getString( route, "description" ));
				params.put("area", MapProxy.getString( route, "area" ));
				params.put("billingid", proxy.getString( "parentid" ));
				params.put("billdate", billdate);
				params.put("collectorid", collectorid);
				params.put("type", "route");
				
				String sql = ApplicationDBUtil.createInsertSQLStatement("collection_group", params);

				xdata = new HashMap();
				xdata.put("type", "insert");
				xdata.put("sql", sql);
				
				sqlParams.add( xdata );
//				appdbsb.append( sql );
			}
			
			List<Map> billings = new ArrayList<Map>();
			if (data.containsKey("billings")) {
				billings = (List<Map>) data.get("billings");
			}
			
			if (billings.isEmpty()) return;
			
			collectionsheetdb.dropIndex();
			
			String objid;
			BigDecimal amountdue;
			for (Map item : billings) {
				proxy = new MapProxy( item );
				amountdue = new BigDecimal("0");				
				objid = proxy.getString("objid");
				
				Map xitem = collectionsheetdb.findCollectionSheet( objid );
				if (xitem.isEmpty()) {
					amountdue = new BigDecimal(proxy.getString("amountdue")).setScale(2);

					Map params = new HashMap();
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
					params.put("type", "NORMAL");
					params.put("paymentmethod", proxy.getString("paymentmethod"));
					params.put("isfirstbill", proxy.getInteger("isfirstbill"));
					params.put("totaldays", proxy.getInteger("totaldays"));
					
					String sql = ApplicationDBUtil.createInsertSQLStatement("collectionsheet", params);
					
					xdata = new HashMap();
					xdata.put("type", "insert");
					xdata.put("sql", sql);
					
					sqlParams.add( xdata );
//					appdbsb.append( sql );
				} else {
					if (xitem.containsKey("amountdue")) {
						amountdue = new BigDecimal( xitem.get("amountdue").toString() ).setScale(2);
					}
				}
				
				if (proxy.containsKey("notes")) {
					List<Map> list = (List<Map>) proxy.get("notes");
					
					String txndate;
					for (Map map : list) {
						MapProxy m = new MapProxy( map );
						
						txndate = new SimpleDateFormat("yyyy-MM-dd").parse(m.getString("dtcreated")).toString();
						
						Map params = new HashMap();
						params.put("objid", m.getString("objid"));
						params.put("parentid", proxy.getString("objid"));
						params.put("itemid", proxy.getString("parentid"));
						params.put("txndate", txndate);
						params.put("dtcreated", m.getString("dtcreated"));
						params.put("remarks", m.getString("remarks"));
						
						String sql = ApplicationDBUtil.createInsertSQLStatement("notes", params);
						
						xdata = new HashMap();
						xdata.put("type", "insert");
						xdata.put("sql", sql);
						
						sqlParams.add( xdata );
//						appdbsb.append( sql );
					}
				}
				
				if (proxy.containsKey("payments")) {
					List<Map> list = (List<Map>) proxy.get("payments");
					
					for (Map map : list) {
						MapProxy m = new MapProxy( map );
						
						BigDecimal amount = new BigDecimal(m.getString("amount"));
						String posttype = "Schedule";
						int val = amount.compareTo(amountdue);
						if (val < 0) {
							posttype = "Underpayment";
						} else if (val > 0) {
							posttype = "Overpayment";
						}
						
						Map params = new HashMap();
						params.put("objid", m.getString("objid"));
						params.put("parentid", m.getString("parentid"));
						params.put("itemid", m.getString("itemid"));
						params.put("billingid", m.getString("billingid"));
						params.put("collector_objid", collectorid);
						params.put("txndate", m.getString("txndate"));
						params.put("refno", m.getString("refno"));
						params.put("posttype", posttype);
						params.put("paytype", m.getString("paytype"));
						params.put("amount", m.getDouble("amount"));
						params.put("paidby", m.getString("paidby"));;
						
						String option = m.getString("payoption");
						params.put("payoption", option);
						
						if ("check".equals(option)) {
							params.put("bank_objid", m.getString("bank_objid"));
							params.put("bank_name", m.getString("bank_name"));
							params.put("check_no", m.getString("check_no"));
							params.put("check_date", m.getString("check_date"));
						}
						
						String sql = ApplicationDBUtil.createInsertSQLStatement("payment", params);
						
						xdata = new HashMap();
						xdata.put("type", "insert");
						xdata.put("sql", sql);
						
						sqlParams.add( xdata );
						
//						appdbsb.append( sql );
					}
				}
				
				if (proxy.containsKey("remarkslist")) {
					List<Map> list = (List<Map>) proxy.get("remarkslist");
					
					for (Map map : list) {
						MapProxy m = new MapProxy( map );
						
						int isfollowup = m.getInteger("isfollowup");

						Map params = new HashMap();
						params.put("objid", "REM" + UUID.randomUUID().toString());
						params.put("parentid", proxy.getString("objid"));
						params.put("txndate", m.getString("txndate"));
						params.put("collectorname", m.getString("collectorname"));
						params.put("remarks", m.getString("remarks"));
						
						String tablename = "collector_remarks";
						if (isfollowup == 1) {
							tablename = "followup_remarks";
						}
						
						String sql = ApplicationDBUtil.createInsertSQLStatement(tablename, params);
						
						xdata = new HashMap();
						xdata.put("type", "insert");
						xdata.put("sql", sql);
						
						sqlParams.add( xdata );
//						appdbsb.append( sql );
					}					
				}
				
				if (proxy.containsKey("remarks") && proxy.get("remarks") != null) {

					Map params = new HashMap();
					params.put("objid", proxy.getString("objid"));
					params.put("billingid", proxy.getString("billingid"));
					params.put("itemid", proxy.getString("parentid"));
					params.put("remarks", proxy.getString("remarks"));
					params.put("collector_objid", profile.getUserId());
					params.put("collector_name", profile.getFullName());
					
					String sql = ApplicationDBUtil.createInsertSQLStatement("remarks", params);
					
					xdata = new HashMap();
					xdata.put("type", "insert");
					xdata.put("sql", sql);
					
					sqlParams.add( xdata );
					
//					appdbsb.append( sql );
				}
				
				if (proxy.containsKey("amnesty") && proxy.get("amnesty") != null) {
					MapProxy m = new MapProxy((Map) proxy.get("amnesty"));
					if (!m.isEmpty()) {
						Map params = new HashMap();
						params.put("objid", m.getString("objid"));
						params.put("parentid", proxy.getString("objid"));
						params.put("refno", m.getString("refno"));
						params.put("dtstarted", m.getString("dtstarted"));
						params.put("dtended", m.getString("dtended"));
						params.put("amnestyoption", m.getString("amnestyoption"));						
						params.put("iswaivepenalty", m.getInteger("iswaivepenalty"));
						params.put("iswaiveinterest", m.getInteger("iswaiveinterest"));
						MapProxy offer = new MapProxy((Map) m.get("grantedoffer"));
						params.put("grantedoffer_amount", offer.getDouble("amount"));
						params.put("grantedoffer_days", offer.getInteger("days"));
						params.put("grantedoffer_months", offer.getInteger("months"));
						params.put("grantedoffer_isspotcash", offer.getInteger("isspotcash"));
						params.put("grantedoffer_date", offer.getString("date"));
						
						String sql = ApplicationDBUtil.createInsertSQLStatement("amnesty", params);
						
						xdata = new HashMap();
						xdata.put("type", "insert");
						xdata.put("sql", sql);
						
						sqlParams.add( xdata );
						
//						appdbsb.append( sql );
					}
				}
				
				if (proxy.containsKey("segregation")) {
					List<Map> list = (List<Map>) proxy.get("segregation");
					
					for (Map map : list) {
						MapProxy m = new MapProxy( map );
						Map params = new HashMap();
						params.put("collectionsheetid", proxy.getString("objid"));
						params.put("segregationtypeid", m.getString("segregationid"));
						
						String sql = ApplicationDBUtil.createInsertSQLStatement("collectionsheet_segregation", params);
						
						xdata = new HashMap();
						xdata.put("type", "insert");
						xdata.put("sql", sql);
						
						sqlParams.add( xdata );
						
//						appdbsb.append( sql );
					}
				}
			}
			
			
//			println("sql-> " + appdbsb.toString());
			for (Map map : sqlParams) {
				String str = "";
				if (map.containsKey("type")) {
					str += map.get("type").toString() + ": ";
				}
				
				if (map.containsKey("sql")) {
					str+= map.get("sql").toString();
				}
				
				println("str-> " + str);
			}
			
			if (sqlParams.size() > 0) {
				SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
				try {
					appdb.beginTransaction();
					ApplicationDBUtil.executeSQL( appdb, sqlParams );
//					appdb.execSQL( appdbsb.toString() );					
					appdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					appdb.endTransaction();
				}
			}

			collectionsheetdb.addIndex();			
		}
		
		/* old code for billing insert
		private void xrunImpl(Map map) throws Exception {
			SQLTransaction clfcdb = new SQLTransaction("clfc.db");
//			SQLTransaction paymentdb = new SQLTransaction("clfcpayment.db");
//			SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");
//			try {
//				clfcdb.beginTransaction();
//				synchronized (MainDB.LOCK) {
//					Map params = new HashMap();
//					params.put("name", "trackerid");
//					params.put("value", map.get("trackerid").toString());
//					clfcdb.insert("sys_var", params);
//				}
//				clfcdb.commit();
//			} catch (Exception e) {
//				Map params = new HashMap();
//				params.put("trackerid", map.get("trackerid").toString());
//				for (int i=0; i<10; i++) {
//					try {
//						svc.removeTracker(params);
//						break;
//					} catch (Exception ex) {;}
//				}
//				throw e;
//			} finally {
//				clfcdb.endTransaction();
//			}
			
			try {
				clfcdb.beginTransaction();
//				paymentdb.beginTransaction();
//				remarksdb.beginTransaction();
				
				execute(clfcdb, map);
				
				clfcdb.commit();
//				paymentdb.commit();
//				remarksdb.commit();
			} catch (Exception e) {
				throw e;
			} finally {
				clfcdb.endTransaction();
//				paymentdb.endTransaction();
//				remarksdb.endTransaction();
			}
		}
		
		private void execute(SQLTransaction clfcdb, Map map) throws Exception {
			DBSystemService systemSvc = new DBSystemService();
			systemSvc.setDBContext(clfcdb.getContext());
			systemSvc.setCloseable(false);
			
			Map params = new HashMap();
			
			UserProfile prof = SessionContext.getProfile();
			String collectorid = (prof != null? prof.getUserId() : "");
			
			if (collectorid == null || "".equals(collectorid)) {
				throw new RuntimeException("Collector not specified.");
			}
			
			String billdate = MapProxy.getString(route, "billdate");
			if (billdate == null || "".equals(billdate)) {
				throw new RuntimeException("Billdate is required.");
			}

			MapProxy proxy = new MapProxy((Map) map.get("item"));
			String billingid = proxy.getString("parentid");
			synchronized (MainDB.LOCK) {
				boolean flag = systemSvc.hasBillingid(collectorid, billdate);
				println("has billing id " + flag);
//				billingid = systemSvc.getBillingid(collectorid, billdate);
				if (flag == false) {
					params = new HashMap();
					params.put("name", collectorid + "-" + billdate);
					params.put("value", billingid);
					clfcdb.insert("sys_var", params);
				}
				
//				boolean flag = systemSvc.hasBillingByCollector( collectorid );
//				billingid = s
			}
			
			if (!proxy.isEmpty()) {
				Map item = new HashMap();
				item.put("objid", proxy.getString("objid"));
				item.put("state", "ACTIVE");
				item.put("description", MapProxy.getString(route, "description"));
				item.put("area", MapProxy.getString(route, "area"));
				item.put("billingid", proxy.getString("parentid"));
				item.put("billdate", billdate);
				item.put("collectorid",collectorid);
				item.put("type", "route");
				synchronized (MainDB.LOCK) {
					clfcdb.insert("collection_group", item);
				}
			}
//			params = new HashMap();
//			params.put("routecode",route.get("code").toString());
//			params.put("state", "ACTIVE");
//			params.put("routedescription", route.get("description").toString());
//			params.put("routearea", route.get("area").toString());
//			params.put("sessionid", billingid);
//			params.put("collectorid", SessionContext.getProfile().getUserId());
//			clfcdb.insert("route", params);
			
			DBCollectionSheet csSvc = new DBCollectionSheet();
			csSvc.setDBContext(clfcdb.getContext());
			List billings = (ArrayList) map.get("billings");
			List list;
			String loanappid;
			csSvc.dropIndex();
			Map p = new HashMap();
			int size = billings.size();
						
			Map item;
			int s;
			MapProxy m, offer;
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
					params.put("type", "NORMAL");
					params.put("paymentmethod", proxy.getString("paymentmethod"));
					params.put("isfirstbill", proxy.getInteger("isfirstbill"));
					params.put("totaldays", proxy.getInteger("totaldays"));
					synchronized (MainDB.LOCK) {
						clfcdb.insert("collectionsheet", params);
					}
				}
				
				if (proxy.containsKey("notes")) {
					list = (List) proxy.get("notes");
					s = list.size();
					String txndate;
					for (int j=0; j<s; j++) {
						m = new MapProxy((Map) list.get(j));

						txndate = new SimpleDateFormat("yyyy-MM-dd").parse(m.getString("dtcreated")).toString();
//						txndate = ApplicationUtil.formatDate(java.sql.Date.valueOf(m.getString("dtcreated")), "yyyy-MM-dd");
						
//						params.clear();
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
//					System.out.println("payments " + list);
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
						
//						params.clear();
						params = new HashMap();
						params.put("objid", m.getString("objid"));
						params.put("parentid", m.getString("parentid"));
//						params.put("state", m.getString("state"));
						params.put("itemid", m.getString("itemid"));
						params.put("billingid", m.getString("billingid"));
//						params.put("trackerid", m.getString("trackerid"));
						params.put("collector_objid", collectorid);
//						params.put("collector_name", SessionContext.getProfile().getFullName());
//						params.put("borrower_objid", m.getString("borrower_objid"));
//						params.put("borrower_name", m.getString("borrower_name"));
//						params.put("loanapp_objid", m.getString("loanapp_objid"));
//						params.put("loanapp_appno", m.getString("loanapp_appno"));
//						params.put("routecode", m.getString("routecode"));
						params.put("txndate", m.getString("txndate"));
						params.put("refno", m.getString("refno"));
						params.put("posttype", posttype);
						params.put("paytype", m.getString("paytype"));
						params.put("amount", m.getDouble("amount"));
						params.put("paidby", m.getString("paidby"));
//						params.put("isfirstbill", m.getString("isfirstbill"));
//						params.put("lng", m.getDouble("lng"));
//						params.put("lat", m.getDouble("lat"));
//						params.put("type", m.getString("type"));
						
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
//						params.clear();
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
//					params.clear();
					params = new HashMap();
					params.put("objid", proxy.getString("objid"));
					params.put("billingid", proxy.getString("billingid"));
					params.put("itemid", proxy.getString("parentid"));
					params.put("remarks", proxy.getString("remarks"));
					params.put("collector_objid", (prof != null? prof.getUserId() : ""));
					params.put("collector_name", (prof != null? prof.getFullName() : ""));
//					params.put("state", "CLOSED");
//					params.put("trackerid", "UPLOADED");
//					params.put("txndate", Platform.getApplication().getServerDate().toString());
//					params.put("borrower_objid", proxy.getString("acctid"));
//					params.put("borrower_name", proxy.getString("acctname"));
//					params.put("loanapp_objid", proxy.getString("loanappid"));
//					params.put("loanapp_appno", proxy.getString("appno"));
//					params.put("routecode", proxy.getString("routecode"));
//					params.put("lng", 0.00);
//					params.put("lat", 0.00);
//					params.put("type", "SYSTEM");
					synchronized (MainDB.LOCK) {
						clfcdb.insert("remarks", params);
					}
				}
				
				if (proxy.containsKey("amnesty") && proxy.get("amnesty") != null) {
					m = new MapProxy((Map) proxy.get("amnesty"));
					if (!m.isEmpty()) {
//						params.clear();
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
					println("successfully added segregation");
				}
			}
			csSvc.addIndex();
		}
		*/
	}
}
