package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.db.android.DBContext;

public class SpecialCollectionDB {

	public String getTableName() { return "specialcollection"; }
	
	public void changeStateById( String objid, String state ) throws Exception {
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		try {
			 appdb.beginTransaction();
			 
			 String sql = "update " + getTableName() + " set state='" + state + "' where objid='" + objid + "'";
			 appdb.execSQL( sql );
			 
			 appdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			appdb.endTransaction();
		}
	}
	
	public List<Map> getSpecialCollectionRequestsByCollectorid( String collectorid ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where collectorid='" + collectorid + "';";
			
			cursor = appdb.rawQuery(sql, new String[] {}); 
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
			
			appdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			appdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}

	public int noOfSpecialCollectionByCollector( String objid ) throws Exception {
		int count = 0;
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " where collector_objid='" + objid + "';";
			
			cursor = appdb.rawQuery(sql, new String[] {});
			if (cursor != null) {
				count = cursor.getCount();
			}
			
			appdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			appdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return count;
	}	
	
}
