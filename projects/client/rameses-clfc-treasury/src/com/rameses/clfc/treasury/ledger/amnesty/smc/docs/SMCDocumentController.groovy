package com.rameses.clfc.treasury.ledger.amnesty.smc.docs

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class SMCDocumentController extends CRUDController 
{
    @Binding
    def binding;
    
    String serviceName = 'SMCDocumentService';
    
    boolean allowApprove = false;
    boolean allowDelete = false;
    boolean allowEdit = true;
    
    Map createPermission = [role: 'LEGAL_OFFICER', domain: 'LOAN'];
    Map editPermission = [role: 'LEGAL_OFFICER', domain: 'LOAN'];
    
    Map createEntity() {
        return [objid: 'SMCD' + new UID(), type: 'USER', _allowedit: true];
    }
    
    void afterOpen( data ) {
        if (!data._allowedit) data._allowedit = false;
        allowEdit = data._allowedit;
        binding?.refresh('formActions');
    }
}

