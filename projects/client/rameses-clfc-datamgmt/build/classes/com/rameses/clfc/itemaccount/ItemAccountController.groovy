package com.rameses.clfc.itemaccount

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class ItemAccountController extends CRUDController {

    @Binding
    def binding;
    
    @Caller
    def caller;
    
    String serviceName = "LoanItemAccountService";
    String entityName = "loana:itemaccount";
    
    boolean allowApprove = false;
    boolean allowDelete = false;
    boolean allowEdit = true;
    
    Map createEntity() {
        return [objid: "ITMACCT" + new UID(), txnstate: "DRAFT"];
    }
    
    def getTypeList() {
        return service.getTypeList();
    }
    
    void afterCreate( data ) {
        checkEditable(data);
    }
    
    void afterOpen( data ) {
        checkEditable(data);
    }
    
    void checkEditable( data ) {
        allowEdit = false;
        if (data.txnstate?.matches("DRAFT|DEACTIVATED")) {
            allowEdit = true;
        }
        binding?.refresh("formActions");
    }
    
    void afterSave( data ) {
        EventQueue.invokeLater({ caller?.reload(); });
    }
        
    void activateDocument() {
        entity = service.activateDocument(entity);
        checkEditable(entity);
        EventQueue.invokeLater({ caller?.reload(); });
    }
    
    void deactivateDocument() {
        entity = service.deactivateDocument(entity);
        checkEditable(entity);
        EventQueue.invokeLater({ caller?.reload(); });
    }
    
}

