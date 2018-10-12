package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;

public class RemarksServiceDB {

	public String getTableName() { return "remarks"; }
	
	public boolean hasRemarksById( String objid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
		Cursor cursor = null;
		try {
			remarksdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " where objid='" + objid + "' limit 1";
			
			cursor = remarksdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			remarksdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			remarksdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public boolean hasUnpostedRemarks() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
		Cursor cursor = null;
		try {
			remarksdb.beginTransaction();
			
			String sql = "select objid from " + getTableName();
			sql +=  " where state='PENDING'";
			sql += " and forupload=1";
			sql += " limit 1;";
			
			cursor = remarksdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			remarksdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			remarksdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public boolean hasUnpostedRemarksByCollector( String collectorid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
		Cursor cursor = null;
		try {
			remarksdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " ";
			sql += "where state='PENDING' ";
			sql += "and collector_objid='" + collectorid + "' ";
			sql += "limit 1;";
			
			cursor = remarksdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			remarksdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			remarksdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public boolean hasUnpostedRemarksByCollectorAndItemid( String collectorid, String itemid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
		Cursor cursor = null;
		try {
			remarksdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " ";
			sql += "where state='PENDING' ";
			sql += "and collector_objid='" + collectorid + "' ";
			sql += "and itemid='" + itemid + "' ";
			sql += "limit 1;";

			cursor = remarksdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			remarksdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			remarksdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public List<Map> getRemarksForDateResolving() throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
		Cursor cursor = null;
		try {
			remarksdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where dtposted is null;";
			
			cursor = remarksdb.rawQuery(sql, new String[] {});
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
			
			remarksdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			remarksdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}

	public boolean hasRemarksForDateResolving() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
		Cursor cursor = null;
		try {
			remarksdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " where dtposted is null limit 1;";
			
			cursor = remarksdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			remarksdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			remarksdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public List<Map> getForUploadRemarks() throws Exception {
		return getForUploadRemarks(0);
	}
	
	public List<Map> getForUploadRemarks( int limit ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
		Cursor cursor = null;
		try {
			remarksdb.beginTransaction();
			
			String sql = "select * from " + getTableName();
			sql += " where forupload=1";
			sql += " and state='PENDING';";
			
			cursor = remarksdb.rawQuery(sql, new String[] {});
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
			
			remarksdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			remarksdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}		
		
		return list;
	}
	
	public boolean hasRemarksForUpload() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
		Cursor cursor = null;
		try {
			remarksdb.beginTransaction();

			String sql = "select objid from " + getTableName();
			sql += " where forupload=1";
			sql += " and state='PENDING'";
			sql += " limit 1;";
			
			cursor = remarksdb.rawQuery(sql, new String[]{});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			remarksdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			remarksdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public boolean hasUnpostedRemarksById( String objid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
		Cursor cursor = null;
		try {
			remarksdb.beginTransaction();
			
			String sql = "select objid from " + getTableName();
			sql += " where state='PENDING'";
			sql += " and objid='" + objid + "'";
			sql += " limit 1;";
			
			cursor = remarksdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			remarksdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			remarksdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
		
	
}
