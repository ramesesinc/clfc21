package com.rameses.clfc.treasury.ledger.amnesty

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class ItemController 
{
    @Binding
    def binding;
    
    @Service('LedgerAmnestyService')
    def service;
    
    def entity, type, mode = 'read';
    def handler, objid, preventity;
    def daysList, maxDay = 31;
    def approveHandler;
    
    @PropertyChangeListener
    def listener = [
        'entity.withmd': { o->
            switch (o) {
                case 0: entity.usedate = 0;
                        entity.date = null;
                        entity.day = 0;
                        entity.month = 0;
                        break;
            }
            binding?.refresh();
        },
        'entity.usedate': { o->
            switch (o) {
                case 0: entity.date = null;
                        break;
                case 1: entity.day = 0;
                        entity.month = 0;
                        break;
            }
            /*
            if (o == 1) {
                entity.day = 0;
                entity.month = 0;
            } else if (o == 0) {
                entity.date = null;
            }
            */
            binding?.refresh();
        }
    ];
    
    void init() {
        objid = 'AI' + new UID();
        entity = [objid: objid, txnstate: 'DRAFT', withmd: 0, usedate: 0, month: 0, day: 0];
        mode = 'create';
        resetDaysList();
    }
    
    void resetDaysList() {
        daysList = [];
        for (int i=0; i <= maxDay; i++) { daysList << i; }
        binding?.refresh();
    }
    
    def copy( src ) {
        def dest = [:];
        dest.putAll(src);
        return dest;
    }
    
    void open() {
        if (entity) {
            preventity = copy(entity);
            entity = preventity;
            //entity = copy(preventity);
            objid = entity?.objid;
        }
        resetDaysList();
    }
    
    def cancel() {
        return '_close';
    }
    
    def close() {
        return '_close';
    }
    
    def ok() {
        if (!entity.txnstate) entity.txnstate = 'DRAFT';
        if (handler) handler(entity);
        return '_close';
    }
    
    void approve() {
        if (!MsgBox.confirm('You are about to approve this recommendation. Cotinue?')) return;
        
        def xent = [objid: entity?.objid];
        xent = service.approveItem(xent);
        if (xent) {
            entity.putAll(xent);
        }
        if (approveHandler) approveHandler(entity);
        binding?.refresh('formActions');
    }
    
    void disapprove() {
        if (!MsgBox.confirm('You are about to disapprove this recommendation. Continue?')) return;
        
        def xent = [objid: entity?.objid];
        xent = service.disapproveItem(xent);
        if (xent) {
            entity.putAll(xent);
        }
        if (approveHandler) approveHandler(entity);
        binding?.refresh('formActions');
    }
}

