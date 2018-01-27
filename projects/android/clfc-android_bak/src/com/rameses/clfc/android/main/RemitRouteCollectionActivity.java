package com.rameses.clfc.android.main;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.ControlActivity;
import com.rameses.clfc.android.MainDB;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.db.DBRouteService;
import com.rameses.client.android.SessionContext;
import com.rameses.client.android.UIDialog;
import com.rameses.db.android.DBContext;
import com.rameses.db.android.SQLTransaction;
import com.rameses.util.MapProxy;

public class RemitRouteCollectionActivity extends ControlActivity 
{
	private final int POSITION_KEY = "position".hashCode(); 
	
	private ProgressDialog progressDialog;
	private LayoutInflater inflater;
	private List<Map> routes;
	private SQLTransaction txn;
	private DBRouteService routeSvc = new DBRouteService();
//	private ListView lv_routes;
	private LinearLayout ll_routes;
	private LinearLayout ll_followups;
	private RelativeLayout rl_followups;
	private List<Map> list;
	private Map params;
	private Map itm;
	private int listSize;
	private Map item;
	private Map route;
	
	@Override
	protected void onCreateProcess(Bundle savedInstanceState) {
		setContentView(R.layout.template_footer);
		setTitle("Select a Route to remit");
		
		RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.rl_container);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		inflater.inflate(R.layout.activity_routelist, rl_container, true);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		
		rl_followups = (RelativeLayout) findViewById(R.id.rl_followups);
		rl_followups.setVisibility(View.GONE);
		
//		lv_routes = (ListView) findViewById(R.id.lv_route);
		((TextView) findViewById(R.id.tv_header_route)).setText("Route:");
		ll_routes = (LinearLayout) findViewById(R.id.ll_routes);
	}
	
	protected void onStartProcess() {
		super.onStartProcess();
		loadRoutes();
	}
	
	public void loadRoutes() {
		getHandler().post(new Runnable() {
			public void run() {
				synchronized (MainDB.LOCK) {
					DBContext ctx = new DBContext("clfc.db");
					routeSvc.setDBContext(ctx);
					try {
						routes = routeSvc.getRoutesByCollectorid(SessionContext.getProfile().getUserId());
					} catch (Throwable t) {
						UIDialog.showMessage(t, RemitRouteCollectionActivity.this);
					}
				}
				
				try {
					ll_routes.removeAllViews();
					ll_routes.removeAllViewsInLayout();
					int size = routes.size();
					View child;
					MapProxy item;
					TextView tv_description;
					TextView tv_area;
					RelativeLayout.LayoutParams layoutParams;
					for (int i=0; i<size; i++) {
						item = new MapProxy((Map) routes.get(i));
						item.put("code", item.getString("routecode"));
						item.put("area", item.getString("routearea"));
						item.put("description", item.getString("routedescription"));
						
						child = inflater.inflate(R.layout.item_route, null);
						if (!"REMITTED".equals(item.getString("state"))) {
							child.setClickable(true);
							child.setOnClickListener(routeOnClickListener);
							child.setOnLongClickListener(routeOnLongClickListener);
							child.setTag(POSITION_KEY, i);
						} else {
							addRemittedOverlay(child);
						}						

						tv_description = (TextView) child.findViewById(R.id.tv_route_description);
						tv_area = (TextView) child.findViewById(R.id.tv_route_area);

						layoutParams = (RelativeLayout.LayoutParams) tv_description.getLayoutParams();
						layoutParams.leftMargin = 5;
						tv_description.setLayoutParams(layoutParams);
						
						layoutParams = (RelativeLayout.LayoutParams) tv_area.getLayoutParams();
						layoutParams.leftMargin = 5;
						tv_area.setLayoutParams(layoutParams);
//						layoutParams.leftMargin = 5;
//						tv_description.setLayoutParams(layoutParams);
//						tv_area.setLayoutParams(layoutParams);
						
						tv_description.setText(item.getString("description"));
						tv_area.setText(item.getString("area"));
						ll_routes.addView(child);
					}
//					lv_route.setAdapter(new RouteAdapter(RouteListActivity.this, routes));
				} catch (Throwable t) {
					UIDialog.showMessage(t, RemitRouteCollectionActivity.this);
				}
			}
		});
	}	

	private View.OnClickListener routeOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			view.setBackgroundResource(android.R.drawable.list_selector_background);
//			view.setPadding(5, 0, 0, 0);
			int idx = Integer.parseInt(view.getTag(POSITION_KEY).toString());
//			System.out.println("index-> "+idx);
			route = (Map) routes.get(idx);
			
			if (!"REMITTED".equals(route.get("state")+"")) {
				UIDialog dialog = new UIDialog() {
					public void onApprove() {
						remit(route);
					}
				};
				dialog.confirm("You are about to remit collections for this route. Continue?");
			}
		}
	};
	
	private View.OnLongClickListener routeOnLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View view) {
			// TODO Auto-generated method stub
			view.setBackgroundResource(android.R.drawable.list_selector_background);
			return false;
		}
	};

	private void addRemittedOverlay(View child) {
		View overlay = inflater.inflate(R.layout.overlay_void_text, null);
		((TextView) overlay).setTextColor(this.getResources().getColor(R.color.red));
		((TextView) overlay).setText("REMITTED");
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 1);
		overlay.setLayoutParams(layoutParams);
		((RelativeLayout) child).addView(overlay);
	}
	
	private void selectedItem(AdapterView<?> parent, View view, int position, long id) {
		item = (Map) parent.getItemAtPosition(position);
		final Map route = item;
		if (!item.get("state").equals("REMITTED")) {
			UIDialog dialog = new UIDialog() {
				public void onApprove() {
					remit(route);
				}
			};
			dialog.confirm("You are about to remit collections for this route. Continue?");
		}
	}
	
	private void remit(Map route) {
		try {
			new RemitRouteCollectionController(this, progressDialog, route).execute();
		} catch (Throwable t) {
			UIDialog.showMessage(t, RemitRouteCollectionActivity.this);
		}
	}
//	
}
