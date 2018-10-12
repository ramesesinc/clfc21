package com.rameses.clfc.android.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.db.android.DBContext;


public class XDBCSVoid extends AbstractDBMapper
{

	public String getTableName() { return "void_request"; }

	public boolean hasPendingVoidRequest(String itemid, String csid) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT objid FROM " + getTableName() 
						 + " WHERE itemid = $P{itemid}"
						 + " AND csid = $P{csid} "
						 + "AND state = 'PENDING'"
						 + " LIMIT 1";
			Map params = new HashMap();
			params.put("itemid", itemid);
			params.put("csid", csid);
			return (ctx.getCount(sql, params) > 0);
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public Map findVoidRequestByPaymentid(String paymentid) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT * FROM " + getTableName()
						 + " WHERE paymentid =?";
			return ctx.find(sql, new Object[]{paymentid});
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}

	public Map findVoidRequestById( String objid ) throws Exception {
		Properties data = new Properties();
		
		SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
		try {
			appdb.beginTransaction();
			
			String sql = "select * from " + getTableName() + " where objid='" + objid + "';";
			
			Cursor cursor = appdb.rawQuery(sql, new String[] {});
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
		}
		
		return data;
	}
}
