package com.rameses.clfc.ils.fact

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class ILSFactController extends CRUDController {
	
    @Binding
    def binding;
    
    String serviceName = "ILSFactService";
    
    boolean allowDelete = false;
    boolean allowApprove = false;
    boolean allowUndo = false;
    
    def selectedCategory, selectedField;
    def prevcategories, prevfields;
    
    Map createEntity() {
        def data = [
            objid       : "IRF" + new UID(), 
            txnstate    : "DRAFT", 
            fields      : [],
            categories  : []
        ];
        return data;
    } 
    
    void afterCreate( data ) {
        categoryHandler?.reload();
        fieldListHandler?.reload();
    }
    
    void afterOpen( data ) {
        categoryHandler?.reload();
        fieldListHandler?.reload();
    }
    
    void afterEdit( data ) {
        prevcategories = [];
        data.categories?.each{ o->
            def i = [:];
            i.putAll(o);
            prevcategories << i;
        }
        
        prevfields = [];
        data.fields?.each{ o->
            def i = [:];
            i.putAll(o);
            prevfields << i;
        }
    }
    
    void beforeCancel() {
        entity.categories = prevcategories;
        entity.fields = prevfields;
    }
    
    void afterCancel() {
        categoryHandler?.reload();
        fieldListHandler?.reload();
    }
    
    def categoryHandler = [
        fetchList: { o->
            if (!entity.categories) entity.categories = [];
            return entity.categories;
        }
    ] as ListPaneModel;
    
    def addCategory() {
        def handler = { o->
            if (!o.factid) o.factid = entity.objid;
            
            entity.categories << o;
            categoryHandler?.reload();
        }
        
        def op = Inv.lookupOpener("ils:fact:category", [entity: [:], mode: mode, handler: handler]);
        if (!op) return null;
        return op;
    }
    
    void removeCategory() {
        if (!selectedCategory) return;
        
        entity.categories.remove(selectedCategory);
        categoryHandler?.reload();
    }
    
    def fieldListHandler = [
        fetchList: { o->
            if (!entity.fields) entity.fields = [];
            entity.fields.sort{ it.seqno }
            return entity.fields;
        }
    ] as BasicListModel;
    
    def addField() {
        def handler = { o->
            if (!o.parentid) o.parentid = entity.objid;
            o.seqno = entity.fields.size();
            if (!o.seqno) o.seqno = 0;
            
            if (!entity._added) entity._added = [];
            entity._added << o;
            
            entity.fields << o;
            resolveFieldListSequence();
            fieldListHandler?.reload();
        }
        
        def op = Inv.lookupOpener("ils:fact:field:create", [entity: [:], mode: mode, handler: handler]);
        if (!op) return null;
        return op;
    }
    
    def editField() {
        if (!selectedField) return; 
        
        def handler = { o->
            def item = entity.fields.find{ it.objid==o.objid }
            if (item) {
                item.clear();
                item.putAll(o);
                item._edited = true;
            }
            
            resolveFieldListSequence();
            fieldListHandler?.reload();
        }
        
        def op = Inv.lookupOpener("ils:fact:field:open", [entity: selectedField, mode: mode, handler: handler]);
        if (!op) return null;
        return op;
    }
    
    void removeField() {
        if (!selectedField) return null;
        
        if (!entity._removed) entity._removed = [];
        entity._removed << selectedField;
        
        if (entity._added) entity._added.removed(selectedField);
        
        entity.fields.remove(selectedField);
        resolveFieldListSequence();
        fieldListHandler?.reload();
    }
    
    void resolveFieldListSequence() {
        entity.fields.eachWithIndex{ itm, idx-> 
            itm.seqno = idx;
        }
    }
    
    void moveUpField() {
        if (!selectedField || selectedField.seqno == 0) return;
        
        def item = entity.fields.find{ it.seqno == (selectedField.seqno - 1) }
        if (item) {
            item.seqno++;
        }
        selectedField.seqno--;
        entity.fields.sort{ it.seqno }
        fieldListHandler?.reload();
    }
    
    void moveDownField() {
        if (!selectedField || selectedField.seqno >= (entity.fields.size() - 1)) return;
        
        def item = entity.fields.find{ it.seqno == (selectedField.seqno + 1) }
        if (item) {
            item.seqno--;
        }
        selectedField.seqno++;
        entity.fields.sort{ it.seqno }
        fieldListHandler?.reload();
    }
}

