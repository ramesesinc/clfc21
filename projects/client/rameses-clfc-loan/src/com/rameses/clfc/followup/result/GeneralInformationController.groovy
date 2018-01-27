package com.rameses.clfc.followup.result

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class GeneralInformationController 
{
    @Binding
    def binding;
    
    @Service('LoanLedgerFollowupResultService')
    def service;
        
    def entity, mode = 'read';
    
    @PropertyChangeListener
    def listener = [
        'entity.txndate': { o->
            /*
            entity?.remove('draft');
            entity?.remove('borrower');
            entity?.remove('loanapp');
            entity?.remove('ledgerid');
            entity?.remove('amnestyoption');
            entity?.remove('availedamnesty');
            entity?.remove('rejectedamnesty');
            if (!entity?._removerequest) entity?._removerequest = [];
            def rm = entity?.remove('overrideamnesty');
            if (rm) entity?._removerequest.addAll(rm);
            //entity?.remove('_removerequest');
            binding?.refresh();
            */
        }
    ]
    
    void init() {}
    
    def getBorrowerLookup() {
        def handler = { o->
            if (entity.borrower) {
                if (!MsgBox.confirm('Changing of borrower will remove all information related to the previous borrower. Continue?')) return;
            }
            
            def data = service.getLedgerInfo(o);
            if (data) {
                entity.putAll(data);
            }
            entity.remove('amnestyoption');
            binding?.refresh();
        }
        
        def params = [
            onselect: handler,
            date    : entity?.txndate
        ]
        def op = Inv.lookupOpener('followup-borrower:lookup', params);
        if (!op) return null;
        return op;
    }
}

