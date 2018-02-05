package com.rameses.clfc.patch.ledger.edit.posting.fix

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class EditFixLedgerPostingController 
{    
    @Service('EditFixLedgerPostingService')
    def service;
    
    def entity, refid;
    void init() {
        entity?.refid = refid;
        entity = service.getLedgerInfo(entity);
        if (!entity) entity = [:];
    }
    
    def selectedItem;
    def listHandler = [
        fetchList: { o->
            o.payment = selectedPayment;
            o.refid = refid;
            def list = service.getDetail(o);
            if (!list) list = [];
            return list;
        },
        getColumnList: { o->
            def list = service.getColumns(o);
            if (!list) list = [];
            return list;
        }
    ] as BasicListModel;
    
    def selectedPayment;
    def paymentHandler = [
        getItems: {
            if (!entity?.payments) entity.payments = [];
            return entity.payments;
        },
        onselect: { o->
            listHandler?.reload();
        }
    ] as ListPaneModel;
    
    def editPosting() {
        if (!selectedPayment) return;
        
        def op = Inv.lookupOpener('posting:fix:edit', [entity: selectedPayment, refid: refid]);
        if (!op) return null;
        return op;
    }
}

