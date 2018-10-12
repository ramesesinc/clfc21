package com.rameses.clfc.android;


import java.util.HashMap;
import java.util.Map;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.rameses.client.android.AbstractActionBarActivity;
import com.rameses.client.android.AppContext;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.Task;
import com.rameses.client.android.UIApplication;
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;

class XNetworkCheckerService 
{
	private Map CONNECTION_MODES = new HashMap();
	private final int[] CONNECTION_PRIORITY = new int[]{ ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE };
	private ApplicationImpl app;
	private NetworkInfo networkInfo;
	private Handler handler = new Handler();
//	private String[] modes = new String[]{"NOT CONNECTED"};
	private boolean serviceStarted = false;
	private Task actionTask;
	
	public XNetworkCheckerService(ApplicationImpl app) {
		this.app = app;
		serviceStarted = false;
		populateConnectionModes();
	}
	
	private void populateConnectionModes() {
		CONNECTION_MODES = new HashMap();
		CONNECTION_MODES.put("", "NOT CONNECTED");
		CONNECTION_MODES.put(ConnectivityManager.TYPE_WIFI, "ONLINE_WIFI");
		CONNECTION_MODES.put(ConnectivityManager.TYPE_MOBILE, "ONLINE_MOBILE");
	}
	
	public void start() {		
		if (serviceStarted == false) {
			createTask();
			Platform.getTaskManager().schedule( actionTask, 1000, 1000 );
			serviceStarted = true;
		}
		
	}
	
	public void restart() {
		if (serviceStarted==true) {
			actionTask.cancel();
			actionTask = null;
			serviceStarted = false;
		}
		start();
	}
	
	private void createTask() {
		if (actionTask == null) {
			actionTask = new Task() {
				
				public void run() {
					try {
						runImpl();
					} catch(Throwable t) {
						t.printStackTrace();
					}
				}
				
				private void runImpl() throws Exception {

					AppSettingsImpl settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
//					println("boot completed-> " + settings.getString("boot_completed", "not found"));
//					println("shutdown-> " + settings.getString("shutdown", "not found"));
					
//		            ClientContext cctx = ClientContext.getCurrentContext(); 
//		            ApplicationUtil.println("NetworkCheckerService", "client context-> " + cctx.getEnv());
//					UIApplication app = Platform.getApplication();
//					ApplicationUtil.println("NetworkCheckerService", "env-> " + app.getAppEnv());

	            	app.setNetworkStatus( 3 ); 
					String m = "";
					int connection;
	            	for (int i=0; i<CONNECTION_PRIORITY.length; i++) {	
	            		m = CONNECTION_MODES.get("").toString();
	            		connection = CONNECTION_PRIORITY[i];
			            try {
			    			boolean connected = checkConnection( connection );
			    			if (connected == true) {
				    			m = CONNECTION_MODES.get( connection ).toString();
				    			break;
			    			}
			            } catch (Exception e) {
			            	ApplicationUtil.println("NetworkCheckerService", "error-> " + e);
			            }
	            	}
//	            	ApplicationUtil.println("NetworkCheckerService", "mode-> " + m);
					
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
				
				private boolean checkConnection( int networkType ) throws Exception {
					boolean flag = false;
					
					AppSettingsImpl settings = (AppSettingsImpl) app.getAppSettings();
					
					String apphost = "";
					int networkStatus = 3;
					switch (networkType) {
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
					println("apphost-> " + apphost);
					
					SessionContext sess = AppContext.getSession();
					Map env = new HashMap();
					if (sess != null) env = sess.getHeaders();
//		            System.out.println("app env " + cctx.getAppEnv());
		            ScriptServiceContext ssc = new ScriptServiceContext(appenv); 
		            ServiceProxy service = ssc.create("DateService", env);
		            
		            try {
		            	service.invoke("getServerDate", null);
		            	flag = true;
		            	app.setNetworkStatus( networkStatus );
		            } catch (Exception e) {
		            	
		            }
		            
		            return flag;
//		            return ssc.create(name, cctx.getEnv());
				}
			};
		}
	}
	
	private void println( Object msg ) {
		ApplicationUtil.println("NetworkCheckerService", msg.toString() );
	}
	
	/*
	public void start() {
		if (handler == null) { 
			handler = new Handler();
			new RunnableImpl().run(); 
		} 
	}
	*/
		
	/*
	private class RunnableImpl implements Runnable 
	{
		NetworkCheckerService root = NetworkCheckerService.this;
		
		public void run() {
			try {
				runImpl();
			} catch(Throwable t) {
				t.printStackTrace();
			}
			
			try {
				root.handler.postDelayed(this, 1000); 
			} catch(Throwable t) {
				t.printStackTrace();
			}			
		}
		
		private void runImpl() throws Exception {

//            ClientContext cctx = ClientContext.getCurrentContext(); 
//            ApplicationUtil.println("NetworkCheckerService", "client context-> " + cctx.getEnv());
//			UIApplication app = Platform.getApplication();
//			ApplicationUtil.println("NetworkCheckerService", "env-> " + app.getAppEnv());
            try {
    			checkConnection();	
            } catch (Exception e) {
            	ApplicationUtil.println("NetworkCheckerService", "error-> " + e);
            }
			
			final AbstractActionBarActivity activity = app.getCurrentActionBarActivity();
			if (activity == null) return;
			
			activity.runOnUiThread(new Runnable() {
				public void run() {
					View v = activity.findViewById(R.id.tv_mode);
					if (v != null) ((TextView) v).setText(modes[0]);
				} 
			}); 
		}
		
		private void checkConnection() throws Exception {
			
			UIApplication app = Platform.getApplication();
			SessionContext sess = AppContext.getSession();
			Map env = new HashMap();
			if (sess != null) env = sess.getHeaders();
//            System.out.println("app env " + cctx.getAppEnv());
            ScriptServiceContext ssc = new ScriptServiceContext(app.getAppEnv()); 
            ServiceProxy service = ssc.create("DateService", env);
            
            ApplicationUtil.println("NetworkCheckerService", "date-> " + service.invoke("getServerDate", null));
            
//            return ssc.create(name, cctx.getEnv());
		}
		
		private void xrunImpl() throws Exception {
			networkInfo = app.getConnectivityManager().getActiveNetworkInfo();
			
			final String[] modes = new String[]{"NOT CONNECTED"};
//			ApplicationUtil.println("NetworkCheckerService", "network info-> " + networkInfo);
			if (networkInfo != null && networkInfo.isConnected()) {
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					app.setNetworkStatus(0);
					modes[0] = "ONLINE_WIFI";
				} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					app.setNetworkStatus(1); 
					modes[0] = "ONLINE_MOBILE";
				} else {
					app.setNetworkStatus(3);
				}
			} else {
				app.setNetworkStatus(3);
			}
			
			final AbstractActionBarActivity activity = app.getCurrentActionBarActivity();
			if (activity == null) return;
			
			activity.runOnUiThread(new Runnable() {
				public void run() {
					View v = activity.findViewById(R.id.tv_mode);
					if (v != null) ((TextView) v).setText(modes[0]);
				} 
			}); 
		}
	}
	*/
}