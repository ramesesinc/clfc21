package com.rameses.clfc.android.db;

import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;

public class CSVoidDB {

	public String getTableName() { return "void_request"; }

	public boolean hasPendingVoidRequest( String itemid, String csid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select objid from " + getTableName();
			sql += " where itemid='" + itemid + "'";
			sql += " and csid='" + csid + "'";
			sql += " and state='PENDING'";
			sql += " limit 1";
			
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
	
	public Map findVoidRequestById( String objid ) throws Exception {
		Properties data = new Properties();
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where objid='" + objid + "';";
			
			cursor = appdb.rawQuery(sql, new String[]{});
			if (cursor != null) {
				cursor.moveToFirst();
				
				String[] colnames = cursor.getColumnNames();
				for (int i=0; i<colnames.length; i++) {
					String name = colnames[i];
					try {
						data.put(name, cursor.getString( i ));
					} catch (Exception e) {}
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

	public Map findVoidRequestByPaymentid( String paymentid ) throws Exception {
		Properties data = new Properties();
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where paymentid='" + paymentid + "';";
			
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
	
}
