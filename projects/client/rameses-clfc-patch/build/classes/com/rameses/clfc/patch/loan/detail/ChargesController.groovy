package com.rameses.clfc.patch.loan.detail

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class ChargesController {

    @Binding
    def binding;
    
    def entity;
    def selectedCharge;
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.charges) entity.charges = [];
        listHandler?.reload();
    }
    
    def listHandler = [
        fetchList: { o->
            if (!entity.charges) entity.charges = [];
            return entity.charges;
        }
    ] as BasicListModel;
    
    def addCharge() {
        def handler = { o->
            def i = entity.charges?.find{ it.acctid==o.acctid }
            if (i) throw new RuntimeException("Charge has already been added.");
            
            entity.charges << o;
            listHandler?.reload();
            binding?.refresh("total");
        }
        def op = Inv.lookupOpener("change:loanapp:detail:chargeitem:create", [entity: [:], handler: handler]);
        if (!op) return null;
        return op;
    }
    
    void removeCharge() {
        if (!selectedCharge) return;
        
        if (!MsgBox.confirm("You are about to remove this item. Continue?")) return;
        
        entity.charges?.remove(selectedCharge);
        listHandler?.reload();
        binding?.refresh("total");
    }
    
    def getTotal() {
        if (!entity.charges) return 0;
        
        def ttl = entity.charges?.amount.sum();
        if (!ttl) ttl = 0;
        return ttl;
    }
}

