package com.rameses.clfc.audit.tag.withpartial

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class WithPartialTagPaymentController 
{
    @Binding
    def binding;
    
    @Service('WithPartialPaymentTagService')
    def service;
    
    String title = 'With Partial Payment Tag';
    
    def entity, refno, txndate;
    def borrowerLookup = Inv.lookupOpener('ledgertag:lookup', [
         onselect: { o->
             entity = service.getLedgerInfo(o);
             listHandler?.reload();
             binding?.refresh();
         }
    ]);

    def close() {
        return '_close';
    }
    
    def selectedItem;
    def listHandler = [
        getColumns: { o->
            return service.getColumns(o);
        },
        fetchList: { o->
            o.ledgerid = entity?.ledger?.objid;
            o.refno = refno;
            o.txndate = txndate;
            return service.getPayments(o);
        }
    ] as BasicListModel;    
    
    void reload() {
        listHandler?.reload();
    }
    
    def addTag() {
        if (!selectedItem) return;
        
        def handler = { o->
            selectedItem.postingdate = o;
            service.addTag(selectedItem);;
            reload();
        }
        def op = Inv.lookupOpener('tag:withpartial:addinfo', [handler: handler]);
        if (!op) return null;
        return op;
        
    }
    
    void removeTag() {
        if (!selectedItem) return;
        
        service.removeTag(selectedItem);
        reload();
    }
}

