package com.rameses.clfc.ledger.specialcollection;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
import java.rmi.server.UID;

class SpecialCollectionPopupController extends AbstractSpecialCollectionController
{
    String serviceName = 'LoanSpecialCollectionService';
    def allowcreate = true, billing;
    
    Map createEntity() {
        def map = [
            objid       : 'SC' + new UID(),
            state       : 'DRAFT',
            txntype     : 'ONLINE',
            billingid   : billing?.objid, 
            txndate     : dateSvc.getServerDateAsString().split(" ")[0],
            collector   : billing?.collector
        ];
        showconfirmation = true;
        return map;
    }
    
    void open() {
        super.open();
        billing = [objid: entity?.billingid, collector: entity?.collector];
    }
    
    void init() {
        if (entity) {
            billing = [:];
            billing.putAll(entity);
        }
        create();
    }
}