package com.rameses.clfc.patch.ledger.payment

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class PaymentInfoController 
{
    def entity, handler;
    void init() {
        entity = [objid: 'LLP' + new UID()];
    }
    
    def doOk() {
        if (handler) handler(entity);
        return '_close';
    }
    
    def doCancel() {
        return '_close';
    }
}

