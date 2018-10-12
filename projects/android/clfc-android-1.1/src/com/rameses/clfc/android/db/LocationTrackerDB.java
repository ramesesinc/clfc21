package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.db.android.DBContext;
import com.rameses.util.MapProxy;

public class LocationTrackerDB {
	
	public String getTableName() { return "location_tracker"; }
	
	public int getLastSeqnoByCollectorid( String collectorid ) throws Exception {
		int seqno = 0;
		
		SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
		Cursor cursor = null;		
		try {
			trackerdb.beginTransaction();
			
			String sql = "select objid, seqno from " + getTableName();
			sql += " where collectorid='" + collectorid + "'";
			sql += " order by seqno desc limit 1"; 
						
			cursor = trackerdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst(); 
				
				Properties data = new Properties();
				String[] colnames = cursor.getColumnNames();
				for (int i=0; i<colnames.length; i++) {
					String name = colnames[i];
					try {
						data.put(name, cursor.getString( i ));
					} catch (Throwable t) {;}
				} 
				
				if (!data.isEmpty()) {
					seqno = MapProxy.getInteger(data, "seqno");
				}
			}
			
			trackerdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally { 
			trackerdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return seqno;
	}
	
	public List<Map> getTrackersForDateResolving() throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
		Cursor cursor = null;
		try {
			trackerdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where dtposted is null";
						
			cursor = trackerdb.rawQuery(sql, new String[] {});
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
			
			trackerdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			trackerdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	public boolean hasTrackerForDateResolving() throws Exception {
		boolean flag = false;
		
		SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
		Cursor cursor = null;
		try {
			trackerdb.beginTransaction();
			
			String sql = "select objid from " + getTableName() + " where dtposted is null limit 1";
			
			cursor = trackerdb.rawQuery(sql, new String[] {});
			if (cursor != null && cursor.getCount() > 0) {
				flag = true;
			}
			
			trackerdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			trackerdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return flag;
	}

	public List<Map> getLocationTrackersByCollectoridAndLessThanDate( String collectorid, String date ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
		Cursor cursor = null;
		try {
			trackerdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " ";
			sql += "where collectorid='" + collectorid + "' ";
			sql += "and txndate <= '" + date + "'";
			sql += "and forupload=1;";
			
			cursor = trackerdb.rawQuery(sql, new String[] {});
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
			
			trackerdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			trackerdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	public int getNoOfTrackersByCollectorid( String collectorid ) throws Exception {
		int count = 0;
		
		SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
		Cursor cursor = null;
		try {
			trackerdb.beginTransaction();
			
			String sql = "select objid from " + getTableName();
			sql += " where collectorid='" + collectorid + "';";
			
			cursor = trackerdb.rawQuery(sql, new String[] {});
			if (cursor != null) {
				count = cursor.getCount();
			}
			
			trackerdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			trackerdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return count;
	}
	
	public List<Map> getForUploadLocationTrackers() throws Exception {
		return getForUploadLocationTrackers( 0 );
	}
	
	public List<Map> getForUploadLocationTrackers( int limit ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase trackerdb = ApplicationDatabase.getTrackerWritableDB();
		Cursor cursor = null;
		try {
			trackerdb.beginTransaction();
			
			String sql = "select * from " + getTableName();
			sql += " where forupload=1";
			sql += " order by seqno";
			if (limit > 0) sql += " limit " + limit;
			sql += ";";
			
			cursor = trackerdb.rawQuery(sql, new String[] {});
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
			
			trackerdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			trackerdb.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return list;
	}
	
	
	private void println( Object msg ) {
		ApplicationUtil.println("LocationTrackerDB", msg.toString());
	}
}
