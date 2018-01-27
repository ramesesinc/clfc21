package com.rameses.clfc.android.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.MainDB;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.db.DBCollectionSheet;
import com.rameses.clfc.android.db.DBPaymentService;
import com.rameses.clfc.android.db.DBVoidService;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.Task;
import com.rameses.client.android.UIDialog;
import com.rameses.client.interfaces.UserProfile;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.MapProxy;

public class CollectionSheetListFragment extends Fragment {

	private final int SIZE = 11;
	private final int POSITION_KEY = "position".hashCode();
	
	private int addToSize = 0, isfirstbill;
	private LinearLayout ll_cs;
	private EditText et_search;
	private Handler handler;
	private String collectiondate = "", segregationid;
	private List<Map> csList = new ArrayList<Map>();
	private LayoutInflater inflater;
	private MapProxy proxy;

	private DBPaymentService paymentSvc = new DBPaymentService();
	private DBVoidService voidSvc = new DBVoidService();
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.fragment_collectionsheet, container, false);

		Date date = Platform.getApplication().getServerDate();
		if (date != null) {
			collectiondate = ApplicationUtil.formatDate(date, "yyyy-MM-dd");
		}
		
		handler = new Handler();
		ll_cs = (LinearLayout) view.findViewById(R.id.ll_collectionsheet);
		et_search = (EditText) getActivity().findViewById(R.id.et_search);
		et_search.addTextChangedListener(new TextWatcher(){
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				addToSize = 0;
				loadCS(String.valueOf(s));
			}

			public void afterTextChanged(Editable s) {}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		});

		addToSize = 0; 

		ll_cs.removeAllViews();
		ll_cs.removeAllViewsInLayout();
		
		View child = inflater.inflate(R.layout.item_collectionsheet, null);
		((TextView) child.findViewById(R.id.tv_item_collectionsheet)).setText("Loading...");
		((ImageView) child.findViewById(R.id.iv_item_collectionsheet)).setVisibility(View.GONE);
		ll_cs.addView(child);
		
		return view;
	}	
	
	public void onStart() {
		super.onStart();
		Bundle args = getArguments();
		if (args.containsKey("TAG")) {
			reload(args.getString("TAG"));
		}
	}
	
	public void setAddToSize(int addToSize) {
		this.addToSize = addToSize;
	}
	
	void reload() {
		reload(segregationid);
	}
	
	public void reload(String segregationid) {
		if (segregationid != null) {
			this.segregationid = segregationid;
		}
		/*
		if (this.segregationid == null || this.segregationid.equals("")) {
			this.segregationid = segregationid;
		}
		*/
		//println("segregationid " + this.segregationid);
		//println("reload list with tag " + tag);
		loadCS(et_search.getText().toString());
	}
	
	private void loadCS(String searchtext) {
		loadCS(searchtext, segregationid);
	}
	
	private void loadCS(String searchtext, String segregationid) {
		//println("segregationid " + segregationid);
		handler.postAtTime(new LoadCSTask(searchtext, segregationid, (SIZE + addToSize), collectiondate, handler), 300);
	}
	
	void loadCSList(List<Map> list, int size, boolean eof) {
		csList = list;
		ll_cs.removeAllViews();
		ll_cs.removeAllViewsInLayout();

		View child;
		RelativeLayout.LayoutParams layoutParams;
		
		if (size > 0) {
			TextView tv_info_name, tv_route;
			ImageView iv_info_paid;
			int noOfPayments = 0, noOfVoids = 0;
			String date, cbsno;
			View overlay;
			for (int i=0; i<size; i++) {
				child = inflater.inflate(R.layout.item_collectionsheet, null);
				tv_info_name = (TextView) child.findViewById(R.id.tv_item_collectionsheet);
				iv_info_paid = (ImageView) child.findViewById(R.id.iv_item_collectionsheet);

				proxy = new MapProxy((Map) list.get(i));
				
				cbsno = proxy.getString("cbsno");
				
				if (cbsno == null || "".equals(cbsno)) {
					child.setClickable(true);
					child.setOnClickListener(collectionSheetOnClickListener);
					child.setOnLongClickListener(collectionSheetOnLongClickListener);	
				} else {
					child.setClickable(false);
					child.setOnClickListener(null);
					child.setOnLongClickListener(null);
					
					layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
					layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 1);
					overlay = inflater.inflate(R.layout.overlay_void_text, null);
					((TextView) overlay).setTextColor(this.getResources().getColor(R.color.red));
//					((TextView) overlay).setText(payment.get("refno").toString());
					((TextView) overlay).setText("REMITTANCE PENDING");
					overlay.setLayoutParams(layoutParams);
					((RelativeLayout) child).addView(overlay); 
				}
				child.setTag(POSITION_KEY, i);
				
				
				tv_info_name.setText(proxy.getString("borrower_name"));
				
				tv_route = (TextView) child.findViewById(R.id.tv_item_route);
				tv_route.setText(proxy.getString("route"));
				
				iv_info_paid.setVisibility(View.GONE);
				noOfPayments = proxy.getInteger("noofpayments");
				noOfVoids = proxy.getInteger("noofvoids");
				if (noOfPayments > 0 && noOfPayments > noOfVoids) {
					if (proxy.getInteger("isfirstbill") == 1) {
						proxy.put("isfirstbill", 0); 
					}
					iv_info_paid.setVisibility(View.VISIBLE);
				}
				ll_cs.addView(child);
			} 
			
			if (eof == false) {
				child = inflater.inflate(R.layout.item_string, null);
				
				child.setClickable(true);
				child.setOnClickListener(viewMoreOnClickListener);
				child.setOnLongClickListener(collectionSheetOnLongClickListener);
				
				TextView tv_str = (TextView) child.findViewById(R.id.tv_item_str);
				tv_str.setText("View more..");
				tv_str.setTextColor(getResources().getColor(R.color.blue));
				ll_cs.addView(child);
			}
		} else {
			boolean hasBilling = false;
			try {
				hasBilling = ApplicationUtil.hasBilling();
			} catch (Throwable t) {
				t.printStackTrace();
				hasBilling = false;
			}
			
			if (size == 0 && hasBilling == true) {				
				child = inflater.inflate(R.layout.item_string, null);
				
				child.setClickable(true);
				child.setOnClickListener(captureOnClickListener);
				child.setOnLongClickListener(collectionSheetOnLongClickListener);

				TextView tv_str = (TextView) child.findViewById(R.id.tv_item_str);
				tv_str.setText("Capture Payment");
//				tv_str.setTextColor(getResources().getColor(R.color.blue));
				ll_cs.addView(child);	
			}
		}
	}	
	private View.OnClickListener captureOnClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			v.setBackgroundResource(android.R.drawable.list_selector_background);
			println("capture payment");
			Intent intent = new Intent((CollectionSheetListActivity) getActivity(), CapturePaymentActivity.class);
			startActivity(intent);
		}
	};
	
	private View.OnClickListener viewMoreOnClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			v.setBackgroundResource(android.R.drawable.list_selector_background);
			addToSize += 5;
			reload();
		}
	};
	
	private View.OnClickListener collectionSheetOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			v.setBackgroundResource(android.R.drawable.list_selector_background);
			int idx = Integer.parseInt(v.getTag(POSITION_KEY).toString());
			
			final Map cs = (Map) csList.get(idx);
			isfirstbill = MapProxy.getInteger(cs, "isfirstbill");
//			System.out.println("cs " + cs);
			if (isfirstbill != 1) {
//				Intent intent = new Intent(CollectionSheetListActivity.this, XCollectionSheetInfoActivity.class);
				Intent intent = new Intent((CollectionSheetListActivity) getActivity(), CollectionSheetInfoMainActivity.class);
				intent.putExtra("objid", MapProxy.getString(cs, "objid"));
				startActivity(intent);
			} else if (isfirstbill == 1) {
				final String[] items = {"Schedule", "Overpayment"};
				UIDialog dialog = new UIDialog((CollectionSheetListActivity) getActivity()) {
					public void onSelectItem(int index) {
						String type = "schedule";
						if (index == 1) type = "over";
						synchronized (MainDB.LOCK) {
							SQLTransaction txn = new SQLTransaction("clfc.db");
							Map params = new HashMap();
							params.put("objid", MapProxy.getString(cs, "objid"));
							params.put("type", type);
							try {
								txn.beginTransaction();
								String sql = "UPDATE collectionsheet SET paymentmethod = $P{type} WHERE objid = $P{objid}";
								txn.execute(sql, params);
								txn.commit();
							} catch (Throwable t) {
								t.printStackTrace();
							} finally {
								txn.endTransaction();
							}
						}
						
//						Intent intent = new Intent(CollectionSheetListActivity.this, XCollectionSheetInfoActivity.class);
						Intent intent = new Intent((CollectionSheetListActivity) getActivity(), CollectionSheetInfoMainActivity.class);
						intent.putExtra("objid", MapProxy.getString(cs, "objid"));
						startActivity(intent);
					}
				};
				
				dialog.select(items);
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
	
	
	void println(String msg) {
		Log.i("CollectionSheetListFragment", msg);
	}
	
	private class LoadCSTask extends Task {
		
		private String searchtext, segregationid, date;
		private int size;
		private Handler handler;
		
		public LoadCSTask(String searchtext, String segregationid, int size, String date, Handler handler) {
			this.searchtext = searchtext;
			this.segregationid = segregationid;
			this.size = size;
			this.date = date;
			this.handler = handler;
		}
		
		public void run() {
			String st = et_search.getText().toString().trim();
			if (!searchtext.equals(st)) return;
			
			try {
				runImpl();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		private void runImpl() throws Exception {
			int totalcs = 0;
			DBCollectionSheet cs = new DBCollectionSheet();
			DBContext ctx = new DBContext("clfc.db");
			cs.setDBContext(ctx);
			
			UserProfile prof = SessionContext.getProfile();
			String collectorid = (prof != null? prof.getUserId() : "");

			if (searchtext == null) searchtext = "";
			if (collectorid == null) collectorid = "";
			if (date == null) date = "";
			if (segregationid == null) segregationid = "";
			
			try {
				totalcs = cs.countByDateAndCollectorWithSegregationid(date, collectorid, segregationid);
			} catch (Throwable t) {
				t.printStackTrace();
				UIDialog.showMessage(t, (CollectionSheetListActivity) getActivity());
			}
			
			
			searchtext = (!searchtext.equals("")? searchtext : "") + "%";
			Map params = new HashMap();
			params.put("searchtext", searchtext);
			params.put("collectorid", collectorid);
			params.put("date", date);
			params.put("segregationid", segregationid);
			
			ctx = new DBContext("clfc.db");
			cs.setDBContext(ctx);
			
			try {
				csList = cs.getCollectionSheetsByItemWithSearchtextAndSegregationid(params, size);
			} catch (Throwable t) {
				t.printStackTrace();
				UIDialog.showMessage(t, (CollectionSheetListActivity) getActivity());
			}
			
			int listSize = totalcs;
			boolean eof = true;
			
			if (csList.size() == size && csList.size() < totalcs) {
				listSize = csList.size() - 1;
				eof = false;
			} else if (csList.size() < size) {
				listSize = csList.size();
			}
			
			final List<Map> list = csList;
			final int s = listSize;
			final boolean bool = eof;
			handler.post(new Runnable() {
				public void run() {
					loadCSList(list, s, bool);
				}
			});
		}
	}
}
