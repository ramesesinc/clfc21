package com.rameses.clfc.ledger.noncash

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class CaptureNoncashController {
	
    @Binding
    def binding;
    
    @Service("CaptureLoanLedgerNoncashService")
    def service;
    
    String title = "Capture Non-cash";
    
    def ledgerid, entity, data;
    
    void init() {
        data = service.getInfo( [ledgerid: ledgerid] );
        entity = [
            objid   : 'CLNC' + new UID(),
            parentid: ledgerid,
            txnstate: 'DRAFT',
            txntype : 'CAPTURE',
            borrower: data.borrower,
            appno   : data.loanapp.appno
        ]
    }
    
    def cancel() {
        return "_close";
    }
    
    def save() {
        if (!MsgBox.confirm("The non-cash captured is ${entity.amount}. Continue?")) return;
        service.saveCapturedNoncash( entity );
        MsgBox.alert("Non-cash successfully captured.");
        return "_close";
    }
}

