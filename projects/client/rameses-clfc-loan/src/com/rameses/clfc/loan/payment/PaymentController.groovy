package com.rameses.clfc.loan.payment.bak;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.framework.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class PaymentController {
    
    @Binding
    def binding;
    
    @Service("LoanOnlineCollectionService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    @Service("BankService")
    def bankSvc;
    
    String title = "Collect Payment";
    
    def ledgerid, entity, data;
    
    @PropertyChangeListener
    def listener = [
        "entity.payoption": { o->
            if (o != 'check') {
                entity.bank = [:];
                entity.check = [:];
                binding?.refresh();
            }
        }
    ]
    
    void init() {
        data = service.getBillingInfo( [ledgerid: ledgerid] );
        entity = [
            objid       : 'OCD' + new UID(),
            parentid    : 'OC' + new UID(),
            txndate     : dateSvc.getServerDateAsString().split(" ")[0],
            payoption   : 'cash',
            bank        : [:],
            check       : [:]
        ];
    }
    
    def getOptionList() {
        return service.getPaymentOptions();
    }
    
    def getBankList() {
        return bankSvc.getList([state: 'ACTIVE']);
    }
    
    def cancel() {
        return "_close";
    }
    
    def save() {
        if (!MsgBox.confirm("The amount paid is ${entity.amount}. Continue?")) return;
        service.create( [ledgerid: ledgerid], entity );
        MsgBox.alert("Payment successfully saved!");
        return "_close";
    }
    
    
}