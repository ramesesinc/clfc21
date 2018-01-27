package com.rameses.clfc.android.db;

import java.util.List;
import java.util.Map;

import com.rameses.db.android.DBContext;

public class DBSpecialCollection extends AbstractDBMapper
{
	public String getTableName() { return "specialcollection"; }
	
	public List<Map> getSpecialCollectionRequestsByCollectorid(String collectorid) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT * FROM "+getTableName()+" WHERE collectorid=?";
			return ctx.getList(sql, new Object[]{collectorid});
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public void changeStateById(Map params) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "UPDATE "+getTableName()+" SET state='"+params.get("state").toString()+"' WHERE objid='"+params.get("objid").toString()+"'";
			ctx.execute(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
}
