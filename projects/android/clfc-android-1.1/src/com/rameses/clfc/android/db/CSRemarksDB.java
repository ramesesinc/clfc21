package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationUtil;

public class CSRemarksDB {

	public String getTableName() { return "remarks"; }
	
	public Map findRemarksById( String objid ) throws Exception {
		Properties data = new Properties();
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where objid='" + objid + "' limit 1";
			
			cursor = appdb.rawQuery(sql, new String[] {});
			if (cursor != null) {
				cursor.moveToFirst();
				
				String[] colnames = cursor.getColumnNames();
				for (int i=0; i<colnames.length; i++) {
					String name = colnames[i];
					try {
						data.put(name, cursor.getString( i ));
					} catch (Exception e) {;}
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
		
		return data;
	}
	
	public boolean hasRemarksById( String objid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where objid='" + objid + "' limit 1";
			
			cursor = appdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
//				cursor.moveToFirst();
//				
//				List<Map> list = new ArrayList<Map>();
//				
//				Properties data;
//				String[] colnames;
//				while (!cursor.isAfterLast()) {
//					data = new Properties();
//					colnames = cursor.getColumnNames();
//					
//					for (int i=0; i<colnames.length; i++) {
//						String name = colnames[i];
//						try {
//							data.put(name, cursor.getString( i ));
//						} catch (Exception e) {}
//					}
//					
//					if (!data.isEmpty()) {
//						list.add( data );
//					}
//					
//					cursor.moveToNext();
//				}
//				
//				for (Map map : list) {
//					ApplicationUtil.println("CSRemarksDB", "item-> " + map);
//				}
				
				
				
				
				flag = true;
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
		
		return flag;
	}

}
