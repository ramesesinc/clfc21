package com.rameses.clfc.loan.payment.capture

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;

class CapturePaymentController {
    
    @Binding
    def binding;
    
    @Service("LoanCapturePaymentService")
    def service
    
    def entity, mode = "read";
    def preventity, prevlist;
    def selectedPayment;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    
    void open() {
        entity = service.open( entity );
        mode = "read";
        binding?.refresh();
        listHandler?.reload();
    }
    
    def close() {
        return "_close";
    }
    
    def cancel() {
        if (mode == "edit") {
            
            if (prevenity) {
                entity = preventity;
            }
            
            if (prevlist) {
                entity.list = prevlist;
            }
            
            mode = "read";
            
            binding?.refresh();
            listHandler?.reload();
            
            return null;
        }
        return "_close";
    }
    
    def listHandler = [
        fetchList: { o->
            if (!entity.list) entity.list = [];
            return entity.list;
        }
    ] as BasicListModel;

    void returnForMapping() {
        entity = service.returnForMapping( entity );
        binding?.refresh();
        listHandler?.reload();
    }
    
    void submitForVerification() {
        if (!MsgBox.confirm("You are about to submit this document for verification. Continue?")) return;
        
        entity = service.submitForVerification( entity );
        binding?.refresh();
        listHandler?.reload();
    }
    
    def verify() {
        if (!MsgBox.confirm('You are about to verify this document. Continue?')) return;
        
        def handler;
        
        if (!handler) {
            handler = [
                onMessage: { o->
                    //println 'EOF ' + AsyncHandler.EOF;
                    if (o == AsyncHandler.EOF) {
                        loadingOpener.handle.binding.fireNavigation("_close");
                        return;
                    }

                    entity = o;
                    
                    loadingOpener.handle.binding.fireNavigation("_close");
                    MsgBox.alert("Successfully verified document!");
                    binding.refresh();
                    listHandler?.reload();
                },
                onTimeout: {
                    handler?.retry(); 
                },
                onCancel: {
                    println 'processing cancelled.';
                    //fires when cancel() method is executed 
                }, 
                onError: { o->
                    loadingOpener.handle.binding.fireNavigation("_close");
                    MsgBox.err(o.message);
                }
            ] as AbstractAsyncHandler;
        }
        service.asyncVerify(entity, handler);
        return loadingOpener;
    }
}

