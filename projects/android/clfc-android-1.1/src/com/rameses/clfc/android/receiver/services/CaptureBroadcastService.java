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
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.CapturePaymentDB;
import com.rameses.clfc.android.services.LoanPostingService;
import com.rameses.client.android.Platform;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MapProxy;

public class CaptureBroadcastService extends Service {

	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(5);
	private ScheduledFuture<?> captureHandle;

	private boolean serviceStarted = false;

	private CapturePaymentDB capturepaymentdb = new CapturePaymentDB();
	
	private Thread captureThread = new Thread() {
		
		CaptureBroadcastService root = CaptureBroadcastService.this;
		
		public void run() {
			try {
				captureDateResolver();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		private void captureDateResolver() throws Exception {
			if (app.getIsDateSync() == false) {
				root.stop();
				return;
	 		}

//			StringBuilder sb = new StringBuilder();
			List<Map> sqlParams = new ArrayList<Map>();
			Map data = new HashMap();
			
			List<Map> list = capturepaymentdb.getPaymentsForDateResolving();
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
				timemillis -= timedifference;
				
				Timestamp timestamp = new Timestamp( timemillis );
				String sql = "update capture_payment ";
				sql += "set dtposted='" + timestamp.toString() + "',";
				sql += "dtpaid='" + timestamp.toString() + "',";
				sql += "forupload=1 ";
				sql += "where objid='" + objid + "';";
				
				data = new HashMap();
				data.put("type", "update");
				data.put("sql", sql);
				
				sqlParams.add( data );
				
//				sb.append( sql );
//				String sql = "UPDATE capture_payment SET dtposted='" + timestamp + "', dtpaid='" + timestamp + "', forupload=1 WHERE objid='" + m.get("objid").toString() + "'";
//				capturedb.execute(sql);
			}
			
			if (app.getIsDateSync() == false) {
				root.stop();
				return;
			}
			
			if (sqlParams.size() > 0) {
				SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
				try {
					capturedb.beginTransaction();
					ApplicationDBUtil.executeSQL( capturedb, sqlParams );
//					capturedb.execSQL( sb.toString() );
					capturedb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					capturedb.endTransaction();
				}
			}
			
			broadcastCapture();
			
			boolean hasfordateresolving = capturepaymentdb.hasPaymentForDateResolving();
			boolean hasforupload = capturepaymentdb.hasPaymentForUpload();
			if (hasfordateresolving == false && hasforupload == false) {
				root.stop();
			}
		}
		
		private void broadcastCapture() throws Exception {
			List<Map> sqlParams = new ArrayList<Map>();
			Map data = new HashMap();
			
			List<Map> list = capturepaymentdb.getForUploadPayments();
//			StringBuilder sb = new StringBuilder();

			for (Map map : list) {
				MapProxy proxy = new MapProxy((Map) map);
				
				String mode = "ONLINE_WIFI";
				int networkStatus = app.getNetworkStatus();
				if (networkStatus == 1) {
					mode = "ONLINE_MOBILE";
				}
				
				if (networkStatus == 3) {
					break;
				}

				
				String captureid = proxy.getString("captureid");
				String txndate = proxy.getString("txndate");
				
				Map collector = new HashMap();
				collector.put("objid", proxy.getString("collector_objid"));
				collector.put("name", proxy.getString("collector_name"));


				Map params = new HashMap();
				params.put("captureid", captureid);
				params.put("sessionid", proxy.getString("billingid"));
				params.put("state", proxy.getString("state"));
				params.put("trackerid", proxy.getString("trackerid"));
				params.put("lng", Double.parseDouble(proxy.getString("lng")));
				params.put("lat", Double.parseDouble(proxy.getString("lat")));
				params.put("txndate", txndate);
				params.put("type", "CAPTURE");
				params.put("mode", mode);
				params.put("collector", collector);	
				
				Map payment = new HashMap();
				payment.put("objid", proxy.getString("objid"));
				payment.put("borrowername", proxy.getString("borrowername"));
//					payment.put("refno", proxy.getString("refno"));
				payment.put("dtpaid", proxy.getString("dtpaid"));
				payment.put("amount", proxy.getString("amount"));
				payment.put("paidby", proxy.getString("paidby"));
				
				String option = proxy.getString("payoption");
				payment.put("option", option);
				
				if ("check".equals(option)) {
					Map bank = new HashMap();
					bank.put("objid", proxy.getString("bank_objid"));
					bank.put("name", proxy.getString("bank_name"));
					payment.put("bank", bank);
					
					Map check = new HashMap();
					check.put("no", proxy.getString("check_no"));
					check.put("date", proxy.getString("check_date"));
					payment.put("check", check);
				}
				
				params.put("payment", payment); 
				
				Map param = new HashMap();
				String enc = new Base64Cipher().encode(params);
				param.put("encrypted", enc);
				
				Map response = new HashMap();
				for (int j=0; j<10; j++) {
					LoanPostingService service = new LoanPostingService();
					try {
						response = service.postCapturePaymentEncrypt(param);
						break;
					} catch (Throwable e) {
						e.printStackTrace();
					} 
				}
				
				
				if (response != null && response.containsKey("response")) {
					String str = MapProxy.getString(response, "response").toLowerCase();

					if (str.equals("success")) {
						
						String sql = "update capture_payment ";
						sql += "set state='CLOSED' ";
						sql += "where objid='" + proxy.getString("objid") + "';";
						
						data = new HashMap();
						data.put("type", "update");
						data.put("sql", sql);
						
						sqlParams.add( data );
						
//						sb.append( sql );	
					}
				}
			}

			if (sqlParams.size() > 0) {
				SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
				try {
					capturedb.beginTransaction();
					ApplicationDBUtil.executeSQL( capturedb, sqlParams );
//					capturedb.execSQL( sb.toString() ); 
					capturedb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					capturedb.endTransaction();
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
						
			if (captureThread.getState().equals( Thread.State.NEW ) || 
					captureThread.getState().equals( Thread.State.TERMINATED )) {
				
//				int timeout = settings.getTrackerTimeout();
				int delay = settings.getUploadTimeout();
				captureHandle = scheduledPool.scheduleWithFixedDelay( captureThread, 1, delay, TimeUnit.SECONDS );
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
			if (captureHandle != null) {
				captureHandle.cancel( true );
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
