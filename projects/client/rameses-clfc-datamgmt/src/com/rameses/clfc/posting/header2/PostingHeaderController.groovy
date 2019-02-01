package com.rameses.clfc.posting.header2

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class PostingHeaderController extends CRUDController {
	
    @Binding
    def binding;
    
    String serviceName = "NewPostingHeaderService";
    String entityName = "posting:header";
    
    Map createPermission = [role: 'ADMIN_SUPPORT', domain: 'ADMIN'];
    Map editPermission = [role: 'ADMIN_SUPPORT', domain: 'ADMIN']; 
    
    boolean allowApprove = false;
    boolean allowDelete = false;
    
    Map createEntity() {
        return [objid: "PH" + new UID(), seqno: 0];
    } 
    
    def getCategoryList() {
        return service.getCategories().findAll{ it.category != null }.collect{ it.category }
    }
    
    def getTypeList() {
        return service.getTypes();
    }
    
    def getDataTypeList() {
        return service.getDataTypes();
    }
    
}

