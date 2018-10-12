package com.rameses.clfc.android.receiver.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.RemarksServiceDB;
import com.rameses.clfc.android.services.LoanPostingService;
import com.rameses.client.android.Platform;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MapProxy;

public class RemarksBroadcastService extends Service {

	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(5);
	private ScheduledFuture<?> remarksHandle;

	private boolean serviceStarted = false;
	
	private RemarksServiceDB remarksservicedb = new RemarksServiceDB();
	
	private Thread remarksThread = new Thread() {
		
		RemarksBroadcastService root = RemarksBroadcastService.this;
		
		public void run() {
			ApplicationUtil.println("RemarksBroadcastService", "broadcast remarks");
			try {
				remarksDateResolver();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		private void remarksDateResolver() throws Exception {
			if (app.getIsDateSync() == false) {
				root.stop();
				return;
			}

//			StringBuilder sb = new StringBuilder();
			List<Map> sqlParams = new ArrayList<Map>();
			Map data = new HashMap();
			
			List<Map> list = remarksservicedb.getRemarksForDateResolving();
			for (Map m : list) {
				String objid = MapProxy.getString( m, "objid" );

			 	Calendar cal = Calendar.getInstance();
				if (m.containsKey("dtsaved")) {
					cal.setTime(java.sql.Timestamp.valueOf(m.get("dtsaved").toString()));
				}
				
		 		long timedifference = 0L;
				if (m.containsKey("timedifference")) {
					timedifference = Long.parseLong(m.get("timedifference").toString());
				}
				
				long timemillis = cal.getTimeInMillis();
				timemillis += timedifference;
				
				Timestamp timestamp = new Timestamp( timemillis );
				String sql = "update remarks ";
				sql += "set dtposted='" + timestamp.toString() + "',";
				sql += "txndate='" + timestamp.toString() + "',";
				sql += "forupload=1 ";
				sql += "where objid='" + objid + "';";
				
				data = new HashMap();
				data.put("type", "update");
				data.put("sql", sql);
				
				sqlParams.add( data );
				
//				sb.append( sql );					
			} 
			
			if (app.getIsDateSync() == false) {
				root.stop();
				return;
			}
			
			if (sqlParams.size() > 0) {
				SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
				try {
					remarksdb.beginTransaction();
					ApplicationDBUtil.executeSQL( remarksdb, sqlParams );
//					remarksdb.execSQL( sb.toString() );
					remarksdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					remarksdb.endTransaction();
				}
			}
			
			broadcastRemarks();
			
			boolean hasfordateresolving = remarksservicedb.hasRemarksForDateResolving();
			boolean hasforbroadcast = remarksservicedb.hasRemarksForUpload();
			if (hasfordateresolving == false && hasforbroadcast == false) {
				root.stop();
			}
			
		}
		
		private void broadcastRemarks() throws Exception {
//			StringBuilder sb = new StringBuilder();
			List<Map> sqlParams = new ArrayList<Map>();
			Map data = new HashMap();
			
			List<Map> list = remarksservicedb.getForUploadRemarks();
			for (Map map : list) {
				MapProxy proxy = new MapProxy((Map) map);
				String objid = proxy.getString("objid");
				
				String mode = "ONLINE_WIFI";
				int networkStatus = app.getNetworkStatus();
				if (networkStatus == 1) {
					mode = "ONLINE_MOBILE";
				} else if (networkStatus == 3) {
					break;
				}
				
				Map params = new HashMap();
				params.put("sessionid", proxy.getString("billingid"));
				params.put("itemid", proxy.getString("itemid"));
				params.put("routecode", proxy.getString("routecode"));
				params.put("trackerid", proxy.getString("trackerid"));
				params.put("mode", mode);
				params.put("longitude", Double.parseDouble(proxy.getString("lng")));
				params.put("latitude", Double.parseDouble(proxy.getString("lat")));
				params.put("type", proxy.getString("type"));
				params.put("remarks", proxy.getString("remarks"));
				params.put("txndate", proxy.getString("txndate"));
				
				Map collector = new HashMap();
				collector.put("objid", proxy.getString("collector_objid"));
				collector.put("name", proxy.getString("collector_name"));					
				params.put("collector", collector);
				
				Map collectionsheet = new HashMap();
				collectionsheet.put("detailid", proxy.getString("objid"));
				
				Map loanapp = new HashMap();
				loanapp.put("objid", proxy.getString("loanapp_objid"));
				loanapp.put("appno", proxy.getString("loanapp_appno"));
				collectionsheet.put("loanapp", loanapp);
				
				Map borrower = new HashMap();
				borrower.put("objid", proxy.getString("borrower_objid"));
				borrower.put("name", proxy.getString("borrower_name"));
				collectionsheet.put("borrower", borrower);
				
				params.put("collectionsheet", collectionsheet);						
				
				Map param = new HashMap();
				String enc = new Base64Cipher().encode( params );
				param.put("encrypted", enc);
				
				Map response = new HashMap();
				for (int j=0; j<10; j++) {
					try {
						LoanPostingService service = new LoanPostingService();
						response = service.updateRemarksEncrypt( param );
						break;
					} catch (Throwable e) { e.printStackTrace(); }
				}
				
				if (response.containsKey("response")) {
					String str = response.get("response").toString().toLowerCase();
					if ("success".equals( str )) {
						
						String sql = "update remarks ";
						sql += "set state='CLOSE' ";
						sql += "where objid='" + objid + "';";
						
						data = new HashMap();
						data.put("type", "update");
						data.put("sql", sql);
						
						sqlParams.add( data );
						
//						sb.append( sql );
					}
				}
			}
			
			if (sqlParams.size() > 0) {
				SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
				try {
					remarksdb.beginTransaction();
					ApplicationDBUtil.executeSQL( remarksdb, sqlParams );
//					remarksdb.execSQL( sb.toString() );
					remarksdb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					remarksdb.endTransaction();
				}
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
						
			if (remarksThread.getState().equals( Thread.State.NEW ) || 
					remarksThread.getState().equals( Thread.State.TERMINATED )) {
				
//				int timeout = settings.getTrackerTimeout();
				int delay = settings.getUploadTimeout();
				remarksHandle = scheduledPool.scheduleWithFixedDelay( remarksThread, 1, delay, TimeUnit.SECONDS );
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
			if (remarksHandle != null) {
				remarksHandle.cancel( true );
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
