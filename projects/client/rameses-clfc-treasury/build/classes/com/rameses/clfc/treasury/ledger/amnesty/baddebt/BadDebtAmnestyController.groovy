package com.rameses.clfc.treasury.ledger.amnesty.baddebt

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class BadDebtAmnestyController extends CRUDController
{
    @Binding
    def binding;
    
    String serviceName = 'LedgerAmnestyBadDebtService';
    
    boolean allowDelete = false;
    boolean allowApprove = false;
    boolean allowEdit = true;
    
    def preventity, loanapp;
    def ledgerLookup = Inv.lookupOpener('amnestyledger:lookup', [
        onselect: { o->
            def item =  service.getLedgerInfo([ledgerid: o.objid]);
            if (item) {
                entity.putAll(item);
                entity.amount = entity.ledger.balance;
            }
            binding?.refresh();
        }
    ]);

    Map createEntity() {
        return [
            objid   : 'ABD' + new UID(),
            txnstate: 'DRAFT',
            txnmode : 'ONLINE'
        ];
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

