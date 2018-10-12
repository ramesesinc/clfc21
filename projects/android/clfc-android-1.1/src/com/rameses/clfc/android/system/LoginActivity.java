package com.rameses.clfc.android.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.SettingsMenuActivity;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.BankDB;
import com.rameses.clfc.android.db.CollectionGroupDB;
import com.rameses.clfc.android.db.SegregationDB;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIAction;
import com.rameses.client.android.UIApplication;
import com.rameses.client.android.UIDialog;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.client.interfaces.UserSetting;
import com.rameses.client.services.LoginService;
import com.rameses.util.Base64Cipher;
import com.rameses.util.Encoder;

public class LoginActivity extends SettingsMenuActivity 
{
	private ProgressDialog progressDialog;
//	private LoanTrackerService service = new LoanTrackerService();
//	private Handler myhandler;
	private BankDB bankdb = new BankDB();
	private SegregationDB segregation = new SegregationDB();
	private CollectionGroupDB collectionGroup = new CollectionGroupDB();
	
	public boolean isCloseable() { return false; }

	@Override
	protected void onCreateProcess(Bundle savedInstanceState) {
		super.onCreateProcess(savedInstanceState);
		
		setTitle("CLFC Collection - ILS");
		setContentView(R.layout.template_footer);
		RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.rl_container);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.activity_login, rl_container, true);
		
		RelativeLayout login_container = (RelativeLayout) findViewById(R.id.login_container);
		inflater.inflate(R.layout.dialog_login, login_container, true);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		
	}
	
	protected void afterBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}
	
	protected void onStartProcess() {
		super.onStartProcess();

//		AppSettingsImpl sets = (AppSettingsImpl) Platform.getApplication().getAppSettings();
//		boolean flag = false;
//		if ("true".equals(sets.getDebugEnabled())) {
//			 flag = true;
//		}
//		Platform.setDebug(flag);
		//Platform.setDebug((Boolean) sets.get("debug_enabled")); 
//	     File dir = Environment.getExternalStorageDirectory();
//	     File file = new File(dir, "clfclog.txt");
//	     new UIDialog().showMessage("clfclog.txt file is exist -> "+file.exists());
//		setValue(R.id.login_username, "louie");
//		setValue(R.id.login_password, "2");
		requestFocus(R.id.login_username);		
		new UIAction(this, R.id.btn_login) {
			protected void onClick() {
				try {
					doLogin();
				} catch(Throwable t) {
					UIDialog.showMessage("[ERROR] " + t.getMessage());
				}
			}
		};
	}
	
	private void doLogin() {
//		DBContext ctx = new DBContext("clfc.db");
//		try {
//			String sql = "SELECT cs.borrower_name, cg.type, cgt.level"
//					+ " FROM collectionsheet cs"
//					+ " INNER JOIN collection_group cg ON cs.itemid = cg.objid"
//					+ " INNER JOIN collection_group_type cgt ON cg.type = cgt.type"
//					+ " ORDER BY cgt.level";
//			List list = ctx.getList(sql, new HashMap());
//			Map item = new HashMap();
//			for (int i = 0; i < list.size(); i++) {
//				item = (Map) list.get(i);
//				println("item " + item);
//			}
//		} catch (Throwable t) {
//			t.printStackTrace();
//			throw t;
//		} finally {
//			ctx.close();
//		}
//		
//		if (1==1) {
//			throw new RuntimeException("stopping");
//		}
		
		
		String username = getValueAsString(R.id.login_username);
		String password = getValueAsString(R.id.login_password);
		if (isEmpty(username)) {
			requestFocus(R.id.login_username);
			ApplicationUtil.showShortMsg("Username is required");
			return;
		} 
		
		if (isEmpty(password)) {
			requestFocus(R.id.login_password);
			ApplicationUtil.showShortMsg("Password is required");
			return;
		}
		
		progressDialog.setMessage("Logging in...");
		if (!progressDialog.isShowing()) progressDialog.show();
					
		println("before run action process");
//		if (myhandler == null) myhandler = new Handler();
//		myhandler.postAtTime(new ActionProcess(username, password), 300);
		new Thread(new ActionProcess(username, password)).start();
//		getHandler().post(new ActionProcess(username, password));
//		Platform.runAsync(new ActionProcess(username, password));
	}
	
	private Handler errorhandler = new Handler() {

		public void handleMessage(Message msg) {	
			System.out.println("Passing error handle");
			try { 
				Bundle data = msg.getData();			
				Object o = data.getSerializable("response"); 
				if (o instanceof Throwable) {
					Throwable t = (Throwable) o;
					ApplicationUtil.showShortMsg("[ERROR] " + t.getMessage());
					//ApplicationUtil.showShortMsg("[ERROR] " + t.getMessage(), LoginActivity.class);
				} else {
					ApplicationUtil.showShortMsg("[ERROR] " + o);	
				}
			} finally { 
				if (progressDialog.isShowing()) progressDialog.dismiss(); 
			}
		}
	}; 
	
	private Handler successhandler = new Handler() {

		public void handleMessage(Message msg) {
			System.out.println("Passing success handler");
			if (progressDialog.isShowing()) progressDialog.dismiss();
			
			Bundle data = msg.getData();			
			Map map = (Map) data.getSerializable("response");
			
//			SessionContext sess = AppContext.getSession();
//			SessionProvider provider = sess.getProvider();
//			Gson gson = new Gson();
//			String env = gson.toJson(provider.getEnv());
//			println("env: " + env);
//			String provider = gson.toJson(sess.getProvider());
//			println("provider " + provider);
			//map.put("provider", provider);
					
			UIApplication app = Platform.getApplication();
			app.getAppSettings().putAll( map );
			app.restartSuspendTimer();
			app.resumeAppLoader();
			
			
		}
	};
	
	private void println(Object msg) {
//		System.out.println("[LoginActivity] " + str);
		ApplicationUtil.println("LoginActivity", msg.toString());
	}
	
	private class ActionProcess implements Runnable 
	{
		private String username;
		private String password;
		
		public ActionProcess(String username, String password) {
			this.username = username;
			this.password = password;
		} 
		
		private void disconnectedLogin(String user, String pwd, LoginService loginSvc) throws Exception {
			boolean validatepwd = true;
//			println("[disconnectedLogin] sessionid " + SessionContext.getSessionId());
			UserProfile prof = SessionContext.getProfile();
//			println("profile " + prof);
//			println("full name: " + prof.getFullName());
//			println("job title: " + prof.getJobTitle());
//			println("name: " + prof.getName());
//			println("userid: " + prof.getUserId());
//			println("username: " + prof.getUserName());
			
			String xuid = (prof != null? prof.getUserId() : "");
			if (xuid == null || "".equals(xuid)) {
				throw new Exception("No session stored for this terminal.");
			}
			
			String uid = (prof != null? prof.getUserName() : "");
			
			AppSettingsImpl settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
			Map map = settings.getAll();
			
			println("[disconnectedLogin] uid " + uid);
			println("[disconnectedLogin] has encpwd " + map.containsKey("encpwd"));
			
//			int networkStatus = ApplicationUtil.getNetworkStatus();
//			if (networkStatus < 3 || (!uid.equalsIgnoreCase(user) || !map.containsKey("encpwd"))) {
//				validatepwd = false;
//			}
			
//			int networkStatus = ApplicationUtil.getNetworkStatus();
//			if (networkStatus == 3 || (uid.equalsIgnoreCase(user) && map.containsKey("encpwd"))) {
//				validatepwd = true;
//			}
			String encpwd = Encoder.MD5.encode(pwd, user);
			String str_encpwd = map.get("encpwd").toString();
			if (!str_encpwd.equals(encpwd)) {
                throw new Exception("Access denied. Please check your username and password"); 
			}
//			if (validatepwd == false) {
//				loginSvc.login(user, pwd);
//			} else {
//				String encpwd = Encoder.MD5.encode(pwd, user);
//				String str_encpwd = map.get("encpwd").toString();
//				if (!str_encpwd.equals(encpwd)) {
//	                throw new Exception("Access denied. Please check your username and password"); 
//				}
//			} 
		}
		
		public void run() { 
			println("login starting");
			boolean success = false;			
			Bundle data = new Bundle();
			Message msg = errorhandler.obtainMessage();
			
			try {
				LoginService loginSvc = new LoginService();
				
				String sessionid = SessionContext.getSessionId();
				println("sessionid " + sessionid);
				int networkStatus = ApplicationUtil.getNetworkStatus();
				if (networkStatus < 3) {
					println("before login service");
					loginSvc.login(username, password);
					println("after login service");
				} else if (networkStatus == 3) {
					disconnectedLogin(username, password, loginSvc);
				}
				println("after login");
				
//				if (1==1) {
//					throw new RuntimeException("stopping login");
//				}
												
				UserSetting userSets = SessionContext.getSettings();
				String onlinehost 	= userSets.getOnlineHost();
				String offlinehost 	= userSets.getOfflineHost();
				int sessiontimeout 	= userSets.getSessionTimeout();
				int uploadtimeout 	= userSets.getUploadDelay();
				int trackertimeout 	= userSets.getTrackerDelay();
				int port 			= userSets.getPort();

				
				HashMap<String, Object> map = new HashMap();
				if (port > 0) map.put("host_port", port);
				if (onlinehost != null) map.put("host_online", onlinehost);
				if (offlinehost != null) map.put("host_offline", offlinehost);
				if (uploadtimeout > 0) map.put("timeout_upload", uploadtimeout);				
				if (sessiontimeout > 0) map.put("timeout_session", sessiontimeout);
				if (trackertimeout > 0) map.put("timeout_tracker", trackertimeout);
 
				UserProfile profile = SessionContext.getProfile();
				//List list = (List) SessionContext.getProfile().get("BANKS");
				//List list = (List) profile.get

//				StringBuilder sb = new StringBuilder();
				List<Map> sqlParams = new ArrayList<Map>();
				Map xdata = new HashMap();
				
				List<Map> list = (List<Map>) profile.get("BANKS");
				if (list != null && !list.isEmpty()) {
					
					String sql = "delete from bank;";

					xdata = new HashMap();
					xdata.put("type", "delete");
					xdata.put("sql", sql);
					
					sqlParams.add( xdata );
					
//					synchronized (MainDB.LOCK) {
//						DBContext ctx = new DBContext("clfc.db");
//						try {
//							ctx.delete("bank", new HashMap(), null);
//						} catch (Throwable t) {
//							t.printStackTrace();
//						} finally {
//							ctx.close();
//						}
//					}
					
					String objid = null, name = null;
					for (Map item : list) {
						if (item.containsKey("objid")) {
							objid = item.get("objid").toString();
							
							name = ""; 
							if (item.containsKey("name")) {
								name = item.get("name").toString();
							}
							
//							sql = "insert or ignore into bank(`objid`,`name`) values ('" + objid + "','" + name + "');";
							sql = "insert into bank(`objid`,`name`) values ('" + objid + "','" + name + "');";
							
							xdata = new HashMap();
							xdata.put("type", "insert");
							xdata.put("sql", sql);
							
							sqlParams.add( xdata );
							
							/*
							Map m = bankdb.findById( objid );
							if (m == null || m.isEmpty()) {
								sql = "insert or ignore into bank(`objid`,`name`) values ('" + objid + "','" + name + "');";
							} else {
								sql = "update bank set name='" + name + "' where objid='" + objid + "';";
							}
							*/
							
//							if (sql != null && !sql.trim().equals("")) {
//								sb.append( sql );
//							}
						} 
					}
						
				
//					int size = list.size();
//					Map params = new HashMap();
//					MapProxy proxy;
//					String objid;
//					for (int i=0; i<size; i++) {
//						proxy = new MapProxy((Map) list.get(i));
//						objid = proxy.getString("objid");
//						
//						params = new HashMap();
//						params.put("name", proxy.getString("name"));
//						synchronized (MainDB.LOCK) {
//							DBContext ctx = new DBContext("clfc.db");
//							try {
//								Map item = ctx.find("SELECT * FROM bank WHERE objid=?", new Object[]{ objid });
//								if (item == null) {
//									params.put("objid", objid);
//									ctx.insert("bank", params);	
//								} else {
//									ctx.update("bank", params, "objid='" + objid + "'");
//								}
//							} catch (Throwable t) {
//								t.printStackTrace();
//							} finally {
//								ctx.close();
//							}
//						}
//					}
				} 
				
				list = (List<Map>) profile.get("SEGREGATION");
				if (list != null && !list.isEmpty()) {
					
					String sql = "delete from segregation;";
					
					xdata = new HashMap();
					xdata.put("type", "delete");
					xdata.put("sql", sql);
					
					sqlParams.add( xdata );
					
//					sb.append(  );
//					synchronized (MainDB.LOCK) {
//						DBContext ctx = new DBContext("clfc.db");
//						try {
////							ctx.delete("segregation", new HashMap(), null);
//							ctx.execute("delete from segregation");
//						} catch (Throwable t) {
//							t.printStackTrace();
//						} finally {
//							ctx.close();
//						}
//					}
					 
					String objid = null, name = null;
					for (Map item : list) {
						if (item.containsKey("objid")) {
							objid = item.get("objid").toString();
							
							name = "";
							if (item.containsKey("name")) {
								name = item.get("name").toString();
							}
							
//							sql = "insert or ignore into segregation(`objid`,`name`) values ('" + objid + "','" + name + "');";
							sql = "insert into segregation(`objid`,`name`) values ('" + objid + "','" + name + "');";
							
							xdata = new HashMap();
							xdata.put("type", "insert");
							xdata.put("sql", sql);
							
							sqlParams.add( xdata );
							
							/*
							Map m = segregation.findById( objid );
							if (m == null || m.isEmpty()) {
								sql = "insert or ignore into segregation(`objid`,`name`) values ('" + objid + "','" + name + "');";
							} else {
								sql = "update segregation set name='" + name + "' where objid='" + objid + "';";
							}
							*/
							
//							if (sql != null && !sql.trim().equals("")) {
//								sb.append( sql );
//							}
						}
					}

//					int size = list.size();
//					Map params = new HashMap();
//					MapProxy proxy;
//					String objid = "";
//					for (int i=0; i<size; i++) {
//						proxy = new MapProxy((Map) list.get(i));
//						objid = proxy.getString("objid");
//						
//						params = new HashMap();
//						params.put("name", proxy.getString("name"));
//						synchronized (MainDB.LOCK) {
//							DBContext ctx = new DBContext("clfc.db");
//							try {
//								Map item = ctx.find("SELECT * FROM segregation WHERE objid=?", new Object[]{ objid });
//								if (item == null) {
//									params.put("objid", objid);
//									ctx.insert("segregation", params);	
//								} else {
//									ctx.update("segregation", params, "objid='" + objid + "'");
//								}
//							} catch (Throwable t) {
//								t.printStackTrace();
//							} finally {
//								ctx.close();
//							}
//						}
//					}
				}
				
				if (sqlParams.size() > 0) {
					SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
					try {
						appdb.beginTransaction();
						ApplicationDBUtil.executeSQL( appdb, sqlParams );
//						appdb.execSQL( sb.toString() );
						appdb.setTransactionSuccessful();
					} catch (Throwable t) {
						t.printStackTrace();
					} finally {
						appdb.endTransaction();
					} 
				}
				
				AppSettingsImpl settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
				String collector_state = settings.getCollectorState();

				if (networkStatus < 3) {
//					ApplicationUtil.println("LoginActivity", "result " + loginSvc.getResult());
					String result = new Base64Cipher().encode(loginSvc.getResult());
//					ApplicationUtil.println("LoginActivity", "result " + result);
					map.put("result", result);
					map.put("encpwd", loginSvc.getEncpwd());
				}
				
//				Date dt = Platform.getApplication().getServerDate();
//				String str_dt = settings.getString("tracker_date", null);
//				if (str_dt != null) {
//					Date xdt = java.sql.Date.valueOf(str_dt);
//					if (dt.compareTo(xdt) != 0) {
//						ApplicationUtil.renewTracker();
//						ApplicationUtil.renewCapture();
//					}
//				}
				
				UserProfile prof = SessionContext.getProfile();
				String userid = (prof == null)? "" : prof.getUserId();
				System.out.println("userid " + userid);
				
//				DBContext ctx = new DBContext("clfc.db");
//				DBCollectionGroup dbcg = new DBCollectionGroup();
//				dbcg.setDBContext(ctx);
//				dbcg.setCloseable(false);
				
				String date = ApplicationUtil.formatDate(Platform.getApplication().getServerDate(), "yyyy-MM-dd");
				
				boolean hasBilling = collectionGroup.hasCollectionGroupByCollectoridAndDate( userid, date );
				
//				String collectorid = SessionContext.getProfile().getUserId();
//				boolean hasBilling = false;
//				try {
//					hasBilling = dbcg.hasCollectionGroupByCollectorAndDate(collectorid, date);
//				} catch (Throwable t) {
//					throw t;
//				} finally {
//					ctx.close();
//				}
				
//				System.out.println("has billing " + hasBilling);
//				String prefix = ApplicationUtil.getPrefix();
				
				String trackerid = settings.getTrackerid();
//				String xtrackerid = "";
//				System.out.println("trackerid " + trackerid);
				if ((trackerid == null || "".equals(trackerid)) || hasBilling == false) {
					Map tracker = ApplicationUtil.renewTracker();
					if (tracker != null && !tracker.isEmpty()) {
//						xtrackerid = tracker.get(prefix + "trackerid").toString();
						map.putAll(tracker);
					}
				} 
				
				println("trackerid " + trackerid);
				map.put("collector_trackerid", trackerid);
//				System.out.println("trackerid: " + trackerid);
//				System.out.println("xtrackerid: " + xtrackerid);

				String captureid = settings.getCaptureid();
//				String xcaptureid = "";
//				System.out.println("captureid " + captureid);
				if ((captureid == null || "".equals(captureid)) || hasBilling == false) {
					Map capture = ApplicationUtil.renewCapture();
					if (capture != null && !capture.isEmpty()) {
//						xcaptureid = capture.get(prefix + "captureid").toString();
						map.putAll(capture);
					} 
				} 
//				System.out.println("captureid: " + captureid);
//				System.out.println("xcaptureid: " + xcaptureid);

				if ("logout".equals(collector_state)) {
					map.put("collector_state", "login");
				}
				
				/* Resolve cancelled billing*/
				
//				if (networkStatus < 3) {
//					resolveCancelledBilling();
//				}
				
				
//					
//					String prefix = "";
//					
//					ApplicationUtil.renewTracker();
//					ApplicationUtil.renewCapture();
					/*
					String date = ApplicationUtil.formatDate(Platform.getApplication().getServerDate(), "yyMMdd");
					String userid = SessionContext.getProfile().getUserId();
					map.put("captureid", "CP" + date + UUID.randomUUID().toString());
					map.put("trackerid", "TRCK" + date + UUID.randomUUID().toString());
					map.put("tracker_owner", userid);
					map.put("tracker_date", ApplicationUtil.formatDate(Platform.getApplication().getServerDate(), "yyyy-MM-dd"));
					*/
					
//					Location location = NetworkLocationProvider.getLocation();
//					double lng = (location != null? location.getLongitude() : 0.00);
//					double lat = (location != null? location.getLatitude() : 0.00);
//					
//					Map params = new HashMap();
//					params.put("trackerid", map.get("trackerid").toString());
//					params.put("terminalid", TerminalManager.getTerminalId());
//					params.put("userid", userid);
//					params.put("lng", lng);
//					params.put("lat", lat);
//					Map response = service.start(params);
//					
//					if (response.containsKey("response") && response.get("response").toString().toLowerCase().equals("success")) {
//						map.put("trackerid", response.get("trackerid").toString());
//						map.put("tracker_owner", userid);
//					}
//				}
				
//				System.out.println("date " + Platform.getApplication().getServerDate().toString());
//				AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(Platform.getApplication().getServerDate());
//				am.setTime(cal.getTimeInMillis());
//				
//				System.out.println("time " + cal.getTime());
				
				data.putSerializable("response", map);
				msg = successhandler.obtainMessage();
				success = true;
				
			} catch (Throwable e) {
				data.putSerializable("response", e);
				e.printStackTrace();
			} finally { 
				msg.setData(data);
				if (success) {
					successhandler.sendMessage(msg);
				} else {
					errorhandler.sendMessage(msg);
				}
			}
		}
		
		/** removed because there is already a thread resolving cancelled billings
		private void resolveCancelledBilling() throws Exception {
			
			StringBuilder sb = new StringBuilder();
			List<Map> list = collectionGroup.getAllCollectionBilling();
			
			for (Map item : list) {
				
				Map result 
				LoanPostingService service = new LoanPostingService();
			}
			
		}
		
		private void xresolveCancelledBilling() throws Exception {
			SQLTransaction clfcdb = new SQLTransaction("clfc.db");
			 
			synchronized (MainDB.LOCK) {
				try {
					clfcdb.beginTransaction();
					
					resolveCancelledBillingImpl(clfcdb);
					
					clfcdb.commit();
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				} finally {
					clfcdb.endTransaction();
				}
			}
			
		}
		
		private void xresolveCancelledBillingImpl(SQLTransaction clfcdb) throws Exception {
			UserProfile prof = SessionContext.getProfile();
			String collectorid = (prof != null? prof.getUserId() : null);
			
			//String sql = "SELECT * FROM collection_group WHERE collectorid=?";
			String sql = "SELECT * FROM collection_group";
//			List<Map> list = clfcdb.getList(sql, new Object[]{collectorid});
			List<Map> list = clfcdb.getList(sql);
			int size = list.size();
			
			LoanPostingService svc = new LoanPostingService();
			
			Map item = new HashMap(), result = new HashMap();
			Boolean iscancelled = false;
			for (int i=0; i<size; i++) {
				item = (Map) list.get(i);
				
				result = svc.checkCancelledBilling(item);
				if (result != null && result.containsKey("iscancelled")) {
					iscancelled = Boolean.valueOf(result.get("iscancelled").toString());
					if (iscancelled == true) {
						removeCancelledBilling(clfcdb, item);
					}
				}
			}
		}
		
		private void xremoveCancelledBilling(SQLTransaction clfcdb, Map billing) throws Exception {
			String itemid = MapProxy.getString(billing, "objid");
			removeCancelledBillingImpl(clfcdb, itemid);
			
//			if (1==1) {
//				throw new RuntimeException("trying to remove cancelled billing");
//			}
		}
		
		private void xremoveCancelledBillingImpl(SQLTransaction clfcdb, String itemid) throws Exception {
			DBCollectionSheet dbcs = new DBCollectionSheet();
			dbcs.setDBContext(clfcdb.getContext());
			dbcs.setCloseable(false);
			
			List list = dbcs.getCollectionSheetsByItem(itemid);
			Map item = new HashMap();
			String objid = "";
			for (int i=0; i<list.size(); i++) {
				item = (Map) list.get(i);
				objid = "";
				if (item.containsKey("objid")) {
					objid = item.get("objid").toString();
				}
				
				if (objid != null && !"".equals(objid)) {
					clfcdb.delete("amnesty", "parentid=?", new Object[]{objid});
					clfcdb.delete("collector_remarks", "parentid=?", new Object[]{objid});
					clfcdb.delete("followup_remarks", "parentid=?", new Object[]{objid});
				}
			}

			clfcdb.delete("remarks", "itemid=?", new Object[]{itemid});
			clfcdb.delete("notes", "itemid=?", new Object[]{itemid});
			clfcdb.delete("void_request", "itemid=?", new Object[]{itemid});
			clfcdb.delete("payment", "itemid=?", new Object[]{itemid});
			clfcdb.delete("collectionsheet", "itemid=?", new Object[]{itemid});
			clfcdb.delete("specialcollection", "objid=?", new Object[]{itemid});
			clfcdb.delete("collection_group", "objid=?", new Object[]{itemid});
		}
		*/
	}
}
