package com.rameses.clfc.treasury.otherreceipt

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class OtherReceiptPaymentController {
    
    @Binding
    def binding;
    
    @Service('BankService')
    def bankSvc;
    
    @Service('DateService')
    def dateSvc;
    
    @PropertyChangeListener
    def listener = [
        'entity.payoption': { o->
            if (o == 'cash') {
                entity.check = [:];
                entity.bank = [:];
            }
            binding?.refresh('entity.(check|bank).*');
        }
    ];
    
    def entity, handler;
    def optionList = ['cash', 'check'];
    
    void init() {
        entity = [
            objid       : 'ORD' + new UID(), 
            txndate     : dateSvc.getServerDateAsString().split(' ')[0],
            check       : [:],
            bank        : [:],
            onlindeposit: 0
        ];
    }
    
    def getBankList() {
        return bankSvc.getList([state: 'ACTIVE']);
    }
    
    def doCancel() {
        return '_close';
    }
    
    def doOk() {
        if (handler) handler(entity);
        return '_close';
    }
}

