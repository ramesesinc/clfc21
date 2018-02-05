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
        return [objid: "PH" + new UID(), fields: [], seqno: 0];
    }
    
    def selectedField;
    def fieldListHandler = [
        fetchList: { o->
            if (!entity.fields) entity.fields = [];
            return entity.fields;
        }
    ] as BasicListModel;
    
    def addField() {
        
        def handler = { o->
            def i = entity.fields?.find{ it.fact.objid==o.fact.objid && it.field.objid==o.field.objid }
            if (i) throw new RuntimeException("Field has already been selected.");
            
            if (!o.objid) o.objid  = "PHFF" + new UID();
            if (!o.parentid) o.parentid = entity.objid;
            o.title = o.fact.varname + "_" + o.field.name;
            
            if (!entity.fields) entity.fields = [];
            entity.fields << o;
            
            fieldListHandler?.reload();
            binding?.refresh("exprOpener");
        }
        
        def params = [
            onselect: handler,
            category: entity.category
        ];
        
        def op = Inv.lookupOpener("posting:header:field:select", params);
        if (!op) return null;
        return op;
    }
    
    void removeField() {
        if (!selectedField) return;
        
        entity.fields.remove(selectedField);
        fieldListHandler?.reload();
        binding?.refresh("exprOpener");
    }
    
    def getVarList() {
        def list = [];
        entity.fields.each{ o->
            list << [caption: o.title, title: o.title, signature: o.title, handler: o.field.handler];
        }
        return list;
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
    
    def getExprOpener() {
        def handler = { expr->
            entity.postingexpr = expr;
        }
        
        entity.expr = entity.postingexpr;
        def params = [
            entity  : entity,
            mode    : mode,
            handler : handler,
            vars    : getVarList()
        ]
        
        def op = Inv.lookupOpener("posting:header:expression", params);
        if (!op) return null;
        return op;
    }
    
    void testExpression() {
        service.testExpression(entity);
    }
    
}

