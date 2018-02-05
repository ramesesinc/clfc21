package com.rameses.clfc.patch.ledger.payment

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class LedgerPaymentController 
{
    @Binding
    def binding;
    
    @Service('LedgerPaymentUpdateService')
    def service;
    
    String title = 'Ledger Payment Update';
    
    def entity;
    def borrowerLookup = Inv.lookupOpener('payment:update:ledger:lookup', [
         onselect: { o->
             entity = service.getLedgerInfo(o);
             binding?.refresh();
             listHandler?.reload();
         }
    ]);

    def searchtext, selectedItem;
    def listHandler = [
        fetchList: { o->
            o.ledgerid = entity?.ledger?.objid;
            o.searchtext = searchtext;
            def list = service.getPayments(o);
            if (!list) list = [];
            return list;
        },
        getColumnList: { o->
            def list = service.getPaymentListColumns(o);
            if (!list) list  = [];
            return list;
        }
    ] as BasicListModel;
    
    void search() {
        listHandler?.reload();
    }
    
    def close() {
        return '_close';
    }
    
    def addPayment() {
        def handler = { o->
            service.addPayment([ledgerid: entity?.ledger?.objid, payment: o]);
            listHandler?.reload();
        }
        
        def op = Inv.lookupOpener('ledger:payment:update:create', [handler: handler]);
        if (!op) return null;
        return op;
    }
    
    void removePayment() {
        if (!MsgBox.confirm('You are about to remove this payment. Continue?')) return;
        
        service.removePayment(selectedItem);
        listHandler?.reload();
    }
}

