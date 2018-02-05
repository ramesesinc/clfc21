package com.rameses.clfc.ledger.adjustment

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class LedgerAdjustmentGeneralInfoController {

    @Binding
    def binding;
    
    @Caller
    def caller;
    
    @PropertyChangeListener
    def listener = [
        "entity.debit.item": { o->
            entity.debit.type = null;
            if (o){
                entity.debit.type = o.name;
            }
        },
        "entity.credit.item": { o->
            entity.credit.type = null;
            if (o) {
                entity.credit.type = o.name;
            }
        },
        "entity.debit.amount": { o->
            entity.credit.amount = o;
            binding?.refresh('entity.credit.amount');
        }
    ]
    
    def entity, mode = 'read';
    void init() {
        if (!entity) entity = [:];
        if (!entity.debit) entity.debit = [item: [:]];
        if (!entity.credit) entity.credit = [item: [:]];
    }
    
    def getTypeList() {
        return caller?.service.getTypeList();
    }
}

