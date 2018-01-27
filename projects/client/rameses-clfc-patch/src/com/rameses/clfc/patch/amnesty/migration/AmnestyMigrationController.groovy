package com.rameses.clfc.patch.amnesty.migration

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;

class AmnestyMigrationController 
{
    @Service('AmnestyMigrationService')
    def service;
    
    String title = 'Migrate Amnesty';
    
    def close() {
        return '_close';
    }
    
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    
    public def runMigration() {
        def handler;
        
        if (!handler) {
            handler = [
                onMessage: { o->
                    if (o == AsyncHandler.EOF) {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        return;
                    }
                    
                    try {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        MsgBox.alert("Successfully migrated amnesties! ");

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                },
                onTimeout: {
                    handler?.retry();
                },
                onCancel: {
                    println 'processing cancelled.';
                    //fires when cancel() method is executed 
                }, 
                onError: { o->
                    println 'onerror';
                       loadingOpener?.handle?.binding?.fireNavigation("_close");
                    MsgBox.err(o.message);
                }
            ] as AbstractAsyncHandler;
        }
        
        service.runMigration(handler);
        return loadingOpener;
    }
    
    public def runMigrationForApproved() {
        def handler;
        
        if (!handler) {
            handler = [
                onMessage: { o->
                    if (o == AsyncHandler.EOF) {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        return;
                    }
                    
                    try {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        MsgBox.alert("Successfully migrated amnesties! ");

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                },
                onTimeout: {
                    handler?.retry();
                },
                onCancel: {
                    println 'processing cancelled.';
                    //fires when cancel() method is executed 
                }, 
                onError: { o->
                    println 'onerror';
                       loadingOpener?.handle?.binding?.fireNavigation("_close");
                    MsgBox.err(o.message);
                }
            ] as AbstractAsyncHandler;
        }
        
        service.runMigrationForApproved(handler);
        return loadingOpener;
    }
    
    public def resolveDateReturned() {
        def handler;
        
        if (!handler) {
            handler = [
                onMessage: { o->
                    if (o == AsyncHandler.EOF) {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        return;
                    }
                    
                    try {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        MsgBox.alert("Successfully resolved date returned! ");

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                },
                onTimeout: {
                    handler?.retry();
                },
                onCancel: {
                    println 'processing cancelled.';
                    //fires when cancel() method is executed 
                }, 
                onError: { o->
                    println 'onerror';
                       loadingOpener?.handle?.binding?.fireNavigation("_close");
                    MsgBox.err(o.message);
                }
            ] as AbstractAsyncHandler;
        }
        
        service.resolveDateReturned(handler);
        return loadingOpener;
    }
    
    public def resolveTerm() {
        def handler;
        
        if (!handler) {
            handler = [
                onMessage: { o->
                    if (o == AsyncHandler.EOF) {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        return;
                    }
                    
                    try {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        MsgBox.alert("Successfully resolve term!");

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                },
                onTimeout: {
                    handler?.retry();
                },
                onCancel: {
                    println 'processing cancelled.';
                    //fires when cancel() method is executed 
                }, 
                onError: { o->
                    println 'onerror';
                    loadingOpener?.handle?.binding?.fireNavigation("_close");
                    MsgBox.err(o.message);
                }
            ] as AbstractAsyncHandler;
        }
        
        service.resolveAmnestyWithNoMaturity(handler);
        //svc.rebuild(entity, handler);
        return loadingOpener;
    }
    
    public def resolveRejectedAmnesty() {
        def handler;
        if (!handler) {
            handler = [
                onMessage: { o->
                    if (o == AsyncHandler.EOF) {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        return;
                    }
                    
                    try {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        MsgBox.alert("Successfully resolved rejected amnesty!");

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                },
                onTimeout: {
                    handler?.retry();
                },
                onCancel: {
                    println 'processing cancelled.';
                    //fires when cancel() method is executed 
                }, 
                onError: { o->
                    println 'onerror';
                    loadingOpener?.handle?.binding?.fireNavigation("_close");
                    MsgBox.err(o.message);
                }
            ] as AbstractAsyncHandler;
        }
        
        service.resolveRejectedAmnesty(handler);
        //svc.rebuild(entity, handler);
        return loadingOpener;
    }
    
    public def resolveAvailedAmnesty() {
        def handler;
        if (!handler) {
            handler = [
                onMessage: { o->
                    if (o == AsyncHandler.EOF) {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        return;
                    }
                    
                    try {
                        loadingOpener?.handle?.binding?.fireNavigation("_close");
                        MsgBox.alert("Successfully resolved availed amnesty!");

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                },
                onTimeout: {
                    handler?.retry();
                },
                onCancel: {
                    println 'processing cancelled.';
                    //fires when cancel() method is executed 
                }, 
                onError: { o->
                    println 'onerror';
                    loadingOpener?.handle?.binding?.fireNavigation("_close");
                    MsgBox.err(o.message);
                }
            ] as AbstractAsyncHandler;
        }
        
        service.resolveAvailedAmnesty(handler);
        //svc.rebuild(entity, handler);
        return loadingOpener;
    }
}
