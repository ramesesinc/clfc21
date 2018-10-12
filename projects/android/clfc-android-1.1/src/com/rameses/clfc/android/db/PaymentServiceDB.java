package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationUtil;

public class PaymentServiceDB {

	public String getTableName() { return "payment"; }
	
	public boolean hasPayments( String objid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " where parentid='" + objid + "' limit 1";
//			String sql = "select * from " + getTableName();
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
//			ApplicationUtil.println("PaymentServiceDB", "count " + cursor.getCount());
			
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
//			if (cursor != null) {
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
//						} catch (Exception e) {;}
//					}
//					
//					if (!data.isEmpty()) {
//						list.add( data );
//					}
//					
//					cursor.moveToNext();
//				}
//				for (Map m : list) {
//					ApplicationUtil.println("PaymentServiceDB", "pyt-> " + m);
//				}
//			}
			
//			if (cursor != null) {
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
//				}
//				
//				for (Map m : list) {
//					ApplicationUtil.println("PaymentServiceDB", "pyt-> " + m);
//				}
//			}
			
//			if (cursor != null && cursor.getCount() > 0) {
//				 flag = true;
//			}
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public int noOfPayments( String objid ) throws Exception {
		int count = 0;
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " where parentid='" + objid + "'";
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
			if (cursor != null) {
				count = cursor.getCount();
			}
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return count;
	}
	
	public Map findPaymentById( String objid ) throws Exception {
		Properties data = new Properties();
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where objid='" + objid + "' limit 1";
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
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
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return data;
	}
	
	public List<Map> getPaymentsByItem( String itemid ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where itemid='" + itemid + "';";
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
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
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	public boolean hasUnpostedPaymentsByCollectorAndItemid( String collectorid, String itemid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " ";
			sql += "where state='PENDING' ";
			sql += "and collector_objid='" + collectorid + "' ";
			sql += "and itemid='" + itemid + "' ";
			sql += "limit 1;";
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public boolean hasUnpostedPaymentsByCollector( String collectorid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " ";
			sql += "where state='PENDING' ";
			sql += "and collector_objid='" + collectorid + "' ";
			sql += "limit 1;";
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}

	public boolean hasUnpostedPayments( String objid ) throws Exception {
		boolean flag = false;
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();
			
			String sql = "select objid from " + getTableName();
			sql += " where state='PENDING'";
			sql += " and parentid='" + objid + "'";
			sql += " limit 1;";
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public boolean hasUnpostedPayments() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();
			
			String sql = "select objid from "+ getTableName();
			sql += " where state='PENDING'";
			sql += " and forupload=1";
			sql += " limit 1;";
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	
	public List<Map> getPaymentsByForupload( Map params ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		if (params.containsKey("forupload")) {
			SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
			Cursor cursor = null;
			try {
				paymentdb.beginTransaction();
				
				String forupload = params.get("forupload").toString();
				
				String sql = "select * from " + getTableName() + " where forupload=" + forupload + ";";
				
				cursor = paymentdb.rawQuery(sql, new String[] {});
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
				
				paymentdb.setTransactionSuccessful();
			} catch (Exception e) {
				throw e;
			} finally {
				paymentdb.endTransaction();
				if (cursor != null) {
					cursor.close();
				}
			}
		}
		
		return list;
	}

	public List<Map> getPaymentsForDateResolving() throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where dtposted is null;";
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
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
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	public boolean hasPaymentForDateResolving() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " where dtposted is null limit 1;";
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
	public List<Map> getForUploadPayments() throws Exception {
		return getForUploadPayments( 0 );
	}
	
	public List<Map> getForUploadPayments( int limit ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();
			
			String sql = "select * from " + getTableName();
			sql += " where forupload=1 ";
			sql += " and state='PENDING'";
			if (limit > 0) sql += " limit " + limit;
			sql += ";";
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
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
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	public boolean hasPaymentForUpload() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase paymentdb = ApplicationDatabase.getPaymentWritableDB();
		Cursor cursor = null;
		try {
			paymentdb.beginTransaction();			

			String sql = "select objid from " + getTableName();
			sql += " where forupload=1 ";
			sql += " and state='PENDING'";
			sql += " limit 1;";
			
			cursor = paymentdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			paymentdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			paymentdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}
}
