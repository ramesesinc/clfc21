package com.rameses.clfc.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationUtil;

public class CollectionSheetDB {
	
	public String getTableName() { return "collectionsheet"; }	

	public List<Map> getCollectionSheetByItemid( String itemid ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where itemid='" + itemid + "'";
			
			cursor = appdb.rawQuery(sql, new String[] {});
			if (cursor != null) {
				cursor.moveToFirst(); 
				
//				int counter = 0;
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
//					counter++;
				}
//				println("count: " + cursor.getCount() + " list count: " + list.size() + " movetonext: " + counter);
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
	
	public Map findCollectionSheet( String objid ) throws Exception {
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
	
	public int countByDateAndCollectorWithSegregationid( String date, String collectorid, String segregationid ) throws Exception {
		int count = 0;
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();

			String sql = "select c.objid from " + getTableName() + " c";
			sql += " inner join collection_group g on c.itemid=g.objid";
			sql += " inner join collectionsheet_segregation s on c.objid=s.collectionsheetid";
			sql += " where g.billdate='" + date + "'";
			sql += " and g.collectorid='" + collectorid + "'";
			sql += " and s.segregationtypeid='" + segregationid + "'";
			
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
	
	public List<Map> getCollectionSheetsByItemWithSearchtextAndSegregationid( Map params, int limit ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select c.*, g.description as route, g.cbsno,";
			sql += " (select count(p.objid) from payment p where p.parentid=c.objid) as noofpayments,";
			sql += " (select count(v.objid) from void_request v where v.csid=c.objid) as noofvoids";
			sql += " from " + getTableName() + " c";
			sql += " inner join collection_group g on c.itemid=g.objid";
			sql += " inner join collection_group_type t on g.type=t.type";
			sql += " inner join collectionsheet_segregation s on c.objid=s.collectionsheetid";
			sql += " where g.billdate='" + params.get("date").toString() + "'";
			sql += " and g.collectorid='" + params.get("collectorid").toString() + "'";
			sql += " and s.segregationtypeid='" + params.get("segregationid").toString() + "'";
			sql += " and c.borrower_name like '" + params.get("searchtext").toString() + "'";
			sql += " order by t.level, g.description, c.seqno";
			if (limit > 0) sql += " limit " + limit;
			
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
	
	public void dropIndex() throws Exception {
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		try {
			appdb.beginTransaction();
			
			String sql = "drop index if exists idx_collectionsheet;";
			appdb.execSQL( sql );
			
			appdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			appdb.endTransaction();
		}
	}
	
	public void addIndex() throws Exception {
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		try {
			appdb.beginTransaction();
			
			String sql = "create index if not exists idx_collectionsheet on collectionsheet(borrower_name, itemid);";
			appdb.execSQL( sql );
			
			appdb.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			appdb.endTransaction();
		}
	}
	
	public List<Map> getUnremittedCollectionSheetsByCollector( String collectorid ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select cs.* from " + getTableName() + " cs ";
			sql += "inner join collection_group cg on cs.itemid=cg.objid ";
			sql += "where cg.state <> 'REMITTED' ";
			sql += "and cg.collectorid='" + collectorid + "';";
			
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
	
	public List<Map> getCollectionSheetsByCollector( String collectorid, String searchtext ) throws Exception {
		List<Map> list = new ArrayList<Map>();
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		Cursor cursor = null;
		try {
			appdb.beginTransaction();
			
			String sql = "select cs.* from " + getTableName() + " cs";
			sql += " inner join collection_group cg on cs.itemid=cg.objid";
			sql += " where cg.collectorid='" + collectorid + "'";
			sql += " and cs.borrower_name LIKE '" + searchtext + "';";
			
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
	
	private void println( Object msg ) {
		ApplicationUtil.println("CollectionSheetDB", msg.toString());
	}
}
