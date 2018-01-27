package com.rameses.clfc.android.receiver;

import java.lang.reflect.Method;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.rameses.client.android.AppSettings;
import com.rameses.client.android.Platform;

public class SmsService extends Service {

	private String TAG = "[SmsService]";
	
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onstart");
			
		String KEY = intent.getStringExtra("KEY");
		Log.i(TAG, KEY);
		
		Bundle bundle = intent.getExtras();
		 
        try {
			if (bundle != null) {
			     
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				 
				SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[0]);
				String message = currentMessage.getDisplayMessageBody();
				
				String key = "";
				if (message.length() >= 4) key = message.substring(0, 4);
				if (KEY.equals(key.toLowerCase())) { 
//						String msgtext = "";
						String code = "";
						if (message.length() >= 9) code = message.substring(5, 9);
						
						Log.i(TAG, "code: " + code);
						if ("1000".equals(code)) {
							checkPhoneStatus(currentMessage); 
						} else if ("1002".equals(code)) {
							String txt = message.substring(10, message.length());
							changeSettings(txt);
						}
//						switch (code) {
//							case "1000" : checkPhoneStatus(currentMessage); 
//										  break;
//							case "1002"	: String txt = message.substring(10, message.length());
//										  changeSettings(txt);
//										  break;
//						}
//				    	if (message != null && !"".equals(message)) {
//					        SmsManager sms = SmsManager.getDefault();
//					        sms.sendTextMessage(phoneNumber, null, msgtext, null, null);
//				    	}
				}
			}
 
        } catch (Exception e) {
            Log.e(TAG, "Exception SmsService " +e);
             
        }
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return super.onStartCommand(intent, flags, startId);
	}
	
	private void changeSettings( String str ) {
		AppSettings appSettings = Platform.getApplication().getAppSettings();
		
		Map settings = appSettings.getAll();		
		String[] list = str.split(",");
		String[] textList = null;
		String key = "", val = "";
		
		Log.i(TAG, "settings " + settings);
		
		try {
			for (int i=0; i<list.length; i++) {
				textList = list[i].split("=");
				
				if (textList.length == 2) {
					key = textList[0];
					val = textList[1];
					
					if (settings.containsKey(key)) {
						settings.put(key, val);
					}
				}  
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			appSettings.putAll(settings);
		} 
	}
	
	private void checkPhoneStatus( SmsMessage currentMessage ) {
		WifiManager wifimngr = (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
		LocationManager locationmngr = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
		String message = "Wi-fi enabled: " + wifimngr.isWifiEnabled() + "\n";
		
		boolean mobileDataEnabled = false; // Assume disabled
		ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
		    Class cmClass = Class.forName(cm.getClass().getName());
		    Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
		    method.setAccessible(true); // Make the method callable
		// get the setting for "mobile data"
		    mobileDataEnabled = (Boolean)method.invoke(cm);
		} catch (Exception e) {
		    // Some problem accessible private API
		// TODO do whatever error handling you want here
		}
		
		message += "Mobile enabled: " + mobileDataEnabled + "\n";
		message += "GPS enabled: " + locationmngr.isProviderEnabled(LocationManager.GPS_PROVIDER) + "\n";
		message += "Network enabled: " + locationmngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
				
        getSmsManager().sendTextMessage(getPhoneNumber(currentMessage), null, message, null, null);
	}
	
	private String getPhoneNumber( SmsMessage currentMessage ) {
		return currentMessage.getDisplayOriginatingAddress();
	}
	
	
	private SmsManager getSmsManager() {
		return SmsManager.getDefault();
	}
}
