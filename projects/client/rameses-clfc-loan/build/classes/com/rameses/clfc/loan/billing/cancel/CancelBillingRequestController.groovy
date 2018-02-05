package com.rameses.clfc.loan.billing.cancel

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class CancelBillingRequestController extends CRUDController {

    @Binding
    def binding;
    
    @Caller
    def caller;
    
    String serviceName = "LoanCancelBillingRequestService";
    
    boolean allowApprove = false;
    boolean allowDelete = false;
    boolean allowCreate = false;
    boolean allowEdit = false;
    
    void approveDocument() {
        entity = service.approveDocument(entity);
        EventQueue.invokeLater({
             binding?.refresh();
             caller?.reload();
        });
    }
    
    void disapproveDocument() {
        entity = service.disapproveDocument(entity);
        EventQueue.invokeLater({
             binding?.refresh();
             caller?.reload();
        });
    }
}

