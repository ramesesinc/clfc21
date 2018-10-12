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
import com.rameses.clfc.android.db.RemarksRemovedDB;
import com.rameses.clfc.android.services.LoanPostingService;
import com.rameses.client.android.Platform;
import com.rameses.util.MapProxy;

public class RemarksRemovedBroadcastService extends Service {

	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(5);
	private ScheduledFuture<?> remarksRemoveHandle;

	private boolean serviceStarted = false;

	private RemarksRemovedDB remarksremoveddb = new RemarksRemovedDB();
	
	private Thread remarksRemovedThread = new Thread() {
		
		RemarksRemovedBroadcastService root = RemarksRemovedBroadcastService.this;
		
		public void run() {
			try {
				broadcastRemarksRemoved();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		private void broadcastRemarksRemoved() throws Exception {
//			StringBuilder sb = new StringBuilder();
			List<Map> sqlParams = new ArrayList<Map>();
			Map data = new HashMap();
			
			List<Map> list = remarksremoveddb.getPendingRemarksRemoved();
			for (Map item : list) {
				MapProxy proxy = new MapProxy( item );
				String objid = proxy.getString("objid");

				int networkStatus = app.getNetworkStatus();
				if (networkStatus == 3) {
					break;
				}
				
				Map params = new HashMap();
				params.put("detailid", proxy.getString("objid"));
				
				Map response = new HashMap();
				for (int j=0; j<10; j++) {
					try {
						LoanPostingService svc = new LoanPostingService();
						response = svc.removeRemarks( params );
//						removeRemarks(response, proxy.getString("objid"));
						break;
					} catch (Throwable e) {;}
				}
				
				if (response != null || !response.isEmpty()) {
					if (response.containsKey("response")) {
						String str = response.get("response").toString().toLowerCase();
						if ("success".equals(str)) {
							
							String sql = "delete from remarks_removed where objid='" + objid + "';";
							
							data = new HashMap();
							data.put("type", "delete");
							data.put("sql", sql);
							
							sqlParams.add( data );
						}
					}
				}
			}
			
			if (sqlParams.size() > 0) {
				SQLiteDatabase remarksremoveddb = ApplicationDatabase.getRemarksRemovedWritableDB();
				try {
					remarksremoveddb.beginTransaction();
					ApplicationDBUtil.executeSQL( remarksremoveddb, sqlParams );
//					remarksremoveddb.execSQL( sb.toString() );
					remarksremoveddb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					remarksremoveddb.endTransaction();
				}
			}
			
			boolean flag = remarksremoveddb.hasPendingRemarksRemoved();
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
						
			if (remarksRemovedThread.getState().equals( Thread.State.NEW ) || 
					remarksRemovedThread.getState().equals( Thread.State.TERMINATED )) {
				
				int delay = settings.getUploadTimeout();
				remarksRemoveHandle = scheduledPool.scheduleWithFixedDelay( remarksRemovedThread, 1, delay, TimeUnit.SECONDS );
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
			if (remarksRemoveHandle != null) {
				remarksRemoveHandle.cancel( true );
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
