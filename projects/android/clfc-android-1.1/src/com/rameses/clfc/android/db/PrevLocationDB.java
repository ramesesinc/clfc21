package com.rameses.clfc.android.db;

import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;

public class PrevLocationDB {
	
	public String getTableName() { return "prev_location"; }
		
	public Map getPrevLocation( String trackerid ) throws Exception {
		Properties data = new Properties();
		
		SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
		Cursor cursor = null;
		try {
			trackerdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where trackerid='" + trackerid + "' limit 1";
			
			cursor = trackerdb.rawQuery(sql, new String[] {});
			if (cursor != null) {
				cursor.moveToFirst();
				
				String[] colnames = cursor.getColumnNames();
				for (int i=0; i<colnames.length; i++) {
					String name = colnames[i];
					try {
						data.put(name, cursor.getString( i ));
					} catch (Throwable t) {;}
				}
			}
			
			trackerdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			trackerdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return data;
	}

}
