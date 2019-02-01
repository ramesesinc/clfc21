package com.rameses.clfc.ledger.proceeds

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class ProceedController {
	
    @Binding
    def binding;
    
    @Service("LoanLedgerProceedsService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    String title = "Collect Proceed";
    
    def ledgerid, entity, data;
    
    void init() {
        data = service.getInfo( [ledgerid: ledgerid] );
        entity = [
            objid   : 'LPROC' + new UID(),
            parentid: ledgerid,
            txnstate: 'DRAFT', 
            txntype : 'ONLINE',
            txndate : dateSvc.getServerDateAsString().split(" ")[0],
            borrower: data.borrower
        ];
    }
    
    def cancel() {
        return "_close";
    }
    
    def save() {
        if (!MsgBox.confirm("The proceed collected is ${entity.amount}. Continue?}")) return;
        service.saveCollectedProceed( entity );
        MsgBox.alert("Proceed successfully collected.");
        return "_close";
    }
}

