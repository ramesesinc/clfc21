package com.rameses.clfc.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.rameses.db.android.AbstractDB;

public class StatusDB extends AbstractDB {

	public final static Object LOCK = new Object(); 
	
	public StatusDB(Context ctx, String dbname, int dbversion) {
		super(ctx, dbname, dbversion); 
		System.out.println("instantiate StatusDB");
	}

	protected void onCreateProcess(SQLiteDatabase sqldb) { 
		System.out.println("clfcstatus oncreateprocess");
		try { 
			loadDBResource(sqldb, "clfcstatusdb_create"); 
			System.out.println("clfcstatus created");
		} catch(RuntimeException re) {
			throw re; 
		} catch(Exception e) {
			throw new RuntimeException(e.getMessage(), e); 
		}
	}

	protected void onUpgradeProcess(SQLiteDatabase sqldb, int arg1, int arg2) {
		System.out.println("clfcstatus onUpgradeProcess");
		try { 
			loadDBResource(sqldb, "clfcstatusdb_upgrade");
			onCreate(sqldb); 
		} catch(RuntimeException re) {
			throw re; 
		} catch(Exception e) {
			throw new RuntimeException(e.getMessage(), e); 
		}		
	}

}
