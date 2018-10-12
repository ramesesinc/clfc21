package com.rameses.clfc.android.main;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rameses.clfc.android.ApplicationDatabase;
import com.rameses.clfc.android.ApplicationUtil;
import com.rameses.clfc.android.R;
import com.rameses.clfc.android.db.ApplicationDBUtil;
import com.rameses.clfc.android.db.CSRemarksDB;
import com.rameses.clfc.android.db.CollectionSheetDB;
import com.rameses.clfc.android.db.CollectorRemarksDB;
import com.rameses.clfc.android.db.RemarksServiceDB;
import com.rameses.client.android.Platform;
import com.rameses.client.android.UIDialog;
import com.rameses.util.MapProxy;

public class CollectorRemarksFragment extends Fragment {
	
	private ListView listview;
	private Handler handler = new Handler();
	
	private CSRemarksDB csremarksdb = new CSRemarksDB();
	private CollectionSheetDB collectionsheetdb = new CollectionSheetDB();
	private CollectorRemarksDB collectorremarksdb = new CollectorRemarksDB();
	private RemarksServiceDB remarksservicedb = new RemarksServiceDB();
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_listview, container, false);
		
		listview = (ListView) view.findViewById(R.id.listview);
		
		return view;
	}
	
	public void onStart() {
		super.onStart();
		try {
			loadRemarks();
		} catch (Throwable t) {
			t.printStackTrace();
			UIDialog.showMessage(t, ((CollectionSheetInfoMainActivity) getActivity()));
		}
	}
	
	public void reloadRemarks() {
		loadRemarks();
	}
	
	private void loadRemarks() {
		Bundle args = getArguments();
		final String objid = args.getString("objid");
		
		handler.post(new Runnable() {
			
			public void run() {
				try {
					runImpl();
				} catch (Throwable t) {
					t.printStackTrace();
					UIDialog.showMessage(t, ((CollectionSheetInfoMainActivity) getActivity()));
				}
			}
			
			private void runImpl() throws Exception {
				List<Map> list = collectorremarksdb.getRemarks( objid );
				
				boolean flag = false;				
				Map item = collectionsheetdb.findCollectionSheet( objid );
				if (item != null && !item.isEmpty()) {
					String val = "";
					if (item.containsKey("type")) {
						val = item.get("type").toString().toLowerCase();
					}
					
					if (!val.equals("followup")) {
						flag = true;
					}
				}
				
				item = new HashMap();
				if (flag == true) {
					item = csremarksdb.findRemarksById( objid );
				}
				
				if (item != null && !item.isEmpty()) {
					Map map = new HashMap();
					String date = new SimpleDateFormat("yyyy-MM-dd").format(Platform.getApplication().getServerDate());
					map.put("txndate", date);
					map.put("collectorname", item.get("collector_name").toString());
					map.put("remarks", item.get("remarks").toString());
					list.add(0, map);
				}

				listview.setAdapter(new RemarksAdapter(getActivity(), list));
				listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						if (position > 0) return true;
						
						boolean hasremarks = false;
						try {
							hasremarks = csremarksdb.hasRemarksById( objid );
						} catch (Throwable t) {;}
						
						if (hasremarks == false) return true;
						
						try {
							hasremarks = remarksservicedb.hasRemarksById( objid );
						} catch (Throwable t) {;}
						
						if (hasremarks == false) return true;
						
						CharSequence[] items = {"Edit Remarks", "Remove Remarks"};
						UIDialog dialog = new UIDialog((CollectionSheetInfoMainActivity) getActivity()) {
							public void onSelectItem(int index) {
								switch (index) {
									case 0: editRemarks(); break;
									case 1: removeRemarks(); break;
								}
							}
						};
						
						dialog.select(items);
						
						return false;
					}
					
					private void editRemarks() {
						UIDialog dialog = new UIDialog((CollectionSheetInfoMainActivity) getActivity()) {
							
							public boolean onApprove(Object value) {
								if (value == null || "".equals(value.toString())) {
									ApplicationUtil.showShortMsg("Remarks is required.");
									return false;
								}
								
								final CollectionSheetInfoMainActivity activity = (CollectionSheetInfoMainActivity) getActivity();
								
								String appdbsql = "update remarks set remarks='" + value.toString() + "' where objid='" + objid + "';";
								
								SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
								try {
									appdb.beginTransaction();
									appdb.execSQL( appdbsql );
									appdb.setTransactionSuccessful();
								} catch (Throwable t) {
									t.printStackTrace();
									UIDialog.showMessage(t, activity);
								} finally {
									appdb.endTransaction();
								}								
								
								String remarksdbsql = "update remarks set remarks='" + value.toString() + "', state='PENDING' where objid='" + objid + "';";
								
								SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
								try {
									remarksdb.beginTransaction();
									remarksdb.execSQL( remarksdbsql );
									remarksdb.setTransactionSuccessful();
								} catch (Throwable t) {
									t.printStackTrace();
									UIDialog.showMessage(t, activity);
								} finally {
									remarksdb.endTransaction();
								}

								ApplicationUtil.showShortMsg("Successfully updated remark.");
								
								activity.getHandler().post(new Runnable() {
									public void run() {
										loadRemarks();
										activity.supportInvalidateOptionsMenu();
										activity.sendBroadcast(new Intent("rameses.clfc.REMARK_START_SERVICE"));
										//start remarks service
//										activity.getApp().remarksSvc.start();
									}
								});
								return true;
							}
						};
						
						Map item = new HashMap();
						try {
							item = csremarksdb.findRemarksById( objid );
						} catch (Throwable t) {
							t.printStackTrace();
						}
						
						String value = "";
						if (item != null && !item.isEmpty()) value = item.get("remarks").toString();
						dialog.input(value);
					}
					
					private void removeRemarks() {
						final CollectionSheetInfoMainActivity activity = (CollectionSheetInfoMainActivity) getActivity();
						
						String sql = "delete from remarks where objid='" + objid + "';";
						SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
						try {
							remarksdb.beginTransaction();
							remarksdb.execSQL( sql );
							remarksdb.setTransactionSuccessful();
						} catch (Throwable t) {
							 UIDialog.showMessage(t, activity);
						} finally {
							remarksdb.endTransaction();
						}
						
						sql = "delete from remarks where objid='" + objid + "';";
						SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();						
						try {
							appdb.beginTransaction();
							appdb.execSQL( sql );
							appdb.setTransactionSuccessful();
						} catch (Throwable t) {
							UIDialog.showMessage(t, activity);
						} finally {
							appdb.endTransaction();
						}
						
						Map collectionsheet = new HashMap();						
						try {
							collectionsheet = collectionsheetdb.findCollectionSheet( objid );
						} catch (Throwable t) {
							 UIDialog.showMessage(t, activity);
						}
						
						Map params = new HashMap();
						params.put("objid", objid);
						params.put("billingid", MapProxy.getString(collectionsheet, "billingid"));
						params.put("itemid", MapProxy.getString(collectionsheet, "itemid"));
						params.put("state", "PENDING");
						
						sql = ApplicationDBUtil.createInsertSQLStatement("remarks_removed", params);
						
						SQLiteDatabase remarksremoveddb = ApplicationDatabase.getRemarksRemovedWritableDB();
						try {
							remarksremoveddb.beginTransaction();
							remarksremoveddb.execSQL( sql );
							remarksremoveddb.setTransactionSuccessful();
						} catch (Throwable t) {
							 UIDialog.showMessage(t, activity);
						} finally {
							remarksremoveddb.endTransaction();
						}					
						
						ApplicationUtil.showShortMsg("Successfully removed remarks.");
						activity.getHandler().post(new Runnable() {
							public void run() {
								loadRemarks();
								activity.supportInvalidateOptionsMenu();
								activity.sendBroadcast(new Intent("rameses.clfc.REMARK_REMOVED_START_SERVICE"));
							}
						});
					}
					
				});
				
			}
			/*
			public void xrun() {
				DBContext ctx = new DBContext("clfc.db");
				
//				DBCollectorRemarks dbremarks = new DBCollectorRemarks();
//				dbremarks.setDBContext(ctx);
//				dbremarks.setCloseable(false);
				
				List<Map> list = new ArrayList<Map>();
				
				try {
//					list = dbremarks.getRemarks(objid);
					list = collectorremarksdb.getRemarks( objid );
				} catch (Throwable t) {
					t.printStackTrace();
					UIDialog.showMessage(t, ((CollectionSheetInfoMainActivity) getActivity()));
				}
//				
//				ctx = new DBContext("clfc.db");
//				DBCSRemarks remarkscs = new DBCSRemarks();
//				remarkscs.setDBContext(ctx);
//				remarkscs.setCloseable(false);
//				
//				DBCollectionSheet dbcs = new DBCollectionSheet();
//				dbcs.setDBContext(ctx);
//				dbcs.setCloseable(false);
				
				Map item = new HashMap();
				try {
					boolean flag = false;
					
					Map xitem = collectionsheetdb.findCollectionSheet( objid );
					
					
//					Map xitem = dbcs.findCollectionSheet(objid);
					if (xitem != null && !xitem.isEmpty()) {
						String type = xitem.get("type").toString();
						if (!type.toLowerCase().equals("followup")) {
							flag = true;
						}
					}
					
					if (flag == true) {
//						item = remarkscs.findRemarksById(objid);
						item = csremarksdb.findRemarksById( objid );
					}
				} catch (Throwable t) {
					t.printStackTrace();
					UIDialog.showMessage(t, ((CollectionSheetInfoMainActivity) getActivity()));
				} finally {
					ctx.close();
				}
				
				if (item != null && !item.isEmpty()) {
					Map map = new HashMap();
					String date = new SimpleDateFormat("yyyy-MM-dd").format(Platform.getApplication().getServerDate());
					map.put("txndate", date);
					map.put("collectorname", item.get("collector_name").toString());
					map.put("remarks", item.get("remarks").toString());
					list.add(0, map);
				}
				
				listview.setAdapter(new RemarksAdapter(getActivity(), list));
				listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						if (position > 0) return true;
						
						boolean hasremarks = false;
//						DBContext ctx = new DBContext("clfc.db");
//						DBCSRemarks csremarks = new DBCSRemarks();
//						csremarks.setDBContext(ctx);
//						csremarks.setCloseable(false);
						
						try {
//							hasremarks = csremarks.hasRemarksById(objid);
							hasremarks = csremarksdb.hasRemarksById( objid );
						} catch (Throwable t) {;}
						
						if (hasremarks == false) return true;
						
//						ctx = new DBContext("clfcremarks.db");
//						DBRemarksService remarkssvc = new DBRemarksService();
//						remarkssvc.setDBContext(ctx);
//						remarkssvc.setCloseable(false);
						
						try {
//							hasremarks = remarkssvc.hasRemarksById(objid);
							hasremarks = remarksservicedb.hasRemarksById( objid );
						} catch (Throwable t) {;}
						
						if (hasremarks == false) return true;
						
						CharSequence[] items = {"Edit Remarks", "Remove Remarks"};
						UIDialog dialog = new UIDialog((CollectionSheetInfoMainActivity) getActivity()) {
							public void onSelectItem(int index) {
								switch (index) {
									case 0: editRemarks(); break;
									case 1: removeRemarks(); break;
								}
							}
						};
						
						dialog.select(items);
						
						return false;
					}
					
					private void editRemarks() {
						UIDialog dialog = new UIDialog((CollectionSheetInfoMainActivity) getActivity()) {
							
							public boolean onApprove(Object value) {
								if (value == null || "".equals(value.toString())) {
									ApplicationUtil.showShortMsg("Remarks is required.");
									return false;
								}
								
								final CollectionSheetInfoMainActivity activity = (CollectionSheetInfoMainActivity) getActivity();
								
								String appdbsql = "update remarks set remarks='" + value.toString() + "' where objid='" + objid + "';";
								
								SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();
								try {
									appdb.beginTransaction();
									appdb.execSQL( appdbsql );
									appdb.setTransactionSuccessful();
								} catch (Throwable t) {
									t.printStackTrace();
									UIDialog.showMessage(t, activity);
								} finally {
									appdb.endTransaction();
								}								
								
								String remarksdbsql = "update remarks set remarks='" + value.toString() + "', state='PENDING' where objid='" + objid + "';";
								
								SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
								try {
									remarksdb.beginTransaction();
									remarksdb.execSQL( remarksdbsql );
									remarksdb.setTransactionSuccessful();
									activity.sendBroadcast(new Intent("rameses.clfc.REMARK_START_SERVICE"));
								} catch (Throwable t) {
									t.printStackTrace();
									UIDialog.showMessage(t, activity);
								} finally {
									remarksdb.endTransaction();
								}

								ApplicationUtil.showShortMsg("Successfully updated remark.");
								
								activity.getHandler().post(new Runnable() {
									public void run() {
										loadRemarks();
										
										//start remarks service
//										activity.getApp().remarksSvc.start();
									}
								});

//								SQLTransaction clfcdb = new SQLTransaction("clfc.db");
//								SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");
//
//								final CollectionSheetInfoMainActivity activity = (CollectionSheetInfoMainActivity) getActivity();
//								
//								try {
//									clfcdb.beginTransaction();
//									remarksdb.beginTransaction();
//									
//									onApproveImpl(clfcdb, remarksdb, value.toString());
//									
//									clfcdb.commit();
//									remarksdb.commit();
//									
//									activity.getHandler().post(new Runnable() {
//										public void run() {
//											loadRemarks();
//											activity.getApp().remarksSvc.start();
//										}
//									});
//								} catch (Throwable t) {
//									t.printStackTrace();
//									UIDialog.showMessage(t, activity);
//								} finally {
//									clfcdb.endTransaction();
//									remarksdb.endTransaction();
//								}

								return true;
							}
//							
//							private void onApproveImpl(SQLTransaction clfcdb, 
//									SQLTransaction remarksdb, String remarks) throws Exception {
//								
//								Map params = new HashMap();
//								params.put("remarks", remarks);
//								
//								synchronized (MainDB.LOCK) {
//									clfcdb.update("remarks", "objid='"+objid+"'", params);
//								}
//
//								params.put("state", "PENDING");
//								synchronized (RemarksDB.LOCK) {
//									remarksdb.update("remarks", "objid='"+objid+"'", params);
//								}
////								remarksdb.update("remarks", "loanappid='"+loanappid+"'", params);
//								ApplicationUtil.showShortMsg("Successfully updated remark.");
//								
//							}
						};
//						
//						DBContext ctx = new DBContext("clfc.db");
//						DBCSRemarks csremarks = new DBCSRemarks();
//						csremarks.setDBContext(ctx);
//						csremarks.setCloseable(false);
						
						Map item = new HashMap();
						try {
//							item = csremarks.findRemarksById(objid);
							item = csremarksdb.findRemarksById( objid );
						} catch (Throwable t) {
							t.printStackTrace();
						}
						
						String value = "";
						if (item != null && !item.isEmpty()) value = item.get("remarks").toString();
						dialog.input(value);
					}
					
					private void removeRemarks() {
						final CollectionSheetInfoMainActivity activity = (CollectionSheetInfoMainActivity) getActivity();
						
						String sql = "delete from remarks where objid='" + objid + "';";
						SQLiteDatabase remarksdb = ApplicationDatabase.getRemarksWritableDB();
						try {
							remarksdb.beginTransaction();
							remarksdb.execSQL( sql );
							remarksdb.setTransactionSuccessful();
						} catch (Throwable t) {
							 UIDialog.showMessage(t, activity);
						} finally {
							remarksdb.endTransaction();
						}
						
//						synchronized (RemarksDB.LOCK) {
//							SQLTransaction remarksdb = new SQLTransaction("clfcremarks.db");
//							try {
//								remarksdb.beginTransaction();
//								remarksdb.delete("remarks", "objid=?", new Object[]{objid});
//								remarksdb.commit();
//							} catch (Throwable t) {
//								 UIDialog.showMessage(t, activity);
//								
//							} finally {
//								remarksdb.endTransaction();
//							}
//						}
						
						
						sql = "delete from remarks where objid='" + objid + "';";
						SQLiteDatabase appdb = ApplicationDatabase.getAppWritableDB();						
						try {
							appdb.beginTransaction();
							appdb.execSQL( sql );
							appdb.setTransactionSuccessful();
						} catch (Throwable t) {
							UIDialog.showMessage(t, activity);
						} finally {
							appdb.endTransaction();
						}
						
//						synchronized (MainDB.LOCK) {
//							SQLTransaction clfcdb = new SQLTransaction("clfc.db");
//							try {
//								clfcdb.beginTransaction();
//								clfcdb.delete("remarks", "objid=?", new Object[]{objid});
//								clfcdb.commit();
//							} catch (Throwable t) {
//								 UIDialog.showMessage(t, activity);
//								
//							} finally {
//								clfcdb.endTransaction();
//							}
//						}

						Map collectionsheet = new HashMap();
//						DBContext ctx = new DBContext("clfc.db");
//						DBCollectionSheet dbcs = new DBCollectionSheet();
//						dbcs.setDBContext(ctx);
						
						try {
							collectionsheet = collectionsheetdb.findCollectionSheet( objid );
						} catch (Throwable t) {
							 UIDialog.showMessage(t, activity);
						}
						
						sql = "insert or ignore into remarks_removed(`objid`,`billingid`,`itemid`,`state`) ";
						sql += "values(";
						
						//objid
						sql += "'" + objid + "'";
						
						//billingid
						sql += ",'" + collectionsheet.get("billingid").toString() + "'";
						
						//itemid
						sql += ",'" + collectionsheet.get("itemid").toString() + "'";
						
						//state
						sql += ",'PENDING'";
						
						sql += ");";
						
						SQLiteDatabase remarksremoveddb = ApplicationDatabase.getRemarksRemovedWritableDB();
						try {
							remarksremoveddb.beginTransaction();
							remarksremoveddb.execSQL( sql );
							remarksremoveddb.setTransactionSuccessful();
						} catch (Throwable t) {
							 UIDialog.showMessage(t, activity);
						} finally {
							remarksremoveddb.endTransaction();
						}
					

//				 		Map params = new HashMap();
//				 		params.put("objid", objid);
//				 		params.put("billingid", collectionsheet.get("billingid").toString());
//				 		params.put("itemid", collectionsheet.get("itemid").toString());
//				 		params.put("state", "PENDING");
//				 		
//						synchronized (RemarksRemovedDB.LOCK) {
//							SQLTransaction remarksremoveddb = new SQLTransaction("clfcremarksremoved.db");
//							try {
//								remarksremoveddb.beginTransaction();
//						 		remarksremoveddb.insert("remarks_removed", params);						 		
//								remarksremoveddb.commit();
//							} catch (Throwable t) {
//								 UIDialog.showMessage(t, activity);
//								
//							} finally {
//								remarksremoveddb.endTransaction();
//							}
//						}
						
						ApplicationUtil.showShortMsg("Successfully removed remarks.");
						activity.getHandler().post(new Runnable() {
							public void run() {
								loadRemarks();
								//start remarks removed service
//								activity.getApp().remarksRemovedSvc.start();
								activity.supportInvalidateOptionsMenu();
								activity.sendBroadcast(new Intent("rameses.clfc.REMARK_REMOVED_START_SERVICE"));
							}
						});
					}
					
				});
			
			}
			*/
		});
	}
	
}
