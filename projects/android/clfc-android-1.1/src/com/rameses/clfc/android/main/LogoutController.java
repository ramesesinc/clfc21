package com.rameses.clfc.android.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.CollectionGroupDB;
import com.rameses.clfc.android.db.CollectionSheetDB;
import com.rameses.clfc.android.db.PaymentServiceDB;
import com.rameses.clfc.android.db.RemarksServiceDB;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIActionBarActivity;
import com.rameses.client.android.UIApplication;
import com.rameses.client.android.UIDialog;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.client.services.LogoutService;
import com.rameses.util.MapProxy;

class LogoutController 
{
//	private UIActivity activity;
	private UIActionBarActivity activity;
	private ProgressDialog progressDialog;
	
	private CollectionSheetDB collectionsheetdb = new CollectionSheetDB();
	private CollectionGroupDB collectiongroupdb = new CollectionGroupDB();
	private PaymentServiceDB paymentservicedb = new PaymentServiceDB();
	private RemarksServiceDB remarksservicedb = new RemarksServiceDB();
	
	LogoutController(UIActionBarActivity activity, ProgressDialog progressDialog) {
		this.activity = activity;
		this.progressDialog = progressDialog;
	}
	
	void execute() throws Exception {
		if (hasUnpostedTransactions()) { 
			ApplicationUtil.showShortMsg("Cannot logout. There are still unposted/unremitted transactions.");
			
		} else {
			UIDialog dialog = new UIDialog(activity) {				
				public void onApprove() {
					progressDialog.setMessage("Logging out...");
					if (!progressDialog.isShowing()) progressDialog.show();
					
					Platform.runAsync(new LogoutActionProcess()); 
				}
			};
			dialog.confirm("Are you sure you want to logout?");
		}	
	}
	
	private boolean hasUnpostedTransactions() throws Exception {
		UserProfile profile = SessionContext.getProfile();
		String collectorid = (profile != null? profile.getUserId() : "");
		
		if (paymentservicedb.hasUnpostedPaymentsByCollector( collectorid )) return true;		
		if (remarksservicedb.hasUnpostedRemarksByCollector( collectorid )) return true;
				
		boolean flag = false;
		List<Map> list = collectionsheetdb.getUnremittedCollectionSheetsByCollector( collectorid );
		if (!list.isEmpty()) {
			flag = true;
		}
		return flag;
	}
	
//	private boolean xhasUnpostedTransactions() throws Exception {
//		DBContext paymentdb = new DBContext("clfcpayment.db");
//		DBContext remarksdb = new DBContext("clfcremarks.db");
//		DBContext clfcdb = new DBContext("clfc.db");
//		try {
//			boolean flag = hasUnpostedTransactionsImpl(paymentdb, remarksdb, clfcdb);
//			return flag;
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			paymentdb.close();
//			remarksdb.close();
//			clfcdb.close();
//		}
//	}
//	
//	private boolean hasUnpostedTransactionsImpl(DBContext paymentdb, DBContext remarksdb, DBContext clfcdb) 
//			throws Exception {
//		DBPaymentService paymentSvc = new DBPaymentService();
//		paymentSvc.setDBContext(paymentdb);
//		paymentSvc.setCloseable(false);
//		
//		String collectorid = SessionContext.getProfile().getUserId();
//		if (paymentSvc.hasUnpostedPaymentsByCollector(collectorid)) return true;
//		
//		DBRemarksService remarksSvc = new DBRemarksService();
//		remarksSvc.setDBContext(remarksdb);
//		remarksSvc.setCloseable(false);
//		
//		if (remarksSvc.hasUnpostedRemarksByCollector(collectorid)) return true;
//		
//		DBCollectionSheet collectionSheet = new DBCollectionSheet();
//		collectionSheet.setDBContext(clfcdb);
//		collectionSheet.setCloseable(false);
//		
//		boolean flag = false;
//		List<Map> list = collectionSheet.getUnremittedCollectionSheetsByCollector(collectorid);
//		if (!list.isEmpty()) {
//			flag = true;
////			String sql = "";
////			String objid = "";
////			Map map;
////			for (int i=0; i<list.size(); i++) {
////				map = (Map) list.get(i);
////				
////				objid = map.get("objid").toString();
////				sql = "SELECT objid FROM payment WHERE parentid=? LIMIT 1";
////				if (!paymentdb.getList(sql, new Object[]{objid}).isEmpty()) {
////					flag = true;
////					break;
////				}
////				
////				sql = "SELECT objid FROM remarks WHERE objid=? LIMIT 1";
////				if (!remarksdb.getList(sql, new Object[]{objid}).isEmpty()) {
////					flag = true;
////					break;
////				}
////			}
//		}
//		return flag;
//	}

	
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
			if (progressDialog.isShowing()) progressDialog.dismiss();

			UIApplication uiapp = Platform.getApplication();
			uiapp.getAppSettings().put("collector_state", "logout");
			uiapp.getAppSettings().remove("trackerid");
			uiapp.getAppSettings().remove("tracker_owner");
			uiapp.getAppSettings().remove("captureid");			
			uiapp.logout(); 
		}
	};	
	
	private class LogoutActionProcess implements Runnable 
	{
		public void run() {
			Bundle data = new Bundle();
			Message msg = errorhandler.obtainMessage();
			Boolean success = false;
			try {
				runImpl();
				data.putString("response", "success");
				msg = successhandler.obtainMessage();
				success = true;
				
			} catch(Throwable t) { 
				data.putSerializable("response", t);			
				t.printStackTrace();
			} finally { 
				msg.setData(data);
				if (success) {
					successhandler.sendMessage(msg);
				} else {
					errorhandler.sendMessage(msg);
				}
			}
		}
		
		private void runImpl() throws Exception {
			UserProfile profile = SessionContext.getProfile();
			String collectorid = (profile != null? profile.getUserId() : "");

			
			List<Map> appSqlParams = new ArrayList<Map>();
			List<Map> captureSqlParams = new ArrayList<Map>();
			List<Map> paymentSqlParams = new ArrayList<Map>();
			List<Map> remarksSqlParams = new ArrayList<Map>();
			List<Map> remarksRemovedSqlParams = new ArrayList<Map>();
			List<Map> voidRequestSqlParams = new ArrayList<Map>();
			
			Map xdata = new HashMap();
			
//			StringBuilder appdbsb = new StringBuilder();
//			StringBuilder capturedbsb = new StringBuilder();
//			StringBuilder paymentdbsb = new StringBuilder();
//			StringBuilder remarksdbsb = new StringBuilder();
//			StringBuilder remarksremoveddbsb = new StringBuilder();
//			StringBuilder voidrequestdbsb = new StringBuilder();
			
			List<Map> list = collectiongroupdb.getCollectionGroupsByCollector( collectorid );
			for (Map item : list) {
				MapProxy proxy = new MapProxy( item );
				String objid = proxy.getString("objid");
				String billingid = proxy.getString("billingid");
				
				String sql = "delete from payment where itemid='" + objid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
				
				paymentSqlParams.add( xdata );
				appSqlParams.add( xdata );
				
//				paymentdbsb.append( sql );
//				appdbsb.append( sql );
				
				sql = "delete from remarks where itemid='" + objid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
			
				remarksSqlParams.add( xdata );
				appSqlParams.add( xdata );
				
//				remarksdbsb.append( sql );
//				appdbsb.append( sql );

				sql = "delete from remarks_removed where itemid='" + objid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
				
				remarksRemovedSqlParams.add( xdata );
				
//				remarksremoveddbsb.append( sql );

				sql = "delete from void_request where itemid='" + objid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
				
				voidRequestSqlParams.add( xdata );
				
//				voidrequestdbsb.append( sql );

				
				appSqlParams.addAll( processCollection( objid ) );
//				sql = processCollection( objid );
//				appdbsb.append( sql );
				
				sql = "delete from collection_group where objid='" + objid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
				
				appSqlParams.add( xdata );
				
//				appdbsb.append( sql );
				
				sql = "delete from capture_payment where billingid='" + billingid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
				
				captureSqlParams.add( xdata );
				
//				capturedbsb.append( sql );
			}
			String sql = "delete from specialcollection where collector_objid='" + collectorid + "';";
			
			xdata = new HashMap();
			xdata.put("type", "delete");
			xdata.put("sql", sql);
			
			appSqlParams.add( xdata );
			
//			appdbsb.append( sql );

			sql = "delete from capture_payment where collector_objid='" + collectorid + "';";
			
			xdata = new HashMap();
			xdata.put("type", "delete");
			xdata.put("sql", sql);
			
			captureSqlParams.add( xdata );
			
//			capturedbsb.append( sql );

			sql = "delete from bank;";
			
			xdata = new HashMap();
			xdata.put("type", "delete");
			xdata.put("sql", sql);
			
			appSqlParams.add( xdata );
			
//			appdbsb.append( sql );
			
			String date = ApplicationUtil.formatDate(Platform.getApplication().getServerDate(), "yyyy-MM-dd");
			sql = "delete from sys_var where name='" + collectorid + "-" + date + "';";
			
			xdata = new HashMap();
			xdata.put("type", "delete");
			xdata.put("sql", sql);
			
			appSqlParams.add( xdata );
			
//			appdbsb.append( sql );
			
			SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
			SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
			SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
			SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
			SQLiteDatabase remarksremoveddb = ApplicationDatabase.getRemarksRemovedWritableDB();
			SQLiteDatabase voidrequestdb = ApplicationDatabase.getVoidRequestWritableDB();
			try {
				appdb.beginTransaction();				
				if (appSqlParams.size() > 0) {
					ApplicationDBUtil.executeSQL( appdb, appSqlParams );
				}
				
//				if (!appdbsb.toString().trim().equals("")) {
//					appdb.execSQL( appdbsb.toString() );
//				}
				
				capturedb.beginTransaction();				
				if (captureSqlParams.size() > 0) {
					ApplicationDBUtil.executeSQL( capturedb, captureSqlParams );
				}
				
//				if (!capturedbsb.toString().trim().equals("")) {
//					capturedb.execSQL( capturedbsb.toString() );
//				}
				
				paymentdb.beginTransaction();				
				if (paymentSqlParams.size() > 0) {
					ApplicationDBUtil.executeSQL( paymentdb, paymentSqlParams );
				}
				
//				if (!paymentdbsb.toString().trim().equals("")) {
//					paymentdb.execSQL( paymentdbsb.toString() );
//				}
				
				remarksdb.beginTransaction();
				if (remarksSqlParams.size() > 0) {
					ApplicationDBUtil.executeSQL( remarksdb, remarksSqlParams );
				}
				
//				if (!remarksdbsb.toString().trim().equals("")) {
//					remarksdb.execSQL( remarksdbsb.toString() );
//				}
				
				remarksremoveddb.beginTransaction();
				if (remarksRemovedSqlParams.size() > 0) {
					ApplicationDBUtil.executeSQL( remarksremoveddb, remarksRemovedSqlParams );
				}
				
//				if (!remarksremoveddbsb.toString().trim().equals("")) {
//					remarksremoveddb.execSQL( remarksremoveddbsb.toString() );
//				}
				
				voidrequestdb.beginTransaction();
				if (voidRequestSqlParams.size() > 0) {
					ApplicationDBUtil.executeSQL( voidrequestdb, remarksRemovedSqlParams );
				}
				
//				if (!voidrequestdbsb.toString().trim().equals("")) {
//					voidrequestdb.execSQL( voidrequestdbsb.toString() );
//				}
				
				voidrequestdb.setTransactionSuccessful();
				remarksremoveddb.setTransactionSuccessful();
				remarksdb.setTransactionSuccessful();
				paymentdb.setTransactionSuccessful();
				capturedb.setTransactionSuccessful();
				appdb.setTransactionSuccessful();
				
			} catch (Exception e) {
				throw e;
			} finally {				
				appdb.endTransaction();
				capturedb.endTransaction();
				paymentdb.endTransaction();
				remarksdb.endTransaction();
				remarksremoveddb.endTransaction();
				voidrequestdb.endTransaction();
			}
			
			new LogoutService().logout(); 
		}
		
		private List<Map> processCollection( String itemid ) throws Exception {
			List<Map> sqlParams = new ArrayList<Map>();
			Map xdata = new HashMap();
			
			List<Map> list = collectionsheetdb.getCollectionSheetByItemid( itemid );
			for (Map item : list) {
				String objid = MapProxy.getString( item, "objid" );
				
				String sql = "delete from notes where parentid='" + objid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
				
				sqlParams.add( xdata );
				
//				sb.append( sql );

				sql = "delete from amnesty where parentid='" + objid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
				
				sqlParams.add( xdata );
				
//				sb.append( sql );
				
				sql = "delete from collector_remarks where parentid='" + objid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
				
				sqlParams.add( xdata );
				
//				sb.append( sql );

				sql = "delete from followup_remarks where parentid='" + objid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
				
				sqlParams.add( xdata );
				
//				sb.append( sql );

				sql = "delete from collectionsheet_segregation where collectionsheetid='" + objid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
				
				sqlParams.add( xdata );
				
//				sb.append( sql );
				
				sql = "delete from collectionsheet where objid='" + objid + "';";
				
				xdata = new HashMap();
				xdata.put("type", "delete");
				xdata.put("sql", sql);
				
				sqlParams.add( xdata );
				
//				sb.append( sql ); 
			}
			
			return sqlParams;
		}
		
		/*
		private void processCollection(SQLTransaction clfcdb, String itemid) throws Exception {
			String sql = "SELECT * FROM collectionsheet WHERE itemid=?";
			List<Map> sheets = clfcdb.getList(sql, new Object[]{itemid});
			String objid;
			Map data;
			int count = 0; 
			while (!sheets.isEmpty()) {
				data = sheets.remove(0);
				objid = MapProxy.getString(data, "objid");
				count = clfcdb.delete("notes", "parentid=?", new Object[]{objid});
				Log.i("CollectionSheetListFragment", "notes deleted " + count);
				
				count = clfcdb.delete("amnesty", "parentid=?", new Object[]{objid});
				Log.i("CollectionSheetListFragment", "amnesty deleted " + count);
				
				count = clfcdb.delete("collector_remarks", "parentid=?", new Object[]{objid});
				Log.i("CollectionSheetListFragment", "collector remarks deleted " + count);
				
				count = clfcdb.delete("followup_remarks", "parentid=?", new Object[]{objid});
				Log.i("CollectionSheetListFragment", "followup remarks deleted " + count);

				count = clfcdb.delete("collectionsheet_segregation", "collectionsheetid=?", new Object[]{objid});
				Log.i("CollectionSheetListFragment", "collectionsheet - segregation delete " + count);
				
				count = clfcdb.delete("collectionsheet", "objid=?", new Object[]{objid});
				Log.i("CollectionSheetListFragment", "collectionsheet deleted " + count); 
			}
		}
		*/
		
		/*
		private void xrunImpl() throws Exception {
			SQLTransaction clfcdb = new SQLTransaction("clfc.db");
			SQLTransaction capturedb = new SQLTransaction("clfccapture.db");
			SQLTransaction paymentdb = new SQLTransaction("clfcpayment.db");
			SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");
			SQLTransaction remarksremoveddb = new SQLTransaction("clfcremarksremoved.db");
			SQLTransaction requestdb = new SQLTransaction("clfcrequest.db");
			//SQLTransaction trackerdb = new SQLTransaction("clfctracker.db");
			try {
				clfcdb.beginTransaction();
				capturedb.beginTransaction();
				paymentdb.beginTransaction();
				remarksdb.beginTransaction();
				remarksremoveddb.beginTransaction();
				requestdb.beginTransaction();
				//trackerdb.beginTransaction();
				execute(clfcdb, capturedb, paymentdb, remarksdb, remarksremoveddb, requestdb);
				
				clfcdb.commit();
				capturedb.commit();
				paymentdb.commit();
				remarksdb.commit();
				remarksremoveddb.commit();
				requestdb.commit();
				//trackerdb.commit();
			} catch(Exception e) {
				throw e; 
			} finally { 
				clfcdb.endTransaction();  
				capturedb.endTransaction();
				paymentdb.endTransaction();
				remarksdb.endTransaction();
				remarksremoveddb.endTransaction();
				requestdb.endTransaction();
				//trackerdb.endTransaction();
			} 
		}
		*/	
		
		/*
		private void execute(SQLTransaction clfcdb, SQLTransaction capturedb, 
				SQLTransaction paymentdb, SQLTransaction remarksdb, SQLTransaction remarksremoveddb,
				SQLTransaction requestdb) throws Exception {
			
			String collectorid = SessionContext.getProfile().getUserId();
			List<Map> collectionGroups = clfcdb.getList("SELECT * FROM collection_group WHERE collectorid=?", new Object[]{collectorid});			
			String objid, billingid;
			Map data;
						
			while (!collectionGroups.isEmpty()) {
				data = collectionGroups.remove(0);
				objid = MapProxy.getString(data, "objid");
				billingid = MapProxy.getString(data, "billingid");
				
				paymentdb.delete("payment", "itemid=?", new Object[]{objid});
				clfcdb.delete("payment", "itemid=?", new Object[]{objid});
				
				remarksdb.delete("remarks", "itemid=?", new Object[]{objid});
				clfcdb.delete("remarks", "itemid=?", new Object[]{objid});

				remarksremoveddb.delete("remarks_removed", "itemid=?", new Object[]{objid});
				requestdb.delete("void_request", "itemid=?", new Object[]{objid});
				processCollection(clfcdb, objid);
				clfcdb.delete("collection_group", "objid=?", new Object[]{objid});
				
				capturedb.delete("capture_payment", "billingid=?", new Object[]{billingid});
			}
			clfcdb.delete("specialcollection", "collector_objid=?", new Object[]{collectorid});
			capturedb.delete("capture_payment", "collector_objid=?", new Object[]{collectorid});

			clfcdb.delete("bank", null);
			String date = ApplicationUtil.formatDate(Platform.getApplication().getServerDate(), "yyyy-MM-dd");
			clfcdb.delete("sys_var", "name=?", new Object[]{collectorid+"-"+date});		
			try { 
				new LogoutService().logout(); 
			} catch (Exception e) { 
				e.printStackTrace(); 
				throw e;
			}			
		} 
		*/
		
		
	}	
}
