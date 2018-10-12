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
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.VoidServiceDB;
import com.rameses.clfc.android.services.LoanPostingService;
import com.rameses.client.android.Platform;

public class VoidRequestBroadcastService extends Service {

	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(5);
	private ScheduledFuture<?> voidRequestHandle;

	private boolean serviceStarted = false;
	
	private VoidServiceDB voidService = new VoidServiceDB();
	
	private Thread voidRequestThread = new Thread() {
		
		VoidRequestBroadcastService root = VoidRequestBroadcastService.this;
		
		public void run() {
			try {
				broadcastVoidRequest();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		private void broadcastVoidRequest() throws Exception {			
			List<Map> list = voidService.getPendingVoidRequests();			
			List<Map> appSqlParams = new ArrayList<Map>();
			List<Map> voidRequestSqlParams = new ArrayList<Map>();
			
			Map data = new HashMap();
			String objid = null, state = null;
			for (Map item : list) {
				Map params = new HashMap();
				if (item.containsKey("objid")) {
					objid = item.get("objid").toString();

					params.put("voidid", objid);
					
					Map response = new HashMap();
					for (int i=0; i<3; i++) {
						try {
							LoanPostingService service = new LoanPostingService();
							response = service.checkVoidPaymentRequest( params );
						} catch (Throwable t) {}
					}
					if (response != null && response.containsKey("state")) {
						state = response.get("state").toString();
					}
					
					if (state != null && !state.equals("")) {
						String sql = null;
						
						data = new HashMap();
						
						if ("APPROVED".equals( state )) {
							sql = "update void_request set state='APPROVED' where objid='" + objid + "';";
							data.put("type", "update");
							
						} else if ("DISAPPROVED".equals( state )) {
							sql = "delete from void_request where objid='" + objid + "';";
							data.put("type", "delete");
						}
						
						if (sql != null && !sql.equals("")) {
							data.put("sql", sql);
							
							voidRequestSqlParams.add( data );
							appSqlParams.add( data );
							
						}
					}
				}
			}
			
			if (voidRequestSqlParams.size() > 0) {
				SQLiteDatabase voidrequestdb = ApplicationDatabase.getVoidRequestWritableDB();
				try {
					voidrequestdb.beginTransaction();
					ApplicationDBUtil.executeSQL( voidrequestdb, voidRequestSqlParams );
//					voidrequestdb.execSQL( voidrequestdb.toString() );
					voidrequestdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					voidrequestdb.endTransaction();
				}
			}
			
			if (appSqlParams.size() > 0) {
				SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
				try {
					appdb.beginTransaction();
					ApplicationDBUtil.executeSQL( appdb, appSqlParams );
//					appdb.execSQL( appdbsb.toString() );
					appdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					appdb.endTransaction();
				}
			}
			
			boolean flag = voidService.hasPendingVoidRequest();
			if (flag == false) {
				root.stop();
			}
			
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
						
			if (voidRequestThread.getState().equals( Thread.State.NEW ) || 
					voidRequestThread.getState().equals( Thread.State.TERMINATED )) {
				
				voidRequestHandle = scheduledPool.scheduleWithFixedDelay( voidRequestThread, 1, 5, TimeUnit.SECONDS );
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
			if (voidRequestHandle != null) {
				voidRequestHandle.cancel( true );
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
