package com.rameses.clfc.treasury.otherreceipt

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class OtherReceiptPaymentController {
    
    @Binding
    def binding;
    
    @Service('DateService')
    def dateSvc;
    
    @PropertyChangeListener
    def listener = [
        'payoption': { o->
            entity.payoption = o.reftype;
            /*
            if (o == 'cash') {
                entity.check = [:];
                entity.bank = [:];
            }
            binding?.refresh('entity.(check|bank).*');
            */
        }
    ];
    
    def entity, handler, payoption;
    //def optionList = ['cash', 'check'];
    def getOptionList() {
        def ops = Inv.lookupOpeners('plugin:otherreceipt');
        //println 'ops ' + ops;
        def props, list = [];
        ops?.each{ o->
            props = o.properties;
            list << [reftype: props.reftype, caption: o.caption, idx: props.idx]
        }
        list.sort{ it.idx }
        
        //pritln 'list ' + list;
        return list;
    }
    
    void init() {
        entity = [
            objid       : 'ORD' + new UID(), 
            txndate     : dateSvc.getServerDateAsString().split(' ')[0]
        ];
    }
    
    def getOpener() {
        if (!payoption) return null;
        
        def invtype = "otherreceipt:" + payoption.reftype;
        def op = Inv.lookupOpener(invtype, [entity: entity]);
        if (!op) return null;
        return op;
    }
    
    def doCancel() {
        return '_close';
    }
    
    def doOk() {
        if (handler) handler(entity);
        return '_close';
    }
}

