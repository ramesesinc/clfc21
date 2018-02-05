package com.rameses.clfc.loan.payment.capture

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.framework.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;

class CapturePaymentMappingController {
    
    @Binding
    def binding;
    
    @Service("LoanCapturePaymentService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    String title = "Mapping Captured Payments";
    
    def date, collector, collection;
    def action = "init", mode = "read";
    def entity, preventity, prevlist;
    def selectedPayment, isCollector = false;
    def headers = ClientContext.currentContext.headers;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    
    @PropertyChangeListener
    def listener = [
        "collector": { o->
            if (isCollector == true) {
                collector = [
                    objid   : headers.USERID,
                    name    : headers.NAME
                ]
            }
        }
    ]
    
    def ledgerLookupHandler = Inv.lookupOpener("ledgerborrower:lookup", [ 
        onselect: { o-> 
            selectedPayment.ledgerid = o.objid;
            selectedPayment.ledger = o; 
        }
    ]);

    void init() {
        date = dateSvc.getServerDateAsString().split(" ")[0];
        collector = [:];
        collection = [:];
        
        if (headers.ROLES.containsKey("LOAN.FIELD_COLLECTOR")) {
            isCollector = true;
        }
        
        action = "init";
        binding?.refresh();
    }
    
    def getCollectorList() {
        if (!date) return [];
        return service.getForMappingCollectors( [date: date] );
    }
    
    def getCollectionList() {
        if (!date || !collector) return [];
        return service.getForMappingCollection( [date: date, collectorid: collector?.objid] );
    }
    
    def close() {
        return "_close";
    }
    
    def next() {
        entity = service.open( [objid: collection?.objid] );
        listHandler?.reload();
        action = "collection";
        mode = "read";
        return "collection";
    }
    
    def back() {
        action = "init";
        return "default";
    }    

    def listHandler = [
        fetchList: { o->
            if (!entity.list) entity.list = [];
            return entity.list;
        }
    ] as BasicListModel;
    
    void edit() {
        preventity = [:];
        preventity.putAll(entity);
        
        prevlist = [];
        entity.list.each{ 
            def item = [:];
            item.putAll(it);
            prevlist << item;
        }
        
        mode = "edit";
        binding?.refresh();
    }
    
    void cancel() {
        if (preventity) {
            entity = preventity;
        }
        
        if (prevlist) {
            entity.list = prevlist;
        }
        
        mode = "read";
        
        binding?.refresh();
        listHandler?.reload();
    }
    
    void save() {
        def unmapped = entity.list.find{ it.ledger == null };
        if (unmapped) {
            throw new Exception("Payment for borrower " + unmapped.borrowername + " has not been mapped.");
        }

        if (!MsgBox.confirm("You are about to save mapped payments. Continue?")) return;
        
        entity = service.save(entity);
        mode = "read";
        binding?.refresh();
        listHandler?.reload();
    }
    
    void submitForVerification() {
        if (!MsgBox.confirm("You are about to submit this request for verification. Continue?")) return;
        
        entity = service.submitForVerification( entity );
        binding?.refresh();
        listHandler?.reload();
    }
    
    void returnForMapping() {
        entity = service.returnForMapping( entity );
        binding?.refresh();
        listHandler?.reload();
    }
    
    /*
    void verify() {
        if (!MsgBox.confirm("You are about to verify this document. Continue?")) return;
        
        entity = service.verify( entity );
        binding?.refresh();
        listHandler?.reload();
    }
    */
   
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
    
    void remit() {
        if (!MsgBox.confirm("You are about to remit this document. Continue?")) return;
        
        entity = service.remit(entity);
        binding?.refresh();
        listHandler?.reload();
    }
}

