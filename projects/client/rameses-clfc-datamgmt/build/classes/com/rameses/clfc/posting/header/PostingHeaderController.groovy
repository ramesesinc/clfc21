package com.rameses.clfc.posting.header

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class PostingHeaderController extends CRUDController 
{
    @Binding
    def binding;
    
    String serviceName = 'PostingHeaderService';
    String entityName = 'postingheader';
    
    Map createPermission = [role: 'ADMIN_SUPPORT', domain: 'ADMIN'];
    Map editPermission = [role: 'ADMIN_SUPPORT', domain: 'ADMIN']; 
    
    def typeList = ["DEFAULT"];
    def dataTypeList = ["decimal", "date"];
    def preventity, varlist = [];
    
    Map createEntity() {
        return [groupwith: []];
    }
    
    boolean allowApprove = false;
    boolean allowDelete = false;
    
    void afterCreate( data ) {
        varlist = service.getDefaultVarList();
    }
    
    void afterOpen( data ) {
        varlist = service.getDefaultVarList();
    }
        
    void afterEdit( data ) {
        preventity = [:];
        if (data) {
            preventity.putAll(data);
        }
    }
    
    void afterCancel() {
        if (preventity) {
            entity = preventity;
        }
    }
    
    def selectedItem;
    def listHandler = [
        fetchList: { o->
            if (!entity.groupwith) entity.groupwith = [];
            return entity.groupwith;
        }
    ] as BasicListModel;
    
    def addItem() {
        def handler = { o->
        
        }
        def op;
        if (!op) return null;
        return op;
    }
    
    void removeItem() {
        if (!MsgBox.confirm('You are about to remove this item. Continue?')) return;
        
        if (!entity._removedgroupwith) entity._removedgroupwith = [];
        entity._removedgroupwith << selectedItem;
        
        if (entity._addedgroupwith) entity._addedgroupwith.remove(selectedItem);
        
        if (entity.groupwith) entity.groupwith.remove(selectedItem);
        
        listHandler?.reload();
    }
    
    def getExprOpener() {
        def handler = { expr->
            entity.postingexpr = expr;
        }
        
        entity.expr = entity.postingexpr;
        def params = [
            entity  : entity,
            mode    : mode,
            handler : handler,
            vars    : varlist
        ]
        def op = Inv.lookupOpener("posting:header:expression", params);
        if (!op) return null;
        return op;
    }
}

