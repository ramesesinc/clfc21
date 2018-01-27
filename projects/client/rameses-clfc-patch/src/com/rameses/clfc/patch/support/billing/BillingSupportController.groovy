package com.rameses.clfc.patch.support.billing

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;

class BillingSupportController 
{
    @Service('BillingSupportService')
    def service;
    
    String title = 'Billing Support';
    
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    
    def resolveRemittedCollection() {
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
                        MsgBox.alert("Successfully resolved remitted collection! ");

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
        
        service.resolveRemittedCollection(handler);
        return loadingOpener;
    }
    
    def resolvePostedCollection() {
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
                        MsgBox.alert("Successfully resolved posted collection! ");

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
        
        service.resolvePostedCollection(handler);
        return loadingOpener;
    }
    
    def close() {
        return '_close';
    }
}

