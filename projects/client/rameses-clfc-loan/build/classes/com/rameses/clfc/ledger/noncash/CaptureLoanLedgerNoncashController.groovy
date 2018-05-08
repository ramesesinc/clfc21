package com.rameses.clfc.ledger.noncash

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class CaptureLoanLedgerNoncashController extends CRUDController
{
    @Binding
    def binding;
    
    String serviceName = "CaptureLoanLedgerNoncashService";
    String entityName = 'captureledgernoncash';
    
    boolean allowDelete = false;
    boolean allowApprove = false;
    boolean allowEdit = true;
    
    Map createPermission = [domain: 'LOAN', role: 'CAO_OFFICER,CASHIER'];
    Map editPermission = [domain: 'LOAN', role: 'CAO_OFFICER,CASHIER'];
    
    def borrowerLookupHandler = Inv.lookupOpener('ledgerborrower:lookup', [
        onselect: { o->
            //entity.ledger = o;
            entity.borrower = o.borrower;
            entity.parentid = o.objid;
            entity.appno = o.loanapp.appno;
        }
    ]);

    Map createEntity() {
        allowEdit = true;
        return [
            objid   : 'CLNC' + new UID(),
            txnstate: 'DRAFT',
            txntype : 'CAPTURE'
        ];
    }
    
    void afterOpen( data ) {
        checkEditable(data);
    }
    
    private void checkEditable( data ) {
        allowEdit = false;
        if (data.txnstate == 'DRAFT') {
            allowEdit = true;
        }
        binding?.refresh('formActions');
    }
    
    void collect() {
        if (!MsgBox.confirm("You are about to collect this non-cash. Continue?")) return;
        
        entity = service.collect(entity);
        checkEditable(entity);
    }
    
    def getStatus() {
        def str = (entity.txntype? entity.txntype : '') + (entity.txnstate? ' - ' + entity.txnstate : '');
        
        return str;
    }
    
}

