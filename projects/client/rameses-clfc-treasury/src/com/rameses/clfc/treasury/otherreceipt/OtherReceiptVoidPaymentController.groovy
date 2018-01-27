package com.rameses.clfc.treasury.otherreceipt

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class OtherReceiptVoidPaymentController {
    
    @Caller
    def caller;
    
    @Service('OtherReceiptVoidPaymentService')
    def service;
    
    @Service('DateService')
    def dateSvc;
    
    def entity, mode = 'read', payment;
    
    Map createEntity() {
        return [
            objid           : 'ORVR' + new UID(),
            refid           : payment?.objid,
            otherreceiptid  : payment?.parentid,,
            payment         : payment,
            txndate         : dateSvc.getServerDateAsString().split(' ')[0],
            txnstate        : 'DRAFT'
        ];
    }
    
    void create() {
        entity = createEntity();
        mode = 'create';
    }
    
    void open() {
        entity = service.open(entity);
        mode = 'read';
    }
    
    def close() {
        return '_close';
    }
    
    def cancel() {
        return close();
    }
    
    void save() {
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        entity = service.save(entity);
        mode = 'read';
        EventQueue.invokeLater({
            caller?.reloadPayments();
        });
    }
    
    void approveDocument() {
        if (!MsgBox.confirm('You are about to approve this document. Continue?')) return;
        
        entity = service.approveDocument(entity);
        mode = 'read';
        EventQueue.invokeLater({
            caller?.reloadPayments();
        });
    }
    
    void disapprove() {
        if (!MsgBox.confirm('You are about to disapprove this document. Continue?')) return;
        
        entity = service.disapprove(entity);
        mode = 'read';
        EventQueue.invokeLater({
            caller?.reloadPayments();
        });
    }
}

