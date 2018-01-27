package com.rameses.clfc.ledger.followupcollection;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
import java.rmi.server.UID;

class FollowupCollectionController extends AbstractFollowupCollectionController
{
    String serviceName = 'LoanFollowupCollectionService';
    
    Map createEntity() {
        def map = [
            objid       : 'FC' + new UID(),
            state       : 'DRAFT',
            billingid   : 'LB' + new UID(),
            txntype     : 'ONLINE',
            txndate     : dateSvc.getServerDateAsString().split(" ")[0],
            //itemtype    : 'followup'
        ];
        showconfirmation = true;
        return map;
    }
}