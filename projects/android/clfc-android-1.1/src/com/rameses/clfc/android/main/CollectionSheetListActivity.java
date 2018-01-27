package com.rameses.clfc.android.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.ControlActivity;
import com.rameses.clfc.android.MainDB;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.db.DBSegregation;
import com.rameses.client.android.Platform;
import com.rameses.client.android.UIDialog;
import com.rameses.db.android.DBContext;
import com.rameses.util.MapProxy;

public class CollectionSheetListActivity extends ControlActivity {

	private ViewPager tab;
	private ActionBar actionBar;
	private CollectionSheetListTabPagerAdapter tabAdapter;
	
	protected void onCreateProcess(Bundle savedInstanceState) {
		super.onCreateProcess(savedInstanceState);
		
		setTitle("CLFC Collection - ILS");
		setContentView(R.layout.template_footer);
		RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.rl_container);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.activity_collectionsheet_tab, rl_container, true);

		Date date = Platform.getApplication().getServerDate();
		if (date != null) {
			((TextView) findViewById(R.id.tv_date)).setText("Collection Date: " + ApplicationUtil.formatDate(date, "MMM dd, yyyy"));
		}
		
		List list = new ArrayList();
		synchronized (MainDB.LOCK) {
			DBContext ctx = new DBContext("clfc.db");
			DBSegregation svc = new DBSegregation();
			svc.setDBContext(ctx);

			try {
				//collectionsheet = new MapProxy(collectionsheetdb.findCollectionSheet(objid));
				list = svc.getList();
			} catch (Throwable t) {
				t.printStackTrace();
				UIDialog.showMessage(t, CollectionSheetListActivity.this);
			}
			
		}
		int size = list.size();
		
		tabAdapter = new CollectionSheetListTabPagerAdapter(getSupportFragmentManager());
		Map params = new HashMap();
		if (size > 0) {
			Map map = (Map) list.get(0);
			params.put("TAG", MapProxy.getString(map, "objid"));
		}
		tabAdapter.setParams(params);
		
		tab = (ViewPager) findViewById(R.id.pager);
		tab.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				actionBar = getSupportActionBar();
				actionBar.setSelectedNavigationItem(position);
				
				//reloadList(actionBar.getTabAt(position).getTag().toString());
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
				reloadList(t.getTag().toString());
			}

			public void onTabUnselected(Tab t, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}
		};

		if (size > 0) {
			Map map = new HashMap();
			String name, tag;
			for (int i=0; i<size; i++) {
				map = (Map) list.get(i);
				name = MapProxy.getString(map, "name");
				//tag = name.replaceAll("\\s", "_");
				tag = MapProxy.getString(map, "objid");
				
				actionBar.addTab(actionBar.newTab().setTag(tag).setText(name).setTabListener(tabListener));
			}
		}
	}
	
	
	protected void onStartProcess() {
		super.onStartProcess();
		
		reloadList();
//		Log.i("CollectionSheetListFragment", "current fragment " + tabAdapter.getCurrentFragment());
//		Log.i("CollectionSheetListFragment", "tab tag " + );
	}
	
	private void reloadList() {
		Fragment fragment = tabAdapter.getCurrentFragment();
		if (fragment != null) {
			String segregationid = (actionBar.getSelectedTab() != null? actionBar.getSelectedTab().getTag().toString() : "");
			reloadList(segregationid);
		}
	}
	
	private void reloadList(String segregationid) {
		Fragment fragment = tabAdapter.getCurrentFragment();
		if (fragment instanceof CollectionSheetListFragment) {
			CollectionSheetListFragment csFragment = (CollectionSheetListFragment) fragment;
			csFragment.setAddToSize(0);
			csFragment.reload(segregationid);
		}
	}
	
	void println(String msg) {
		Log.i("CollectionSheetListActivity", msg);
	}
}
