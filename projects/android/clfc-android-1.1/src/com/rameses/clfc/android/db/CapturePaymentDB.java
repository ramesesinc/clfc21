package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;

public class CapturePaymentDB {

	public String getTableName() { return "capture_payment"; }
	
	public boolean hasPayments( String collectorid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
		Cursor cursor = null;
		try {
			capturedb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " where collector_objid='" + collectorid + "' limit 1";
			
			cursor = capturedb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			capturedb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			capturedb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public boolean hasPayments( String collectorid, String date ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
		Cursor cursor = null;
		try {
			capturedb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " ";
			sql += "where collector_objid='" + collectorid + "' ";
			sql += "and txndate='" + date + "' ";
			sql += "limit 1;";

			cursor = capturedb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			capturedb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			capturedb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public boolean hasForUploadPayments() throws Exception {
		boolean flag = false;		
		
		SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
		Cursor cursor = null;
		try {
			capturedb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " where forupload=1 and state='PENDING' limit 1;";
			
			cursor = capturedb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			capturedb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			capturedb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public boolean hasForUploadPayment( String collectorid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
		Cursor cursor = null;
		try {
			capturedb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " ";
			sql += "where forupload=1 ";
			sql += "and state='PENDING' ";
			sql += "and collector_objid='" + collectorid + "' ";
			sql += "limit 1;";
			
			cursor = capturedb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			capturedb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			capturedb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public List<Map> getPaymentsByForupload( Map params ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		if (params.containsKey("forupload")) {
			SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
			Cursor cursor = null;
			try {
				capturedb.beginTransaction();
				
				String forupload = params.get("forupload").toString();
				String sql = "select * from " + getTableName() + " where forupload=" + forupload + ";";
				
				cursor = capturedb.rawQuery(sql, new String[] {});
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
				
				capturedb.setTransactionSuccessful();
			} catch (Exception e) {
				throw e;
			} finally {
				capturedb.endTransaction();
				if (cursor != null) {
					cursor.close();
				}
			}
		}
		
		return list;
	}

	public List<Map> getPaymentsForDateResolving() throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
		Cursor cursor = null;
		try {
			capturedb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where dtposted is null;";
			
			cursor = capturedb.rawQuery(sql, new String[] {});
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
			
			capturedb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			capturedb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
 		
		return list;
	}

	public List<Map> getForUploadPayments() throws Exception {
		return getForUploadPayments( 0 );
	}
	
	public List<Map> getForUploadPayments( int limit ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
		Cursor cursor = null;
		try {
			capturedb.beginTransaction();
			
			String sql = "select * from " + getTableName();
			sql += " where forupload=1";
			sql += " and state='PENDING'";
			if (limit > 0) sql += " limit " + limit;
			sql += ";";

			cursor = capturedb.rawQuery(sql, new String[] {});
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
			
			capturedb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			capturedb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	public boolean hasPaymentForUpload() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
		Cursor cursor = null;
		try {
			capturedb.beginTransaction();

			String sql = "select objid from " + getTableName();
			sql += " where forupload=1";
			sql += " and state='PENDING'";
			sql += " limit 1;";
			
			cursor = capturedb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			capturedb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			capturedb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}

	public List<Map> getPaymentsByCollector( String collectorid ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
		Cursor cursor = null;
		try {
			capturedb.beginTransaction();
			
			String sql = "select * from " + getTableName();
			sql += " where collector_objid='" + collectorid + "'";
			sql += " order by borrowername;";
			
			cursor = capturedb.rawQuery(sql, new String[] {});
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
			
			capturedb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			capturedb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}

	public boolean hasPaymentForDateResolving() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
		Cursor cursor = null;
		try {
			capturedb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " where dtposted is null limit 1;";
			
			cursor = capturedb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			capturedb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			capturedb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public boolean hasUnpostedPayments() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase capturedb = ApplicationDatabase.getCaptureWritableDB();
		Cursor cursor = null;
		try {
			capturedb.beginTransaction();
			
			String sql = "select objid from " + getTableName();
			sql += " where forupload=1";
			sql += " and state='PENDING'";
			sql += " limit 1;";
			
			cursor = capturedb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			capturedb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			capturedb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}	
	
}
