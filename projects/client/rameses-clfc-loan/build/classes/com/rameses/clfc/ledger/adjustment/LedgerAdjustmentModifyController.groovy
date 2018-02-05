package com.rameses.clfc.ledger.adjustment

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class LedgerAdjustmentModifyController {

    @Binding
    def binding;
    
    @Caller
    def caller;
    
    @PropertyChangeListener
    def listener = [
        "entity.modify.debit.item": { o->
            entity.debit.type = null;
            if (o){
                entity.modify.debit.type = o.name;
            }
        },
        "entity.modify.credit.item": { o->
            entity.credit.type = null;
            if (o) {
                entity.modify.credit.type = o.name;
            }
        },
        "entity.modify.debit.amount": { o->
            entity.modify.credit.amount = o;
            binding?.refresh('entity.modify.credit.amount');
        }
    ]
    
    def entity, mode = 'read';
    void init() {
        if (!entity) entity = [:];
        if (!entity.modify) entity.modify = [
            debit: [item: [:]], 
            credit: [item: [:]]
        ];
    }
    
    def getTypeList() {
        return caller?.service.getTypeList();
    }
}

