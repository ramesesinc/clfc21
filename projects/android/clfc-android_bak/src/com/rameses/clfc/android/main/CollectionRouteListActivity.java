package com.rameses.clfc.android.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.ControlActivity;
import com.rameses.clfc.android.MainDB;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.db.DBRouteService;
import com.rameses.client.android.Platform;
import com.rameses.client.android.SessionContext;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;

public class CollectionRouteListActivity extends ControlActivity 
{	
	private ListView lv_route;
	private LayoutInflater inflater;
	private DBContext clfcdb;
	private DBRouteService routeSvc = new DBRouteService();
	private List<Map> list;
	private List<Map> routes = new ArrayList<Map>();
	private TextView tv_billdate;
	private String str;
	private Map params;
	private Map itm;
	private int listSize;
	private Map route;
	private String billdate;
	
	protected void onCreateProcess(Bundle savedInstanceState) {
		setContentView(R.layout.template_footer);
		setTitle("Collection Sheet");

		RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.rl_container);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.activity_collectionsheet_route, rl_container, true);

		tv_billdate = (TextView) findViewById(R.id.tv_billdate);
		lv_route = (ListView) findViewById(R.id.lv_route);
		View header = (View) getLayoutInflater().inflate(R.layout.header_route, null);
		lv_route.addHeaderView(header, null, false);
	}
	
	protected void onStartProcess() {
		super.onStartProcess();
		
		getHandler().post(new Runnable() {
			public void run() {
				synchronized (MainDB.LOCK) {
					clfcdb = new DBContext("clfc.db"); 
					try {
						runImpl(clfcdb);
//						onStartImpl();
					} catch(Throwable e) {
						Platform.getLogger().log(e);
						System.out.println("[CollectionRouteListActivity] error caused by "+e.getClass().getName() + ": " + e.getMessage()); 
					}
				}
			}
			
			private void runImpl(DBContext clfcdb) throws Exception {
				billdate = "Collection Date: ";
				
				list = routeSvc.getRoutesByCollectorid(SessionContext.getProfile().getUserId());
				routes.clear();
//				System.out.println("billdate -> "+billdate);
				if (!list.isEmpty()) {
					str = ApplicationUtil.formatDate(Platform.getApplication().getServerDate(), "MMM dd, yyyy");//new java.text.SimpleDateFormat("MMM dd, yyyy").format(Platform.getApplication().getServerDate());
//					System.out.println("str -> "+str);
					billdate += str;
					listSize = list.size();
					for (int i=0; i<listSize; i++) {
						itm = (Map) list.get(i);
						
						params = new HashMap();
						params.put("code", itm.get("routecode").toString());
						params.put("description", itm.get("routedescription").toString());
						params.put("area", itm.get("routearea").toString());
						params.put("state", itm.get("state").toString());
						routes.add(params);
					}
				}
				tv_billdate.setText(billdate);
				lv_route.setAdapter(new RouteAdapter(CollectionRouteListActivity.this, routes));	
			}
		});
		lv_route.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedItem(parent, view, position, id);
			}
		}); 
	}
	
	private void selectedItem(AdapterView<?> parent, View view, int position, long id) {
		route = (Map) parent.getItemAtPosition(position);
		if (!route.get("state").toString().equals("REMITTED")) {
			Intent intent = new Intent(this, CollectionSheetListActivity.class);
			intent.putExtra("routecode", route.get("code").toString());
			startActivity(intent);
		}
	}
}
