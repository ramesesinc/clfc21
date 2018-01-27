package com.rameses.clfc.treasury.ledger.amnesty.override

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class ItemController 
{
    @Service('OverrideAmnestyRequestService')
    def service;
    
    @Binding
    def binding;
    
    def entity, mode = 'read', preventity;
    def daysList, maxDay = 31;
    def handler, loanapp, borrower;
    def ledgerid;

    @PropertyChangeListener
    def listener = [
        'entity.usedate': { o->
            if (o == 1) {
                entity.day = 0;
                entity.month = 0;
            } else if (o == 0) {
                entity.date = null;
            }
            binding?.refresh();
        }
    ];
    
    def getAmnestyLookup() {
        println 'entity ' + entity;
        def handler = { o->
            //println 'selected amnesty ' + o;
            def data = service.getAmnestyInfo(o);
            if (data) {
                entity.amnesty = data;
                entity.ledger = data.ledger;
                binding?.refresh();
            }

        }
        def op = Inv.lookupOpener('override:amnesty:request:amnesty:lookup', [onselect: handler, ledgerid: entity?.ledgerid]);
        if (!op) return null;
        
        return op;
    }
    
    void create() {
        entity = [
            objid   : 'OAR' + new UID(), 
            txnstate: 'DRAFT',
            usedate : 0, 
            month   : 0, 
            day     : 0,
            ledgerid: ledgerid
        ];
        if (loanapp) {
            entity.loanapp = [:];
            entity.loanapp.putAll(loanapp);
        }
        if (borrower) {
            entity.borrower = [:];
            entity.borrower.putAll(borrower);
        }
        mode = 'create';
        resetDaysList();
        binding?.refresh();
    }
    
    void resetDaysList() {
        daysList = [];
        for (int i=0; i <= maxDay; i++) { daysList << i; }
        binding?.refresh();
    }
    
    def copy( src ) {
        def data = [:];
        data.putAll(src);
        return data;
    }
    
    void open() {
        //entity = service.open(entity);
        preventity = copy(entity);
        entity = preventity;
        resetDaysList();
        binding?.refresh();
        //println 'mode-> ' + mode;
    }
    
    def cancel() {
        if (mode=='edit') {
            /*
            println 'preventity ' + preventity;
            if (preventity) {
                entity.putAll(preventity);
            }
            */
        }
        return '_close';
    }
    
    def save() {
        if (handler) handler(entity);
        return '_close';
    }
    
    def close() {
        return '_close';
    }
    
    void submitForApproval() {
        if (!MsgBox.confirm('You are about to submit this document for approval. Continue?')) return;
        
        def data = service.submitForApproval(entity);
        if (data) {
            entity.putAll(data);
        }
    }
    
    /*
    def doOk() {
        if (handler) handler(entity);
        return '_close';
    }
    
    def doCancel() {
        return '_close';
    }
    */
}

