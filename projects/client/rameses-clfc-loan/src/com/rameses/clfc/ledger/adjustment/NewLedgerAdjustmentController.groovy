package com.rameses.clfc.ledger.adjustment

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;
import java.rmi.server.UID;

class NewLedgerAdjustmentController {

    @Caller
    def caller;
    
    @Binding
    def binding;
    
    @Service("NewLedgerAdjustmentController")
    def service;
    
    def entity, mode = 'read';
    def ledger, preventity;
    
    def createEntity() {
        return [
            objid       : 'LLA' + new UID(),
            txnstate    : 'DRAFT',
            ledgerid    : ledger?.objid,
            borrower    : ledger?.borrower,
            requesttype : 'ADJUSTMENT',
            debit       : [:],
            credit      : [:]
        ];
    }
    def getTypeList() {
        return service.getTypeList();
    }
    
    void create() {
        entity = createEntity();
        mode = "create";
    }
    
    void open() {
        entity = service.open(entity);
        mode = "open";
    }
    
    def close() {
        return "_close";
    }
    
    def cancel() {
        if (mode == "edit") {
            
            mode = "read";
            
            return null;
        }
        
        return "_close";
    }
    
    void edit() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        mode = "edit";
    }
    
    void save() {
        
        if (!MsgBox.confirm("You are about to save this document. Continue?")) return;
        
        if (mode == "create") {
            entity = service.create(entity);
        } else if (mode == "edit") {
            entity = service.update(entiyt);
        }
        
        mode = "read";
        
        EventQueue.invokeLater({
             caller?.reload();
             binding?.refresh();
        });
    }
    
    
    
}

