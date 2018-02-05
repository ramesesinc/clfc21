package com.rameses.clfc.loan.capture

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class AssessmentOtherChargeController {
	
    def handler, entity;
    def lookupItemAccount = Inv.lookupOpener("loan:itemaccount:lookup", [
        onselect: { o->
            entity.acctid = o.objid;
            entity.title = o.title;
        }
    ]);
    
    
    void init() {
        entity = [objid: "LCHRG" + new UID()];
    } 
    
    def doOk() {
        if (handler) handler(entity);
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
}

