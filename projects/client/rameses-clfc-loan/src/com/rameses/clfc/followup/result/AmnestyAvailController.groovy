package com.rameses.clfc.followup.result

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class AmnestyAvailController 
{
    @Binding
    def binding;
    
    @Service('LoanLedgerFollowupResultService')
    def service;
    
    def entity, mode = 'read';
    def availedamnesty;
    
    void init() {
        availedamnesty = entity.availedamnesty;
        if (!availedamnesty) availedamnesty = [:];
    }
    
    def getLookupAmnesty() {
        def params = [
            onselect    : { o->
                def params = [objid: o.objid, txndate: entity?.txndate];
                def item = service.getAmnestyRecommendationInfo(params);
                if (item) {
                    entity.availedamnesty = item;
                    availedamnesty = item;
                    binding?.refresh();
                }
            },
            loanappid   : entity?.loanapp?.objid,
            txndate     : entity?.txndate
        ]
        def op = Inv.lookupOpener('followup:amnesty:lookup', params);
        if (!op) return null;
        
         return op;
    }
    
    
    void removeAmnesty() {
        entity.availedamnesty = null;
        availedamnesty = null;
        binding?.refresh();
    }
}

