package com.rameses.clfc.android.main;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.ControlActivity;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.BankDB;
import com.rameses.clfc.android.db.SystemDB;
import com.rameses.client.android.AppSettings;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIAction;
import com.rameses.client.android.UIDialog;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.util.MapProxy;

public class CapturePaymentActivity extends ControlActivity 
{
	private ApplicationImpl app;
	private AppSettingsImpl settings;
	private LayoutInflater inflater;
	
	private Spinner bankSpinner;
	private Spinner optionSpinner;
	private String option = "cash";
	
	private RelativeLayout rl_check;
	private EditText et_checkno;
	private EditText et_checkdate;
	
	private Map bank;
	private List<Map> bankSrc;
	private List<String> banks = new ArrayList<String>();
	
	private int year;
	private int month;
	private int day;
	
	private EditText et_borrower;
	private EditText et_amount;
	private EditText et_paidby;
	
	private String borrower;
	private String checkno;
	private String checkdate;
	private String amount;
	private String paidby;
	private String objid;
	
	private BankDB bankdb = new BankDB();
	private SystemDB systemdb = new SystemDB();
	
	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	@Override
	protected void onCreateProcess(Bundle savedInstanceState) {
		setTitle("CLFC Collection - ILS");
		setContentView(R.layout.template_footer);
		
		RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.rl_container);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.activity_capturepayment, rl_container, true);

		Date dt2 = Platform.getApplication().getServerDate();
		String dt = "";
		if (dt2 != null) {
			dt = ApplicationUtil.formatDate(dt2, "yyMMdd");
		}
		objid = "CPD" + dt + UUID.randomUUID().toString();
		app = (ApplicationImpl) Platform.getApplication();
		settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
		
		rl_check = (RelativeLayout) findViewById(R.id.rl_check);
		et_checkno = (EditText) findViewById(R.id.et_payment_checkno);
		et_checkno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					String t = et_checkno.getText().toString();
					et_checkno.setSelection(0, t.length());
				}
			}
		});
		
		et_checkdate = (EditText) findViewById(R.id.et_payment_checkdate);
		et_checkdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					String t = et_checkdate.getText().toString();
					et_checkdate.setSelection(0, t.length());
				}
			}
		});
		
		et_borrower = (EditText) findViewById(R.id.et_borrower);
		et_borrower.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					String t = et_borrower.getText().toString();
					et_borrower.setSelection(0, t.length());
				}
			}
		});
				
		et_amount = (EditText) findViewById(R.id.et_payment_amount);
		et_amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					String t = et_amount.getText().toString();
					et_amount.setSelection(0, t.length());
				}
			}
		});
		
		et_paidby = (EditText) findViewById(R.id.et_payment_paidby);
		et_paidby.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					String t = et_paidby.getText().toString();
					et_paidby.setSelection(0, t.length());
				}
			}
		});
				
		optionSpinner = (Spinner) findViewById(R.id.payment_option);
		List<String> options = new ArrayList<String>();
		options.add("cash");
		options.add("check");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
         
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		         
		optionSpinner.setAdapter(dataAdapter);
		optionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
//				System.out.println("item " + parent.getItemAtPosition(position).toString());
				option = parent.getItemAtPosition(position).toString();
				loadCheckUI();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		bankSrc = new ArrayList<Map>();
		try {
			bankSrc = bankdb.getBanks();	
		} catch (Throwable t) {
			t.printStackTrace();
		}

//		synchronized (MainDB.LOCK) {
//			DBContext ctx = new DBContext("clfc.db");
//			DBBankService bankSvc = new DBBankService();
//			bankSvc.setDBContext(ctx);
//			bankSvc.setCloseable(false);
//			try {
//				bankSrc = bankSvc.getBanks();
//			} catch (Throwable t) {
//				t.printStackTrace();
//			} finally {
//				ctx.close();
//			}
//		} 
		
		if (bankSrc != null && !bankSrc.isEmpty()) {
			int size = bankSrc.size();
			Map item;
			for (int i=0; i<size; i++) {
				item = (Map) bankSrc.get(i);
				
				banks.add(MapProxy.getString(item, "name"));				
			} 
		}	
		bankSpinner = (Spinner) findViewById(R.id.spinner_bank);
//		ArrayAdapter<Map> bankAdapter = new ArrayAdapter<Map>(this, android.R.layout.simple_spinner_item, bankSrc);
		ArrayAdapter<String> bankAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, banks);
         
		bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		         
		bankSpinner.setAdapter(bankAdapter);
		bankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				bank = (Map) bankSrc.get(position);
//				System.out.println("item " + parent.getItemAtPosition(position).toString());
//				option = parent.getItemAtPosition(position).toString();
//				loadCheckUI();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		final Calendar c = Calendar.getInstance();
		c.setTime(Platform.getApplication().getServerDate());
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		Button btn_checkdate = (Button) findViewById(R.id.btn_checkdate);
		btn_checkdate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DatePickerDialog(CapturePaymentActivity.this, datePickerListener, year, month, day).show();
			}
		});
		
		new UIAction(this, R.id.btn_ok) {
			protected void onClick() {
				try {
					doSavePayment();
				} catch(Throwable t) {
					t.printStackTrace();
					UIDialog.showMessage("[ERROR] " + t.getMessage()); 
				}
			}
		};
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			
			String m = (((month+1)+"").length() < 2? "0" : "") + (month+1);
			StringBuilder sb = new StringBuilder();
			sb.append(year + "-" + m + "-" + day);
			et_checkdate.setText(sb);
		}
	};
	
	@Override
	protected void onStartProcess() {
		super.onStartProcess();

		loadCheckUI();
		et_borrower.requestFocus();
		
	}
	
	private void loadCheckUI() {
		rl_check.setVisibility(View.GONE);
		if ("check".equals(option)) {
			rl_check.setVisibility(View.VISIBLE);
			et_checkno.setText("");
			et_checkdate.setText("");
			bank = new HashMap();
			bankSpinner.setSelection(0);
			
			et_checkno.requestFocus();
		}
	}
	
	private void doSavePayment() {
		borrower = et_borrower.getText().toString();
		if ("".equals(borrower)) {
			ApplicationUtil.showShortMsg("Borrower is required.");
			et_borrower.requestFocus();
			return;
		}
		
		if ("".equals(option)) {
			ApplicationUtil.showShortMsg("Option is required.");
			return;
		}
		
		checkno = "";
		checkdate = "";
		if ("check".equals(option)) {
			checkno = et_checkno.getText().toString();
			if ("".equals(checkno)) {
				ApplicationUtil.showShortMsg("Check no. is required.");
				et_checkno.requestFocus();
				return;
			}
			
			checkdate = et_checkdate.getText().toString();
			if ("".equals(checkdate)) {
				ApplicationUtil.showShortMsg("Check date is required.");
				et_checkdate.requestFocus();
				return;
			}
		}		
		
		amount = et_amount.getText().toString();
		if ("".equals(amount)) {
			ApplicationUtil.showShortMsg("Amount is required.");
			et_amount.requestFocus();
			return;
		}
		
		paidby = et_paidby.getText().toString();
		if ("".equals(paidby)) {
			ApplicationUtil.showShortMsg("Paid by is required.");
			et_paidby.requestFocus();
			return;
		}

		UIDialog dialog = new UIDialog(this) {
			public void onApprove() {
				onApproveImpl();
			}
		};
		String message = "Borrower: " + borrower;
		message += "\nAmount Paid: " + amount.toString();
		message += "\n\nEnsure that all information are correct. Continue?";
		dialog.confirm(message);
	}
	
	private void println(String msg) {
		Log.i("CapturePaymentActivity", msg);
	}
	
	private void onApproveImpl() {
		getHandler().post(new Runnable() {
			public void run() {
				
				try {
					runImpl();
					sendBroadcast(new Intent("rameses.clfc.CAPTURE_START_SERVICE"));
					finish();
				} catch (Throwable t) {
					t.printStackTrace();
					UIDialog.showMessage(t, CapturePaymentActivity.this);
				}
				
//				synchronized (CaptureDB.LOCK) {
//					DBContext clfcdb = new DBContext("clfc.db");
//					SQLTransaction txn = new SQLTransaction("clfccapture.db");
//					
//					try {
//						txn.beginTransaction();
//						execPayment(txn, clfcdb);
//						txn.commit();
//						app.captureDateResolverSvc.start();
//						finish();
//					} catch (Throwable t) {
//						t.printStackTrace();
//						UIDialog.showMessage(t, CapturePaymentActivity.this);
//					} finally {
//						txn.endTransaction();
//						clfcdb.close();
//					}
//				}
			}
			
			private void runImpl() throws Exception {
				
				UserProfile profile = SessionContext.getProfile();
				if (profile == null) {
					throw new RuntimeException("Collector is required.");
				}
				
				Date dt = Platform.getApplication().getServerDate();
				if (dt == null) {
					throw new RuntimeException("Date is required.");
				}

				String collectorid = profile.getUserId();
				String date = ApplicationUtil.formatDate(dt, "yyyy-MM-dd");
				
				String captureid = settings.getCaptureid();
				if (captureid == null) {
					captureid = "";
				}

				String billingid = systemdb.getBillingid( collectorid, date );
				if (billingid == null) {
					billingid = "";
				}
				
				String trackerid = settings.getTrackerid();
				if (trackerid == null) {
					trackerid = "";
				}
				
				String lng = "0", lat = "0";
				Location location = NetworkLocationProvider.getLocation();
				if (location != null) {
					lng = location.getLongitude() + "";
					lat = location.getLatitude() + "";
				}
				
				Map params = new HashMap();
				params.put("objid", objid);
				params.put("captureid", captureid);
				params.put("state", "PENDING");
				params.put("billingid", billingid);
				params.put("txndate", date);
				params.put("borrowername", borrower);
				params.put("amount", new BigDecimal( amount ));
				params.put("payoption", option);
				params.put("paidby", paidby);
				params.put("dtpaid", dt.toString());
				params.put("lng", lng);
				params.put("lat", lat);
				params.put("type", "CAPTURE");
				params.put("collector_objid", profile.getUserId());
				params.put("collector_name", profile.getFullName());
				params.put("trackerid", trackerid);
				
				if ("check".equals(option)) {
					params.put("bank_objid", bank.get("objid").toString());
					params.put("bank_name", bank.get("name").toString());
					params.put("check_no", checkno);
					params.put("check_date", java.sql.Date.valueOf(checkdate));
				}
				
				
				params.put("forupload", 0);
				
				Calendar cal = Calendar.getInstance();
				Date phonedate = new Timestamp( cal.getTimeInMillis() );
				params.put("dtsaved", phonedate.toString());
				
				AppSettings settings = Platform.getApplication().getAppSettings();
				Map map = settings.getAll();
				
				long timedifference = 0L;
				if (map.containsKey("timedifference")) {
					timedifference = settings.getLong("timedifference");
				}
				
				params.put("timedifference", timedifference);

				String sql = ApplicationDBUtil.createInsertSQLStatement("capture_payment", params);
				
				SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
				try {
					capturedb.beginTransaction();
					capturedb.execSQL( sql );
					capturedb.setTransactionSuccessful();
				} catch (Exception e) {
					throw e;
				} finally {
					capturedb.endTransaction();
				}
				
//				String sql = "insert or ignore into capture_payment(`objid`,`captureid`,`state`,`billingid`,`txndate`";
//				sql += ",`borrowername`,`amount`,`payoption`,`paidby`,`lng`,`lat`,`type`,`collector_objid`,`collector_name`,`trackerid`";
//				sql += ",`forupload`,`dtsaved`,`timedifference`";
//				
//				if ("check".equals(option)) {
//					sql += ",`bank_objid`,`bank_name`,`check_no`,`check_date`";
//				}
//				sql += ") values (";
//				
//				//objid
//				sql += "'" + objid + "'";
//				
//				//captureid
//				String captureid = settings.getCaptureid();
//				if (captureid == null || captureid.trim().equals("")) {
//					captureid = "";
//				}
//				sql += ",'" + captureid + "'";
//				
//				//state
//				sql += ",'PENDING'";
//				
//				//billingid
//				String billingid = systemdb.getBillingid( collectorid, date );
//				if (billingid == null || billingid.trim().equals("")) {
//					billingid = "";
//				}
//				sql += ",'" + billingid + "'";
//				
//				//txndate
//				sql += ",'" + date + "'";
//				
//				//borrower
//				sql += ",'" + borrower + "'";
//				
//				//amount
//				sql += "," + new BigDecimal( amount );
//				
//				//payoption
//				sql += ",'" + option + "'";
//				
//				//paidby
//				sql += ",'" + paidby + "'";
//				
//				//dtpaid
//				sql += ",'" + dt.toString() + "'";
//
//				String lng = "0", lat = "0";
//				Location location = NetworkLocationProvider.getLocation();
//				if (location != null) {
//					lng = location.getLongitude() + "";
//					lat = location.getLatitude() + "";
//				}
//				
//				//lng
//				sql += ",'" + lng + "'";
//				
//				//lat
//				sql += ",'" + lat + "'";
//				
//				//type
//				sql += ",'CAPTURE'";
//				
//				//collector_objid
//				sql += ",'" + profile.getUserId() + "'";
//				
//				//collector_name
//				sql += ",'" + profile.getFullName() + "'";
//
//				//trackerid
//				String trackerid = settings.getTrackerid();
//				if (trackerid == null || trackerid.trim().equals("")) {
//					trackerid = "";
//				}
//				sql += ",'" + trackerid + "'";
//				
//				//forupload
//				sql += ",0";
//				
//				//dtsaved
//				Calendar cal = Calendar.getInstance();
//				Date phonedate = new Timestamp(cal.getTimeInMillis());
//				sql += ",'" + phonedate.toString() + "'";
//								
//				//timedifference
//				AppSettings settings = Platform.getApplication().getAppSettings();
//				Map map = settings.getAll();
//				
//				long timedifference = 0L;
//				if (map.containsKey("timedifference")) {
//					timedifference = settings.getLong("timedifference");
//				}
//				
//				sql += "," + timedifference;
//								
//				
//				if ("check".equals(option)) {
//					
//					//bank_objid
//					sql += ",'" + bank.get("objid").toString() + "'";
//					
//					//bank_name
//					sql += ",'" + bank.get("name").toString() + "'";
//					
//					//check_no
//					sql += ",'" + checkno + "'";
//					
//					//check_date
//					sql += "," + checkdate + "'";
////					params.put("check_date", java.sql.Date.valueOf(checkdate));
//				}
//				System.out.println("params-> "+params);
				
								
//				txn.insert("capture_payment", params);
				
			}
			
//			private void execPayment(SQLTransaction txn, DBContext clfcdb) throws Exception {
//				String trackerid = settings.getTrackerid();
//				String billingid = "";
//				
////				DBSystemService systemSvc = new DBSystemService();
////				systemSvc.setDBContext(clfcdb);
////				systemSvc.setCloseable(false);
//				
//				UserProfile prof = SessionContext.getProfile();
//				String collectorid = (prof != null? prof.getUserId() : "");
//				
//				if (collectorid == null || "".equals(collectorid)) {
//					throw new RuntimeException("Collectorid is required.");
//				}
//				
//				String date = "";
//				Date dt = Platform.getApplication().getServerDate();
//				if (dt != null) date = ApplicationUtil.formatDate(dt, "yyyy-MM-dd");
//				
//				if (date == null || "".equals(date)) {
//					throw new RuntimeException("Date is required.");
//				}
//				
//				billingid = systemdb.getBillingid( collectorid, date );
//				
////				synchronized (MainDB.LOCK) {
////					try {
////						billingid = systemSvc.getBillingid(collectorid, date);
////					} catch (Exception e) {
////						e.printStackTrace();
////						throw e;
////					}
////				}
//				
//				String captureid = settings.getCaptureid();
//				Location location = NetworkLocationProvider.getLocation();
//				double lng = (location == null? 0.0 : location.getLongitude());
//				double lat = (location == null? 0.0 : location.getLatitude());
//				
//				String strLng = String.valueOf(lng), strLat = String.valueOf(lat);
////				System.out.println("location-> "+location);
//				Map params = new HashMap();
//				params.put("objid", objid);
//				params.put("captureid", captureid);
//				params.put("state", "PENDING");
//				params.put("billingid", billingid);
////				params.put("txndate", Platform.getApplication().getServerDate().toString());
//				params.put("txndate", date);
//				params.put("borrowername", borrower);
//				params.put("amount", Double.parseDouble(amount));
//				params.put("payoption", option);
//				params.put("paidby", paidby);
//				params.put("dtpaid", dt.toString());
////				params.put("lng", lng);
////				params.put("lat", lat);
//				params.put("lng", strLng);
//				params.put("lat", strLat);
//				params.put("type", "CAPTURE");
//				params.put("collector_objid", (prof != null? prof.getUserId() : ""));
//				params.put("collector_name", (prof != null? prof.getFullName() : ""));
//				params.put("trackerid", trackerid);
//				
//				if ("check".equals(option)) {
//					params.put("bank_objid", bank.get("objid").toString());
//					params.put("bank_name", bank.get("name").toString());
//					params.put("check_no", checkno);
//					params.put("check_date", java.sql.Date.valueOf(checkdate));
//				}		
////				System.out.println("params-> "+params);
//				
//				
//				params.put("forupload", 0);
//				
//				Calendar cal = Calendar.getInstance();
////				Date phonedate = java.sql.Timestamp.valueOf(DATE_FORMAT.format(cal.getTime()));
//				Date phonedate = new Timestamp(cal.getTimeInMillis());
//				params.put("dtsaved", phonedate.toString());
//				
//				AppSettings settings = Platform.getApplication().getAppSettings();
//				Map map = settings.getAll();
//				
//				long timedifference = 0L;
//				if (map.containsKey("timedifference")) {
//					timedifference = settings.getLong("timedifference");
//				}
//				
//				params.put("timedifference", timedifference);
//				
//				
//				txn.insert("capture_payment", params);
////				System.out.println("done insert");
//			}
		});
	}
}
