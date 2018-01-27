package com.rameses.clfc.android.main;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.ControlActivity;
import com.rameses.clfc.android.MainDB;
import com.rameses.clfc.android.PaymentDB;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.RemarksDB;
import com.rameses.clfc.android.RemarksRemovedDB;
import com.rameses.clfc.android.VoidRequestDB;
import com.rameses.clfc.android.db.DBCollectionSheet;
import com.rameses.clfc.android.db.DBPaymentService;
import com.rameses.clfc.android.db.DBPrevLocation;
import com.rameses.clfc.android.db.DBRemarksService;
import com.rameses.clfc.android.db.DBVoidService;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIDialog;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.MapProxy;

public class CollectionSheetInfoActivity extends ControlActivity 
{
	private AppSettingsImpl settings;
	private String loanappid = "";
	private String detailid = "";
	private String appno = "";
	private String acctid = "";
	private String acctname = "";
	private String sessionid = "";
	private String cstype = "";
	private BigDecimal overpayment = new BigDecimal("0").setScale(2);
	private BigDecimal dailydue = new BigDecimal("0").setScale(2);
	private String routecode = "";
	private String refno = "";
	private String paymenttype = "";
	private int totaldays = 0;
	private int isfirstbill = 0;
	private RelativeLayout rl_general = null;
	private RelativeLayout rl_payment = null;
	private RelativeLayout rl_remarks = null;
	private RelativeLayout rl_notes = null;
	private AlertDialog dialog = null;
	private SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy");
	private LayoutInflater inflater;
	private Map remarks;
	private ProgressDialog progressDialog;
	private RelativeLayout rl_container;
	
	private DBCollectionSheet dbCollectionSheet = new DBCollectionSheet();	
	private DBPaymentService paymentSvc = new DBPaymentService();
	private DBVoidService voidSvc = new DBVoidService();
	private DBRemarksService remarksSvc = new DBRemarksService();

	private Map collectionSheet = new HashMap();
	private BigDecimal amountdue = new BigDecimal("0").setScale(2);
	private BigDecimal loanamount = new BigDecimal("0").setScale(2);
	private BigDecimal balance = new BigDecimal("0").setScale(2);
	private BigDecimal interest = new BigDecimal("0").setScale(2);
	private BigDecimal penalty = new BigDecimal("0").setScale(2);
	private BigDecimal others = new BigDecimal("0").setScale(2);
	private int term = 0;
	private int size;
	private String duedate = "";
	private String homeaddress = "";
	private String collectionaddress = "";
	private List<Map> payments;
	private LinearLayout ll_info_payments;

	private String type = "";
	private String voidType = "";
	private RelativeLayout child = null;
	private View overlay = null;
	private BigDecimal amount;
	private Map payment;
	private Map voidRequest;
	private RelativeLayout.LayoutParams layoutParams;
	private RelativeLayout remarks_child;
	private CharSequence[] remarks_items = {"Edit Remarks", "Remove Remarks"};

	protected void onCreateProcess(Bundle savedInstanceState) {
		setContentView(R.layout.template_footer);
		setTitle("Collection Sheet Info");

		rl_container = (RelativeLayout) findViewById(R.id.rl_container);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.activity_collectionsheetinfo, rl_container, true);

		Intent intent = getIntent();
		loanappid = intent.getStringExtra("loanappid");
		detailid = intent.getStringExtra("detailid");
		routecode = intent.getStringExtra("routecode");
		paymenttype = intent.getStringExtra("paymenttype");
		isfirstbill = intent.getIntExtra("isfirstbill", 0);

		rl_general = (RelativeLayout) findViewById(R.id.layout_info_general);
		rl_payment = (RelativeLayout) findViewById(R.id.layout_info_payment);
		rl_remarks = (RelativeLayout) findViewById(R.id.layout_info_remarks);
		rl_notes = (RelativeLayout) findViewById(R.id.layout_info_notes);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		
		settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
		ll_info_payments = (LinearLayout) findViewById(R.id.ll_info_payments);
	}

	protected void onStartProcess() {
		super.onStartProcess();
		onStartPocessImpl();
//		try {
//			
//		}  catch (Throwable t) {
//			t.printStackTrace();
//			UIDialog.showMessage(t, CollectionSheetInfoActivity.this); 
//		} finally {
//			clfcdb.close();
//			paymentdb.close();
//			remarksdb.close();
//			requestdb.close();
//		}
	}

	private void onStartPocessImpl() {
		System.out.println("on start process collection sheet info");
		getHandler().post(new Runnable() {
			public void run() {
				synchronized (MainDB.LOCK) {
					DBContext clfcdb = new DBContext("clfc.db");
					try {
						runImpl(clfcdb);
					} catch (Throwable t) {
						t.printStackTrace();
						UIDialog.showMessage(t, CollectionSheetInfoActivity.this); 
					} finally {
						clfcdb.close();
					}
				}		
			}
			
			private void runImpl(DBContext clfcdb) throws Exception {
				dbCollectionSheet.setDBContext(clfcdb);
				
//				System.out.println("loanappid-> "+loanappid);
//				System.out.println("clfcdb -> "+clfcdb);
				collectionSheet = dbCollectionSheet.findCollectionSheetByLoanappid(loanappid);	
				
				if (collectionSheet != null && !collectionSheet.isEmpty()) {
					acctid = collectionSheet.get("acctid").toString();
					sessionid = collectionSheet.get("sessionid").toString();
					acctname = collectionSheet.get("acctname").toString();
					appno = collectionSheet.get("appno").toString();
					cstype = collectionSheet.get("type").toString();
					amountdue = new BigDecimal(collectionSheet.get("amountdue").toString());
					loanamount = new BigDecimal(collectionSheet.get("loanamount").toString());
					balance = new BigDecimal(collectionSheet.get("balance").toString());
					dailydue = new BigDecimal(collectionSheet.get("dailydue").toString());
					overpayment = new BigDecimal(collectionSheet.get("overpaymentamount").toString());
					interest = new BigDecimal(collectionSheet.get("interest").toString());
					penalty = new BigDecimal(collectionSheet.get("penalty").toString());
					others = new BigDecimal(collectionSheet.get("others").toString());
					term = Integer.parseInt(collectionSheet.get("term").toString());
					refno = collectionSheet.get("refno").toString();
					homeaddress = collectionSheet.get("homeaddress").toString();
					collectionaddress = collectionSheet.get("collectionaddress").toString();
					try {
						duedate = df.format(new SimpleDateFormat("yyyy-MM-dd").parse(collectionSheet.get("duedate").toString()));	
					} catch (Exception e) {;}
					totaldays = amountdue.divide(dailydue, 2, BigDecimal.ROUND_HALF_UP).intValue();
					if (paymenttype == null || paymenttype.equals("")) {
						paymenttype = collectionSheet.get("paymentmethod").toString();
					}
				}
				
				((TextView) findViewById(R.id.tv_info_acctname)).setText(acctname);
				((TextView) findViewById(R.id.tv_info_appno)).setText(appno);
				((TextView) findViewById(R.id.tv_info_loanamount)).setText(formatValue(loanamount));
				((TextView) findViewById(R.id.tv_info_balance)).setText(formatValue(balance));
				((TextView) findViewById(R.id.tv_info_dailydue)).setText(formatValue(dailydue));
				((TextView) findViewById(R.id.tv_info_amountdue)).setText(formatValue(amountdue));
				((TextView) findViewById(R.id.tv_info_overpayment)).setText(formatValue(overpayment));
				((TextView) findViewById(R.id.tv_info_duedate)).setText(duedate);
				((TextView) findViewById(R.id.tv_info_homeaddress)).setText(homeaddress);
				((TextView) findViewById(R.id.tv_info_collectionaddress)).setText(collectionaddress);
				((TextView) findViewById(R.id.tv_info_interest)).setText(formatValue(interest));
				((TextView) findViewById(R.id.tv_info_penalty)).setText(formatValue(penalty));
				((TextView) findViewById(R.id.tv_info_others)).setText(formatValue(others));
				((TextView) findViewById(R.id.tv_info_term)).setText(term+" days");
			}
		});
		
		getHandler().post(new Runnable() {
			public void run() {
				payments = new ArrayList<Map>();
				synchronized (PaymentDB.LOCK) {
					DBContext paymentdb = new DBContext("clfcpayment.db");
					paymentSvc.setDBContext(paymentdb);
					try {
						payments = paymentSvc.getPaymentsByLoanappid(loanappid);
					} catch (Throwable t) {
						t.printStackTrace();
						UIDialog.showMessage(t, CollectionSheetInfoActivity.this); 
					}	
				}

				rl_notes.setVisibility(View.GONE);
				rl_payment.setVisibility(View.GONE);				
				if (payments != null && !payments.isEmpty()) {
					rl_payment.setVisibility(View.VISIBLE);

					size = payments.size();
					synchronized (VoidRequestDB.LOCK) { 
						DBContext requestdb = new DBContext("clfcrequest.db");
						voidSvc.setDBContext(requestdb);
						voidSvc.setCloseable(false);
						try {
							for (int i=0; i<size; i++) {
								payment = (Map) payments.get(i);
								payment.put("hasrequest", false);
								
								voidRequest = voidSvc.findVoidRequestByPaymentid(MapProxy.getString(payment, "objid"));
								if (voidRequest != null && !voidRequest.isEmpty()) {
									payment.put("hasrequest", true);
									payment.put("requeststate", MapProxy.getString(voidRequest, "state"));
								}
							}
						} catch (Throwable t) {
							t.printStackTrace();
							UIDialog.showMessage(t, CollectionSheetInfoActivity.this); 
						} finally {
							requestdb.close();
						}	
					}
					
					ll_info_payments.removeAllViews();
					ll_info_payments.removeAllViewsInLayout();
					boolean hasrequest = false;
					for (int i=0; i<size; i++) {
						child = (RelativeLayout) inflater.inflate(R.layout.item_payment, null);
						payment = (Map) payments.get(i);

						((TextView) child.findViewById(R.id.tv_info_refno)).setText(payment.get("refno").toString());
						((TextView) child.findViewById(R.id.tv_info_txndate)).setText(payment.get("txndate").toString());
						((TextView) child.findViewById(R.id.tv_info_paidby)).setText(payment.get("paidby").toString());
						
						String ptype = payment.get("paymenttype").toString();
						System.out.println("payment type " + ptype);
						if (payment.get("paymenttype").toString().equals("schedule")) {
							type = "Schedule/Advance";
						} else if (payment.get("paymenttype").toString().equals("over")) {
							type = "Overpayment";
						}
						((TextView) child.findViewById(R.id.tv_info_paymenttype)).setText(type);
						
						amount = new BigDecimal(payment.get("paymentamount").toString()).setScale(2);
						((TextView) child.findViewById(R.id.tv_info_paymentamount)).setText(formatValue(amount));
						child.setTag(R.id.paymentid, payment.get("objid").toString());
						
						hasrequest = MapProxy.getBoolean(payment, "hasrequest");
//						voidRequest = voidSvc.findVoidRequestByPaymentid(payment.get("objid").toString());
						if (hasrequest == false) {
							addPaymentProperties(child);
						} else if (hasrequest == true) {
							voidType = MapProxy.getString(payment, "requeststate");
							layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
							layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 1);
							overlay = inflater.inflate(R.layout.overlay_void_text, null);
							if (voidType.equals("PENDING")) {
								child.setOnClickListener(null);
								child.setOnLongClickListener(null);
								child.setClickable(false);
								((TextView) overlay).setTextColor(getResources().getColor(R.color.red));
								((TextView) overlay).setText("VOID REQUEST PENDING");
								overlay.setLayoutParams(layoutParams);
								child.addView(overlay); 
							} else if (voidType.equals("APPROVED")) {
								((TextView) overlay).setTextColor(getResources().getColor(R.color.green));
								((TextView) overlay).setText("VOID APPROVED");
								overlay.setLayoutParams(layoutParams);
								((RelativeLayout) child).addView(overlay);
								//addApprovedVoidPaymentProperies(child);
							}
						}
						ll_info_payments.addView(child);
					}
				}
			}
		});
		
		getHandler().post(new Runnable() {
			public void run() {
				synchronized (RemarksDB.LOCK) {
					DBContext remarksdb = new DBContext("clfcremarks.db");
					try {
						runImpl(remarksdb);
					} catch (Throwable t) {
						t.printStackTrace();
						UIDialog.showMessage(t, CollectionSheetInfoActivity.this); 
					}
				}
			}
			
			private void runImpl(DBContext remarksdb) throws Exception {
				remarksSvc.setDBContext(remarksdb);
				
				try {
					remarks = remarksSvc.findRemarksByLoanappid(loanappid);
				} catch (Exception e) {;}
				
				rl_remarks.setVisibility(View.GONE);
				if (remarks != null && !remarks.isEmpty()) {
					rl_remarks.setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.tv_info_remarks)).setText(remarks.get("remarks").toString());
					remarks_child = (RelativeLayout) findViewById(R.id.rl_info_remarks);
					addRemarksProperties(remarks_child);
				}
			}
		});
	}
	
	private void addRemarksProperties(View child) {
		child.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// TODO Auto-generated method stub
				view.setBackgroundResource(android.R.drawable.list_selector_background);
			}
		});
		child.setOnLongClickListener(new View.OnLongClickListener() {
			 @Override
			 public boolean onLongClick(View v) {
			 // TODO Auto-generated method stub
				 v.setBackgroundResource(android.R.drawable.list_selector_background);
				 UIDialog dialog = new UIDialog() {
					 public void onSelectItem(int index) {
						 switch(index) {
						 	case 0:
						 		showRemarksDialog("edit");
						 		break;
						 	case 1:
						 		removeRemarks();									 		
						 		break;
						 }
//						 SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");
//						 SQLTransaction remarkremoveddb = new SQLTransaction("clfcremarksremoved.db");
//						 try {
//							 remarksdb.beginTransaction();
//							 remarkremoveddb.beginTransaction();
//							 onSelectedItemImpl(remarksdb, remarkremoveddb, index);
//							 remarksdb.commit();
//							 remarkremoveddb.commit();
//						 } catch (Throwable t) {
//							 UIDialog.showMessage(t, CollectionSheetInfoActivity.this);
//						 } finally {
//							 remarksdb.endTransaction();
//							 remarkremoveddb.endTransaction();
//						 }
					 }
					 
//					 private void onSelectedItemImpl(SQLTransaction remarksdb, SQLTransaction remarksremoveddb, int index) {
//						 switch(index) {
//						 	case 0:
//						 		showRemarksDialog("edit");
//						 		break;
//						 	case 1:
//						 		removeRemarks();									 		
//						 		break;
//						 }
//					 }
				 };
				 dialog.select(remarks_items);
				 return false;
			};
		});
	}
	
	private void removeRemarks() {
		synchronized (RemarksDB.LOCK) {
			SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");			
			try {
				remarksdb.beginTransaction();
				remarksdb.delete("remarks", "loanappid=?", new Object[]{loanappid});
				remarksdb.commit();
			} catch (Throwable t) {
				 UIDialog.showMessage(t, CollectionSheetInfoActivity.this);
				
			} finally {
				remarksdb.endTransaction();
			}
		}
		
		synchronized (RemarksRemovedDB.LOCK) {
			SQLTransaction remarksremoveddb = new SQLTransaction("clfcremarksremoved.db");
			try {
				remarksremoveddb.beginTransaction();

		 		Map params = new HashMap();
		 		params.put("loanappid", loanappid);
		 		params.put("state", "PENDING");
		 		params.put("detailid", detailid);
		 		remarksremoveddb.insert("remarks_removed", params);
		 		
				remarksremoveddb.commit();
			} catch (Throwable t) {
				 UIDialog.showMessage(t, CollectionSheetInfoActivity.this);
				
			} finally {
				remarksremoveddb.endTransaction();
			}
		}
		remarks = null;
		rl_remarks.setVisibility(View.GONE);
		ApplicationUtil.showShortMsg("Successfully removed remarks.");
		getHandler().post(new Runnable() {
			public void run() {
				getApp().remarksRemovedSvc.start();
			}
		});
	}

	private void addPaymentProperties(View child) {
		child.setClickable(true);
		child.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			// TODO Auto-generated method stub
				v.setBackgroundResource(android.R.drawable.list_selector_background);
			}
		});
		child.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
				final View view = v;
				v.setBackgroundResource(android.R.drawable.list_selector_background);
				CharSequence[] items = {"Void"};
				UIDialog dialog = new UIDialog() {
					public void onSelectItem(int index) {
						switch(index) {
							case 0:
								showVoidDialog(view);
								break;
						}	
					}
				};
				dialog.select(items);
				return false;
			}
		});
	}
	
	private void showVoidDialog() {
		showVoidDialog(null);
	}

	private void showVoidDialog(View child) {
		final View payment = child;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Reason for void");
		View view = inflater.inflate(R.layout.dialog_remarks, null);
		builder.setView(view);
		builder.setPositiveButton("Submit", null);
		builder.setNegativeButton("Cancel", null);
		dialog = builder.create();
		dialog.show();
		
		Button btn_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		btn_positive.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// TODO Auto-generated method stub
				SQLTransaction txn = new SQLTransaction("clfc.db");
				try {
					txn.beginTransaction();
					onClickImpl(txn, payment);
					txn.commit();
				} catch (Throwable t) {
					UIDialog.showMessage(t, CollectionSheetInfoActivity.this);
				} finally {
					txn.endTransaction();
				}
			}
			
			private void onClickImpl(SQLTransaction txn, View view) throws Exception {
				Map params = new HashMap();
				params.put("objid", "VOID"+UUID.randomUUID());
				params.put("txndate", Platform.getApplication().getServerDate().toString());
				params.put("paymentid", payment.getTag(R.id.paymentid));
				params.put("routecode", routecode);
				params.put("state", "PENDING");
				
				Map collector = new HashMap();
				collector.put("objid", SessionContext.getProfile().getUserId());
				collector.put("name", SessionContext.getProfile().getFullName());
				params.put("collector", collector);
				
				DBCollectionSheet dbCs = new DBCollectionSheet();
				dbCs.setDBContext(txn.getContext());

				Map collectionSheet = new HashMap();
				try {
					collectionSheet = dbCs.findCollectionSheetByLoanappid(loanappid);
				} catch (Exception e) {
					throw e;
				}
				Map loanapp = new HashMap();
				loanapp.put("objid", collectionSheet.get("loanappid").toString());
				loanapp.put("appno", collectionSheet.get("appno").toString());
				params.put("loanapp", loanapp);
				
				params.put("collectionid", collectionSheet.get("sessionid").toString());
				params.put("reason", ((EditText) dialog.findViewById(R.id.remarks_text)).getText().toString());
				
				try {
					new VoidRequestController(CollectionSheetInfoActivity.this, progressDialog, params, view, dialog).execute();
				} catch (Throwable t) {
					UIDialog.showMessage(t, CollectionSheetInfoActivity.this);
				}
			}
		});
	}
	private String formatValue(Object number) {
		DecimalFormat df = new DecimalFormat("#,###,##0.00");
		StringBuffer sb = new StringBuffer();
		FieldPosition fp = new FieldPosition(0);
		df.format(number, sb, fp);
		return sb.toString();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.payment, menu);
		if (remarks != null && !remarks.isEmpty()) {
			((MenuItem) menu.findItem(R.id.payment_addremarks)).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.payment_addpayment:
				DBContext paymentdb = new DBContext("clfcpayment.db");
				DBContext requestdb = new DBContext("clfcrequest.db");
				System.out.println("requestdb -> "+requestdb);
				try { 
//					paymentdb.beginTransaction();
//					requestdb.beginTransaction();
					addPaymentImpl(paymentdb, requestdb);
//					paymentdb.commit();
//					requestdb.commit();
				} catch (Throwable t) {
					t.printStackTrace();
					UIDialog.showMessage(t, CollectionSheetInfoActivity.this); 
				} finally {
					paymentdb.close();
					requestdb.close();
				}
				break;
			case R.id.payment_addremarks:
				showRemarksDialog("create");
				break;
		}
		return true;
	}
	
	private void addPaymentImpl(DBContext paymentdb, DBContext requestdb) throws Exception {
		voidSvc.setDBContext(requestdb);
		
		paymentSvc.setDBContext(paymentdb);
		paymentSvc.setCloseable(false);
		if (voidSvc.hasPendingVoidRequestByLoanappid(loanappid)) {
			ApplicationUtil.showShortMsg("[ERROR] Cannot add payment. No confirmation for void requested at the moment.");
			
		} else {
			Intent intent = new Intent(this, PaymentActivity.class);
			intent.putExtra("loanappid", loanappid);
			intent.putExtra("detailid", detailid);
			intent.putExtra("routecode", routecode);
			intent.putExtra("totaldays", totaldays);
			intent.putExtra("isfirstbill", isfirstbill);
			intent.putExtra("refno", refno);
			intent.putExtra("appno", appno);
			intent.putExtra("borrowerid", acctid);
			intent.putExtra("borrowername", acctname);
			intent.putExtra("sessionid", sessionid);
			intent.putExtra("cstype", cstype);
			
			if (paymentSvc.hasPaymentsByLoanappid(loanappid)) {
				refno += (paymentSvc.noOfPaymentsByLoanappid(loanappid)+1);
			}
			intent.putExtra("refno", refno);
			System.out.println("payment type " + paymenttype);
			intent.putExtra("paymenttype", paymenttype);
			intent.putExtra("overpayment", overpayment.toString());
			
			BigDecimal amount = dailydue;
			if (paymenttype.equals("over")) {
				amount = overpayment;
			}
			intent.putExtra("amount", amount.toString());
			startActivity(intent);
		}
		paymentdb.close();
	}
	//
	 public void showRemarksDialog(String mode) {
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 builder.setTitle("Remarks");
		 View view = inflater.inflate(R.layout.dialog_remarks, null);
		 builder.setView(view);
		 builder.setPositiveButton("  Ok  ", null);
		 builder.setNegativeButton("Cancel", null);
		 dialog = builder.create();
		 dialog.show();
		 if (!mode.equals("create")) {
			 EditText et_remarks = (EditText) dialog.findViewById(R.id.remarks_text);
			 et_remarks.setText(remarks.get("remarks").toString());
			 et_remarks.setSelection(0, et_remarks.getText().toString().length());
		 }
		 Button btn_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		 btn_positive.setOnClickListener(new RemarksValidationListener(dialog, mode));
	 }
	 
	 private class RemarksValidationListener implements View.OnClickListener 
	 {
		 private final Dialog dialog;
		 private final String mode;
		 private Location location;
		 private Map params = new HashMap();
		 private String mRemarks = null;
		 
		 public RemarksValidationListener(Dialog dialog, String mode) {
			 this.dialog = dialog;
			 this.mode = mode;
		 }

		 public void onClick(View v) {
			 // TODO Auto-generated method stub 
			 	mRemarks = ((EditText) dialog.findViewById(R.id.remarks_text)).getText().toString();
			 	if (mRemarks.trim().equals("")) {
					 ApplicationUtil.showShortMsg("Remarks is required.");
					 return;
			 	}
			 	
			 	String trackerid = settings.getTrackerid();
//			 	DBSystemService systemSvc = new DBSystemService();
//			 	synchronized (MainDB.LOCK) {
//				 	DBContext clfcdb = new DBContext("clfc.db");
//			 		systemSvc.setDBContext(clfcdb);
//			 		
//			 		try {
//			 			trackerid = systemSvc.getTrackerid();
//			 		} catch (Throwable t) {
//						UIDialog.showMessage(t, CollectionSheetInfoActivity.this); 
//						
//			 		}			 			
//			 	}
			 	
			 	synchronized (RemarksDB.LOCK) {
			 		SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");
			 		DBContext ctx = new DBContext("clfctracker.db");
			 		DBPrevLocation prevLocationSvc = new DBPrevLocation();
			 		prevLocationSvc.setDBContext(ctx);
			 		try {
			 			remarksdb.beginTransaction();
			 			execRemarks(remarksdb, mode, trackerid, prevLocationSvc);
			 			remarksdb.commit();
			 			getHandler().post(new Runnable() {
							public void run() {
								getApp().remarksSvc.start();
							}
						});
			 		} catch (Throwable t) {
			 			t.printStackTrace();
						UIDialog.showMessage(t, CollectionSheetInfoActivity.this);
			 			
			 		} finally {
			 			remarksdb.endTransaction();
			 		}
			 	}
		 }
		 
		private void execRemarks(SQLTransaction remarksdb, String mode, String trackerid, DBPrevLocation prevLocationSvc) throws Exception {
			location = NetworkLocationProvider.getLocation();
			double lng = 0.00;
			double lat = 0.00;
			
			if (location != null) {
				lng = location.getLongitude();
				lat = location.getLatitude();
				
			} else {
				Map prevLocation = prevLocationSvc.getPrevLocation();
				if (prevLocation != null) {
					lng = MapProxy.getDouble(prevLocation, "longitude");
					lat = MapProxy.getDouble(prevLocation, "latitude");
				}
				
			}
					 
			Map params = new HashMap();				 
			params.put("loanappid", loanappid);
			params.put("state", "PENDING");
			params.put("remarks", mRemarks);
			params.put("longitude", lng);
			params.put("latitude", lat);
			params.put("trackerid", trackerid);
			params.put("detailid", detailid);
			params.put("appno", appno);
			params.put("borrowerid", acctid);
			params.put("borrowername", acctname);
			params.put("sessionid", sessionid);
			params.put("routecode", routecode);
			params.put("collectorid", SessionContext.getProfile().getUserId());
			params.put("collectorname", SessionContext.getProfile().getFullName());
			params.put("txndate", Platform.getApplication().getServerDate().toString());
			params.put("cstype", cstype);
			    			 
			if (mode.equals("create")) {
				remarksdb.insert("remarks", params);
				rl_remarks.setVisibility(View.VISIBLE);
				View remarks_child = (RelativeLayout) findViewById(R.id.rl_info_remarks); 
				addRemarksProperties(remarks_child);
				ApplicationUtil.showShortMsg("Successfully added remark.");
			} else if (!mode.equals("create")) {
				System.out.println("loanappid " + loanappid);
				Map params2 = new HashMap();
				params2.put("remarks", mRemarks);
				remarksdb.update("remarks", "loanappid='"+loanappid+"'", params2);
//				remarksdb.update("remarks", "loanappid='"+loanappid+"'", params);
				ApplicationUtil.showShortMsg("Successfully updated remark.");
			}
				
			DBRemarksService remarksSvc = new DBRemarksService();
			remarksSvc.setDBContext(remarksdb.getContext());
			remarks = remarksSvc.findRemarksByLoanappid(loanappid);
			((TextView) findViewById(R.id.tv_info_remarks)).setText(mRemarks);
			dialog.dismiss();
		 }
	 }
}
