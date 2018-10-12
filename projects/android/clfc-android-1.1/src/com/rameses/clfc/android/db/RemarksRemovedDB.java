package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.db.android.DBContext;

public class RemarksRemovedDB {

	public String getTableName() { return "remarks_removed"; }
	
	public List<Map> getPendingRemarksRemoved() throws Exception {
		return getPendingRemarksRemoved( 0 );
	}
	
	public List<Map> getPendingRemarksRemoved( int limit ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase remarksremoveddb = ApplicationDatabase.getRemarksRemovedWritableDB();
		Cursor cursor = null;
		try {
			remarksremoveddb.beginTransaction();
			
			String sql = "select * from " + getTableName();
			sql += " where state='PENDING'";
			if (limit > 0) sql += " limit " + limit;
			sql += ";";
			
			cursor = remarksremoveddb.rawQuery(sql, new String[] {});
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
			
			remarksremoveddb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			remarksremoveddb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	public boolean hasPendingRemarksRemoved() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase remarksremoveddb = ApplicationDatabase.getRemarksRemovedWritableDB();
		Cursor cursor = null;
		try {
			remarksremoveddb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where state='PENDING' limit 1;";
			
			cursor = remarksremoveddb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			remarksremoveddb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			remarksremoveddb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
}
