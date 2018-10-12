package com.rameses.clfc.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.receiver.services.CancelledBillingCheckerService;
import com.rameses.clfc.android.receiver.services.CaptureBroadcastService;
import com.rameses.clfc.android.receiver.services.ConnectivityCheckerService;
import com.rameses.clfc.android.receiver.services.LocationTrackerService;
import com.rameses.clfc.android.receiver.services.MobileStatusTrackerService;
import com.rameses.clfc.android.receiver.services.PaymentBroadcastService;
import com.rameses.clfc.android.receiver.services.RemarksBroadcastService;
import com.rameses.clfc.android.receiver.services.RemarksRemovedBroadcastService;
import com.rameses.clfc.android.receiver.services.RunningTimeService;
import com.rameses.clfc.android.receiver.services.SmsService;
import com.rameses.clfc.android.receiver.services.VoidRequestBroadcastService;
import com.rameses.client.android.AbstractActionBarActivity;
import com.rameses.client.android.Platform;

public class ApplicationBroadcastReceiver extends BroadcastReceiver {
	
	private String getKey() {
		AppSettingsImpl settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
		if (settings != null) {
			return settings.getKey();
		}
		return "";
	}
	
	@Override
	public void onReceive( Context context, Intent intent ) {
		String action = intent.getAction();
		
		ApplicationImpl app = (ApplicationImpl) Platform.getApplication();
		
//		if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
		if (action.equals( Telephony.Sms.Intents.SMS_RECEIVED_ACTION )) {

			Intent smsIntent = new Intent( context, SmsService.class );
			smsIntent.putExtras( intent.getExtras() );
			smsIntent.putExtra( "KEY", getKey() );
			
			context.startService( smsIntent );						
		}	

		if (intent.getAction().equals( Intent.ACTION_TIME_CHANGED )) {
//			updateTime( context, app );
			restartDateSync( app );
		}
		
		if (action.equals( "rameses.clfc.START_SERVICES" )) {
			startApplicationServices( context, app );
		}
		
		if (action.equals( "rameses.clfc.STOP_SERVICES" )) {
			stopApplicationServices( app );
		}
		
		if (action.equals( "rameses.clfc.DESTROY_SERVICES" )) {
			destroyApplicationServices( context, app );
		}
		
		if (action.equals( "rameses.clfc.TRACKER_START_SERVICE" )) {
			startLocationTrackerService( context, app );
		}
		
		if (action.equals( "rameses.clfc.TRACKER_STOP_SERVICE" )) {
			stopLocationTrackerService( app );
		}
		
		if (action.equals( "rameses.clfc.PAYMENT_START_SERVICE" )) {
			startPaymentService( context, app );
		}
		
		if (action.equals( "rameses.clfc.REMARK_START_SERVICE" )) {
			startRemarksService( context, app );
		}
		
		if (action.equals( "rameses.clfc.CAPTURE_START_SERVICE" )) {
			startCaptureService( context, app );
		}
		
		if (action.equals( "rameses.clfc.REMARK_REMOVED_START_SERVICE" )) {
			startRemarksRemovedService( context, app );
		}
		
		if (action.equals( "rameses.clfc.VOID_REQUEST_START_SERVICE" )) {
			startVoidRequestService( context, app );
		}
	}
	
	void startApplicationServices( Context context, ApplicationImpl app ) {
		
		startLocationTrackerService( context, app );
		startConnectivityCheckerService( context, app );
		startStatusTrackerService( context, app );
//		startRunningTimeService( context, app );
		startBillingCheckerService( context, app );		
		startPaymentService( context, app );
		startRemarksService( context, app );
		startCaptureService( context, app );
		startRemarksRemovedService( context, app );
		startVoidRequestService( context, app );
		
	}
	
	void stopApplicationServices( ApplicationImpl app ) {

		stopLocationTrackerService( app );
		stopConnectivityCheckerService( app );
		stopStatusTrackerService( app );
//		stopRunningTimeService( app );
		stopBillingCheckerService( app );		
		stopPaymentService( app );
		stopRemarksService( app );
		stopCaptureService( app );
		stopRemarksRemovedService( app );
		stopVoidRequestService( app );
		
	}
	
	void destroyApplicationServices( Context context, ApplicationImpl app ) {

		destroyLocationTrackerService( context, app );
		destroyConnectivityCheckerService( context, app );
		destroyStatusTrackerService( context, app );
//		destroyRunningTimeService( context, app );
		destroyBillingCheckerService( context, app );
		destroyPaymentService( context, app );
		destroyRemarksService( context, app );
		destroyCaptureService( context, app );
		destroyRemarksRemovedService( context, app );
		destroyVoidRequestService( context, app );
	}
	
	void startLocationTrackerService( Context context, ApplicationImpl app ) {
		
		LocationTrackerService service = app.getLocationTrackerInstance();
		context.startService(new Intent(context, service.getClass()));
	}
	
	void stopLocationTrackerService( ApplicationImpl app ) {
		
		LocationTrackerService service = app.getLocationTrackerInstance();
		if (service.getIsServiceStarted() == true) {
			service.stop();
		}
		
//		context.stopService(new Intent(context, service.getClass()));
	}
	
	void destroyLocationTrackerService( Context context, ApplicationImpl app ) {
		
		LocationTrackerService service = app.getLocationTrackerInstance();
		context.stopService(new Intent(context, service.getClass()));
	}
	
	void startConnectivityCheckerService( Context context, ApplicationImpl app ) {
		
		ConnectivityCheckerService service = app.getConnectivityCheckerInstance();
		context.startService(new Intent(context, service.getClass()));
	}
	
	void stopConnectivityCheckerService( ApplicationImpl app ) {
		
		ConnectivityCheckerService service = app.getConnectivityCheckerInstance();
		if (service.getIsServiceStarted() == true) {
			service.stopTimer();
		}
//		context.stopService(new Intent(context, service.getClass()));
	}
	
	void destroyConnectivityCheckerService( Context context, ApplicationImpl app ) {
		
		ConnectivityCheckerService service = app.getConnectivityCheckerInstance();
		context.stopService(new Intent(context, service.getClass()));
	}
	
	void startStatusTrackerService( Context context, ApplicationImpl app ) {
		
		MobileStatusTrackerService service = app.getMobileStatusTrackerInstance();
		context.startService(new Intent(context, service.getClass()));
	}
	
	void stopStatusTrackerService( ApplicationImpl app ) {
		
		MobileStatusTrackerService service = app.getMobileStatusTrackerInstance();
		if (service.getIsServiceStarted() == true) {
			service.stop();
		}
//		context.stopService(new Intent(context, service.getClass()));
	}
	
	void destroyStatusTrackerService( Context context, ApplicationImpl app ) {
		
		MobileStatusTrackerService service = app.getMobileStatusTrackerInstance();
		context.stopService(new Intent(context, service.getClass()));
	}
	
//	void startRunningTimeService( Context context, ApplicationImpl app ) {
//		
//		RunningTimeService service = app.getRunningTimeInstance();
//		context.startService(new Intent(context, service.getClass()));
//	}
//	
//	void stopRunningTimeService( ApplicationImpl app ) {
//		
//		RunningTimeService service = app.getRunningTimeInstance();
//		if (service.getIsServiceStarted() == true) {
//			service.stop();
//		}
////		context.stopService(new Intent(context, service.getClass()));
//	}
//	
//	void destroyRunningTimeService( Context context, ApplicationImpl app ) {
//		
//		RunningTimeService service = app.getRunningTimeInstance();
//		context.stopService(new Intent(context, service.getClass()));
//	}
	
	void startBillingCheckerService( Context context, ApplicationImpl app ) {
		
		CancelledBillingCheckerService service = app.getBillingCheckerInstance();
		context.startService(new Intent(context, service.getClass()));
		
	}
	
	void stopBillingCheckerService( ApplicationImpl app ) {
		
		CancelledBillingCheckerService service = app.getBillingCheckerInstance();
		if (service.getIsServiceStarted() == true) {
			service.stop();
		}
//		context.stopService(new Intent(context, service.getClass()));
		
	}
	
	void destroyBillingCheckerService( Context context, ApplicationImpl app ) {
		
		CancelledBillingCheckerService service = app.getBillingCheckerInstance();
		context.stopService(new Intent(context, service.getClass()));
	}
	
	void startPaymentService( Context context, ApplicationImpl app ) {
		
		PaymentBroadcastService service = app.getPaymentBroadcastInstance();
		context.startService(new Intent(context, service.getClass()));
	}
	
	void stopPaymentService( ApplicationImpl app ) {
		
		PaymentBroadcastService service = app.getPaymentBroadcastInstance();
		if (service.getIsServiceStarted() == true) {
			service.stop();
		}
//		context.stopService(new Intent( context, service.getClass() ));
	}
	
	void destroyPaymentService( Context context, ApplicationImpl app ) {
		
		PaymentBroadcastService service = app.getPaymentBroadcastInstance();
		context.stopService(new Intent(context, service.getClass()));
	}
	
	void startRemarksService( Context context, ApplicationImpl app ) {
		
		RemarksBroadcastService service = app.getRemarksBroadcastInstance();
		context.startService(new Intent(context, service.getClass()));
	}
	
	void stopRemarksService( ApplicationImpl app ) {
		
		RemarksBroadcastService service = app.getRemarksBroadcastInstance();
		if (service.getIsServiceStarted() == true) {
			service.stop();
		}
//		context.stopService(new Intent(context, service.getClass()));
	}
	
	void destroyRemarksService( Context context, ApplicationImpl app ) {
		
		RemarksBroadcastService service = app.getRemarksBroadcastInstance();
		context.stopService(new Intent(context, service.getClass()));
	}
	
	void startCaptureService( Context context, ApplicationImpl app ) {
		
		CaptureBroadcastService service = app.getCaptureBroadcastInstance();
		context.startService( new Intent( context, service.getClass() ) );
	}
	
	void stopCaptureService( ApplicationImpl app ) {
		
		CaptureBroadcastService service = app.getCaptureBroadcastInstance();
		if (service.getIsServiceStarted() == true) {
			service.stop();
		}
//		context.stopService(new Intent(context, service.getClass()));
	}
	
	void destroyCaptureService( Context context, ApplicationImpl app ) {
		
		CaptureBroadcastService service = app.getCaptureBroadcastInstance();
		context.stopService(new Intent(context, service.getClass()));
	}
	
	void startRemarksRemovedService( Context context, ApplicationImpl app ) {
		
		RemarksRemovedBroadcastService service = app.getRemarksRemovedBroadcastInstance();
		context.startService(new Intent(context, service.getClass()));
	}
	
	void stopRemarksRemovedService( ApplicationImpl app ) {
		
		RemarksRemovedBroadcastService service = app.getRemarksRemovedBroadcastInstance();
		if (service.getIsServiceStarted() == true) {
			service.stop();
		}
//		context.stopService(new Intent(context, service.getClass()));
	}
	
	void destroyRemarksRemovedService( Context context, ApplicationImpl app ) {
		
		RemarksRemovedBroadcastService service = app.getRemarksRemovedBroadcastInstance();
		context.stopService(new Intent(context, service.getClass()));
	}
	
	void startVoidRequestService( Context context, ApplicationImpl app ) {
		
		VoidRequestBroadcastService service = app.getVoidRequestBroadcastInstance();
		context.startService(new Intent(context, service.getClass()));
	}
	
	void stopVoidRequestService( ApplicationImpl app ) {
		
		VoidRequestBroadcastService service = app.getVoidRequestBroadcastInstance();
		if (service.getIsServiceStarted() == true) {
			service.stop();
		}
//		context.stopService(new Intent(context, service.getClass()));
	}
	
	void destroyVoidRequestService( Context context, ApplicationImpl app ) {
		
		VoidRequestBroadcastService service = app.getVoidRequestBroadcastInstance();
		context.stopService(new Intent(context, service.getClass()));
	}
	
	void restartDateSync( ApplicationImpl app ) {
		if (app.getIsDateSync() == true) {
			app.setIsDateSync( false );
			stopPaymentService( app );
			stopRemarksService( app );
			stopCaptureService( app );
			app.syncServerDate();
		}
	}
	
//	void updateTime( ApplicationImpl app ) {
//
//		//AbstractActionBarActivity aa = Platform.getCurrentActionBarActivity();
//		//if (aa == null) aa = Platform.getActionBarMainActivity();
//		
//		app.setIsDateSync( false );
//		
//		/*
//		PaymentBroadcastService paymentService = app.getPaymentBroadcastInstance();
//		if (paymentService.getIsServiceStarted() == true) {
//			stopPaymentService( context, app );
//			while (paymentService.getIsServiceStarted() == true) {}
//		}
//		
//		RemarksBroadcastService remarksService = app.getRemarksBroadcastInstance();
//		if (remarksService.getIsServiceStarted() == true) {
//			stopRemarksService( context, app );
//			while (remarksService.getIsServiceStarted() == true) {}
//		}
//		
//		CaptureBroadcastService captureService = app.getCaptureBroadcastInstance();
//		if (captureService.getIsServiceStarted() == true) {
//			stopCaptureService( context, app );
//			while (captureService.getIsServiceStarted() == true) {}
//		}
//		*/
//		
//		/*
//		Calendar cal = Calendar.getInstance();
//		Calendar xcal = Calendar.getInstance();
//		
//		AppSettings settings = app.getAppSettings();
//		Map map = settings.getAll();
//		if (map.containsKey("phonedate")) {
////			println("server date " + settings.getString("serverdate"));
//			cal.setTime(java.sql.Timestamp.valueOf(settings.getString("phonedate")));
//		}
//		 
//		RunningTimeService runningTimeService = app.getRunningTimeInstance();
//		
//		long timemillis = cal.getTimeInMillis();
//		long elapsedtimemillis = runningTimeService.getElapsedTime();
////		long elapsedtimemillis = instance.getElapsedTimeInMillis();
//		
////		AppRunningTimeUtil instance = AppRunningTimeUtil.getInstance();
//		
//		
//		timemillis += elapsedtimemillis; 
//		
//        long xtimemillis = xcal.getTimeInMillis();
//        
//        long diff = timemillis - xtimemillis;
//        long timediff = diff;
//        
//        if (map.containsKey("timedifference")) {
//        	timediff = settings.getLong("timedifference");
//        }
//        
//        long newtimediff = timediff + diff; 
////        println("saved phonetime-> " + new Timestamp(cal.getTimeInMillis()) + " current phonetime-> " + new Timestamp(xcal.getTimeInMillis()) + " previous timediff-> " + timediff + " current timediff-> " + newtimediff + " elapsed timemillis-> " + elapsedtimemillis);
//        
//        Timestamp currenttime = new Timestamp( xcal.getTimeInMillis() );
//        settings.put("phonedate", currenttime);
//        settings.put("timedifference", newtimediff);
//        
//		instance.reset();
//		*/
//        
//		/*
//		ApplicationUtil.resolvePaymentTimedifference( newtimediff );
//		startPaymentService( context, app );
//		startRemarksService( context, app );
//		startCaptureService( context, app );
//		*/
//	}
	
	void println( Object msg ) {
		ApplicationUtil.println("ApplicationBroadcastReceiver", msg.toString());
	}

}
