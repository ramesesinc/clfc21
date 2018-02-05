package com.rameses.clfc.patch.support.followupresult

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;

class FollowupResultSupportController 
{
    @Service('FollowupResultSupportService')
    def service;
    
    String title = 'Follow-up Result Support'
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    
    def close() {
        return '_close';
    }
    
    def resolveFollowupResult() {
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
                        MsgBox.alert("Successfully resolved follow-up results! ");

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
        
        service.resolveFollowupResult(handler);
        return loadingOpener;
    }
    
    
    def resolveCollectorRemarks() {
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
                        MsgBox.alert("Successfully resolved collector remarks! ");

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
        
        service.resolveCollectorRemarks(handler);
        return loadingOpener;
    }
}

