package com.rameses.clfc.android.main;

import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.TrackerDB;
import com.rameses.clfc.android.db.DBCollectionSheet;
import com.rameses.clfc.android.db.DBLocationTracker;
import com.rameses.clfc.android.db.DBPaymentService;
import com.rameses.clfc.android.db.DBRemarksService;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIActivity;
import com.rameses.client.android.UIApplication;
import com.rameses.client.android.UIDialog;
import com.rameses.client.services.LogoutService;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.MapProxy;

class LogoutController 
{
	private UIActivity activity;
	private ProgressDialog progressDialog;
	
	LogoutController(UIActivity activity, ProgressDialog progressDialog) {
		this.activity = activity;
		this.progressDialog = progressDialog;
	}
	
	void execute() throws Exception {
		if (hasUnpostedTransactions()) { 
			ApplicationUtil.showShortMsg("Cannot logout. There are still unposted transactions");
			
		} else {
			UIDialog dialog = new UIDialog(activity) {
				
				public void onApprove() {
					progressDialog.setMessage("Logging out...");
					if (!progressDialog.isShowing()) progressDialog.show();
					
					Platform.runAsync(new LogoutActionProcess()); 
				}
			};
			dialog.confirm("Are you sure you want to logout?");
		}	/* else if (hasTooManyUnpostedTrackers()) {
			ApplicationUtil.showLongMsg("Cannot logout. There are too many unposted trackers. You can logout if there are 10 or lower unposted trackers left.");
			
		} */	
	}
	
	/*private boolean hasTooManyUnpostedTrackers() throws Exception {
		synchronized (TrackerDB.LOCK) {
			DBContext ctx = new DBContext("clfctracker.db");
			DBLocationTracker tracker = new DBLocationTracker();
			tracker.setDBContext(ctx);
			try {
				boolean flag = tracker.hasTooManyLocationTrackerByCollectorid(SessionContext.getProfile().getUserId());
				return flag;
			} catch (Exception e) {
				throw e;
			}
		}
	}*/
	
	private boolean hasUnpostedTransactions() throws Exception {
		SQLTransaction paymentdb = new SQLTransaction("clfcpayment.db");
		SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");
		SQLTransaction clfcdb = new SQLTransaction("clfc.db");
		try {
			paymentdb.beginTransaction();
			remarksdb.beginTransaction();
			boolean flag = hasUnpostedTransactionsImpl(paymentdb, remarksdb, clfcdb);
			paymentdb.commit();
			remarksdb.commit();
			return flag;
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			remarksdb.endTransaction();
		}
	}
	
	private boolean hasUnpostedTransactionsImpl(SQLTransaction paymentdb, SQLTransaction remarksdb, SQLTransaction clfcdb) throws Exception {		
		DBPaymentService dbPs = new DBPaymentService();
		dbPs.setDBContext(paymentdb.getContext());
		
		if (dbPs.hasUnpostedPayments()) return true;
		
		DBRemarksService dbRs = new DBRemarksService();
		dbRs.setDBContext(remarksdb.getContext());
		
		if (dbRs.hasUnpostedRemarks()) return true;
		
		DBCollectionSheet dbCs = new DBCollectionSheet();
		dbCs.setDBContext(clfcdb.getContext());
		
		boolean flag = false;
		List<Map> list = dbCs.getUnremittedCollectionSheets();
		if (!list.isEmpty()) {
			String sql = "";
			String loanappid = "";
			Map map;
			for (int i=0; i<list.size(); i++) {
				map = (Map) list.get(i);
				
				loanappid = map.get("loanappid").toString();
				sql = "SELECT objid FROM payment WHERE loanappid=? LIMIT 1";
				if (!paymentdb.getList(sql, new Object[]{loanappid}).isEmpty()) {
					flag = true;
					break;
				}
				
				sql = "SELECT loanappid FROM remarks WHERE loanappid=? LIMIT 1";
				if (!remarksdb.getList(sql, new Object[]{loanappid}).isEmpty()) {
					flag = true;
					break;
				}
			}
		}
		return flag;
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
			if (progressDialog.isShowing()) progressDialog.dismiss();

			UIApplication uiapp = Platform.getApplication();
			uiapp.getAppSettings().put("collector_state", "logout");
			uiapp.getAppSettings().remove("trackerid");
			uiapp.logout(); 
		}
	};	
	
	private class LogoutActionProcess implements Runnable 
	{
		public void run() {
			Bundle data = new Bundle();			
			Handler handler = null;
			Message message = null;
			try {
				runImpl();
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
		
		private void runImpl() throws Exception {
			SQLTransaction clfcdb = new SQLTransaction("clfc.db");
			//SQLTransaction trackerdb = new SQLTransaction("clfctracker.db");
			try {
				clfcdb.beginTransaction();
				//trackerdb.beginTransaction();
				execute(clfcdb);
				clfcdb.commit();
				//trackerdb.commit();
			} catch(Exception e) {
				throw e; 
			} finally { 
				clfcdb.endTransaction();  
				//trackerdb.endTransaction();
			} 
		}
				
		private void execute(SQLTransaction clfcdb) throws Exception {
			
			String collectorid = SessionContext.getProfile().getUserId();
			List<Map> routes = clfcdb.getList("SELECT * FROM route WHERE collectorid='"+collectorid+"' AND state='REMITTED'");
			while (!routes.isEmpty()) {
				Map data = routes.remove(0);
				String routecode = MapProxy.getString(data, "routecode");
				processRoute(clfcdb, routecode);
				clfcdb.delete("route", "routecode=?", new Object[]{routecode});
			}

			clfcdb.delete("sys_var", null);
			//trackerdb.delete("location_tracker", "collectorid=?", new Object[]{collectorid});			
			try { 
				new LogoutService().logout(); 
			} catch (Exception e) { 
				e.printStackTrace(); 
			}			
		} 
		
		private void processRoute(SQLTransaction clfcdb, String routecode) throws Exception {
			SQLTransaction paymentdb = new SQLTransaction("clfcpayment.db");
			SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");
			SQLTransaction remarksremoveddb = new SQLTransaction("clfcremarksremoved.db");
			SQLTransaction requestdb = new SQLTransaction("clfcrequest.db");
			try {
				paymentdb.beginTransaction();
				remarksdb.beginTransaction();
				remarksremoveddb.beginTransaction();
				requestdb.beginTransaction();
				
				processRouteImpl(clfcdb, paymentdb, remarksdb, remarksremoveddb, requestdb, routecode);
				
				paymentdb.commit();
				remarksdb.commit();
				remarksremoveddb.commit();
				requestdb.commit();
			} catch (Exception e) {
				throw e;
			} finally {
				paymentdb.endTransaction();
				remarksdb.endTransaction();
				remarksremoveddb.endTransaction();
				requestdb.endTransaction();
			}
		}
		
		private void processRouteImpl(SQLTransaction clfcdb, SQLTransaction paymentdb, SQLTransaction remarksdb, SQLTransaction remarksremoveddb, SQLTransaction requestdb, String routecode) throws Exception {
			String sql = "SELECT * FROM collectionsheet WHERE routecode=? ORDER BY seqno";
			List<Map> sheets = clfcdb.getList(sql, new Object[]{routecode});
			while (!sheets.isEmpty()) {
				Map data = sheets.remove(0);
				String loanappid = MapProxy.getString(data, "loanappid");
				requestdb.delete("void_request", "loanappid=?", new Object[]{loanappid});
				paymentdb.delete("payment", "loanappid=?", new Object[]{loanappid});
				clfcdb.delete("notes", "loanappid=?", new Object[]{loanappid});
				remarksdb.delete("remarks", "loanappid=?", new Object[]{loanappid});
				remarksremoveddb.delete("remarks_removed", "loanappid=?", new Object[]{loanappid});
				clfcdb.delete("collectionsheet", "loanappid=?", new Object[]{loanappid});
				clfcdb.delete("specialcollection", "collectorid=?", new Object[]{SessionContext.getProfile().getUserId()});
				clfcdb.delete("sys_var", "name = 'billingid'");
			} 
		}
	}	
}
