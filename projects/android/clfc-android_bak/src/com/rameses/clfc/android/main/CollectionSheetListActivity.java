package com.rameses.clfc.android.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rameses.clfc.android.ControlActivity;
import com.rameses.clfc.android.MainDB;
import com.rameses.clfc.android.PaymentDB;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.VoidRequestDB;
import com.rameses.clfc.android.db.DBCollectionSheet;
import com.rameses.clfc.android.db.DBPaymentService;
import com.rameses.clfc.android.db.DBVoidService;
import com.rameses.client.android.UIDialog;
import com.rameses.db.android.DBContext;
import com.rameses.util.MapProxy;

public class CollectionSheetListActivity extends ControlActivity 
{
	private final int SIZE = 11;
	private final int POSITION_KEY = "position".hashCode();
	
//	private ListView lv_collectionsheet;
	private LinearLayout ll_collectionsheet;
	private String routecode = "";
	private String type = "";
	private Map item;
	private int isfirstbill;
	private DBCollectionSheet collectionSheet = new DBCollectionSheet();
	private DBPaymentService paymentSvc = new DBPaymentService();
	private DBVoidService voidSvc = new DBVoidService();
	private LayoutInflater inflater;
	private MapProxy proxy;
	private EditText et_search;
	private int totalCollectionSheets = 0;
	private int addSize = 0;
	private List<Map> collectionSheets;
	private ProgressDialog progressDialog;
//	private LinearLayout ll_collectionsheet;
	
	@Override
	protected void onCreateProcess(Bundle savedInstanceState) {
		setContentView(R.layout.template_footer);
		setTitle("Collection Sheet");
		
		RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.rl_container);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.activity_collectionsheet, rl_container, true);
		
		Intent intent = getIntent();
		routecode = intent.getStringExtra("routecode");
		et_search = (EditText) findViewById(R.id.et_search);
//		lv_collectionsheet = (ListView) findViewById(R.id.lv_collectionsheet);
		ll_collectionsheet = (LinearLayout) findViewById(R.id.ll_collectionsheet);
		progressDialog = new ProgressDialog(this);
		
		addSize = 0;
	}
	
	@Override
	protected void onStartProcess() {
		super.onStartProcess();

		synchronized (MainDB.LOCK) {
			DBContext ctx = new DBContext("clfc.db");
			DBCollectionSheet collectionSheet = new DBCollectionSheet();
			collectionSheet.setDBContext(ctx);
			try {
				totalCollectionSheets = collectionSheet.getCountByRoutecode(routecode);
			} catch (Throwable t) {
				UIDialog.showMessage(t, CollectionSheetListActivity.this);
			}
		}
		
		et_search.setSelection(0, et_search.getText().toString().length());
		et_search.requestFocus();
		et_search.addTextChangedListener(new TextWatcher(){
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
//				size = 11;
				addSize = 0;
				loadCollectionSheets(s+"");
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
		});
		
		loadCollectionSheets(et_search.getText().toString());
//		lv_collectionsheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				// TODO Auto-generated method stub
//				selectedItem(parent, view, position, id);
////				CollectionSheetParcelable cs = (CollectionSheetParcelable) parent.getItemAtPosition(position);
////				
////				if (cs.getIsfirstbill() == 1) showDialog(cs);
////				else showCollectionSheetInfo(cs);
//				//System.out.println(cs.getAcctname());
//			}
//		});
	}
	
	private void loadCollectionSheets(String searchtext) {
		Map params = new HashMap();
		params.put("routecode", routecode);
		searchtext = (!searchtext.equals("")? searchtext+"%" : "%");
		params.put("searchtext", searchtext);
		
		synchronized (MainDB.LOCK) {
			DBContext clfcdb = new DBContext("clfc.db");
			collectionSheet.setDBContext(clfcdb);
			try {
				progressDialog.setMessage("processing..");
				if (!progressDialog.isShowing()) progressDialog.show();
				collectionSheets = collectionSheet.getCollectionSheetsByRoutecodeAndSearchtext(params, (SIZE+addSize));
				//System.out.println("collectionsheets -> "+collectionSheets);
				int listSize = collectionSheets.size();
				for (int i=0; i<listSize; i++) {
					System.out.println(collectionSheets.get(i));
				}
				loadCollectionSheetsImpl(collectionSheets);
//				lv_collectionsheet.setAdapter(new CollectionSheetAdapter(this, list));
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				if (progressDialog.isShowing()) progressDialog.dismiss();
			}
		}
//		DBCollectionSheet dbCs = new DBCollectionSheet();		
//		try {
//			List<Map> list = dbCs.getCollectionSheetsByRoutecodeAndSearchtext(params);//txn.getList(, params);
//			lv_collectionsheet.setAdapter(new CollectionSheetAdapter(this, list));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}	
//	
	private void loadCollectionSheetsImpl(List<Map> list) throws Exception {
		System.out.println("total collection sheets -> "+totalCollectionSheets);
		int listSize = totalCollectionSheets;
		boolean eof = true;
		
		if (list.size() == (SIZE+addSize) && list.size() < totalCollectionSheets) {
			listSize = list.size()-1;
//			System.out.println("list size-> "+listSize);
			eof = false;
		} else if (list.size() < (SIZE+addSize)) {
			listSize = list.size();
		}
//		System.out.println("list size-> "+list.size());
		Map map;
		String loanappid;
		synchronized (PaymentDB.LOCK) {
			DBContext paymentdb = new DBContext("clfcpayment.db");
			paymentSvc.setDBContext(paymentdb);
			paymentSvc.setCloseable(false);
			try {
				for (int i=0; i<listSize; i++) {
					map = (Map) list.get(i);
					
					loanappid = map.get("loanappid").toString();
					map.put("noOfPayments", paymentSvc.noOfPaymentsByLoanappid(loanappid));
				}
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				paymentdb.close();
			}
		}
		
		synchronized (VoidRequestDB.LOCK) {
			DBContext requestdb = new DBContext("clfcrequest.db");
			voidSvc.setDBContext(requestdb);
			voidSvc.setCloseable(false);
			try {
				for (int i=0; i<listSize; i++) {
					map = (Map) list.get(i);
					
					loanappid = map.get("loanappid").toString();
					map.put("noOfVoids", voidSvc.noOfVoidPaymentsByLoanappid(loanappid));
				}
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				requestdb.close();
			}
		}
		
//		int total = 0;
//		synchronized (MainDB.LOCK) {
//			DBContext clfcdb = new DBContext("clfc.db");
//			collectionSheet.setDBContext(clfcdb);
//			try {
//				total = collectionSheet.getCountByRoutecode(routecode);
//			} catch (Throwable t) {
//				t.printStackTrace();
//			}
//		}

//		listSize = list.size();
//		boolean eof = true;
//		if (list.size() < total) {
//			listSize = list.size()-1;
//			eof = false;
//		}
		
//		lv_collectionsheet.removeAllViews();
//		lv_collectionsheet.removeAllViewsInLayout();
		ll_collectionsheet.removeAllViews();
		ll_collectionsheet.removeAllViewsInLayout();

		View child;
		TextView tv_info_name;
		ImageView iv_info_paid;
		int noOfPayments = 0;
		int noOfVoids = 0;
		for (int i=0; i<listSize; i++) {
			child = inflater.inflate(R.layout.item_collectionsheet, null);
			tv_info_name = (TextView) child.findViewById(R.id.tv_item_collectionsheet);
			iv_info_paid = (ImageView) child.findViewById(R.id.iv_item_collectionsheet);
			
			child.setClickable(true);
			child.setOnClickListener(collectionSheetOnClickListener);
			child.setOnLongClickListener(collectionSheetOnLongClickListener);
			child.setTag(POSITION_KEY, i);
			
			proxy = new MapProxy((Map) list.get(i));
			tv_info_name.setText(proxy.getString("acctname"));
			

			iv_info_paid.setVisibility(View.GONE);
//			if (item.get("acctname").toString().equals("ARNADO, MARICRIS")) {
//				System.out.println("acctname-> "+item.get("acctname").toString());
//				System.out.println("no of voids -> "+noOfVoids+" no of payments -> "+noOfPayments);
//			}
			noOfPayments = proxy.getInteger("noOfPayments");
			noOfVoids = proxy.getInteger("noOfVoids");
			if (noOfPayments > 0 && noOfPayments > noOfVoids) {
//				System.out.println("item-> "+item);
//				System.out.println("is first bill-> "+item.get("isfirstbill"));
//				
				if (proxy.getInteger("isfirstbill") == 1) {
					proxy.put("isfirstbill", 0); 
				}
//				if (MapProxy.getInteger(item, "isfirstbill") == 1) {
//					item.put("isfirstbill", 0);
//				}
				iv_info_paid.setVisibility(View.VISIBLE);
			}

//			addViewProperties(view);
			ll_collectionsheet.addView(child);
		} 
		
		if (eof == false) {
			child = inflater.inflate(R.layout.item_string, null);
			
			child.setClickable(true);
			child.setOnClickListener(viewMoreOnClickListener);
			child.setOnLongClickListener(collectionSheetOnLongClickListener);
			
			TextView tv_str = (TextView) child.findViewById(R.id.tv_item_str);
			tv_str.setText("View more..");
			tv_str.setTextColor(getResources().getColor(R.color.blue));
			ll_collectionsheet.addView(child);
		}
	}
	
	private View.OnClickListener viewMoreOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			v.setBackgroundResource(android.R.drawable.list_selector_background);
			addSize += 5;
			loadCollectionSheets(et_search.getText().toString());
		}
	};
	
	private View.OnClickListener collectionSheetOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			v.setBackgroundResource(android.R.drawable.list_selector_background);
			int idx = Integer.parseInt(v.getTag(POSITION_KEY).toString());
			
			Map cs = (Map) collectionSheets.get(idx);
			System.out.println("collection sheet-> "+cs);
			isfirstbill = Integer.parseInt(cs.get("isfirstbill").toString());
//			System.out.println("cs-> "+item);
//			boolean hasnext = MapProxy.getBoolean(item, "hasnext");
//			if (hasnext == false) {
				if (isfirstbill != 1) {
					Intent intent = new Intent(CollectionSheetListActivity.this, CollectionSheetInfoActivity.class);
					intent.putExtra("acctname", cs.get("acctname").toString());
					intent.putExtra("loanappid", cs.get("loanappid").toString());
					intent.putExtra("detailid", cs.get("detailid").toString());
					intent.putExtra("appno", cs.get("appno").toString());
					intent.putExtra("amountdue", Double.parseDouble(cs.get("amountdue").toString()));
					intent.putExtra("routecode", cs.get("routecode").toString());
					intent.putExtra("paymenttype", cs.get("paymentmethod").toString());
					intent.putExtra("isfirstbill", Integer.parseInt(cs.get("isfirstbill").toString()));
					startActivity(intent);
				} else if (isfirstbill == 1) {
					showPaymentTypeDialog(cs);
				}
		}
	};
	
	private View.OnLongClickListener collectionSheetOnLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			v.setBackgroundResource(android.R.drawable.list_selector_background);
			return false;
		}
	};
	
//	private void addViewProperties(View view) {
//		view.setClickable(true);
//		view.setOnLongClickListener(new View.OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View v) {
//				// TODO Auto-generated method stub
//				v.setBackgroundResource(android.R.drawable.list_selector_background);
//				return false;
//			}
//		});
//		view.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				v.setBackgroundResource(android.R.drawable.list_selector_background);
//			}
//		});
//	}
	
	private void showPaymentTypeDialog(final Map map) {
		CharSequence[] items = {"Schedule", "Overpayment"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Payment Method");
		builder.setPositiveButton("OK", null);
		builder.setNegativeButton("Cancel", null);
		builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch(which) {
					case 0: type = "schedule"; break;
					case 1: type = "over"; break;
				}
			}
		});
		
		final AlertDialog dialog = builder.create();
		System.out.println("payment type dialog = "+dialog);
		dialog.show();	
		Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		b.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				showCollectionSheetInfo(map);
			}
		});
	}
	
	private void showCollectionSheetInfo(Map map) {
		Intent intent = new Intent(this, CollectionSheetInfoActivity.class);
		intent.putExtra("acctname", map.get("acctname").toString());
		intent.putExtra("loanappid", map.get("loanappid").toString());
		intent.putExtra("detailid", map.get("detailid").toString());
		intent.putExtra("appno", map.get("appno").toString());
		intent.putExtra("amountdue", Double.parseDouble(map.get("amountdue").toString()));
		intent.putExtra("routecode", routecode);
		intent.putExtra("paymenttype", type);
		intent.putExtra("isfirstbill", Integer.parseInt(map.get("isfirstbill").toString()));
		startActivity(intent);		
	}
//
//	private void loadCollectionsheets(Cursor list) {
//		ArrayList<CollectionSheetParcelable> collectionsheets=new ArrayList<CollectionSheetParcelable>();
//		CollectionSheetParcelable cs;
//		if(list.getCount() > 0) {
//			list.moveToFirst();
//			do {
//				cs=new CollectionSheetParcelable();
//				cs.setLoanappid(list.getString(list.getColumnIndex("loanappid")));
//				cs.setDetailid(list.getString(list.getColumnIndex("detailid")));
//				cs.setAppno(list.getString(list.getColumnIndex("appno")));
//				cs.setAcctname(list.getString(list.getColumnIndex("acctname")));
//				cs.setAmountdue(list.getDouble(list.getColumnIndex("amountdue")));
//				cs.setIsfirstbill(list.getInt(list.getColumnIndex("isfirstbill")));
//				collectionsheets.add(cs);
//			} while(list.moveToNext());
//			list.close();
//		}
//		
//		lv_collectionsheet.setAdapter(new CollectionSheetAdapter(context, collectionsheets));
//	}
}
