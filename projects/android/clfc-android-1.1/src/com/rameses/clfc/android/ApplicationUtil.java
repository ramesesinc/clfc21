package com.rameses.clfc.android;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.rameses.clfc.android.db.DBCapturePayment;
import com.rameses.clfc.android.db.DBCollectionGroup;
import com.rameses.clfc.android.db.DBCollectionSheet;
import com.rameses.clfc.android.db.DBPaymentService;
import com.rameses.clfc.android.db.DBRemarksService;
import com.rameses.clfc.android.db.DBSystemService;
import com.rameses.clfc.android.db.DBVoidService;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIApplication;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;


public final class ApplicationUtil 
{
	private static boolean isDeviceRegistered = false;
	private static WifiManager wifimngr;
	private static LocationManager locationmngr;
	private static ConnectivityManager connectivitymngr;

	public static boolean getIsDeviceRegistered() { 
		return isDeviceRegistered; 
	}

	public static void deviceResgistered() {
		isDeviceRegistered = true;
	}

	public static int getNetworkStatus() {
		return ((ApplicationImpl) Platform.getApplication()).getNetworkStatus();
	}

	public static boolean hasCapturedPayments() throws Exception {
		UserProfile prof = SessionContext.getProfile();
		String collectorid = (prof != null? prof.getUserId() : "");
		
		String date = "";
		Date dt = Platform.getApplication().getServerDate();
		if (dt != null) {
			date = formatDate(dt, "yyyy-MM-dd");
		}
		
		return hasCapturedPaymentsImpl(collectorid, date);
	}

	public static boolean hasCapturedPayments(String collectorid, String date) throws Exception {
		return hasCapturedPaymentsImpl(collectorid, date);
	}
	
	private static boolean hasCapturedPaymentsImpl(String collectorid, String date) throws Exception {
		DBContext ctx = new DBContext("clfccapture.db");
		DBCapturePayment dbcp = new DBCapturePayment();
		dbcp.setDBContext(ctx);
		dbcp.setCloseable(false);
		
		boolean hasPayments = false;
		try {
			if (date == null) {
				hasPayments = dbcp.hasPayments(collectorid);
			} else {
				hasPayments = dbcp.hasPayments(collectorid, date);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			ctx.close();
		}
		
		return hasPayments;
	}
	
	public static WifiManager getWifiManager() {
		if (wifimngr == null) {
			wifimngr = (WifiManager) Platform.getApplication().getSystemService(Context.WIFI_SERVICE);
		}
		return wifimngr;
	}
	
	public static LocationManager getLocationManager() {
		if (locationmngr == null) {
			locationmngr = (LocationManager) Platform.getApplication().getSystemService(Context.LOCATION_SERVICE);;
		}
		return locationmngr;
	}
	
	public static ConnectivityManager getConnectivityManager() {
		if (connectivitymngr == null) {
			connectivitymngr = (ConnectivityManager) Platform.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		return connectivitymngr;
	}
	
	public static String createPrefix() {
		Date dt = Platform.getApplication().getServerDate();
		String date = formatDate(dt, "yyyy-MM-dd");
		
		UserProfile prof = SessionContext.getProfile();
		String userid = (prof != null? prof.getUserId() : "");
		
		return createPrefix(date, userid);
	}
	
	public static String createPrefix(String date) {
		UserProfile prof = SessionContext.getProfile();
		String userid = (prof != null? prof.getUserId() : "");
		
		return createPrefix(date, userid);
	}

	public static String createPrefix(String date, String userid) {
		Date dt = java.sql.Date.valueOf(date);
		date = formatDate(dt, "yyyy-MM-dd");
		
		return date + userid;
	}

	public static String getPrefix() {
		UserProfile prof = SessionContext.getProfile();
		String userid = (prof != null? prof.getUserId() : "");

		Date date = Platform.getApplication().getServerDate();
		
		return getPrefix(userid, date);
	}

	public static String getPrefix(Date date) {
		UserProfile prof = SessionContext.getProfile();
		String userid = (prof != null? prof.getUserId() : "");
		
		return getPrefix(userid, date);
	}

	public static String getPrefix(String userid) {
		Date date = Platform.getApplication().getServerDate();
//		String dt = formatDate(date, "yyyy-MM-dd");
//		date = java.sql.Date.valueOf(dt);
		
		return getPrefix(userid, date);
	}

	public static String getPrefix(String userid, Date dt) {
//		Date dt = Platform.getApplication().getServerDate();
//		String date = formatDate(dt, "yyyy-MM-dd");
//		
//		UserProfile prof = SessionContext.getProfile();
//		String userid = (prof != null? prof.getUserId() : "");
		String date = formatDate(dt, "yyyy-MM-dd");
		
		return date + userid;
	}
	
	public static boolean isCollectionCreated(String collectionid) throws Exception {
		boolean flag = false;
		
		DBContext ctx = new DBContext("clfc.db");
		try {
			flag = isCollectionCreatedImpl(ctx, collectionid);
		} catch (Exception e) {
			throw e;
		} finally {
			ctx.close();
		}
		
		return flag;
	}
	
	private static boolean isCollectionCreatedImpl(DBContext clfcdb, String collectionid) throws Exception {
		DBCollectionGroup dbcg = new DBCollectionGroup();
		dbcg.setDBContext(clfcdb);
		dbcg.setCloseable(false);
		
		return dbcg.isCollectionCreatedByCollectionid(collectionid);
	}
	
	public static String getBillingid() throws Exception {
		Date dt = Platform.getApplication().getServerDate();
		String date = "";
		if (dt != null) {
			date = formatDate(dt, "yyyy-MM-dd");
		}
		
		UserProfile prof = SessionContext.getProfile();
		String collectorid = (prof != null? prof.getUserId() : "");
		return getBillingid(collectorid, date);
	}
	
	public static String getBillingid(String collectorid, String date) throws Exception {
		String billingid = "";
		DBContext ctx = new DBContext("clfc.db");
		DBSystemService sysSvc = new DBSystemService();
		sysSvc.setDBContext(ctx);
		sysSvc.setCloseable(false);
		
		try {
			billingid = sysSvc.getBillingid(collectorid, date);
		} catch (Exception e) {
			throw e;
		} finally {
			ctx.close();
		}
		
		return billingid;
	}
	
	public static Map renewTracker() {
		String prefix = getPrefix();
		return renewTracker(prefix);
	}

	public static Map renewTracker(String prefix) {
		Date date = Platform.getApplication().getServerDate();
		
		return renewTracker(prefix, date);
//		AppSettingsImpl settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
//		Date dt = Platform.getApplication().getServerDate();
//		if (dt == null) return new HashMap();
//		
//		String date = formatDate(dt, "yyMMdd");
//		String trackerid = "TRCK" + date + UUID.randomUUID().toString();
//
//		String key = prefix + "trackerid";
//		Map map = new HashMap();
//		map.put(key, trackerid);
//		
//		return map;
//		settings.put(key, trackerid);
		
//		settings.put("tracker_owner", userid);
//		settings.put("tracker_date", ApplicationUtil.formatDate(dt, "yyyy-MM-dd"));
	}
	
	public static Map renewTracker(Date date) {
		String prefix = getPrefix(date);
		return renewTracker(prefix, date);
	}
	
	public static Map renewTracker(String prefix, Date date) {
		if (date == null) return new HashMap();
		
		String dt = formatDate(date, "yyMMdd");
		String trackerid = "TRCK" + dt + UUID.randomUUID().toString();
		
		String key = prefix + "trackerid";
		Map map = new HashMap();
		map.put(key, trackerid);
		
		return map;
	}
	
	
	public static Map renewCapture() {
		String prefix = getPrefix();
		return renewCapture(prefix);
	}
	
	public static Map renewCapture(String prefix) {
//		AppSettingsImpl settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
		Date dt = Platform.getApplication().getServerDate();
		if (dt == null) return new HashMap();
		
		String date = formatDate(dt, "yyMMdd");
		String captureid = "CP" + date + UUID.randomUUID().toString();
		
		String key = prefix + "captureid";
		Map map = new HashMap();
		map.put(key, captureid);
		
		return map;
//		settings.put(key, captureid);
	}

	public static void showLongMsg(String msg) {
		showLongMsg(msg, Platform.getActionBarMainActivity());
	}
	
	public static void showLongMsg(String msg, Activity activity) {
		if (activity == null) return;
		
		showLongMsg(msg, (Context) activity);
	}

	public static void showLongMsg(String msg, Context ctx) {
		if (ctx == null);
		
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}
	
	public static void showShortMsg(String msg) {
		showShortMsg(msg, Platform.getActionBarMainActivity());
	}
	
	public static void showShortMsg(String msg, Activity activity) {
		if (activity == null) return;
		
		showShortMsg(msg, (Context) activity);
	}
	
	public static void showShortMsg(String msg, Context ctx) {
		if (ctx == null) return;

		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	}
	
	private static void println(String msg) {
//		Log.i("[ApplicationUtil]", msg);
		println("[ApplicationUtil]", msg);
	}
	
	public static void println(String tag, String msg) {
		Log.i(tag, msg);
	}

	public static Map<String, Object> createMenuItem(String id, String text, String subtext, int iconid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("iconid", iconid);
		
		String t = "";
		if (text != null) t = text;
		map.put("text", t);
		
		String s = "";
		if (subtext != null) s = subtext;
		map.put("subtext", s);
		
		return map;
	}
	
	public static String getAppHost() {
		ApplicationImpl app = (ApplicationImpl) Platform.getApplication();
		return getAppHost(app.getNetworkStatus());
	}
	
	public static String getAppHost(int networkStatus) {		
		UIApplication app = Platform.getApplication();
		return ((AppSettingsImpl) app.getAppSettings()).getAppHost(networkStatus);
	}	
	
	public static String formatDate(Date date, String format) {
//		System.out.println("date -> "+date+" format -> "+format);
		if (date == null) return null;
		
		return new java.text.SimpleDateFormat(format).format(date);
	}
	
	public static Date parseDate(String date) {
		return java.sql.Date.valueOf(date);
	}
	
	public static boolean hasBilling() throws Exception {
		UserProfile prof = SessionContext.getProfile();
		String collectorid = (prof != null? prof.getUserId() : "");
		
		Date dt = Platform.getApplication().getServerDate();
		
		return hasBilling(collectorid, dt);
	}
	
	public static boolean hasBilling(String collectorid, Date date) throws Exception {
		DBContext ctx = new DBContext("clfc.db");
		DBCollectionGroup dbcg = new DBCollectionGroup();
		dbcg.setDBContext(ctx);

		String dt = formatDate(date, "yyyy-MM-dd");
		
		boolean flag = false;
		try {
//			flag = dbcg.hasCollectionGroupByCollector(collectorid);
			flag = dbcg.hasCollectionGroupByCollectorAndDate(collectorid, dt);
		} catch (Exception e) {
			throw e;
		} finally {
			ctx.close();
		}
		
		return flag;
	}
	
	public static boolean checkUnpostedCapture() throws Exception {
		UserProfile prof = SessionContext.getProfile();
		String collectorid = (prof != null? prof.getUserId() : "");
		
		return checkUnpostedCapture(collectorid);
	}
	
	public static boolean checkUnpostedCapture(String collectorid) throws Exception {
		DBContext ctx = new DBContext("clfccapture.db");
		DBCapturePayment dbcp = new DBCapturePayment();
		dbcp.setDBContext(ctx);
		dbcp.setCloseable(false);
		
		boolean flag = true;
		
		try {
//			flag = dbcp.hasPendingPayments(collectorid);
			flag = dbcp.hasForUploadPayment(collectorid);
		} catch (Exception e) {
			throw e;
		} finally {
			ctx.close();
		}
		
		return flag;
	}
	
	public static boolean checkUnposted() throws Exception {
		return checkUnposted(null);
	}
	
	public static boolean checkPendingVoidRequests(String itemid) throws Exception {
		boolean flag = false;
		
		DBContext clfcdb = new DBContext("clfc.db");
		
		try {
			flag = checkPendingVoidRequestsImpl(clfcdb, itemid);
			return flag;
		} catch (RuntimeException re) {
			throw re; 
		} catch (Exception e) {
			throw e; 
		} catch (Throwable t) {
			throw new Exception(t.getMessage(), t); 
		} finally {
			clfcdb.close();
		}
	}
	
	private static boolean checkPendingVoidRequestsImpl(DBContext clfcdb, String itemid) throws Exception {
		DBVoidService voidSvc = new DBVoidService();
		voidSvc.setDBContext(clfcdb);
		voidSvc.setCloseable(false);
		
		boolean flag = false;
		Map map = voidSvc.findVoidRequestByItemidAndState(itemid, "PENDING");
		if (map != null && !map.isEmpty()) {
			flag = true;
		}
		
		return flag;
	}
	
//	public static boolean xcheckUnpostedTracker() throws Exception  {
//		UserProfile prof = SessionContext.getProfile();
//		String collectorid = (prof == null)? "" : prof.getUserId();
//		return xcheckUnpostedTracker(collectorid);
//	}
//	
//	public static boolean xcheckUnpostedTracker(String collectorid) throws Exception {
//		boolean flag = false;
//
//		DBContext ctx = new DBContext("clfctracker.db");
//		DBLocationTracker trackerSvc = new DBLocationTracker();
//		trackerSvc.setDBContext(ctx);
//		trackerSvc.setCloseable(false);
//		
//		String date = Platform.getApplication().getServerDate().toString();
//		
//		try {
//			flag = trackerSvc.xhasLocationTrackerByCollectoridLessThanDate(collectorid, date);
//			
//			return flag;
//		} catch (RuntimeException re) {
//			throw re; 
//		} catch (Exception e) {
//			throw e; 
//		} catch (Throwable t) {
//			throw new Exception(t.getMessage(), t); 
//		} finally {
//			ctx.close();
//		}
//	}
	
	public static boolean checkUnposted(String itemid) throws Exception {
		boolean flag = false;
		
		DBContext paymentdb = new DBContext("clfcpayment.db");
		DBContext remarksdb = new DBContext("clfcremarks.db");
		DBContext clfcdb = new DBContext("clfc.db");
		try {
			flag = checkUnpostedImpl(paymentdb, remarksdb, clfcdb, itemid);
			return flag;
		} catch (RuntimeException re) {
			throw re; 
		} catch (Exception e) {
			throw e; 
		} catch (Throwable t) {
			throw new Exception(t.getMessage(), t); 
		} finally {
			paymentdb.close();
			remarksdb.close();
			clfcdb.close();
		}
	}
	
	private static boolean checkUnpostedImpl(DBContext paymentdb, DBContext remarksdb, DBContext clfcdb, String itemid) 
			throws Exception {
		boolean flag = false;

		DBPaymentService paymentSvc = new DBPaymentService();
		paymentSvc.setDBContext(paymentdb);
		paymentSvc.setCloseable(false);
		
		String collectorid = SessionContext.getProfile().getUserId();
		if (itemid.equals("") || itemid == null) {
			flag = paymentSvc.hasUnpostedPaymentsByCollectorAndItemid(collectorid, itemid);
		} else {
			flag = paymentSvc.hasUnpostedPaymentsByCollector(collectorid);
		}
		
		if (flag == true) return flag;
		
		DBRemarksService remarksSvc = new DBRemarksService();
		remarksSvc.setDBContext(remarksdb);
		remarksSvc.setCloseable(false);
		
		if (itemid.equals("") || itemid == null) {
			flag = remarksSvc.hasUnpostedRemarksByCollector(collectorid);
		} else {
			flag = remarksSvc.hasUnpostedRemarksByCollectorAndItemid(collectorid, itemid);
		}
		
		if (flag == true) return flag;
		

		DBCollectionSheet collectionSheet = new DBCollectionSheet();
		collectionSheet.setDBContext(clfcdb);
		collectionSheet.setCloseable(false);
		
		if (itemid.equals("") || itemid == null) {
			List<Map> list = collectionSheet.getUnremittedCollectionSheetsByCollector(collectorid);
			if (!list.isEmpty()) {
				String sql = "";
				String objid = "";
				Map map;
				for (int i=0; i<list.size(); i++) {
					map = (Map) list.get(i);
					
					objid = map.get("objid").toString();
					sql = "SELECT objid FROM payment WHERE parentid=? LIMIT 1";
					if (!paymentdb.getList(sql, new Object[]{objid}).isEmpty()) {
						flag = true;
						break;
					}
					
					sql = "SELECT objid FROM remarks WHERE objid=? LIMIT 1";
					if (!remarksdb.getList(sql, new Object[]{objid}).isEmpty()) {
						flag = true;
						break;
					}
				}
			}
		}
		
		return flag;
	}
	
	public static void resolvePaymentTimedifference(long timedifference) {

//		Calendar cal = Calendar.getInstance();
//		Calendar xcal = Calendar.getInstance();
//		
//		AppSettings settings = Platform.getApplication().getAppSettings();
//		Map map = settings.getAll();
//		if (map.containsKey("phonedate")) {
////			println("server date " + settings.getString("serverdate"));
//			cal.setTime(java.sql.Timestamp.valueOf(settings.getString("phonedate")));
//		}
//		 
//		long timemillis = cal.getTimeInMillis();
//        long xtimemillis = xcal.getTimeInMillis();
//        
//        long diff = timemillis - xtimemillis;
//        long timediff = diff;
//        
//        if (map.containsKey("timedifference")) {
//        	timediff = settings.getLong("timedifference");
//        }
//        
//        long newtimediff = timediff - diff;
//
//    	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        settings.put("phonedate", DATE_FORMAT.format(cal.getTime()));
//        settings.put("timedifference", newtimediff);
//        settings.put("timedifference", diff);
//        settings.put("phonedate", xcal.getTime().toString());
//		
		println("resolve timedifference");
		
        synchronized (PaymentDB.LOCK) {
        	SQLTransaction paymentdb = new SQLTransaction("clfcpayment.db");
        	try {
        		paymentdb.beginTransaction();
        		resolvePaymentTimedifference(paymentdb, timedifference);
        		paymentdb.commit();
        	} catch (Exception e) {
        		e.printStackTrace();
        	} finally {
        		paymentdb.endTransaction();
        	}
        }
        
        synchronized (CaptureDB.LOCK) {
        	SQLTransaction capturedb = new SQLTransaction("clfccapture.db");
        	try {
        		capturedb.beginTransaction();
        		resolveCapturePaymentTimedifference(capturedb, timedifference);
        		capturedb.commit();
        	} catch (Exception e) {
        		e.printStackTrace();
        	} finally {
        		capturedb.endTransaction();
        	}
        	
        }
	}
	

	private static void resolvePaymentTimedifference(SQLTransaction paymentdb, long timedifference) throws Exception {
		DBPaymentService paymentSvc = new DBPaymentService();
		paymentSvc.setDBContext(paymentdb.getContext());
		paymentSvc.setCloseable(false);
		
		Calendar cal = Calendar.getInstance();
		
		Map params = new HashMap();
		params.put("forupload", 0);
		List<Map> list = paymentSvc.getPaymentsByForupload(params);
		for (Map m : list) {
			long xtimedifference = 0L;
			if (m.containsKey("timedifference")) {
				xtimedifference = Long.parseLong(m.get("timedifference").toString());
			}
			
			long timemillis = cal.getTimeInMillis();
			if (m.containsKey("dtsaved")) {
				cal.setTime(java.sql.Timestamp.valueOf(m.get("dtsaved").toString()));
				timemillis = cal.getTimeInMillis();
			}
			
			long newtimemillis = timemillis + xtimedifference;
			newtimemillis -= timedifference;
			
			Timestamp timestamp = new Timestamp(newtimemillis);
			String sql = "UPDATE payment SET dtsaved='" + timestamp.toString() + "', timedifference=" + timedifference + " WHERE objid='" + m.get("objid").toString() + "'";
			paymentdb.execute(sql);
//			m.put("timedifference", timedifference);
			
//			m = paymentdb.find("SELECT * FROM payment WHERE objid='" + m.get("objid").toString() + "'");
//			println("pyt data2 " + m);
		}
	}
	
	private static void resolveCapturePaymentTimedifference(SQLTransaction capturedb, long timedifference) throws Exception {
		DBCapturePayment captureSvc = new DBCapturePayment();
		captureSvc.setDBContext(capturedb.getContext());
		captureSvc.setCloseable(false);

		Calendar cal = Calendar.getInstance();
		
		Map params = new HashMap();
		params.put("forupload", 0);
		List<Map> list = captureSvc.getPaymentsByForupload(params);
		for (Map m : list) {
			long xtimedifference = 0L;
			if (m.containsKey("timedifference")) {
				xtimedifference = Long.parseLong(m.get("timedifference").toString());
			}
			
			long timemillis = cal.getTimeInMillis();
			if (m.containsKey("dtsaved")) {
				cal.setTime(java.sql.Timestamp.valueOf(m.get("dtsaved").toString()));
				timemillis = cal.getTimeInMillis();
			}
			
			long newtimemillis = timemillis + xtimedifference;
			newtimemillis -= timedifference;
			
			Timestamp timestamp = new Timestamp(newtimemillis);
			String sql = "UPDATE capture_payment SET dtsaved='" + timestamp.toString() + "', timedifference=" + timedifference + " WHERE objid='" + m.get("objid").toString() + "'";
			capturedb.execute(sql);
			
			
//			println("cp data " + m);
//			m = capturedb.find("SELECT * FROM capture_payment WHERE objid='" + m.get("objid").toString() + "'");
//			println("cp data2 " + m);
			//m.put("timedifference", timedifference);
		}
	}
	

	void showUserInfoDialog(Context context) {
		
		UserProfile prof = SessionContext.getProfile();
		
		String username = (prof != null? prof.getUserName() : "");
		String name = (prof != null? prof.getFullName() : "");
		
		//println("username " + username + " name " + name); 
		
		final Dialog dialog = new Dialog(context);
//		dialog.setContentView(R.layout.dialog_userinfo);
//		dialog.setTitle("User Account");
//		
//		TextView tv_username = (TextView) dialog.findViewById(R.id.tv_username);
//		SpannableString content = new SpannableString(username.toLowerCase());
//		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
//		tv_username.setText(content);
//		
//		TextView tv_name = (TextView) dialog.findViewById(R.id.tv_name);
//		tv_name.setText(name);
//		
//		ImageView icon = (ImageView) dialog.findViewById(R.id.iv_icon);
//		icon.setImageResource(R.drawable.profile);
//		
//		Button btn_logout = (Button) dialog.findViewById(R.id.btn_logout);
//		btn_logout.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				println("logout");
//				ProgressDialog progressDialog = new ProgressDialog(context);
//				try {
//					dialog.dismiss();
//					new LogoutController((UIActionBarActivity) context, progressDialog).logout();
//				} catch (Throwable t) {
//					UIDialog.showMessage(t, UserInfoMenuActivity.this);
//				}
//			}
//		});
		
		dialog.show();
	}
	
	public static void showServerDateStatus(Context context) {

		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_datestatus);
		dialog.setTitle("Server Date Status");
		TextView tv_text = (TextView) dialog.findViewById(R.id.tv_text);
		String text = "Server Date is not synched.";
		if (Platform.getApplication().getIsDateSync() == true) {
			text = "Server Date is synched.";
		}
		tv_text.setText(text);
		 
		dialog.show();
	}
	
	private ApplicationUtil() {
	}
}
