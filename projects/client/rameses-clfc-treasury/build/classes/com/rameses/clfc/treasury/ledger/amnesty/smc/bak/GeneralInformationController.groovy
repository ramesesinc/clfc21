package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class AmnestySMCController
{
    @Binding
    def binding;
    
    @Service('LedgerAmnestySMCService')
    def service;
    
    def entity, mode = 'read';
    
    void init() {}
    
    def borrowerLookup = Inv.lookupOpener('amnestyledger:lookup', [
        onselect: { o->
            entity.ledgerid = o.objid;
            def item = service.getLedgerInfo([ledgerid: o.objid]);
            if (item) {
                if (!item.borrower?.address) item.borrower.address = o.borrower.address;
                entity.putAll(item);
            }
            binding?.refresh('entity.ledger.*');
        }
    ]);
    
}

