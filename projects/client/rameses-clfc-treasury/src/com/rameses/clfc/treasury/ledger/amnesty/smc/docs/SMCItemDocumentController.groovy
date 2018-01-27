package com.rameses.clfc.treasury.ledger.amnesty.smc.docs

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.treasury.ledger.amnesty.smc.*;

class SMCFilingDocumentController extends ItemController
{
    @Binding
    def binding;
    
    String prefix = 'SMCD';
    
    Map createEntity() {
        def data = super.createEntity();
        data.qty = 1;
        return data;
    }
    
    def lookupDocument = Inv.lookupOpener('smc:document:lookup', [
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

