package com.rameses.clfc.android;

import android.database.sqlite.SQLiteDatabase;

import com.rameses.client.android.Platform;
import com.rameses.db.android.AbstractDB;
import com.rameses.db.android.DBManager;

public class ApplicationDatabase {

	private static final int DATABASE_VERSION = 1;
	
	public static final String APP_DBNAME = "clfc.db";
	public static final String TRACKER_DBNAME = "clfctracker.db";
	public static final String STATUS_DBNAME = "clfcstatus.db";
	public static final String CAPTURE_DBNAME = "clfccapture.db";
	public static final String REMARKS_DBNAME = "clfcremarks.db";
	public static final String REMARKS_REMOVED_DBNAME = "clfcremarksremoved.db";
	public static final String VOID_REQUEST_DBNAME = "clfcrequest.db";
	public static final String PAYMENT_DBNAME = "clfcpayment.db";
	public static final String SPECIAL_COLLECTION_DBNAME = "clfcspecialcollection.db";
			
	private static ApplicationDB applicationdb;
	private static TrackerDB trackerdb;
	private static StatusDB statusdb;
	private static CaptureDB capturedb;
	private static RemarksDB remarksdb;
	private static RemarksRemovedDB remarksremoveddb;
	private static VoidRequestDB voidrequestdb;
	private static PaymentDB paymentdb;
	private static SpecialCollectionDB specialcollectiondb;
//	private static TrackerDB trackerdb = new  TrackerDB( Platform.getApplication(), "clfctracker,db")
	
	
	private static SQLiteDatabase appWritableDB;
	private static SQLiteDatabase trackerWritableDB;
	private static SQLiteDatabase statusWritableDB;
	private static SQLiteDatabase captureWritableDB;
	private static SQLiteDatabase remarksWritableDB;
	private static SQLiteDatabase remarksRemovedWritableDB;
	private static SQLiteDatabase voidRequestWritableDB;
	private static SQLiteDatabase paymentWritableDB;
	private static SQLiteDatabase specialCollectionWritableDB;
	
	public synchronized static SQLiteDatabase getAppWritableDB() {
		if (appWritableDB == null) {
			if (applicationdb == null) {
				applicationdb = new ApplicationDB( Platform.getApplication(), APP_DBNAME, DATABASE_VERSION );
			}
			
			AbstractDB adb = DBManager.get( APP_DBNAME );
			if (adb != null) {
				appWritableDB = adb.getWritableDatabase();
			}
		}
		return appWritableDB;
	}
	
	public synchronized static SQLiteDatabase getTrackerWritableDB() {
		if (trackerWritableDB == null) {
			if (trackerdb == null) {
				trackerdb = new TrackerDB( Platform.getApplication() , TRACKER_DBNAME, DATABASE_VERSION );
			}		
			
			AbstractDB adb = DBManager.get( TRACKER_DBNAME );
			if (adb != null) {
				trackerWritableDB = adb.getWritableDatabase();
			}
		}
		
		return trackerWritableDB;
	}
	
	public synchronized static SQLiteDatabase getStatusWritableDB() {
		if (statusWritableDB == null) {
			if (statusdb == null) {
				statusdb = new StatusDB( Platform.getApplication(), STATUS_DBNAME, DATABASE_VERSION );
			}
			
			AbstractDB adb = DBManager.get( STATUS_DBNAME );
			if (adb != null) {
				statusWritableDB = adb.getWritableDatabase();
			}
		}
		return statusWritableDB;
	}
	
	public synchronized static SQLiteDatabase getCaptureWritableDB() {
		if (captureWritableDB == null) {
			if (capturedb == null) {
				capturedb = new CaptureDB( Platform.getApplication(), CAPTURE_DBNAME, DATABASE_VERSION );
			}
			
			AbstractDB adb = DBManager.get( CAPTURE_DBNAME );
			if (adb != null) {
				captureWritableDB = adb.getWritableDatabase();
			}
		}		
		return captureWritableDB;
	}
	
	public synchronized static SQLiteDatabase getRemarksWritableDB() {
		if (remarksWritableDB == null) {
			if (remarksdb == null) {
				remarksdb = new RemarksDB( Platform.getApplication(), REMARKS_DBNAME, DATABASE_VERSION );
			}
			
			AbstractDB adb = DBManager.get( REMARKS_DBNAME );
			if (adb != null) {
				remarksWritableDB = adb.getWritableDatabase();
			}
		}
		return remarksWritableDB;
	}
	
	public synchronized static SQLiteDatabase getRemarksRemovedWritableDB() {
		if (remarksRemovedWritableDB == null) {
			if (remarksremoveddb == null) {
				remarksremoveddb = new RemarksRemovedDB( Platform.getApplication(), REMARKS_REMOVED_DBNAME, DATABASE_VERSION );
			}
			
			AbstractDB adb = DBManager.get( REMARKS_REMOVED_DBNAME );
			if (adb != null) {
				remarksRemovedWritableDB = adb.getWritableDatabase();
			}
		}
		return remarksRemovedWritableDB;
	}
	
	public synchronized static SQLiteDatabase getVoidRequestWritableDB() {
		if (voidRequestWritableDB == null) {			
			if (voidrequestdb == null) {
				voidrequestdb = new VoidRequestDB( Platform.getApplication(), VOID_REQUEST_DBNAME, DATABASE_VERSION );
			}
			
			AbstractDB adb = DBManager.get( VOID_REQUEST_DBNAME );
			if (adb != null) {
				voidRequestWritableDB = adb.getWritableDatabase();
			}
		}
		return voidRequestWritableDB;
	}
	
	public synchronized static SQLiteDatabase getPaymentWritableDB() {
		if (paymentWritableDB == null) {
			if (paymentdb == null) {
				paymentdb = new PaymentDB( Platform.getApplication(), PAYMENT_DBNAME, DATABASE_VERSION );
			}
			
			AbstractDB adb = DBManager.get( PAYMENT_DBNAME );
			if (adb != null) {
				paymentWritableDB = adb.getWritableDatabase();
			}
		}
		return paymentWritableDB;
	}
	
	public synchronized static SQLiteDatabase getSpecialCollectionWritableDB() {
		if (specialCollectionWritableDB == null) {
			if (specialcollectiondb == null) {
				specialcollectiondb = new SpecialCollectionDB( Platform.getApplication(), SPECIAL_COLLECTION_DBNAME, DATABASE_VERSION );
			}
			
			AbstractDB adb = DBManager.get( SPECIAL_COLLECTION_DBNAME );
			if (adb != null) {
				specialCollectionWritableDB = adb.getWritableDatabase();
			}
		}
		return specialCollectionWritableDB;
	}
	
	private static void println( Object msg ) {
		ApplicationUtil.println("ApplicationDatabase", msg.toString());
	}
}
