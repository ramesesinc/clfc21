
package com.rameses.clfc.android.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rameses.clfc.android.ApplicationUtil;

public class ApplicationDBUtil {
	
	public static String createInsertSQLStatement( String tablename, Map params ) {
		String[] invalidString = ApplicationUtil.getInvalidStrings();
		
		String values = "";
		String sql = "insert or ignore into " + tablename + "(";
		Iterator itr = params.keySet().iterator();
				
		Object prevval = null;
		while (itr.hasNext()) {
		    Object val = itr.next();
		    if (val != null) {
		        String s = val.toString();
		        if (prevval != null) {
		            sql += ",";
		            values += ",";
		        }    
		        sql += "`" + s + "`";
		        
		        if (params.containsKey( s )) {
		            Object val2 = params.get( s );
		            if (val2 instanceof Number) {
		                values += val2.toString();
		            } else {
		                if (val2 == null) {
		                    values += "null";
		                } else {
		                	String v = val2.toString();
		                	for (String ss : invalidString) {
		                		if (v.contains( ss )) {
		                			v = v.replaceAll( ss, ss + ss );
		                		}
		                	}
		                	
		                    values += "'" + v + "'";
		                }
		            }
		        } else {
		            values += "null";
		        }
		    }
		    prevval = val;
		}
		sql += ") values (" + values + ");";
				
		return sql;
	}
	
	public static String createDeleteSQLStatement( String tablename, Map params ) {
		String[] invalidString = ApplicationUtil.getInvalidStrings();
		
		String sql = "delete from " + tablename;
		if (!params.isEmpty()) {
			sql += " where";
			Iterator itr = params.keySet().iterator();
			
			Object prevval = null;
			while (itr.hasNext()) {
			    Object val = itr.next();
			    if (val != null) {
			        String s = val.toString();
			        
			        if (params.containsKey( s )) {
				        if (prevval != null) {
				        }
				        sql += " " + s + "=";
			            Object val2 = params.get( s );
			            if (val2 instanceof Number) {
			                sql += val2.toString();
			            } else {
			                if (val2 == null) {
			                    sql += "null";
			                } else {
			                	String v = val2.toString();
			                	for (String ss : invalidString) {
			                		if (v.contains( ss )) {
			                			v = v.replaceAll( ss, ss + ss );
			                		}
			                	}
			                	
			                    sql += "'" + v + "'";
			                }
			            }
			        }
			    }
			    prevval = val;
			}
		}
		
		sql += ";";
		return sql;
	}
	
	public static String createUpdateSQLStatement( String tablename, Map params, Map whereParams ) {
		String[] invalidString = ApplicationUtil.getInvalidStrings();
		
		String sql = "update " + tablename;
		if (!params.isEmpty()) {
			sql += " set";
			Iterator itr = params.keySet().iterator();
			
			Object prevval = null;
			while (itr.hasNext()) {
			    Object val = itr.next();
			    if (val != null) {
			        String s = val.toString();
			        
			        if (params.containsKey( s )) {
				        if (prevval != null) {
				            sql += ",";
				        }
				        sql += " " + s + "=";
			            Object val2 = params.get( s );
			            if (val2 instanceof Number) {
			                sql += val2.toString();
			            } else {
			                if (val2 == null) {
			                    sql += "null";
			                } else {
			                	String v = val2.toString();
			                	for (String ss : invalidString) {
			                		if (v.contains( ss )) {
			                			v = v.replaceAll( ss, ss + ss );
			                		}
			                	}
			                	
			                    sql += "'" + v + "'";
			                }
			            }
			        }
			    }
			    prevval = val;
			}
		}
		
		if (!whereParams.isEmpty()) {
			sql += " where";
			Iterator itr = whereParams.keySet().iterator();
			
			Object prevval = null;
			while (itr.hasNext()) {
			    Object val = itr.next();
			    if (val != null) {
			        String s = val.toString();
			        
			        if (whereParams.containsKey( s )) {
				        if (prevval != null) {
				        	sql += " and";
				        }
				        sql += " " + s + "=";
			            Object val2 = whereParams.get( s );
			            if (val2 instanceof Number) {
			                sql += val2.toString();
			            } else {
			                if (val2 == null) {
			                    sql += "null";
			                } else {
			                	String v = val2.toString();
			                	for (String ss : invalidString) {
			                		if (v.contains( ss )) {
			                			v = v.replaceAll( ss, ss + ss );
			                		}
			                	}
			                	
			                    sql += "'" + v + "'";
			                }
			            }
			        }
			    }
			    prevval = val;
			}
		}
		
		sql += ";";
		return sql;
	}
	
	public static void executeSQL( SQLiteDatabase db, List<Map> sqlParams ) throws Exception {
		for (Map map : sqlParams) {
			String type = "";
			if (map.containsKey("type")) {
				type = map.get("type").toString();
			}
			
			if (type.toLowerCase().equals("insert")) {
//				Map xmap = new HashMap();
//				ContentValues values = xmap;
				
				
			}
			
			
			if (type.toLowerCase().matches("insert|update|delete")) {
				String sql = "";
				if (map.containsKey("sql")) {
					sql = map.get("sql").toString();
				}
				
//				final String s = sql;
//				UIActionBarActivity activity = (UIActionBarActivity) Platform.getApplication().getCurrentActionBarActivity();
//				if (activity != null) {
//					activity.getHandler().post(new Runnable() {
//						
//						public void run() {
//							ApplicationUtil.showLongMsg( s );
//						}
//						
//					});
//				}
				
				db.execSQL( sql );
			}
		}
	}
	
	private static void println( Object msg ) {
		Log.i("ApplicationDBUtil", msg.toString());
	}
	
}
