package com.rameses.clfc.android.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.db.DBCollectionSheet;
import com.rameses.clfc.android.db.DBPaymentService;
import com.rameses.clfc.android.db.DBRemarksService;
import com.rameses.clfc.android.db.DBRouteService;
import com.rameses.clfc.android.services.LoanPostingService;
import com.rameses.client.android.Platform;
import com.rameses.client.android.UIActivity;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.MapProxy;

public class RemitRouteCollectionController 
{
	private UIActivity activity;
	private ProgressDialog progressDialog;
	private Map route;
	
	RemitRouteCollectionController(UIActivity activity, ProgressDialog progressDialog, Map route) {
		this.activity = activity;
		this.progressDialog = progressDialog;
		this.route = route;
	}

	void execute() throws Exception {
		progressDialog.setMessage("Remitting collections for route: "+MapProxy.getString(route, "description")+" - "+MapProxy.getString(route, "area"));
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
				}
			});
			if (progressDialog.isShowing()) progressDialog.dismiss();
			ApplicationUtil.showShortMsg("Collection for route "+route.get("description").toString()+" - "+route.get("area").toString()+" successfully remitted.");
		}
	};
	
	private class RemitCollectionActionProcess implements Runnable {
		public void run() {
			Bundle data = new Bundle();
			Message message = null;
			Handler handler = null; 
			try {
				Map params = getParameters();
				
				LoanPostingService svc = new LoanPostingService();
				Map response = svc.remitRouteCollection(params);	
				
				SQLTransaction txn = new SQLTransaction("clfc.db");
				DBRouteService dbRs = new DBRouteService();
				dbRs.setDBContext(txn.getContext());
				
				dbRs.remitRouteByRoutecode(route.get("code").toString());
				
				data.putString("response", response.get("response").toString());
				message = successhandler.obtainMessage();
				handler = successhandler;
			} catch (Throwable t) {
				t.printStackTrace();
				data.putSerializable("response", t);
				message = errorhandler.obtainMessage();
				handler = errorhandler;
			}
			
			message.setData(data);
			handler.sendMessage(message);
		}
		
		private Map getParameters() throws Exception {
			Map params = new HashMap();
			SQLTransaction clfcdb = new SQLTransaction("clfc.db");
			SQLTransaction paymentdb = new SQLTransaction("clfcpayment.db");
			SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");
			try {
				clfcdb.beginTransaction();
				paymentdb.beginTransaction();
				remarksdb.beginTransaction();
				params = getParametersImpl(clfcdb, paymentdb, remarksdb);
				clfcdb.commit();
				paymentdb.commit();
				remarksdb.commit();
			} catch (Exception e) {
				throw e;
			} finally {
				clfcdb.endTransaction();
				paymentdb.endTransaction();
				remarksdb.endTransaction();
			}
			return params;
		}
		
		private Map getParametersImpl(SQLTransaction clfcdb, SQLTransaction paymentdb, SQLTransaction remarksdb) throws Exception {			
			DBCollectionSheet dbCs = new DBCollectionSheet();
			dbCs.setDBContext(clfcdb.getContext());
			
			DBPaymentService dbPs = new DBPaymentService();
			dbPs.setDBContext(paymentdb.getContext());
			
			DBRemarksService dbRs = new DBRemarksService();
			dbRs.setDBContext(remarksdb.getContext());
			
			int totalcollectionsheets = 0;
			List<Map> list = dbCs.getCollectionSheetsByRoutecode(route.get("code").toString());
			if (!list.isEmpty()) {
				Map map;
				String loanappid = "";
				boolean haspayment = false;
				boolean hasremarks = false;
				for (int i=0; i<list.size(); i++) {
					map = (Map) list.get(i);
					
					loanappid = map.get("loanappid").toString();
					haspayment = dbPs.hasPaymentsByLoanappid(loanappid);
					hasremarks = dbRs.hasRemarksByLoanappid(loanappid);
					
					if (haspayment == true || hasremarks == true) {
						totalcollectionsheets++;
					}
				}
			}
			
			Map params = new HashMap();
			params.put("routecode", route.get("code").toString());
			params.put("sessionid", route.get("sessionid").toString());
			params.put("totalcollection", totalcollectionsheets);
			params.put("totalamount", dbPs.getTotalCollectionsByRoutecode(route.get("code").toString()));
			return params;
		}
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
