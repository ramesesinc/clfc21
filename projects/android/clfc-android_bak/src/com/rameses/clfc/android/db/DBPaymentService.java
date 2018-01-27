package com.rameses.clfc.android.db;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.rameses.db.android.DBContext;

public class DBPaymentService extends AbstractDBMapper 
{

	public String getTableName() { return "payment"; }
	
	public boolean hasUnpostedPayments() throws Exception {		
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT objid FROM "+ getTableName() +" WHERE state='PENDING' LIMIT 1";
			if (ctx.getCount(sql, new Object[]{}) > 0) return true;
			
//			sql = "SELECT loanappid FROM remarks WHERE state='PENDING' LIMIT 1";
//			if (ctx.getCount(sql, new Object[]{}) > 0) return true;
//			
//			sql = " SELECT r.routecode FROM collectionsheet cs " +
//				  " 	INNER JOIN route r ON cs.routecode=r.routecode " +
//				  " 	LEFT JOIN payment p ON cs.loanappid=p.loanappid " +
//				  " 	LEFT JOIN remarks m ON cs.loanappid=m.loanappid " +
//				  " WHERE r.state <> 'REMITTED' AND " +
//				  " 	(p.state IS NOT NULL OR m.state IS NOT NULL) " +
//				  " LIMIT 1 ";			
			return (ctx.getCount(sql, new Object[]{}) > 0);
		} catch(Exception e) {
			throw e; 
		} finally {
			if (isCloseable()) ctx.close(); 
		}		
	}
	
	public boolean hasUnpostedPaymentsByLoanappid(String loanappid) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT objid FROM "+getTableName()+" WHERE state='PENDING' AND loanappid=? LIMIT 1";
			return (ctx.getCount(sql, new Object[]{loanappid}) > 0);
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public boolean hasPaymentsByLoanappid(String loanappid) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT objid FROM "+getTableName()+" WHERE loanappid=? LIMIT 1";
			return (ctx.getCount(sql, new Object[]{loanappid}) > 0);
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public int noOfPaymentsByLoanappid(String loanappid) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT objid FROM "+getTableName()+" WHERE loanappid=?";
			return ctx.getCount(sql, new Object[]{loanappid});
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public List<Map> getPaymentsByLoanappid(String loanappid) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT * FROM "+getTableName()+" WHERE loanappid=?";
			return ctx.getList(sql, new Object[]{loanappid});
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public boolean hasPaymentsByRoutecode(String routecode) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT objid FROM "+getTableName()+" WHERE routecode=? LIMIT 1";
			return (ctx.getCount(sql, new Object[]{routecode}) > 0);
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public List<Map> getPendingPayments(int limit) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT * FROM "+getTableName()+" WHERE state='PENDING' ";
			if (limit > 0) sql += "LIMIT "+limit;
			return ctx.getList(sql, new Object[]{});
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
 	
	public List<Map> getPendingPayments() throws Exception {
		return getPendingPayments(0);
	}
	
	public void approvePaymentById(String id) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "UPDATE "+getTableName()+" SET state='APPROVED' WHERE objid='"+id+"'";
			ctx.execute(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
	
	public BigDecimal getTotalCollectionsByRoutecode(String routecode) throws Exception {
		DBContext ctx = createDBContext();
		try {
			String sql = "SELECT * FROM "+getTableName()+" WHERE routecode=?";
			List<Map> list = ctx.getList(sql, new Object[]{routecode});
			BigDecimal amount = new BigDecimal("0").setScale(2);
			Map map;
			for (int i=0; i<list.size(); i++) {
				map = (Map) list.get(i);
				amount = amount.add(new BigDecimal(map.get("paymentamount").toString()).setScale(2));
			}
			return amount;
		} catch (Exception e) {
			throw e;
		} finally {
			if (isCloseable()) ctx.close();
		}
	}
}
