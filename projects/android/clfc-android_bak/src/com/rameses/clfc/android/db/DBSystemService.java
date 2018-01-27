package com.rameses.clfc.android.db;

import java.util.Map;

import com.rameses.db.android.DBContext;

public class DBSystemService extends AbstractDBMapper 
{
	public String getTableName() { return "sys_var"; }
	
	/*public String getTrackerid() throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT value FROM "+getTableName()+" WHERE name='trackerid'";
			Map map = ctx.find(sql, new Object[]{});
			
			if (map == null) return null;
			return map.get("value").toString();
		} catch(Exception e) {
			throw e; 
		} finally {
			if (isCloseable()) ctx.close(); 
		}	
	}*/
	
	public boolean hasBillingid() throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT name FROM "+getTableName()+" WHERE name='billingid' LIMIT 1";
			return (ctx.getCount(sql, new Object[]{}) > 0);
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public String getBillingid() throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT value FROM "+getTableName()+" WHERE name='billingid'";
			Map map = ctx.find(sql, new Object[]{});

			if (map == null) return null;
			return map.get("value").toString();
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
}
