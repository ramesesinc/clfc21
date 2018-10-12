package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationUtil;

public class VoidServiceDB {

	public String getTableName() { return "void_request"; }

	public List<Map> getPendingVoidRequests() throws Exception {
		return getPendingVoidRequests( 0 );
	}
	
	public List<Map> getPendingVoidRequests( int limit ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase voidrequestdb = ApplicationDatabase.getVoidRequestWritableDB();
		Cursor cursor = null;
		try {
			voidrequestdb.beginTransaction();
			
			String sql = "select *";
			sql += " from " + getTableName();
			sql += " where state='PENDING'";
			if (limit > 0) sql += " limit " + limit;
			sql += ";";
			
			cursor = voidrequestdb.rawQuery(sql, new String[] {});
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
			
			voidrequestdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			voidrequestdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		
		}
		
		return list;
	}
	
	public boolean hasPendingVoidRequest() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase voidrequestdb = ApplicationDatabase.getVoidRequestWritableDB();
		Cursor cursor = null;
		try {
			voidrequestdb.beginTransaction();
			
			String sql = "select objid from "+getTableName()+" where state='PENDING' limit 1";
			
			cursor = voidrequestdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			voidrequestdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			voidrequestdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public int noOfVoidPayments( String objid ) throws Exception {
		int count = 0;
		
		SQLiteDatabase voidrequestdb = ApplicationDatabase.getVoidRequestWritableDB();
		Cursor cursor = null;
		try {
			voidrequestdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " where csid='" + objid + "';";
			
			cursor = voidrequestdb.rawQuery(sql, new String[] {});
			if (cursor != null) {
				count = cursor.getCount();
			}
			
			voidrequestdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			voidrequestdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return count;
	}
	
	public Map findVoidRequestByPaymentidAndState( String objid, String state ) throws Exception {
		Properties data = new Properties();
		
		SQLiteDatabase voidrequestdb = ApplicationDatabase.getVoidRequestWritableDB();
		Cursor cursor = null;
		try {
			voidrequestdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " ";
			sql += "where paymentid='" + objid + "' ";
			sql += "and state='" + state + "';";
			
			cursor = voidrequestdb.rawQuery(sql, new String[] {});
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
			
			voidrequestdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			voidrequestdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return data;
	}
	
	public Map findVoidRequestByPaymentid( String objid ) throws Exception {
		Properties data = new Properties();
		
		SQLiteDatabase voidrequestdb = ApplicationDatabase.getVoidRequestWritableDB();
		Cursor cursor = null;
		try {
			voidrequestdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where paymentid='" + objid + "';";
			
			cursor = voidrequestdb.rawQuery(sql, new String[] {});
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
			
			voidrequestdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			voidrequestdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return data;
	}
	
	public Map findVoidRequestById( String objid ) throws Exception {
		Properties data = new Properties();
		
		SQLiteDatabase voidrequestdb = ApplicationDatabase.getVoidRequestWritableDB();
		Cursor cursor = null;
		try {
			voidrequestdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where objid='" + objid + "';";
			
			cursor = voidrequestdb.rawQuery(sql, new String[] {});
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
			
			voidrequestdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			voidrequestdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return data;
	}

	public Map findVoidRequestByItemidAndState( String itemid, String state ) throws Exception {
		Properties data = new Properties();
		
		SQLiteDatabase voidrequestdb = ApplicationDatabase.getVoidRequestWritableDB();
		Cursor cursor = null;
		try {
			voidrequestdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " ";
			sql += "where itemid='" + itemid + "' ";
			sql += "and state='" + state + "';";
			
			cursor = voidrequestdb.rawQuery(sql, new String[] {});
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
			
			voidrequestdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			voidrequestdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return data;
	}
	
}
