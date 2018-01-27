package com.rameses.clfc.treasury.ledger.amnesty.smc.fee

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.treasury.ledger.amnesty.smc.*;

class SMCItemFeeController extends ItemController
{
    @Binding
    def binding;
    
    String prefix = 'SMCF';
    
    Map createEntity() {
        def data = super.createEntity();
        data.amount = 0;
        return data;
    }
    
    def lookupDocument = Inv.lookupOpener('smc:fee:lookup', [
    onselect: { o->
        entity.putAll(o);
        binding?.refresh();
    }]);
    
    def listHandler = [
        getList: {
            if (!entity.items) entity.items = [];
            return entity.items;
        }
    ] as BasicListModel;	
}

