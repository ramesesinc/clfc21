package com.rameses.clfc.ledger.specialcollection;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
import java.rmi.server.UID;

class SpecialCollectionController extends AbstractSpecialCollectionController
{    
    String serviceName = 'LoanSpecialCollectionService';
    
    Map createEntity() {
        def map = [
            objid       : 'SC' + new UID(),
            state       : 'DRAFT',
            txntype     : 'ONLINE',
            billingid   : 'LB' + new UID(),
            txndate     : dateSvc.getServerDateAsString().split(" ")[0]
        ];
        showconfirmation = true;
        return map;
    }
}