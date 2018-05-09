package com.rameses.clfc.ledger.noncash

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class NoncashController {

    @Binding
    def binding;
    
    @Service("LoanLedgerNoncashService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    String title = "Collect Non-cash";
    
    def ledgerid, entity, data;
    
    void init() {
        data = service.getInfo( [ledgerid: ledgerid] );
        entity = [
            objid   : 'LNC' + new UID(),
            parentid: ledgerid,
            txnstate: 'DRAFT', 
            txntype : 'ONLINE',
            txndate : dateSvc.getServerDateAsString().split(" ")[0],
            borrower: data.borrower,
            appno   : data.loanapp.appno
        ];
    }
    
    def cancel() {
        return "_close";
    }
    
    def save() {
        if (!MsgBox.confirm("The non-cash collected is ${entity.amount}. Continue?")) return;
        service.saveCollectedNoncash( entity );
        MsgBox.alert("Non-cash successfully collected.");
        return "_close";
    }
}

