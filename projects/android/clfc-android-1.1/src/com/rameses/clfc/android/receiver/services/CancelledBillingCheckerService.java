package com.rameses.clfc.android.receiver.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.db.CollectionGroupDB;
import com.rameses.clfc.android.db.CollectionSheetDB;
import com.rameses.clfc.android.services.LoanPostingService;
import com.rameses.client.android.Platform;
import com.rameses.util.Base64Cipher;

public class CancelledBillingCheckerService extends Service {

	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(5);
	private ScheduledFuture<?> checkerHandle;

	private boolean serviceStarted = false;
	
	private CollectionGroupDB collectionGroup = new CollectionGroupDB();
	private CollectionSheetDB collectionSheet = new CollectionSheetDB();

	private Thread checkerThread = new Thread() {
		
		public void run() {
			try {
				runImpl();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		private void runImpl() throws Exception {

			List<Map> list = collectionGroup.getAllCollectionBilling();
			if (list == null || list.isEmpty()) return;

			Map params = new HashMap();
			params.put("list", list);
			
			Map param = new HashMap();
			String enc = new Base64Cipher().encode( params );
			param.put("encrypted", enc);
			
//			ClientContext.getCurrentContext().getAppEnv().get("app.host");
			LoanPostingService service = new LoanPostingService();
			Map response = service.checkBillingCancelledEncrypt( param );
			if (response != null || !response.isEmpty()) {
				removeBillingCancelled( response );
			}
		} 

		
		private void removeBillingCancelled(Map params) throws Exception {
			List<Map> list = new ArrayList<Map>();
			if (params.containsKey("list")) {
				list = (List<Map>) params.get("list");
			}
			
			
			StringBuilder sb = new StringBuilder();
			for (Map data : list) {
				if (data.containsKey("iscancelled")) {
					Boolean iscancelled = Boolean.valueOf(data.get("iscancelled").toString());
					if (iscancelled==true) {
						String sql = removeBillingDetailSql( data );
						if (sql != null && !sql.equals("")) {
							sb.append( sql );
						}
					} 
				}
			}
			
			if (!sb.toString().trim().isEmpty()) {
				SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
				try {
					appdb.beginTransaction();
					appdb.execSQL( sb.toString() );
					appdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					appdb.endTransaction();
				}
			}
		}

		 
		private String removeBillingDetailSql( Map data ) throws Exception {	
			StringBuilder sb = new StringBuilder();
			
			String itemid = "";
			if (data.containsKey("objid")) {
				itemid = data.get("objid").toString();
			}
			
			if (itemid != null && !itemid.equals("")) {
				List<Map> list = collectionSheet.getCollectionSheetByItemid( itemid );
				
				String objid, sql;
				for (Map item: list) {
					if (item.containsKey("objid")) {
						objid = item.get("objid").toString();
						
						sb.append( "delete from amnesty where parentid='" + objid + "';" );
						sb.append( "delete from collector_remarks where parentid='" + objid + "';" );
						sb.append( "delete from followup_remarks where parentid='" + objid + "';" );
						sb.append( "delete from collectionsheet_segregation where collectionsheetid='" + objid + "';" );
						
					}
				}

				sb.append( "delete from remarks where itemid='" + itemid + "';" );
				sb.append( "delete from notes where itemid='" + itemid + "';" );
				sb.append( "delete from void_request where itemid='" + itemid + "';" );
				sb.append( "delete from payment where itemid='" + itemid + "';" );
				sb.append( "delete from collectionsheet where itemid='" + itemid + "';" );
				sb.append( "delete from specialcollection where objid='" + itemid + "';" );
				sb.append( "delete from collection_group where objid='" + itemid + "';" );
			}
			
			return sb.toString();
		}
		
	};
	
	@Override
	public void onCreate() {
		app = (ApplicationImpl) Platform.getApplication();
		settings = (AppSettingsImpl) app.getAppSettings();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		start();
		
		return START_STICKY;
	}
	
	public synchronized boolean getIsServiceStarted() { return serviceStarted; }

	public synchronized void start() {
		if (serviceStarted==false) {
						
			if (checkerThread.getState().equals( Thread.State.NEW ) || 
					checkerThread.getState().equals( Thread.State.TERMINATED )) {
				
				checkerHandle = scheduledPool.scheduleWithFixedDelay( checkerThread, 1, 5, TimeUnit.SECONDS );
			}
			serviceStarted = true;
		}
	}
	
	public synchronized void restart() {
		stop();
		start();
	}
	
	public synchronized void stop() {
		if (serviceStarted==true) {
			if (checkerHandle != null) {
				checkerHandle.cancel( true );
			}
			serviceStarted = false;
		}
	}
	
	public synchronized void shutdown() {
		if (scheduledPool != null) {
			scheduledPool.shutdown();
		}
	}
	
	@Override
	public void onDestroy() {
		stop();
		shutdown();
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
