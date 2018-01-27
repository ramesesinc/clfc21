package com.rameses.clfc.android.main;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.ControlActivity;
import com.rameses.clfc.android.MainDB;
import com.rameses.clfc.android.PaymentDB;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.db.DBSystemService;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIAction;
import com.rameses.client.android.UIDialog;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;

public class PaymentActivity extends ControlActivity 
{
	private ApplicationImpl app;
	private AppSettingsImpl settings;
	
	private String loanappid = "";
	private String detailid = "";
	private String refno = "";
	private String type = "";
	private String borrowerid = "";
	private String borrowername = "";
	private String appno = "";
	private String sessionid = "";
	private String cstype = "";
	private EditText et_amount;
	private EditText et_overpayment;
	private String routecode = "";
	private int isfirstbill = 0;
	private int totaldays = 0;
	private BigDecimal overpayment = new BigDecimal("0").setScale(2);
	private BigDecimal defaultAmount = new BigDecimal("0").setScale(2);
	private String objid = "";
	private double lng = 0.0;
	private double lat = 0.0;
	
	private RelativeLayout rl_overpayment;
	private RelativeLayout rl_container;
	private DecimalFormat decf = new DecimalFormat("#,##0.00");
	private StringBuffer sb = new StringBuffer();
	private FieldPosition fp = new FieldPosition(0);
	private String txndate;
	
	private LayoutInflater inflater;
	private DBContext clfcdb;
	private SQLTransaction paymentdb;
	private DBSystemService systemSvc = new DBSystemService();
	private Location location;
	private Map params;
	private String message;	

	private boolean flag = true;
	private BigDecimal amt = new BigDecimal("0").setScale(2);
	private BigDecimal amt2 = new BigDecimal("0").setScale(2);
	private int td;
	
	@Override
	protected void onCreateProcess(Bundle savedInstanceState) {
		setContentView(R.layout.template_footer);
		rl_container = (RelativeLayout) findViewById(R.id.rl_container);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.activity_payment, rl_container, true);
		
		Intent intent = getIntent();		
		loanappid = intent.getStringExtra("loanappid");
		detailid = intent.getStringExtra("detailid");
		refno = intent.getStringExtra("refno");
		appno = intent.getStringExtra("appno");
		borrowerid = intent.getStringExtra("borrowerid");
		borrowername = intent.getStringExtra("borrowername");
		sessionid = intent.getStringExtra("sessionid");
		cstype = intent.getStringExtra("cstype");
		routecode = intent.getStringExtra("routecode");
		type = intent.getStringExtra("paymenttype");
		isfirstbill = intent.getIntExtra("isfirstbill", 0);
		totaldays = intent.getIntExtra("totaldays", 0);
		String amt = intent.getStringExtra("overpayment");
		overpayment = new BigDecimal(amt).setScale(2);
		amt = intent.getStringExtra("amount");
		defaultAmount = new BigDecimal(amt).setScale(2);
		
		objid = "PT"+UUID.randomUUID().toString();
		app = (ApplicationImpl) Platform.getApplication();
		settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
	}
	
	@Override
	protected void onStartProcess() {
		super.onStartProcess();
		
		rl_overpayment = (RelativeLayout) findViewById(R.id.rl_overpayment);
		rl_overpayment.setVisibility(View.GONE);
		if (type.equals("over")) rl_overpayment.setVisibility(View.VISIBLE);
		
		et_overpayment = (EditText) findViewById(R.id.et_overpayment);
		et_overpayment.setEnabled(false);
		if (isfirstbill == 1) et_overpayment.setEnabled(true);
		//if (overpayment.compareTo(new BigDecimal("0").setScale(2)) > 0) {
			//decf.format(overpayment, sb, fp);
			et_overpayment.setText(overpayment.toString());
		//}
		
		txndate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		((TextView) findViewById(R.id.tv_payment_refno)).setText(refno);
		((TextView) findViewById(R.id.tv_payment_txndate)).setText(txndate);
		et_amount = (EditText) findViewById(R.id.et_payment_amount);
		et_amount.setText(defaultAmount.toString());	
		et_amount.setSelection(0, defaultAmount.toString().length());
		et_amount.requestFocus();
		
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
	
	private void doSavePayment() {
		String paidby = getValueAsString(R.id.et_payment_paidby);
		String amount = getValueAsString(R.id.et_payment_amount);
		if(amount == null || amount.equals("")) {
			ApplicationUtil.showShortMsg("Amount is required.");
		} else {
			flag = true;
			amt = new BigDecimal(amount).setScale(2);
			amt2 = new BigDecimal("0").setScale(2);
			if (type.equals("over")) {
				amt2 = new BigDecimal(et_overpayment.getText().toString()).setScale(2);
				if (isfirstbill == 1) {
					td = amt.divide(amt2, 2, BigDecimal.ROUND_HALF_UP).intValue();
					if (td < totaldays) {
						//Toast.makeText(context, "Amount paid could not cover up to current date based on overpayment amount.", Toast.LENGTH_SHORT).show();
						ApplicationUtil.showShortMsg("Amount paid could not cover up to current date based on overpayment amount.");
						flag = false;
					}
				}
			}
			if (paidby.trim().equals("")) {
				ApplicationUtil.showShortMsg("Paid by is required.");
				flag = false;
			}
			if (flag == true) {
				UIDialog dialog = new UIDialog(this) {
					public void onApprove() {
						onApproveImpl();
					}
				};
				message = "Amount Paid: "+amt.toString();
				if (type.equals("over") && isfirstbill == 1) message += "\nOverpayment: "+amt2.toString();
				message += "\n\nEnsure that all information are correct. Continue?";
				dialog.confirm(message);				
			}
		}
	}
	
	private void onApproveImpl() {
		getHandler().post(new Runnable() {
			public void run() {
				String trackerid = null;
				synchronized (MainDB.LOCK) {
					clfcdb = new DBContext("clfc.db");
					systemSvc.setDBContext(clfcdb);
					
					trackerid = settings.getTrackerid();
					/*try {
						trackerid = systemSvc.getTrackerid();
					} catch (Throwable t) {
						t.printStackTrace();
						UIDialog.showMessage(t, PaymentActivity.this); 
					}*/
				}
				
				System.out.println("trackerid-> "+trackerid);
				synchronized (PaymentDB.LOCK) {
					paymentdb = new SQLTransaction("clfcpayment.db");
					try {
						paymentdb.beginTransaction();
						execPayment(paymentdb, trackerid);
						paymentdb.commit();
						app.paymentSvc.start();
						finish();
					} catch (Throwable t) {
						t.printStackTrace();
						UIDialog.showMessage(t, PaymentActivity.this); 
					} finally {
						paymentdb.endTransaction();
					}
				}
			}
			
			private void execPayment(SQLTransaction paymentdb, String trackerid) throws Exception {
				
				location = NetworkLocationProvider.getLocation();
				lng = (location == null? 0.0 : location.getLongitude());
				lat = (location == null? 0.0 : location.getLatitude());
//				System.out.println("location-> "+location);
				params = new HashMap();
				params.put("objid", objid);
				params.put("state", "PENDING");
				params.put("refno", getValueAsString(R.id.tv_payment_refno));
				params.put("txndate", Platform.getApplication().getServerDate().toString());
				params.put("paymenttype", type);
				params.put("paymentamount", Double.parseDouble(et_amount.getText().toString()));
				params.put("paidby", getValueAsString(R.id.et_payment_paidby));
				params.put("loanappid", loanappid);
				params.put("appno", appno);
				params.put("detailid", detailid);
				params.put("routecode", routecode);
				params.put("isfirstbill", isfirstbill);
//				System.out.println("longitude-> "+location.getLongitude());
				params.put("longitude", lng);
//				System.out.println("latitude-> "+location.getLatitude());
				params.put("latitude", lat);
				params.put("trackerid", trackerid);
				params.put("collectorid", SessionContext.getProfile().getUserId());
				params.put("collectorname", SessionContext.getProfile().getFullName());
				params.put("borrowerid", borrowerid);
				params.put("borrowername", borrowername);
				params.put("sessionid", sessionid);
				params.put("cstype", cstype);
				
//				System.out.println("params-> "+params);
				paymentdb.insert("payment", params);
//				System.out.println("done insert");
			}
			
		});
	}	
}
;