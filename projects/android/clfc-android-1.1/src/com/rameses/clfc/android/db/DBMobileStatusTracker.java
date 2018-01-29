package com.rameses.clfc.android.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rameses.db.android.DBContext;

public class DBMobileStatusTracker extends AbstractDBMapper {

	public String getTableName() { return "mobile_status"; }
	
	public boolean hasStatusTrackers() throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT objid FROM " + getTableName() + " WHERE forupload=1 LIMIT 1";
			return (ctx.getCount(sql, new Object[]{}) > 0);
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public boolean hasTrackerForDateResolving() throws Exception {
		DBContext ctx= createDBContext();
		try {
			String sql = "SELECT objid FROM " + getTableName() + " WHERE dtposted IS NULL LIMIT 1";
			return (ctx.getCount(sql, new HashMap()) > 0);
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}

	public List<Map> getTrackersForDateResolving() throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT * FROM " + getTableName() + " WHERE dtposted IS NULL";
			return ctx.getList(sql, new HashMap());
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public List<Map> getForUploadLocationTrackers(int limit) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT * FROM " + getTableName() + " WHERE forupload=1 ORDER BY txndate ";
			if (limit > 0) sql += "LIMIT " + limit;
			return ctx.getList(sql, new Object[]{});
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public List<Map> getForUploadLocationTrackers() throws Exception {
		return getForUploadLocationTrackers(0);
	}
}
