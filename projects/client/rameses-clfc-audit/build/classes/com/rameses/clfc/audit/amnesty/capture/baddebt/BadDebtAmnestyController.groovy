package com.rameses.clfc.audit.amnesty.capture.baddebt

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class BadDebtAmnestyController extends CRUDController
{
    @Binding
    def binding;
    
    @Service('DateService')
    def dateSvc;
    
    @PropertyChangeListener
    def listener = [
        'entity.dtstarted': { o->
            entity.dtended = o;
            entity.payment?.txndate = o;
            binding?.refresh();
        }
    ];
    
    String serviceName = 'LedgerAmnestyCaptureBadDebtService';
    Map createPermission = [domain: 'AUDIT', role: 'AUDIT_AMNESTY'];
    Map editPermission = [domain: 'AUDIT', role: 'AUDIT_AMNESTY'];
    
    boolean allowDelete = false;
    boolean allowApprove = false;
    boolean allowEdit = true;
    
    def preventity, loanapp;
    def ledgerLookup = Inv.lookupOpener('amnestycapture:ledger:lookup', [
        onselect: { o->
            def item =  service.getLedgerInfo([ledgerid: o.objid]);
            if (item) {
                entity.putAll(item);
                entity.amount = entity.ledger.balance;
                entity.payment?.amount = entity.amount;
            }
            binding?.refresh();
        }
    ]);

    Map createEntity() {
        def data = [
            objid       : 'ABDC' + new UID(),
            txnstate    : 'DRAFT',
            txnmode     : 'CAPTURE',
            dtstarted   : dateSvc.getServerDateAsString()
        ];
        data.payment = [objid: 'LLP' + new UID(), txndate: data.dtstarted];
        
        return data;
    }
    
    void afterCreate( data ) {
        checkEditable(data);
    }
    
    void afterOpen( data ) {
        checkEditable(data);
    }
    
    void checkEditable( data ) {
        allowEdit = false;
        if (data.txnstate == 'DRAFT') {
            allowEdit = true;
        }
        binding?.refresh('formActions');
    }
    
    void afterEdit( data ) {
        preventity = [:];
        preventity.putAll(data);
    }
    
    void afterCancel() {
        if (preventity) {
            entity = preventity;
        }
        binding?.refresh();
    }

    void submitForApproval() {
        if (!MsgBox.confirm('You are about to submit this document for approval. Continue?')) return;
        
        entity = service.submitForApproval(entity);
        checkEditable(entity);
    }
    
    void approveDocument() {
        if (!MsgBox.confirm('You are about to approve this document. Continue?')) return;
        
        entity = service.approveDocument(entity);
        checkEditable(entity);
    }
    
    void disapprove() {
        if (!MsgBox.confirm('You are about to disapprove this document. Continue?')) return;
        
        entity = service.disapprove(entity);
        checkEditable(entity);
    }
    
    def getStatus() {
        def str = '';
        if (entity?.txnmode) str += entity?.txnmode;
        if (str) str += ' - ';
        if (entity?.txnstate) str += entity?.txnstate;
        return str;
    }
}