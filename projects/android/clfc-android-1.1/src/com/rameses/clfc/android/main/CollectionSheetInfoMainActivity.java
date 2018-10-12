package com.rameses.clfc.android.main;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.rameses.clfc.android.AppSettingsImpl;
import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.ControlActivity;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.CSRemarksDB;
import com.rameses.clfc.android.db.CSVoidDB;
import com.rameses.clfc.android.db.CollectionSheetDB;
import com.rameses.clfc.android.db.PrevLocationDB;
import com.rameses.client.android.NetworkLocationProvider;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIDialog;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.util.MapProxy;

public class CollectionSheetInfoMainActivity extends ControlActivity {

	private ViewPager tab;
	private CollectionSheetInfoTabPagerAdapter tabAdapter;
	private ActionBar actionBar;
	private MapProxy collectionsheet;
	private String objid;
	private AppSettingsImpl settings;
	
//	private DBCSVoid voidcs = new DBCSVoid();
	private CSVoidDB csvoiddb = new CSVoidDB(); 
	private CSRemarksDB csremarksdb = new CSRemarksDB();
	private PrevLocationDB prevlocationdb = new PrevLocationDB();
	private CollectionSheetDB collectionsheetdb = new CollectionSheetDB();
	
	protected void onCreateProcess(Bundle savedInstanceState) {
		super.onCreateProcess(savedInstanceState);

		setTitle("CLFC Collection - ILS");
		setContentView(R.layout.template_footer);
		RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.rl_container);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.activity_collectionsheetinfo_main, rl_container, true);
		
		tabAdapter = new CollectionSheetInfoTabPagerAdapter(getSupportFragmentManager());
		
		settings = (AppSettingsImpl) Platform.getApplication().getAppSettings();
		
		Map<String, Object> params = new HashMap();
		objid = getIntent().getStringExtra("objid");
		params.put("objid", objid);
		tabAdapter.setParams(params);
		
		tab = (ViewPager) findViewById(R.id.pager);
		tab.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				actionBar = getSupportActionBar();
				actionBar.setSelectedNavigationItem(position);
				 
				String tag = actionBar.getTabAt(position).getTag().toString();
				if ("PAYMENTS".equals(tag)) {
					reloadPayments();
				} else if ("COL_REMARKS".equals(tag)) {
					reloadRemarks();
				} 
			}  
		});
		tab.setAdapter(tabAdapter);
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			public void onTabReselected(Tab t, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			public void onTabSelected(Tab t, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				tab.setCurrentItem(t.getPosition());
			}

			public void onTabUnselected(Tab t, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}
		};
		
		actionBar.addTab(actionBar.newTab().setTag("GEN_INFO").setText("General Info").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setTag("PAYMENTS").setText("Payments").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setTag("COL_REMARKS").setText("Collector Remarks").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setTag("NOTES").setText("Notes").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setTag("FOL_REMARKS").setText("Follow-up Remarks").setTabListener(tabListener));
	}
	
	protected void onStartProcess() {
		super.onStartProcess();
//		DBContext ctx = new DBContext("clfc.db");
//		DBCollectionSheet collectionsheetdb = new DBCollectionSheet();
//		collectionsheetdb.setDBContext(ctx);
		
		try {
			collectionsheet = new MapProxy(collectionsheetdb.findCollectionSheet( objid ));
		} catch (Throwable t) {
			t.printStackTrace();
			UIDialog.showMessage(t, CollectionSheetInfoMainActivity.this);
		}
		
	}
	
	private void reloadPayments() {
		Fragment fragment = tabAdapter.getCurrentFragment();
		if (fragment instanceof PaymentsFragment) {
			PaymentsFragment paymentsFrag = (PaymentsFragment) fragment;
			int currentNoOfPayments = paymentsFrag.getCurrentNoOfPayments();
			int oldNoOfPayments = paymentsFrag.getOldNoOfPayments();
			if (currentNoOfPayments > oldNoOfPayments) {
				paymentsFrag.reloadPayments();
			}
		}
	}
	
	private void reloadRemarks() {
		Fragment fragment = tabAdapter.getCurrentFragment();
		if (fragment instanceof CollectorRemarksFragment) {
			CollectorRemarksFragment remarksFrag = (CollectorRemarksFragment) fragment;
			remarksFrag.reloadRemarks();
		}
	}
	

	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.payment, menu);
		
		boolean hasremarks = false;
//		DBContext ctx = new DBContext("clfc.db");
//		DBCSRemarks csremarks = new DBCSRemarks();
//		csremarks.setDBContext(ctx);
//		csremarks.setCloseable(false);
		
		try {
//			hasremarks = csremarks.hasRemarksById(objid);
			hasremarks = csremarksdb.hasRemarksById( objid );
		} catch(Throwable t) {;}
		
		if (hasremarks) {
			((MenuItem) menu.findItem(R.id.payment_addremarks)).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.payment_addpayment) {
			
			try {
				addPaymentImpl();
			} catch (Throwable t) {
				t.printStackTrace();
				UIDialog.showMessage(t, CollectionSheetInfoMainActivity.this); 
			}
			
//			DBContext ctx = new DBContext("clfc.db");
//			try { 
////				paymentdb.beginTransaction();
////				requestdb.beginTransaction();
////				addPaymentImpl(ctx);
////				paymentdb.commit();
////				requestdb.commit();
//			} catch (Throwable t) {
//				t.printStackTrace();
//				UIDialog.showMessage(t, CollectionSheetInfoMainActivity.this); 
//			} finally {
////				ctx.close();
////				paymentdb.close();
////				requestdb.close();
//			}
		} else if (item.getItemId() == R.id.payment_addremarks) {
			addRemarks();
//			showRemarksDialog("create");
		}
		return true;
	}
	
	private void addPaymentImpl() throws Exception {
		if (collectionsheet == null || collectionsheet.isEmpty()) return;
		
		String itemid = collectionsheet.getString("itemid");
		String csid = collectionsheet.getString("objid");
		
		if (csvoiddb.hasPendingVoidRequest( itemid, csid )) {
			ApplicationUtil.showShortMsg("[ERROR] Cannot add payment. No confirmation for void requested at the moment.");
		} else {
			Intent intent = new Intent(this, PaymentActivity.class);
//			intent.putExtra("itemid", collectionsheet.get("objid").toString());
			intent.putExtra("itemid", csid);
			startActivity(intent);
		}
	}
	
//	private void xaddPaymentImpl(DBContext ctx) throws Exception {
//		if (collectionsheet == null || collectionsheet.isEmpty()) return;
//		
//		voidcs.setDBContext(ctx);
//		voidcs.setCloseable(false);
//		String itemid = collectionsheet.getString("itemid");//collectionsheet.get("itemid").toString();
//		String csid = collectionsheet.getString("objid");//collectionsheet.get("objid").toString();
////		System.out.println("itemid " + itemid + " csid " + csid);
//		if (voidcs.hasPendingVoidRequest(itemid, csid)) {
//			ApplicationUtil.showShortMsg("[ERROR] Cannot add payment. No confirmation for void requested at the moment.");
//			
//		} else {
//			Intent intent = new Intent(this, PaymentActivity.class);
//			intent.putExtra("itemid", collectionsheet.get("objid").toString());
//			startActivity(intent);
//		}
//	}
	
	private void addRemarks() {
		UIDialog dialog = new UIDialog(CollectionSheetInfoMainActivity.this) {
			public boolean onApprove(Object value) {
				if (value == null || "".equals(value.toString())) {
					ApplicationUtil.showShortMsg("Remarks is required.");
					return false;
				}
				
				boolean flag = saveRemarks(value.toString());
				
				return flag;
			}
			
			private boolean saveRemarks( String remarks ) {
				boolean flag = false;
				try {
					saveRemarksImpl( remarks );
					flag = true;
					sendBroadcast(new Intent("rameses.clfc.REMARK_START_SERVICE"));
					/*start remarks date resolver
		 			getHandler().post(new Runnable() {
						public void run() {
							getApp().remarksDateResolverSvc.start();							
						}
					});
					*/	
				} catch (Throwable t) {
					t.printStackTrace();
					flag = false;
					UIDialog.showMessage(t, CollectionSheetInfoMainActivity.this); 	
				}
				
				if (flag == true) supportInvalidateOptionsMenu();
				
				return flag;
			}
			
			private void saveRemarksImpl( String remarks ) throws Exception {

				UserProfile profile = SessionContext.getProfile();
				if (profile == null) {
					throw new RuntimeException("Collector is required.");
				}
				
				String billingid = collectionsheet.getString( "billingid" );
				String itemid = collectionsheet.getString( "itemid" );
				String collectorid = profile.getUserId();
				String collectorname = profile.getFullName();

				String trackerid = settings.getTrackerid();
				if (trackerid == null) trackerid = "";

				String date = Platform.getApplication().getServerDate().toString();

				String borrowerid = collectionsheet.getString("borrower_objid");
				String borrowername = collectionsheet.getString("borrower_name");
				String loanappid = collectionsheet.getString("loanapp_objid");
				String loanappno = collectionsheet.getString("loanapp_appno");
				String routecode = collectionsheet.getString("routecode");
				String type = collectionsheet.getString("type");
				
				
				Map params = new HashMap();
				
				params.put("objid", objid);
				params.put("billingid", billingid);
				params.put("itemid", itemid);
				params.put("state", "PENDING");
				params.put("trackerid", trackerid);
				params.put("txndate", date);
				params.put("borrower_objid", borrowerid);
				params.put("borrower_name", borrowername);
				params.put("loanapp_objid", loanappid);
				params.put("loanapp_appno", loanappno);
				params.put("collector_objid", collectorid);
				params.put("collector_name", collectorname);
				params.put("routecode", routecode);
				params.put("remarks", remarks);
				params.put("type", type);
				params.put("forupload", 0);

				String lng = "0", lat = "0";
				Location location = NetworkLocationProvider.getLocation();
				if (location != null) {
					lng = location.getLongitude() + "";
					lat = location.getLatitude() + "";
				} else {
					Map prevlocation = prevlocationdb.getPrevLocation( trackerid );
					if (prevlocation != null && !prevlocation.isEmpty()) {
						if (prevlocation.containsKey("lng")) {
							lng = prevlocation.get("lng").toString();
						}
						if (prevlocation.containsKey("lat")) {
							lat = prevlocation.get("lat").toString();
						}
					}
				}
				
				params.put("lng", lng);
				params.put("lat", lat);
				
				//dtsaved
				Calendar cal = Calendar.getInstance();
				Date phonedate = new Timestamp(cal.getTimeInMillis());
				
				params.put("dtsaved", phonedate.toString());
				
				Map map = settings.getAll();
				
				long timedifference = 0L;
				if (map.containsKey("timedifference")) {
					timedifference = settings.getLong("timedifference");
				}
				params.put("timedifference", timedifference);
				
				String sql = ApplicationDBUtil.createInsertSQLStatement("remarks", params);
				
				SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
				try {
					remarksdb.beginTransaction();
					remarksdb.execSQL( sql );
					remarksdb.setTransactionSuccessful();
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				} finally {
					remarksdb.endTransaction();
					
				}
				
				params = new HashMap();
				params.put("objid", objid);
				params.put("billingid", billingid);
				params.put("itemid", itemid);
				params.put("remarks", remarks);
				params.put("collector_objid", collectorid);
				params.put("collector_name", collectorname);
				
				sql = ApplicationDBUtil.createInsertSQLStatement("remarks", params);

				SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
				try {
					appdb.beginTransaction();
					appdb.execSQL( sql );
					appdb.setTransactionSuccessful();
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				} finally {
					appdb.endTransaction();
				}
				
//				synchronized (MainDB.LOCK) {
//					clfcdb.insert("remarks", prm);
//				}

				
				ApplicationUtil.showShortMsg("Successfully added remark.");
			}
			
			
//			private boolean xsaveRemarks(String remarks) {
//				boolean flag = false;
//				
//				SQLTransaction clfcdb = new SQLTransaction("clfc.db");
//				SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");
//		 		DBContext ctx = new DBContext("clfctracker.db");
//		 		DBPrevLocation prevLocationSvc = new DBPrevLocation();
//		 		prevLocationSvc.setDBContext(ctx);
//		 		prevLocationSvc.setCloseable(false);
//		 		
//				try {
//					clfcdb.beginTransaction();
//					remarksdb.beginTransaction();
//					
//					saveRemarksImpl(clfcdb, remarksdb, prevLocationSvc, remarks);
//					flag = true;						
//					clfcdb.commit();
//					remarksdb.commit();	
//		 			getHandler().post(new Runnable() {
//						public void run() {
////							getApp().remarksSvc.start();
//							getApp().remarksDateResolverSvc.start();
//							
//
////							app.paymentDateResolverSvc.start();
//						}
//					});				
//				} catch (Throwable t) {
//					t.printStackTrace();
//					UIDialog.showMessage(t, CollectionSheetInfoMainActivity.this); 	
//					flag = false;
//				} finally {
//					clfcdb.endTransaction();
//					remarksdb.endTransaction();
//					ctx.close();
//				}
//				if (flag == true) supportInvalidateOptionsMenu();
//				
//				return flag;
//			}
			
//			private void saveRemarksImpl(SQLTransaction clfcdb, SQLTransaction remarksdb,
//					DBPrevLocation prevLocationSvc, String remarks) throws Exception {
//				Location location = NetworkLocationProvider.getLocation();
//				double lng = 0.00;
//				double lat = 0.00;
//
//				String trackerid = ((AppSettingsImpl) Platform.getApplication().getAppSettings()).getTrackerid();
//				
//				if (location != null) {
//					lng = location.getLongitude();
//					lat = location.getLatitude();
//					
//				} else {
//					Map prevLocation = prevLocationSvc.getPrevLocation(trackerid);
//					if (prevLocation != null) {
////						lng = MapProxy.getDouble(prevLocation, "longitude");
////						lat = MapProxy.getDouble(prevLocation, "latitude");
//						lng = Double.parseDouble(MapProxy.getString(prevLocation, "lng"));
//						lat = Double.parseDouble(MapProxy.getString(prevLocation, "lat"));
//					}
//				}
//				
//				String strLng = String.valueOf(lng), strLat = String.valueOf(lat);
//				
//				Map params = new HashMap();	
//				params.put("objid", objid);
//				params.put("billingid", collectionsheet.getString("billingid"));
//				params.put("itemid", collectionsheet.getString("itemid"));
//				params.put("state", "PENDING");
//				params.put("trackerid", trackerid);
//				params.put("txndate", Platform.getApplication().getServerDate().toString());
//				params.put("borrower_objid", collectionsheet.getString("borrower_objid"));
//				params.put("borrower_name", collectionsheet.getString("borrower_name"));
//				params.put("loanapp_objid", collectionsheet.getString("loanapp_objid"));
//				params.put("loanapp_appno", collectionsheet.getString("loanapp_appno"));
//				params.put("collector_objid", SessionContext.getProfile().getUserId());
//				params.put("collector_name", SessionContext.getProfile().getFullName());
//				params.put("routecode", collectionsheet.getString("routecode"));
//				params.put("remarks", remarks);
////				params.put("lng", lng);
////				params.put("lat", lat);
//				params.put("lng", strLng);
//				params.put("lat", strLat);
//				params.put("type", collectionsheet.getString("type"));	
//				params.put("forupload", 0);
//				Calendar cal = Calendar.getInstance();
//				
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
//				params.put("timedifference", timedifference);
//				
//				synchronized (RemarksDB.LOCK) {
//					remarksdb.insert("remarks", params);
//				}
//				
//				Map prm = new HashMap();
//				prm.put("objid", params.get("objid").toString());
//				prm.put("billingid", params.get("billingid").toString());
//				prm.put("itemid", params.get("itemid").toString());
//				prm.put("remarks", remarks);
//				prm.put("collector_objid", params.get("collector_objid").toString());
//				prm.put("collector_name", params.get("collector_name").toString());
//				
//				synchronized (MainDB.LOCK) {
//					clfcdb.insert("remarks", prm);
//				}
//				
//				ApplicationUtil.showShortMsg("Successfully added remark.");
//			}
			
		};
		dialog.input(null, "Remarks");
	}
}
