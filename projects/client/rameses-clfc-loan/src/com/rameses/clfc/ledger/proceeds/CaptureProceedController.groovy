package com.rameses.clfc.ledger.proceeds

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class CaptureProceedController {

    @Binding
    def binding;
    
    @Service("CaptureLoanLedgerProceedsService")
    def service;
    
    String title = "Capture Proceed";
    
    def ledgerid, entity, data;
    
    void init() {
        data = service.getInfo( [ledgerid: ledgerid] );
        entity = [
            objid   : 'CLPROC' + new UID(),
            parentid: ledgerid,
            txnstate: 'FOR_SELLING',
            txntype : 'CAPTURE',
            borrower: data.borrower
        ];
    }
    
    def cancel() {
        return "_close";
    }
    
    def save() {
        if (!MsgBox.confirm("The proceed captured is ${entity.amount}. Continue?}")) return;
        service.saveCapturedProceed( entity );
        MsgBox.alert("Proceed successfully captured.");
        return "_close";
    }
}

