package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;

public class BankDB {
	
	public String getTableName() { return "bank"; }
	
	public Map findById( String objid ) throws Exception {

		Properties data = new Properties();
		Cursor cursor = null;
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		try {
			appdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where objid='" + objid + "'";
			
			cursor = appdb.rawQuery(sql, new String[] {});
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
	
	public List<Map> getBanks() throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select * from " + getTableName();
			
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
						} catch (Throwable t) {;}
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
	

//	public List<Map> xgetBanks() throws Exception {
//		DBContext ctx = createDBContext();
//		try {
//			String sql = "SELECT * FROM " + getTableName();
//			return ctx.getList(sql, new Object[]{});
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			if (isCloseable()) ctx.close();
//		}
//	}	

}