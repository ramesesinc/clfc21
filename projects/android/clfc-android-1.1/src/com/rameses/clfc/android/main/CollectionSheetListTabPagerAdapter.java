package com.rameses.clfc.android.main;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.rameses.util.MapProxy;

public class CollectionSheetListTabPagerAdapter 
	extends FragmentStatePagerAdapter {	

	private Map<String, Object> params = new HashMap<String, Object>();
	private Fragment currentFragment;
	 
	public CollectionSheetListTabPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	@Override
	public Fragment getItem(int idx) {
		Bundle args = new Bundle();
		Fragment fragment = new CollectionSheetListFragment();
		if (currentFragment == null && params.containsKey("TAG")) {
			args.putString("TAG", MapProxy.getString(params, "TAG"));
		}
		fragment.setArguments(args);
		currentFragment = fragment;
		return fragment;
	}
	
	public Fragment getCurrentFragment() { return currentFragment; }

	@Override
	public int getCount() {
		return 2;
	}
	
	void println(String msg) {
		Log.i("CollectionSheetListTabPagerAdapter", msg);
	}
}
