package com.rameses.clfc.treasury.ledger.amnesty.override

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class OverrideAmnestyRequestController extends CRUDController
{
    @Binding
    def binding;
    
    String serviceName = 'OverrideAmnestyRequestService';
    String entityName = 'override:amnesty:request';
    
    boolean allowApprove = false;
    boolean allowDelete = false;
    boolean allowEdit = false;
    boolean allowCreate = false;
    
    def daysList, maxDay = 31;
        
    @PropertyChangeListener
    def listener = [
        'entity.usedate': { o->
            if (o == 1) {
                entity.day = 0;
                entity.month = 0;
            } else if (o == 0) {
                entity.date = null;
            }
            binding?.refresh();
        }
    ];
    
    def amnestyLookup = Inv.lookupOpener('override:amnesty:request:amnesty:lookup', [
        onselect: { o->
            println 'selected amnesty ' + o;
        }
    ]);

    Map createEntity() {
        return [
            objid   : 'OAR' + new UID(), 
            txnstate: 'DRAFT',
            usedate : 0, 
            month   : 0, 
            day     : 0
        ];
    }
    
    void afterCreate( data ) {
        resetDaysList();
        checkEditable(data);
    }
    
    void checkEditable( data ) {
        /*
        allowEdit = false;
        if (data.txnstate=='DRAFT') {
            allowEdit = true;
        }
        */
        binding?.refresh();
    }
    
    void resetDaysList() {
        daysList = [];
        for (int i=0; i <= maxDay; i++) { daysList << i; }
        binding?.refresh();
    }
    
    void afterOpen( data ) {
        resetDaysList();
        checkEditable(data);
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
}

