package com.rameses.clfc.android.receiver.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.R;
import com.rameses.client.android.AbstractActionBarActivity;
import com.rameses.client.android.AppContext;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;

public class ConnectivityCheckerService extends Service {

	private Map CONNECTION_MODES = new HashMap();
	private final int[] CONNECTION_PRIORITY = new int[]{ ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE };
	
	private Timer timer;
	private TimerTask timerTask;
	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private boolean serviceStarted = false;
	
	public ConnectivityCheckerService() {
		app = (ApplicationImpl) Platform.getApplication();
		settings = (AppSettingsImpl) app.getAppSettings();
		populateConnectionModes();
	}
	
	private void populateConnectionModes() {
		CONNECTION_MODES = new HashMap();
		CONNECTION_MODES.put("", "NOT CONNECTED");
		CONNECTION_MODES.put(ConnectivityManager.TYPE_WIFI, "ONLINE_WIFI");
		CONNECTION_MODES.put(ConnectivityManager.TYPE_MOBILE, "ONLINE_MOBILE");
	}
	
	@Override
	public int onStartCommand( Intent intent, int flags, int startId ) {
		super.onStartCommand(intent, flags, startId);
		
		startTimer();
		
		return START_STICKY;
	}
	
	public synchronized boolean getIsServiceStarted() { return serviceStarted; }
	
	public synchronized void startTimer() {
		
		if (serviceStarted == false) {
			if (timer == null) timer = new Timer();
					
			initializeTimerTask();
			
			timer.schedule( timerTask, 1000, 1000 );
		
			serviceStarted = true;
		} 
		
	}
	
	public synchronized void restartTimer() {
		if (serviceStarted == true) {
			stopTimer();
		}
		startTimer();
	}
	
	private void initializeTimerTask() {
		timerTask = new TimerTask() {
			
			private boolean networkChanged = false;
			
			public void run() {
				try {
//					println("connectivity checker running");
					runImpl();
				} catch (Throwable t) {}
			}
			
			private void runImpl() throws Exception {
				networkChanged = false;
				boolean hasConnection = false;
				int currentNetworkStatus = app.getNetworkStatus();
				boolean currentConnectivity = app.getIsConnected();
				
				String m = "";
				int connection;
				for (int i=0; i<CONNECTION_PRIORITY.length; i++) {
            		m = CONNECTION_MODES.get("").toString();
            		connection = CONNECTION_PRIORITY[i];
		            try {
		    			boolean connected = checkConnection( connection, currentNetworkStatus );
		    			if (connected == true) {
			    			m = CONNECTION_MODES.get( connection ).toString();
			    			hasConnection = true;
			    			break;
		    			}
		            } catch (Exception e) {
//		            	println("error-> " + e);
		            }
				}
				
				if (hasConnection == false) {
					app.setNetworkStatus( 3 );
					if (currentNetworkStatus != 3) {
						networkChanged = true;
					}
				}
				if (hasConnection == true && currentConnectivity == false) {
					app.setIsConnected( true );
				}

				if (networkChanged == true) {
					final AbstractActionBarActivity activity = app.getCurrentActionBarActivity();
					if (activity == null) return;
					
					final String mode = m;
					activity.runOnUiThread(new Runnable() {
						public void run() {
							View v = activity.findViewById(R.id.tv_mode);
							if (v != null) ((TextView) v).setText( mode );
						} 
					}); 
				}
				
			}

			
			private boolean checkConnection( int networkType, int currentNetworkStatus ) throws Exception {
				boolean flag = false;
				
				String apphost = "";
				int networkStatus = 3;
				switch ( networkType ) {
					case ConnectivityManager.TYPE_WIFI: 
						networkStatus = 0;
						apphost = settings.getAppHost( networkStatus ); 
						break;
					case ConnectivityManager.TYPE_MOBILE: 
						networkStatus = 1;
						apphost = settings.getAppHost( networkStatus ); 
						break;
					default: networkStatus = 3; break;
				}
				Map appenv = new HashMap();
				appenv.putAll( app.getAppEnv() );
				appenv.put("app.host", apphost);
//				println("apphost-> " + apphost);
				
				SessionContext sess = AppContext.getSession();
				Map env = new HashMap();
				if (sess != null) env = sess.getHeaders();
//	            System.out.println("app env " + cctx.getAppEnv());
	            ScriptServiceContext ssc = new ScriptServiceContext(appenv); 
	            ServiceProxy service = ssc.create("DateService", env);
	            
	            try {
	            	service.invoke("getServerDate", null);
	            	flag = true;
	            	if (networkStatus != currentNetworkStatus) {
		            	app.setNetworkStatus( networkStatus );
		            	networkChanged = true;
	            	}
	            } catch (Exception e) {
	            	
	            }
	            
	            return flag;
//	            return ssc.create(name, cctx.getEnv());
			}
		};
	}
	
	public synchronized void stopTimer() {
		if (serviceStarted == true) {
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			serviceStarted = false;
		}
 	}
	
	@Override
	public void onDestroy() {
		stopTimer();
		super.onDestroy();
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void println( Object msg ) {
		ApplicationUtil.println("ConnectivityCheckerService", msg.toString());
	}
	
}
