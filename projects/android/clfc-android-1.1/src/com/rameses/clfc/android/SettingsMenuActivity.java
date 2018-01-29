package com.rameses.clfc.android;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.rameses.clfc.android.system.SettingsActivity;

public class SettingsMenuActivity extends ControlActivity 
{
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.settings:
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				break;
			case R.id.datestatus:
				ApplicationUtil.showServerDateStatus(this);
				break;
		}
//		if (item.getItemId() == R.id.settings) {
//			Intent intent = new Intent(this, SettingsActivity.class);
//			startActivity(intent);
//		}
		return true;
	}
}
