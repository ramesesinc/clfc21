package com.rameses.clfc.android.main;

import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.rameses.clfc.android.ControlActivity;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.db.DBCapturePayment;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIDialog;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.db.android.DBContext;

public class CaptureActivity extends ControlActivity 
{
	private ProgressDialog progressDialog;
	private LayoutInflater inflater;
	private ListView lv_capture;
	private DBCapturePayment capturePayment = new DBCapturePayment();
	private List<Map> list;
	
	@Override
	protected void onCreateProcess(Bundle savedInstanceState) {
		setTitle("CLFC Collection - ILS");
		setContentView(R.layout.template_footer);
		
		RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.rl_container);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.activity_capture, rl_container, true);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		lv_capture = (ListView) findViewById(R.id.lv_capture);
	}
	
	@Override
	protected void onStartProcess() {
		super.onStartProcess();
		loadPayments();
//		lv_specialcollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				// TODO Auto-generated method stub
//				try {
//					selectedItem(parent, view, position, id);
//				} catch (Throwable t) {
//					UIDialog.showMessage(t, CapturePaymentActivity.this);
//				}
////				SpecialCollectionParcelable scp = (SpecialCollectionParcelable) parent.getItemAtPosition(position);
////				progressDialog.setMessage("Downloading collection sheet(s) requested.");
////				if (!progressDialog.isShowing()) progressDialog.show();
////				Executors.newSingleThreadExecutor().submit(new SpecialCollectionRunnable(scp));
//			}
//		});
	}
	
	public void loadPayments() {
		getHandler().post(new Runnable() {
			public void run() {
//				synchronized (MainDB.LOCK) {
					DBContext ctx = new DBContext("clfccapture.db");
//					txn = new SQLTransaction("clfc.db");
					try {
						runImpl(ctx);
					} catch (Throwable t) {
						UIDialog.showMessage(t, CaptureActivity.this);
					} finally {
						ctx.close();
					}
//				}
			}
			
			private void runImpl(DBContext ctx) throws Exception {
				capturePayment.setDBContext(ctx);
				capturePayment.setCloseable(false);
				
				UserProfile prof = SessionContext.getProfile();
				String collectorid = (prof != null? prof.getUserId() : "");
				
				list = capturePayment.getPaymentsByCollector(collectorid); 
				
				lv_capture.setAdapter(new CapturePaymentAdapter(CaptureActivity.this, list));
//				
//				list = specialCollection.getSpecialCollectionRequestsByCollector(SessionContext.getProfile().getUserId());
//				
//				System.out.println("list " + list);
//				lv_specialcollection.setAdapter(new SpecialCollectionAdapter(CapturePaymentActivity.this, list));
//				
//				SQLiteDatabase db = getDbHelper().getReadableDatabase();
//				Cursor result = getDbHelper().getSpecialCollections(db);
//				db.close();
//				ListView lv_specialcollection = (ListView) findViewById(R.id.lv_specialcollection);
		//
//				ArrayList<SpecialCollectionParcelable> list = new ArrayList<SpecialCollectionParcelable>();
//				if (result != null && result.getCount() > 0) {
//					result.moveToFirst();
//					SpecialCollectionParcelable scp = null;
//					do {
//						scp = new SpecialCollectionParcelable();
//						scp.setObjid(result.getString(result.getColumnIndex("objid")));
//						scp.setState(result.getString(result.getColumnIndex("state")));
//						scp.setRemarks(result.getString(result.getColumnIndex("remarks")));
//						list.add(scp);
//					} while(result.moveToNext());
//					result.close();
//				}
//				
//				lv_specialcollection.setAdapter(new SpecialCollectionAdapter(context, list));
//				lv_specialcollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//					@Override
//					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//						// TODO Auto-generated method stub
//						SpecialCollectionParcelable scp = (SpecialCollectionParcelable) parent.getItemAtPosition(position);
//						progressDialog.setMessage("Downloading collection sheet(s) requested.");
//						if (!progressDialog.isShowing()) progressDialog.show();
//						Executors.newSingleThreadExecutor().submit(new SpecialCollectionRunnable(scp));
//					}
//				});
			}
		});
	}
	
//	private void selectedItem(AdapterView<?> parent, View view, int position, long id) throws Exception {
//		item = (Map) parent.getItemAtPosition(position);
//		if (!item.get("state").toString().equals("DOWNLOADED")) {
//			new DownloadSpecialCollectionRequestController(this, progressDialog, item).execute();
//		}
//	}
	
//	private Handler responseHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			Bundle bundle = msg.getData();
//			if (progressDialog.isShowing()) progressDialog.dismiss();
//			ApplicationUtil.showShortMsg(context, bundle.getString("response"));
//		}
//	};
//	
//	private Handler specialCollectionHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			Bundle bundle = msg.getData();
//			ArrayList collectionsheets = (ArrayList) bundle.getParcelableArrayList("collectionsheets");
//			ArrayList routes = (ArrayList) bundle.getParcelableArrayList("routes");
//			
//			Map<String, Object> map;
//			SQLiteDatabase db = getDbHelper().getWritableDatabase();
//			for (int i=0; i<routes.size(); i++) {
//				map = (Map<String, Object>) routes.get(i);
//				if (getDbHelper().findRouteByCode(db, map.get("routecode").toString()).getCount() == 0) {
//					getDbHelper().insertRoute(db, map);
//				} 
//			}
//			for (int i=0; i<collectionsheets.size(); i++) {
//				map = (Map<String, Object>) collectionsheets.get(i);
//				map.put("seqno", getDbHelper().countCollectionSheetsByRoutecode(db, map.get("routecode").toString())+1);
//				map.put("type", "SPECIAL");
//				getDbHelper().insertCollectionsheet(db, map);
//			}
//			//getDbHelper().insertSession(db, bundle.getString("sessionid"));
//			getDbHelper().approveSpecialCollection(db, bundle.getString("requestid"));
//			db.close();
//			if (progressDialog.isShowing()) progressDialog.dismiss();
//			loadRequests();
//		}
//	};
//	private class SpecialCollectionRunnable implements Runnable {
//		private SpecialCollectionParcelable scp;
//		
//		public SpecialCollectionRunnable(SpecialCollectionParcelable scp) {
//			this.scp = scp;
//		}
//		@Override
//		public void run() {
//			Message msg = responseHandler.obtainMessage();
//			Bundle bundle = new Bundle();
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("objid", scp.getObjid());
//			Boolean status = false;
//			ServiceProxy postingProxy = ApplicationUtil.getServiceProxy(context, "DevicePostingService");
//			try {
//				Object response = postingProxy.invoke("downloadSpecialCollection", new Object[]{params});
//				Map<String, Object> result = (Map<String, Object>) response;
//				bundle.putParcelableArrayList("routes", ((ArrayList<RouteParcelable>) result.get("routes")));
//				bundle.putParcelableArrayList("collectionsheets", ((ArrayList<CollectionSheetParcelable>) result.get("list")));
//				Map<String, Object> request = (Map<String, Object>) result.get("request");
//				bundle.putString("sessionid", request.get("billingid").toString());
//				bundle.putString("requestid", request.get("objid").toString());
//				msg = specialCollectionHandler.obtainMessage();
//				status = true;
//			}
//			catch( TimeoutException te ) {
//				bundle.putString("response", "Connection Timeout!");
//			}
//			catch( IOException ioe ) {
//				bundle.putString("response", "Error connecting to Server.");
//			}
//			catch( Exception e ) { 
//				bundle.putString("response", e.getMessage());
//				e.printStackTrace();
//			}
//			finally {
//				msg.setData(bundle);
//				if(status == true) specialCollectionHandler.sendMessage(msg);
//				else responseHandler.sendMessage(msg);
//			}
//		}
//	}
	
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		menu.clear();
//		getMenuInflater().inflate(R.menu.capture, menu);
//		return super.onPrepareOptionsMenu(menu);
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem menuItem) {
//		if (menuItem.getItemId() == R.id.capturepayment_add) {
//			Intent intent = new Intent(this, CapturePaymentActivity.class);
//			startActivity(intent);
//		}
//		return true;
//	}
	
//	private void showSpecialCollectionDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("Special Collection Request");
//		builder.setView(inflater.inflate(R.layout.dialog_remarks, null));
//		builder.setPositiveButton("Ok", null);
//		builder.setNegativeButton("Cancel", null);
//		final AlertDialog dialog = builder.create();
//		dialog.show();
//		Button btn_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
//		btn_positive.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				// TODO Auto-generated method stub
//				try {
//					String remarks = ((EditText) dialog.findViewById(R.id.remarks_text)).getText().toString();
//
//					progressDialog.setMessage("processing..");
//					getHandler().post(new Runnable() {
//						public void run() {
//							if (!progressDialog.isShowing()) progressDialog.show();
//						}
//					});
//					
//					new SpecialCollectionRequestController(CaptureActivity.this, progressDialog, remarks, dialog).execute();
//				} catch (Throwable t) {
//					ApplicationUtil.showShortMsg("[ERROR] "+t.getMessage());
//				}
////				SQLTransaction txn = new SQLTransaction("clfc.db");
////				try {
////					txn.beginTransaction();
////					onClickImpl(txn, view);
////					txn.commit();
////				} catch (Throwable t) {
////					UIDialog.showMessage(t, SpecialCollectionActivity.this);
////				} finally {
////					txn.endTransaction();
////				}
//			}
//		});
//	}
	
}
