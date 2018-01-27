package com.rameses.clfc.audit.amnesty.capture.waiver

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class WaiverAmnestyController extends CRUDController
{
    @Binding
    def binding;
    
    @PropertyChangeListener
    def listener = [
        'entity.waiveinterest': { o->
            switch (o) {
                case 0  : entity.overrideinterest = 1; break;
                case 1  : entity.overrideinterest = 0; break;
            }
            entity.interest = 0;
            binding?.refresh('entity.(overrideinterest|interest)');
        },
        'entity.overrideinterest': { o->
            switch (o) {
                case 0  : entity.waiveinterest = 1;
                          entity.interest = 0;
                          break;
                case 1  : entity.waiveinterest = 0;
                          break;
            }
            binding?.refresh('entity.(waiveinterest|interest)');
        },
        'entity.waivepenalty': { o->
            switch (o) {
                case 1  : entity.overridepenalty = 0; break;
            }
            entity.penalty = 0;
            binding?.refresh('entity.(overridepenalty|penalty)');
        },
        'entity.overridepenalty': { o->
            switch (o) {
                case 1  : entity.waivepenalty = 0;
                          break;
            }
            entity.penalty = 0;
            binding?.refresh('entity.(waivepenalty|penalty)');
        }
    ]
    
    String serviceName = 'LedgerAmnestyCaptureWaiverService'
    Map createPermission = [domain: 'AUDIT', role: 'AUDIT_AMNESTY'];
    Map editPermission = [domain: 'AUDIT', role: 'AUDIT_AMNESTY'];
    
    boolean allowApprove = false;
    boolean allowDelete = false;
    boolean allowEdit = true;
    
    def loanapp;
    def ledgerLookup = Inv.lookupOpener('amnestycapture:ledger:lookup', [
        onselect: { o->
            def item =  service.getLedgerInfo([ledgerid: o.objid]);
            if (item) {
                entity.putAll(item);
            }
            binding?.refresh();
        }
    ]);
    
    Map createEntity() {
        return [
            objid           : 'LACW' + new UID(),
            txnmode         : 'CAPTURE',
            txnstate        : 'DRAFT',
            waiveinterest   : 1,
            overrideinterest: 0,
            interest        : 0,
            waivepenalty    : 0,
            overridepenalty : 0,
            penalty         : 0
        ];
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