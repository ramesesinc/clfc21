package com.rameses.clfc.android.main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.CollectionSheetDB;
import com.rameses.clfc.android.db.LocationTrackerDB;
import com.rameses.clfc.android.db.PaymentServiceDB;
import com.rameses.clfc.android.db.RemarksServiceDB;
import com.rameses.clfc.android.db.VoidServiceDB;
import com.rameses.clfc.android.services.LoanPostingService;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIActionBarActivity;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MapProxy;

public class RemitRouteCollectionController 
{
//	private UIActivity activity;
	private UIActionBarActivity activity;
	private ProgressDialog progressDialog;
	private Map collection;
	private ApplicationImpl app;
	private AppSettingsImpl settings;
	
	private CollectionSheetDB collectionsheetdb = new CollectionSheetDB();
	private PaymentServiceDB paymentservicedb = new PaymentServiceDB();
	private VoidServiceDB voidservicedb = new VoidServiceDB();
	private RemarksServiceDB remarksservicedb = new RemarksServiceDB();
	private LocationTrackerDB locationtrackerdb = new LocationTrackerDB();
	
	RemitRouteCollectionController(UIActionBarActivity activity, ProgressDialog progressDialog, Map collection) {
		this.activity = activity;
		this.progressDialog = progressDialog;
		this.collection = collection;
		app = (ApplicationImpl) Platform.getApplication();
		settings = (AppSettingsImpl) app.getAppSettings();
	} 

	void execute() throws Exception {
		String msg = "Remitting collections for " + collection.get("description").toString();
		if ("route".equals(collection.get("type").toString())) msg += "-" + collection.get("area").toString();
		progressDialog.setMessage(msg);
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (!progressDialog.isShowing()) progressDialog.show();
			}
		});
		
		Platform.runAsync(new RemitCollectionActionProcess());
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
			activity.runOnUiThread(new Runnable() {
				public void run() {
					((RemitRouteCollectionActivity) activity).loadRoutes();
					((RemitRouteCollectionActivity) activity).loadFollowups();
					((RemitRouteCollectionActivity) activity).loadSpecials();
				}
			});
			if (progressDialog.isShowing()) progressDialog.dismiss();
			String desc = "";
			if (collection.containsKey("description")) {
				desc = collection.get("description").toString();
			}
			String str = "Collection for route " + desc + " ";
//			String type = "";
			String type = MapProxy.getString(collection, "type");
//			if (collection.containsKey("type")) {
//				type = collection.get("type").toString();
//			}
			if ("route".equals(type)) {
				str += "- " + collection.get("area").toString() + " ";
			}
			str += "successfully remitted.";
			ApplicationUtil.showShortMsg(str);
//			ApplicationUtil.showShortMsg("Collection for route "+route.get("description").toString()+" - "+route.get("area").toString()+" successfully remitted.");
		}
	};
	
	private void println(String msg) {
		Log.i("[RemitRouteCOllectionController]", msg);
	}
	
	private class RemitCollectionActionProcess implements Runnable {
		public void run() {
			Bundle data = new Bundle();
			Message message = null;
			Handler handler = null; 
			try {
				activity.sendBroadcast(new Intent("rameses.clfc.TRACKER_STOP_SERVICE"));
				
				Location location = NetworkLocationProvider.getLocation();
				String val1 = "0", val2 = "0";
				if (location != null) {
					val1 = String.valueOf( location.getLongitude() );
					val2 = String.valueOf( location.getLatitude() );
				}
				
				BigDecimal lng = new BigDecimal( val1 );
				BigDecimal lat = new BigDecimal( val2 );
				
				Map params = getParameters();
				params.put("lng", lng);
				params.put("lat", lat);
//				System.out.println("params " + params);
				Map param = new HashMap();
				String enc = new Base64Cipher().encode(params);
				param.put("encrypted", enc);
				
				LoanPostingService svc = new LoanPostingService();
				
				Map response = new HashMap();
				response = svc.remitCollectionEncrypt(param);
				
				if (response != null && response.containsKey("response")) {
					String res = response.get("response").toString().toLowerCase();
					if ("success".equals(res)) {
						removeData(params);
					}
				}
//				if (response.containsKey("response") && response.get("response").toString().toLowerCase().equals("success")) {
//					synchronized (MainDB.LOCK) {
//						SQLTransaction txn = new SQLTransaction("clfc.db");
////						SQLTransaction paymentdb = new SQLTransaction("clfcpayment.db");
////						DBPaymentService paymentSvc = new DBPaymentService();
////						paymentSvc.setDBContext(paymentdb.getContext());
//						try {
//							txn.beginTransaction();
//							txn.execute("UPDATE collection_group SET state = 'REMITTED' WHERE objid=?", new Object[]{collection.get("objid").toString()});
////							txn.update(tablename, whereClause)
////							paymentSvc.closePayment(proxy.getString("objid"));
//							txn.commit();
//						} catch (Throwable t) {
//							t.printStackTrace();
//						} finally {
//							txn.endTransaction();
//						}
//					}
//				}
//				SQLTransaction txn = new SQLTransaction("clfc.db");
//				DBRouteService dbRs = new DBRouteService();
//				dbRs.setDBContext(txn.getContext());
				
//				dbRs.remitRouteByRoutecode(route.get("code").toString());
				
				data.putString("response", response.get("response").toString());
				message = successhandler.obtainMessage();
				handler = successhandler;
			} catch (Throwable t) {
				t.printStackTrace();
				data.putSerializable("response", t);
				message = errorhandler.obtainMessage();
				handler = errorhandler;
			} finally {
				activity.sendBroadcast(new Intent("rameses.clfc.TRACKER_START_SERVICE"));
			}
			
			message.setData(data);
			handler.sendMessage(message);
		}
		
		private void removeData( Map params ) throws Exception {
			String itemid = "";
			if (params.containsKey("itemid")) {
				itemid = params.get("itemid").toString();
			}
			
			if (itemid != null && !"".equals( itemid )) {
				try {
					println("before remove dataimpl");
					removeDataImpl( itemid );
					println("before remove trackerimpl");
					removeTrackerImpl( params );
					println("after remove all");
				} catch (Throwable t) {
					throw new RuntimeException("Error remitting collection.");
				}
			}
		}
		
//		private void xremoveData(Map params) throws Exception {
//			String itemid = "";
//			if (params.containsKey("itemid")) {
//				itemid = params.get("itemid").toString();
//			}
//			
//			if (itemid != null && !"".equals(itemid)) {
//				SQLTransaction clfcdb = new SQLTransaction("clfc.db");
//				SQLTransaction trackerdb = new SQLTransaction("clfctracker.db");
//				synchronized (MainDB.LOCK) {
//					try {
//						clfcdb.beginTransaction();
//						trackerdb.beginTransaction();
//						
//						removeDataImpl(clfcdb, itemid);
//						removeTrackersImpl(trackerdb, params);
//												
//						clfcdb.commit();
//						trackerdb.commit();
//					} catch (RuntimeException re) {
//						throw re; 
//					} catch (Exception e) {
//						throw e; 
//					} catch (Throwable t) {
//						throw new Exception(t.getMessage(), t); 
//					} finally {
//						clfcdb.endTransaction();
//						trackerdb.endTransaction();
//					}
//				}
//			} else {
//				throw new RuntimeException("Error remitting collection.");
//			}
//			
//		}
		
		private void removeDataImpl( String itemid ) throws Exception {
//			StringBuilder appdbsb = new StringBuilder();
			List<Map> sqlParams = new ArrayList<Map>();
			Map data = new HashMap();
			
			List<Map> list = collectionsheetdb.getCollectionSheetByItemid( itemid );
			
			String objid, sql;
			for (Map item : list) {
				if (item.containsKey("objid")) {
					objid = item.get("objid").toString();
					
					sql = "delete from amnesty where parentid='" + objid + "';";
					
					data = new HashMap();
					data.put("type", "delete");
					data.put("sql", sql);
					
					sqlParams.add( data );
					
//					appdbsb.append( sql );
					
					sql = "delete from collector_remarks where parentid='" + objid + "';";
					
					data = new HashMap();
					data.put("type", "delete");
					data.put("sql", sql);
					
					sqlParams.add( data );
					
//					appdbsb.append( sql );
					
					sql = "delete from followup_remarks where parentid='" + objid + "';";
					
					data = new HashMap();
					data.put("type", "delete");
					data.put("sql", sql);
					
					sqlParams.add( data );
					
//					appdbsb.append( sql );
					
					sql = "delete from collectionsheet_segregation where collectionsheetid='" + objid + "';";
					
					data = new HashMap();
					data.put("type", "delete");
					data.put("sql", sql);
					
					sqlParams.add( data );
					
//					appdbsb.append( sql );
				}
			}
			
			sql = "delete from remarks where itemid='" + itemid + "';";
			
			data = new HashMap();
			data.put("type", "delete");
			data.put("sql", sql);
			
			sqlParams.add( data );
			
//			appdbsb.append( sql );

			sql = "delete from notes where itemid='" + itemid + "';";
			
			data = new HashMap();
			data.put("type", "delete");
			data.put("sql", sql);
			
			sqlParams.add( data );
			
//			appdbsb.append( sql );

			sql = "delete from void_request where itemid='" + itemid + "';";
			
			data = new HashMap();
			data.put("type", "delete");
			data.put("sql", sql);
			
			sqlParams.add( data );
			
//			appdbsb.append( sql );

			sql = "delete from payment where itemid='" + itemid + "';";
			
			data = new HashMap();
			data.put("type", "delete");
			data.put("sql", sql);
			
			sqlParams.add( data );
			
//			appdbsb.append( sql );
			
			sql = "delete from collectionsheet where itemid='" + itemid + "';";
			
			data = new HashMap();
			data.put("type", "delete");
			data.put("sql", sql);
			
			sqlParams.add( data );
			
//			appdbsb.append( sql );

			sql = "delete from specialcollection where objid='" + itemid + "';";
			
			data = new HashMap();
			data.put("type", "delete");
			data.put("sql", sql);
			
			sqlParams.add( data );
			
//			appdbsb.append( sql );

			sql = "delete from collection_group where objid='" + itemid + "';";
			
			data = new HashMap();
			data.put("type", "delete");
			data.put("sql", sql);
			
			sqlParams.add( data ); 
			
//			appdbsb.append( sql );

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
		}
		
//		private void removeDataImpl(SQLTransaction clfcdb, String itemid) throws Exception {
//			DBCollectionSheet dbcs = new DBCollectionSheet();
//			dbcs.setDBContext(clfcdb.getContext());
//			dbcs.setCloseable(false);
//			
//			List list = dbcs.getCollectionSheetsByItem(itemid);
//			Map item = new HashMap();
//			String objid = "";
//			for (int i=0; i<list.size(); i++) {
//				item = (Map) list.get(i);
//				objid = "";
//				if (item.containsKey("objid")) {
//					objid = item.get("objid").toString();
//				}
//				
//				if (objid != null && !"".equals(objid)) {
//					clfcdb.delete("amnesty", "parentid=?", new Object[]{objid});
//					clfcdb.delete("collector_remarks", "parentid=?", new Object[]{objid});
//					clfcdb.delete("followup_remarks", "parentid=?", new Object[]{objid});
//					clfcdb.delete("collectionsheet_segregation", "collectionsheetid=?", new Object[]{objid});
//				}
//			} 
//
//			clfcdb.delete("remarks", "itemid=?", new Object[]{itemid});
//			clfcdb.delete("notes", "itemid=?", new Object[]{itemid});
//			clfcdb.delete("void_request", "itemid=?", new Object[]{itemid});
//			clfcdb.delete("payment", "itemid=?", new Object[]{itemid});
//			clfcdb.delete("collectionsheet", "itemid=?", new Object[]{itemid});
//			clfcdb.delete("specialcollection", "objid=?", new Object[]{itemid});
//			clfcdb.delete("collection_group", "objid=?", new Object[]{itemid});
//		}
		
		private void removeTrackerImpl( Map params ) throws Exception {
//			StringBuilder trackerdbsb = new StringBuilder();
			List<Map> sqlParams = new ArrayList<Map>();
			Map data = new HashMap();
			
			Map tracker = new HashMap();
			if (params.containsKey("tracker")) {
				tracker = (Map) params.get("tracker");
			}
			
			if (tracker.containsKey("date")) {
				String date = tracker.get("date").toString();
				
				String sql = "delete from location_tracker where txndate<='" + date + "';";
				
				data = new HashMap();
				data.put("type", "delete");
				data.put("sql", sql);
				
				sqlParams.add( data );
				
//				trackerdbsb.append( sql );
			}
			
			if (sqlParams.size() > 0) {
				SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
				try {
					trackerdb.beginTransaction();
					ApplicationDBUtil.executeSQL( trackerdb, sqlParams );
//					trackerdb.execSQL( trackerdbsb.toString() );
					trackerdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					trackerdb.endTransaction();
				}
			}
			
		}
		
//		private void removeTrackersImpl(SQLTransaction trackerdb, Map params) throws Exception {
////			List<Map> list = new ArrayList<Map>();
//			Map tracker = new HashMap();
//			if (params.containsKey("tracker")) {
//				tracker = (Map) params.get("tracker");
//			}
//			
//			String date = "";
//			if (tracker.containsKey("date")) {
//				date = tracker.get("date").toString();
//			}
////			
////			if (tracker.containsKey("list")) {
////				list = (List) tracker.get("list");
////			}
//
//			synchronized (TrackerDB.LOCK) {
//				trackerdb.delete("location_tracker", "txndate <= ?", new Object[]{date});
////				int size = list.size();
////				String objid;
////				for (int i = 0; i < size; i++) {
////					objid = MapProxy.getString(((Map) list.get(i)), "objid");
////					
////					if (objid != null) {
////						trackerdb.delete("location_tracker", "objid=?", new Object[]{objid});
////					}
////				} 
//			}
//		}
		
		private Map getParameters() throws Exception {
			
			String itemid = MapProxy.getString(collection, "objid");
			List<Map> list = collectionsheetdb.getCollectionSheetByItemid( itemid );

			int totalcollectionsheets = 0;
			for (Map item : list) {
				boolean haspayment = false;
				boolean hasremarks = false;
				int noOfPayments = 0;
				int noOfVoid = 0;
				
				if (item.containsKey("objid")) {
					String objid = item.get("objid").toString();
					
					hasremarks = remarksservicedb.hasRemarksById( objid );
					noOfPayments = paymentservicedb.noOfPayments( objid );
					noOfVoid = voidservicedb.noOfVoidPayments( objid );
					
					if (noOfPayments > 0 && noOfPayments > noOfVoid) {
						haspayment = true;
					}

					if (haspayment || hasremarks) {
						totalcollectionsheets++;
					}
				}
			}
						
			BigDecimal totalamount = new BigDecimal("0").setScale(2);
			
			list = paymentservicedb.getPaymentsByItem( itemid );
			
			for (Map item : list) {
				Map request = new HashMap();
				
				if (item.containsKey("objid")) {
					String objid = item.get("objid").toString();
					
					request = voidservicedb.findVoidRequestByPaymentidAndState( objid, "APPROVED" );
				}
				
				if (request == null || request.isEmpty()) {
					if (item.containsKey("amount")) {
						String amount = item.get("amount").toString();
						totalamount = totalamount.add(new BigDecimal( amount ).setScale(2));
					}
				}
			}
			
			Map params = new HashMap();
			params.put("trackerid", settings.getTrackerid());
			params.put("itemid", itemid);
			params.put("sessionid", MapProxy.getString(collection, "billingid"));
			params.put("totalcollection", totalcollectionsheets);
			params.put("totalamount", totalamount);
			
			Map collector = new HashMap();
			collector.put("objid", SessionContext.getProfile().getUserId());
			collector.put("name", SessionContext.getProfile().getName());
			params.put("collector", collector);
			
			boolean haspayment = MapProxy.getBoolean(collection, "haspayment");
			params.put("haspayment", haspayment);
			params.put("cbsno", MapProxy.getString(collection, "cbsno"));
			
			if (haspayment == true) {
				params.put("payments", getPayments( itemid ));
			}
			
			params.put("tracker", getTracker());
			
			return params;
		}
		
		
//		private Map xgetParameters() throws Exception {
//			Map params = new HashMap();
//			DBContext clfcdb = new DBContext("clfc.db");
//			DBContext paymentdb = new DBContext("clfcpayment.db");
//			DBContext remarksdb = new DBContext("clfcremarks.db");
//			DBContext requestdb = new DBContext("clfcrequest.db");
//			DBContext trackerdb = new DBContext("clfctracker.db");
//			try {
//				params = getParametersImpl(clfcdb, paymentdb, remarksdb, requestdb, trackerdb);
//			} catch (Exception e) {
//				throw e;
//			} finally {
//				clfcdb.close();
//				paymentdb.close();
//				remarksdb.close();
//				requestdb.close();
//				trackerdb.close();
//			}
//			return params;
//		}
		
//		private Map xgetParametersImpl(DBContext clfcdb, DBContext paymentdb, DBContext remarksdb, 
//				DBContext requestdb, DBContext trackerdb) throws Exception {
//			DBCollectionSheet collectionSheet = new DBCollectionSheet();
//			collectionSheet.setDBContext(clfcdb);
//			collectionSheet.setCloseable(false);
//			
//			DBPaymentService paymentSvc = new DBPaymentService();
//			paymentSvc.setDBContext(paymentdb);
//			paymentSvc.setCloseable(false);
//			
//			DBVoidService voidSvc = new DBVoidService();
//			voidSvc.setDBContext(requestdb);
//			voidSvc.setCloseable(false);
//			
//			DBRemarksService remarksSvc = new DBRemarksService();
//			remarksSvc.setDBContext(remarksdb);
//			remarksSvc.setCloseable(false);
//			
//			int totalcollectionsheets = 0;
//			System.out.println("collection " + collection);
////			String itemid = collection.get("objid").toString();
//			String itemid = MapProxy.getString(collection, "objid");
//			List<Map> list = collectionSheet.getCollectionSheetsByItem(itemid);
//
//			if (!list.isEmpty()) {
//				Map item;
//				String objid;
//				boolean haspayment;
//				boolean hasremarks;
//				int noOfPayments = 0;
//				int noOfVoid = 0;
//				int size = list.size();
//				for (int i=0; i<size; i++) {
//					item = (Map) list.get(i);
//					
//					haspayment = false;
//					
//					objid = item.get("objid").toString();
////					synchronized (RemarksDB.LOCK) {
//						hasremarks = remarksSvc.hasRemarksById(objid);
////					}
//					
////					synchronized (PaymentDB.LOCK) {
//						noOfPayments = paymentSvc.noOfPayments(objid);
////					}
//					
////					synchronized (VoidRequestDB.LOCK) {
//						noOfVoid = voidSvc.noOfVoidPayments(objid);
////					}
//					
//					if (noOfPayments > 0 && noOfPayments > noOfVoid) {
//						haspayment = true;
//					}
//
//					if (haspayment || hasremarks) {
//						totalcollectionsheets++;
//					}
//				}
//			}
//			
//			BigDecimal totalamount = new BigDecimal("0").setScale(2);
////			synchronized (PaymentDB.LOCK) {
//				list = paymentSvc.getPaymentsByItem(itemid);
////			}
//			
//			if (!list.isEmpty()) {
//				int size = list.size();
//				Map item, request;
//				for (int i=0; i<size; i++) {
//					item = (Map) list.get(i);
////					synchronized (VoidRequestDB.LOCK) {
//						request = voidSvc.findVoidRequestByPaymentidAndState(MapProxy.getString(item, "objid"), "APPROVED");
////					}
//					
//					if (request == null || request.isEmpty()) {
//						totalamount = totalamount.add(new BigDecimal(MapProxy.getString(item, "amount")).setScale(2));
//					}
//				}
//			}
//			
//			println("total amount " + totalamount);
//			
//			
//			AppSettingsImpl settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
//			
//			Map params = new HashMap();
//			params.put("trackerid", settings.getTrackerid());
//			params.put("itemid", itemid);
////			params.put("sessionid", collection.get("billingid").toString());
//			params.put("sessionid", MapProxy.getString(collection, "billingid"));
//			params.put("totalcollection", totalcollectionsheets);
//			params.put("totalamount", totalamount);
//			
//			Map collector = new HashMap();
//			collector.put("objid", SessionContext.getProfile().getUserId());
//			collector.put("name", SessionContext.getProfile().getName());
//			params.put("collector", collector);
//			boolean haspayment = MapProxy.getBoolean(collection, "haspayment");
//			params.put("haspayment", haspayment);
////			params.put("cbsno", collection.get("cbsno").toString());
//			params.put("cbsno", MapProxy.getString(collection, "cbsno"));
//			
//			if (haspayment == true) {
//				params.put("payments", getPayments( itemid ));
//			}
//			
//			params.put("tracker", getTracker(trackerdb));
//			
//			return params;
//		}
		
		private Map getTracker() throws Exception {
			Map data = new HashMap();
			
			String date = Platform.getApplication().getServerDate().toString();
			data.put("date", date);
			
			UserProfile profile = SessionContext.getProfile();
			String collectorid = (profile == null)? "" : profile.getUserId();			
			
			List<Map> list = locationtrackerdb.getLocationTrackersByCollectoridAndLessThanDate(collectorid, date);
			data.put("list", list);
						
			return data;
		}
		
		
//		private Map getTracker(DBContext trackerdb) throws Exception {
//			Map item = new HashMap();
//			
//			String date = Platform.getApplication().getServerDate().toString();
//			item.put("date", date);
//			
//			UserProfile prof = SessionContext.getProfile();
//			String collectorid = (prof == null)? "" : prof.getUserId();
//			
//			DBLocationTracker trackerSvc = new DBLocationTracker();
//			trackerSvc.setDBContext(trackerdb);
//			trackerSvc.setCloseable(false);
//			
//			List list = trackerSvc.getLocationTrackersByCollectoridAndLessThanDate(collectorid, date);
//			item.put("list", list);
//			
//			return item;
//		}
		
		private List<Map> getPayments( String itemid ) throws Exception {
			List<Map> results = new ArrayList<Map>();
			
			List<Map> list = paymentservicedb.getPaymentsByItem( itemid );
			
			MapProxy proxy;
			String objid;
			Map request, payment, borrower, loanapp, bank, check;
			for (Map item : list) {
				proxy = new MapProxy( item );
				objid = proxy.getString("objid");
				
				request = voidservicedb.findVoidRequestByPaymentid( objid );
				
				if (request == null || request.isEmpty()) {
					payment = new HashMap();
					payment.put("objid", proxy.getString("objid"));
					payment.put("routecode", proxy.getString("routecode"));
					payment.put("refno", proxy.getString("refno"));
					payment.put("amount", proxy.getDouble("amount"));
					payment.put("type", proxy.getString("paytype"));
					payment.put("paidby", proxy.getString("paidby"));
					payment.put("dtpaid", proxy.getString("txndate"));
					
					borrower = new HashMap();
					borrower.put("objid", proxy.getString("borrower_objid"));
					borrower.put("name", proxy.getString("borrower_name"));
					payment.put("borrower", borrower);
					
					loanapp = new HashMap();
					loanapp.put("objid", proxy.getString("loanapp_objid"));
					loanapp.put("appno", proxy.getString("loanapp_appno"));
					payment.put("loanapp", loanapp);
					
					String option = proxy.getString("payoption");
					payment.put("option", option);
					if ("check".equals(option)) {
						bank = new HashMap();
						bank.put("objid", proxy.getString("bank_objid"));
						bank.put("name", proxy.getString("bank_name"));
						payment.put("bank", bank);
						
						check = new HashMap();
						check.put("no", proxy.getString("check_no"));
						check.put("date", proxy.getString("check_date"));
						payment.put("check", check);
					}
					
					results.add( payment );
				}
			}
			
			return results;
		}
		
		
//		private List<Map> getPayments(DBContext paymentdb, DBContext requestdb, String itemid) throws Exception {
//			List<Map> result = new ArrayList<Map>();
//			
//			List<Map> payments;
////			synchronized (PaymentDB.LOCK) {
//				DBPaymentService paymentSvc = new DBPaymentService();
//				paymentSvc.setDBContext(paymentdb);
//				try {
//					payments = paymentSvc.getPaymentsByItem(itemid);
//				} catch (Exception e) {
//					throw e;
//				}
////			}
//			
//			if (payments != null || !payments.isEmpty()) {
//				int size = payments.size();
//				Map payment, borrower, loanapp, bank, check;
//				String option;
//				MapProxy proxy;
//				Map request;
//				DBVoidService voidSvc = new DBVoidService();
//				voidSvc.setDBContext(requestdb);
//				voidSvc.setCloseable(false);
//				for (int i=0; i<size; i++) {
//					System.out.println("payment " + payments.get(i));
//					proxy = new MapProxy((Map) payments.get(i));
//					
////					synchronized (VoidRequestDB.LOCK) {
//						try {
//							request = voidSvc.findVoidRequestByPaymentid(proxy.getString("objid"));
//						} catch (Exception e) {
//							throw e;
//						}
////					}
//					
//					if (request == null) {
//						payment = new HashMap();
//						payment.put("objid", proxy.getString("objid"));
//						payment.put("routecode", proxy.getString("routecode"));
//						payment.put("refno", proxy.getString("refno"));
//						payment.put("amount", proxy.getDouble("amount"));
//						payment.put("type", proxy.getString("paytype"));
//						payment.put("paidby", proxy.getString("paidby"));
//						payment.put("dtpaid", proxy.getString("txndate"));
//						
//						borrower = new HashMap();
//						borrower.put("objid", proxy.getString("borrower_objid"));
//						borrower.put("name", proxy.getString("borrower_name"));
//						payment.put("borrower", borrower);
//						
//						loanapp = new HashMap();
//						loanapp.put("objid", proxy.getString("loanapp_objid"));
//						loanapp.put("appno", proxy.getString("loanapp_appno"));
//						payment.put("loanapp", loanapp);
//						
//						option = proxy.getString("payoption");
//						payment.put("option", option);
//						if ("check".equals(option)) {
//							bank = new HashMap();
//							bank.put("objid", proxy.getString("bank_objid"));
//							bank.put("name", proxy.getString("bank_name"));
//							payment.put("bank", bank);
//							
//							check = new HashMap();
//							check.put("no", proxy.getString("check_no"));
//							check.put("date", proxy.getString("check_date"));
//							payment.put("check", check);
//						}
//						
//						result.add(payment);
//					}
//				}
//			}
////			System.out.println("payments2 " + result);
//			return result;
//		}
	}

//	private Handler responseHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			Bundle bundle = msg.getData();
//			if(progressDialog.isShowing()) progressDialog.dismiss();
//			ApplicationUtil.showShortMsg(context, bundle.getString("response"));
//		}
//	};
//	
//	private Handler remitHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			Bundle bundle = msg.getData();
//			String description = bundle.getString("routedescription");
//			String area = bundle.getString("routearea");
//			loadRoutes();
//			if(progressDialog.isShowing()) progressDialog.dismiss();
//			ApplicationUtil.showShortMsg(context, "Successfully remitted collection for route "+description+" - "+area);
//		}
//	};

	
	
//	private class RemitRunnable implements Runnable {
//		private RouteParcelable route;
//		
//		RemitRunnable(RouteParcelable route) {
//			this.route = route;
//		}
//		
//		@Override
//		public void run() {
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("routecode", route.getCode());
//			params.put("sessionid", route.getSessionid());
//			SQLiteDatabase db = getDbHelper().getReadableDatabase();
//			params.put("totalcollection", getDbHelper().getTotalCollectionSheetsByRoutecode(db, route.getCode()));
//			params.put("totalamount", getDbHelper().getTotalCollectionsByRoutecode(db, route.getCode()));
//			db.close();
//			boolean status = false;
//			Message msg = responseHandler.obtainMessage();
//			Bundle bundle = new Bundle();
//			ServiceProxy postingProxy = ApplicationUtil.getServiceProxy(context, "DevicePostingService");
//			try {
//				Object response  = postingProxy.invoke("remitRouteCollection", new Object[]{params});
//				Map<String, Object> result = (Map<String, Object>) response;
//				if (result.containsKey("response") && result.get("response").toString().toLowerCase().equals("success")) {
//					db = getDbHelper().getWritableDatabase();
//					getDbHelper().remitRouteByCode(db, route.getCode());
//					db.close();
//				}
//				bundle.putString("routedescription", route.getDescription());
//				bundle.putString("routearea", route.getArea());
//				msg = remitHandler.obtainMessage();
//				//bundle.putString("response", "Successfully remitted collection for route "+route.getDescription()+" - "+route.getArea());
//				status = true;
//			} catch( TimeoutException te ) {
//				bundle.putString("response", "Connection Timeout!");
//			} catch( IOException ioe ) {
//				bundle.putString("response", "Error connecting to Server.");
//			} catch( Exception e ) { 
//				bundle.putString("response", e.getMessage());
//				e.printStackTrace(); 
//			} finally {
//				msg.setData(bundle);
//				if (status == true) remitHandler.sendMessage(msg);
//				else responseHandler.sendMessage(msg);
//			}
//		}
//	}
}
