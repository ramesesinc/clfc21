package com.rameses.clfc.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.rameses.clfc.android.db.VoidServiceDB;
import com.rameses.clfc.android.services.LoanPostingService;
import com.rameses.client.android.Platform;
import com.rameses.client.android.Task;
import com.rameses.db.android.DBContext;

public class XVoidRequestService 
{
	private final int SIZE = 6;
	
	private ApplicationImpl app;
	private Handler handler;
	private VoidServiceDB voidService = new VoidServiceDB();
//	private SQLTransaction requestdb = new SQLTransaction("clfcrequest.db");
//	private DBVoidService voidService = new DBVoidService();
//	private LoanPostingService svc = new LoanPostingService();
//	private Map map;
//	private Map params = new HashMap();
//	private Map response = new HashMap();
////	private int size;
//	private boolean hasPendingRequest = false;
//	private String state = "";
	private Task actionTask;
	
	public boolean serviceStarted = false;
	
	public XVoidRequestService(ApplicationImpl app) {
		this.app = app;
	}
	
	public void start() {
		if (handler == null) { 
			handler = new Handler();
//			new RunnableImpl().run(); 
		} 
		if (serviceStarted == false) {
			serviceStarted = true;
			createTask();
			Platform.getTaskManager().schedule(actionTask, 1000, 5000);
		}
	}
	
	public void restart() {
		if (serviceStarted == true) {
			actionTask.cancel();
			actionTask = null;
			serviceStarted = false;
		}
		start();
	}
	
	private void createTask() {
		actionTask = new Task() {
			
			public void run() {
				List<Map> list = new ArrayList<Map>();
				boolean hasPendingRequest = false;
				
				try {
					list = voidService.getPendingVoidRequests( SIZE );
					executeRequests( list );					
					hasPendingRequest = voidService.hasPendingVoidRequest();					
				} catch (Throwable t) {
					t.printStackTrace();
				}
				
				if (hasPendingRequest == false) {
					serviceStarted = false;
					this.cancel();
				}
			}
			
//			public void xrun() {
//				List<Map> list = new ArrayList<Map>();
////				synchronized (VoidRequestDB.LOCK) {
////					requestdb = new SQLTransaction("clfcrequest.db");
//					DBContext ctx = new DBContext("clfcrequest.db");
//					voidService.setDBContext(ctx);
//					voidService.setCloseable(false);
////					System.out.println("ApprovePendingVoidRequest");
//					try {
////						requestdb.beginTransaction();
//						list = voidService.getPendingVoidRequests(SIZE); 
////						requestdb.commit();
//					} catch (Throwable t) {
//						System.out.println("[ApprovePendingVoidRequest] error caused by " + t.getClass().getName() + ": " + t.getMessage()); 
//					} finally {
////						requestdb.endTransaction();
//						ctx.close();
//					}
////				}
//				
////				System.out.println("void request list " + list);
//				try {
//					execRequests(list);
//				} catch (Throwable t) {
//					t.printStackTrace();
//				}
//				
//				hasPendingRequest = false;
////				synchronized (VoidRequestDB.LOCK) {
//					ctx = new DBContext("clfcrequest.db");
//					voidService.setDBContext(ctx);
//					voidService.setCloseable(false);
//					try {
//						hasPendingRequest = voidService.hasPendingVoidRequest();
//					} catch (Throwable t) {
//						t.printStackTrace();
//					} finally {
//						ctx.close();
//					}
////				}
//				
//				if (hasPendingRequest == false) {
//					serviceStarted = false;
//					this.cancel();
//				}
//			}
			
			private void executeRequests( List<Map> list ) throws Exception {
				if (!list.isEmpty()) {
					int size = (list.size() < SIZE - 1? list.size() : SIZE - 1);
					
					StringBuilder appdbsb = new StringBuilder();
					StringBuilder voidrequestdbsb = new StringBuilder();
					String objid = null, state = null;
					for (Map item : list) {
						Map params = new HashMap();
						if (item.containsKey("objid")) {
							objid = item.get("objid").toString();

							params.put("voidid", objid);
							
							LoanPostingService service = new LoanPostingService();
							Map response = service.checkVoidPaymentRequest( params );
							if (response != null && response.containsKey("state")) {
								state = response.get("state").toString();
							}
							
							if (state != null && !state.equals("")) {
								String sql = null;
								
								if ("APPROVED".equals( state )) {
									sql = "update void_request set state='APPROVED' where objid='" + objid + "';";
									
								} else if ("DISAPPROVED".equals( state )) {
									sql = "delete from void_request where objid='" + objid + "';";
								}
								
								if (sql != null && !sql.equals("")) {
									voidrequestdbsb.append( sql );
									appdbsb.append( sql );
									
								}
							}
						}
						
					}
					
					if (!voidrequestdbsb.toString().trim().equals("")) {
						SQLiteDatabase voidrequestdb = ApplicationDatabase.getVoidRequestWritableDB();
						try {
							voidrequestdb.beginTransaction();
							voidrequestdb.rawQuery( voidrequestdbsb.toString().trim(), new String[] {} );
							voidrequestdb.setTransactionSuccessful();
						} catch (Exception e) {
							throw e;
						} finally {
							voidrequestdb.endTransaction();
						}
					}
					
					if (!appdbsb.toString().trim().equals("")) {
						SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
						try {
							appdb.beginTransaction();
							appdb.rawQuery( appdbsb.toString().trim(), new String[] {} );
							appdb.setTransactionSuccessful();
						} catch (Exception e) {
							throw e;
						} finally {
							appdb.endTransaction();
						}
					}
				}
			}
			
//			private void execRequests(List<Map> list) {
//				if (!list.isEmpty()) {
//					size = (list.size() < SIZE-1? list.size() : SIZE-1);
//					for (int i=0; i<size; i++) {
//						map = (Map) list.get(i);
//						
//						params.clear();
//						params.put("voidid", map.get("objid").toString());
//						
//						LoanPostingService svc = new LoanPostingService();
//						response = svc.checkVoidPaymentRequest(params);
//						if (response != null) {
//							state = MapProxy.getString(response, "state");
//						}
//						
//						String objid = map.get("objid").toString();
//						requestdb = new SQLTransaction("clfcrequest.db");
//						SQLTransaction clfcdb = new SQLTransaction("clfc.db");
//						voidService.setDBContext(requestdb.getContext());
//						try {
//							requestdb.beginTransaction();
//							clfcdb.beginTransaction();
////									System.out.println("state " + state);
//							if ("APPROVED".equals(state)) {
//								String sql = "UPDATE void_request SET state = 'APPROVED' WHERE objid=?";
//								synchronized (VoidRequestDB.LOCK) {
//									requestdb.execute(sql, new Object[]{objid});
//								}
//								synchronized (MainDB.LOCK) {
//									clfcdb.execute(sql, new Object[]{objid});
//								}
////										voidService.approveVoidPaymentById(objid);
//							} else if ("DISAPPROVED".equals(state)) {
//								synchronized (VoidRequestDB.LOCK) {
//									requestdb.delete("void_request", "objid=?", new Object[]{objid});
//								}
//								synchronized (MainDB.LOCK) {
//									clfcdb.delete("void_request", "objid=?", new Object[]{objid});
//								}
////										voidService.disapproveVoidPaymentById(objid);
//							}
//							requestdb.commit();
//							clfcdb.commit();
//						} catch (Throwable t) {
//							t.printStackTrace();
//						} finally {
//							requestdb.endTransaction();
//							clfcdb.endTransaction();
//						}
//					}
//				}
//			}
		};
	}
}
