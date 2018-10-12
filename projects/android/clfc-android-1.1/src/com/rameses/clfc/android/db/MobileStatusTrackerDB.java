package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.db.android.DBContext;

public class MobileStatusTrackerDB {

	public String getTableName() { return "mobile_status"; }
		
	public List<Map> getForUploadLocationTrackers() throws Exception {
		return getForUploadLocationTrackers( 0 );
	}
	
	public List<Map> getForUploadLocationTrackers( int limit ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase statusdb = ApplicationDatabase.getStatusWritableDB();
		Cursor cursor = null;
		try {
			statusdb.beginTransaction();
			
			String sql = "select * from " + getTableName();
			sql += " where forupload=1";
			sql += " order by txndate";
			if (limit > 0) sql += " limit " + limit;
			sql += ";";
			
			cursor = statusdb.rawQuery(sql, new String[] {});
			if (cursor != null) {
				cursor.moveToFirst();
				
				Properties data;
				String[] colnames;
				while (!cursor.isAfterLast()) {
					data = new Properties();
					colnames = cursor.getColumnNames();
					
					for (int i=0; i<colnames.length; i++) {
						String name = colnames[i];
						try {
							data.put(name, cursor.getString( i ));
						} catch (Exception e) {;}
					}
					
					if (!data.isEmpty()) {
						list.add( data );
					}
					
					cursor.moveToNext();
				}
			}
			
			statusdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			statusdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	public List<Map> getTrackersForDateResolving() throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase statusdb = ApplicationDatabase.getStatusWritableDB();
		Cursor cursor = null;
		try {
			statusdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where dtposted is null;";
			
			cursor = statusdb.rawQuery(sql, new String[] {});
			if (cursor != null) {
				cursor.moveToFirst();
				
				Properties data;
				String[] colnames;
				while (!cursor.isAfterLast()) {
					data = new Properties();
					colnames = cursor.getColumnNames();
					
					for (int i=0; i<colnames.length; i++) {
						String name = colnames[i];
						try {
							data.put(name, cursor.getString( i ));
						} catch (Exception e) {;}
					}
					
					if (!data.isEmpty()) {
						list.add( data );
					}
					
					cursor.moveToNext();
				}
			}
			
			statusdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			statusdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	public boolean hasTrackerForDateResolving() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase statusdb = ApplicationDatabase.getStatusWritableDB();
		Cursor cursor = null;
		try {
			statusdb.beginTransaction();
			
			String sql = "select objid from " + getTableName();
			sql += " where dtposted is null";
			sql += " limit 1;";
			
			cursor = statusdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			statusdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			statusdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
}
