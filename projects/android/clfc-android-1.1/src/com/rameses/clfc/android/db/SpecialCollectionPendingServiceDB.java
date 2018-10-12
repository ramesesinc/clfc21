package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;

public class SpecialCollectionPendingServiceDB {
	
	public String getTableName() { return "specialcollection"; }
	
	public List<Map> getPendingSpecialCollection() throws Exception {
		return getPendingSpecialCollection( 0 );
	}
	
	public List<Map> getPendingSpecialCollection( int limit ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase specialcollectiondb = ApplicationDatabase.getSpecialCollectionWritableDB();
		Cursor cursor = null;
		try {
			specialcollectiondb.beginTransaction();
			
			String sql = "select * from " + getTableName();
			if (limit > 0) sql += " limit " + limit;
			sql += ";";
			
			cursor = specialcollectiondb.rawQuery(sql, new String[] {});
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
			
			specialcollectiondb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			specialcollectiondb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	public boolean hasUnpostedRequest() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase specialcollectiondb = ApplicationDatabase.getSpecialCollectionWritableDB();
		Cursor cursor = null;
		try {
			specialcollectiondb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " limit 1;";
			
			cursor = specialcollectiondb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			specialcollectiondb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			specialcollectiondb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	
}
