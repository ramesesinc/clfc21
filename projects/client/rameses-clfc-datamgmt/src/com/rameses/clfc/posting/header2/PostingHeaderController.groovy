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
        return [objid: "PH" + new UID(), seqno: 0, fieldstoupdate: []];
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
    
    def selectedItem;
    def listHandler = [
        fetchList: { o->
            if (!entity.fieldstoupdate) entity.fieldstoupdate = [];
            return entity.fieldstoupdate;
        }
    ] as BasicListModel;
    
    def addField() {
        def handler = { o->
            if (!entity.fieldstoupdate) entity.fieldstoupdate = [];
            entity.fieldstoupdate << o;
            listHandler?.reload();
        }
        
        def params = [
            mode: mode,
            handler: handler
        ]
        def op = Inv.lookupOpener("posting:header:fieldupdate:create", params);
        if (!op) return null;
        return op;
    }
    
    def editField() {
        if (!selectedItem) return;
        
        def handler = { o->
            def i = entity.fieldstoupdate?.find{ it.objid == o.objid }
            if (i) {
                i.putAll( o );
                listHandler?.reload();
            }
        }
        
        def params = [
            entity: selectedItem,
            mode: mode,
            handler: handler
        ];
        def op = Inv.lookupOpener("posting:header:fieldupdate:open", params);
        if (!op) return null;
        return op;
    }
    
    void removeField() {
        if (!selectedItem) return;
        
        if (entity.fieldstoupdate) entity.fieldstoupdate.remove( selectedItem );
        listHandler?.reload();
    }
}

