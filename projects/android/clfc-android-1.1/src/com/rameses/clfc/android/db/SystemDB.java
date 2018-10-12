package com.rameses.clfc.android.db;

import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.db.android.DBContext;

public class SystemDB {
	
	public String getTableName() { return "sys_var"; }
	
	public boolean hasBillingid( String collectorid, String date ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();

			String name = collectorid + "-" + date;
			String sql = "select name from " + getTableName() + " where name='" + name + "' limit 1";
			
			cursor = appdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
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

	public String getBillingid( String collectorid, String date ) throws Exception {
		String result = "";
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String name = collectorid + "-" + date;
			String sql = "select value from " + getTableName() + " where name='" + name + "'";
			
			cursor = appdb.rawQuery(sql, new String[] {});
			if (cursor != null) {
				cursor.moveToFirst();
				
				Properties data = new Properties();
				String[] colnames = cursor.getColumnNames();
				for (int i=0; i<colnames.length; i++) {
					String colname = colnames[i];
					try {
						data.put(colname, cursor.getString( i ));
					} catch (Throwable t) {;}
				}
				
				if (!data.isEmpty()) {
					result = data.getProperty( "value" );
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
		
		return result;
	}

}
