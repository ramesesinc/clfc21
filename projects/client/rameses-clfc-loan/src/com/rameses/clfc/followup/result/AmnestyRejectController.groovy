package com.rameses.clfc.followup.result

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class AmnestyRejectController 
{
    def entity, mode = 'read';
    def rejectedamnesty, selectedAmnesty;
    
    void init() {
        if (!entity?.rejectedamnesty) entity.rejectedamnesty = [:]
        rejectedamnesty = entity?.rejectedamnesty;
    }
    
    def listHandler = [
        fetchList: { o->
            if (!rejectedamnesty.items) rejectedamnesty.items = [];
            return rejectedamnesty.items;
        }
    ] as BasicListModel;
    
    def addAmnesty() {
        def params = [
            onselect    : { o->
                def i = rejectedamnesty?.items?.find{ it.objid == o.objid }
                if (i) throw new Exception('This amnesty has already been selected.');

                if (!rejectedamnesty.items) rejectedamnesty.items = [];
                def item = [
                    objid   : o.objid,
                    txndate : o.txndate,
                    refno   : o.refno
                ];
                rejectedamnesty.items << item;

                listHandler?.reload();
            },
            loanappid   : entity?.loanapp?.objid,
            txndate     : entity?.txndate
        ]
        
        def op = Inv.lookupOpener('followup:amnesty:reject:lookup', params);
        if (!op) return null;
        
        return op;
    }
    
    void removeAmnesty() {
        if (!MsgBox.confirm('You are about to remove this item. Continue?')) return;
        
        rejectedamnesty.items.remove(selectedAmnesty);
        listHandler?.reload();
    }
}

