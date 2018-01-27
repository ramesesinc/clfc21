package com.rameses.clfc.patch.ledger.edit.posting.branch

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class PaymentPostingDetailController 
{
    @Caller
    def caller;
    
    @Service('EditBranchLedgerPostingService')
    def service;
    
    def handler, entity, refid;
    
    void init() {
        entity = service.getPaymentInfo(entity);
    }
    
    def selectedItem;
    def listHandler = [
        fetchList: { o->
            o.paymentid = entity?.objid;
            o.refid = refid;
            def list = service.getDetailByRefno(o);
            if (!list) list = [];
            return list;
        },
        getColumnList: { o->
            def list = service.getColumns(o);
            if (!list) list = [];
            return list;
        }
    ] as BasicListModel;
    
    def close() {
        return '_close';
    }
	
    def addDetail() {
        def handler = { o->
            o.refid = refid;
            o.paymentid = entity?.objid;
            service.saveLedgerDetail(o);
            EventQueue.invokeLater({
                listHandler?.reload();
                caller?.listHandler?.reload();
            });
        }
        def op = Inv.lookupOpener('edit:posting:branch:detail:create', [handler: handler, payment: entity]);
        if (!op) return null;
        return op;
    }
    
    void removeDetail() {
        if (!selectedItem) return;
        
        if (!MsgBox.confirm('You are about to remove this item. Continue?')) return;
        
        service.removeLedgerDetail(selectedItem);
        EventQueue.invokeLater({
            listHandler?.reload();
            caller?.listHandler?.reload();
        });
    }
    
    def editDetail() {
        if (!selectedItem) return;
        def handler = { o->
            o.refid = refid;
            service.saveLedgerDetail(o);
            EventQueue.invokeLater({
                listHandler?.reload();
                caller?.listHandler?.reload();
            });
        }
        def op = Inv.lookupOpener('edit:posting:branch:detail:edit', [entity: selectedItem, handler: handler]);
        if (!op) return null;
        return op;
    }
}

