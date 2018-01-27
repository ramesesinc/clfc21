package com.rameses.clfc.patch.ledger.edit.posting.waiver

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class PostingDetailController 
{
    def entity, handler, payment;
    
    void create() {
        entity = [
            objid   : 'SD' + new UID(), 
            refno   : payment?.refno, 
            dtpaid  : payment?.txndate
        ];
    }
    
    void edit() {
        def xentity = [:];
        xentity.putAll(entity);
        entity = xentity;
    }
    
    def doOk() {
        if (handler) handler(entity);
        return '_close';
    }
    
    def doCancel() {
        return '_close';
    }
}

