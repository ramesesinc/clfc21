package com.rameses.clfc.patch.ledger.edit.posting

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class EditLedgerPostingController 
{
    @Binding
    def binding;
    
    @Service('EditLedgerPostingService')
    def service;
    
    def ledger, entity;
    
    def getTitle() {
        def str = 'Edit Ledger Posting';
        
        if (entity?.borrower) {
            str += ': ' + entity?.borrower?.name;
        }
        
        return str;
    }
    
    @FormTitle()
    def getFormTitle() {
        def str = 'Ledger Posting';
        if (ledger) {
            str += ': ' + ledger?.borrower?.name;
        }
        return str;
    }
    
    @FormId
    def getFormId() {
        if (!ledger) return new UID();
        return 'EL' + ledger?.objid;
    }
    
    void init() {
        entity = service.openLedger([objid: ledger?.objid]);
        binding?.refresh('title');
    }
    
    def selectedItem;
    def listHandler = [
        getItems: {
            return service.getLedgerPosting([ledgerid: ledger?.objid]);
        }, 
        onselect: { o->
            binding?.refresh('opener');
        }
    ] as ListPaneModel;
    
    def getOpener() {
        if (!selectedItem) return;
        
        def invtype = 'edit:posting:' + selectedItem?.type?.toLowerCase();
        def op = Inv.lookupOpener(invtype, [entity: entity, refid: selectedItem?.refid]);
        if (!op) return null;
        return op;
    }
    
    def close() {
        return '_close';
    }
}

