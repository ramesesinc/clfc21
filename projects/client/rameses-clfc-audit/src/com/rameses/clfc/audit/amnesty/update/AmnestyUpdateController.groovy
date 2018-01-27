package com.rameses.clfc.audit.amnesty.update

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class AmnestyOnlineUpdateController extends CRUDController {
    
    @Binding
    def binding;
    
    @PropertyChangeListener
    def listener = [
        'type': { o->
            entity.reftype = o?.type;
            entity.remove('amnesty');
            entity.remove('availed');
            entity.remove('update');
            binding?.refresh('opener');
        }
    ]
    
    String serviceName = 'AmnestyUpdateService';
    String entityName = 'amnesty:update';
    
    Map createEntity() {
        return [
            objid   : 'AOU' + new UID(),
            txnstate: 'DRAFT'
        ];
    }
    
    boolean allowDelete = false;
    boolean allowApprove = false;
    boolean allowEdit = true;
    
    def type;
    
    void afterCreate( data ) {
        type = null;
        binding?.refresh();
    }
    
    void afterOpen( data ) {
        checkEditable(data);
        setTypeValue(data);
    }
    
    void afterSave( data ) {
        checkEditable(data);
        setTypeValue(entity);
    }
    
    void checkEditable( data ) {
        allowEdit = false;
        if (data.txnstate == 'DRAFT') {
            allowEdit = true;
        }
        binding?.refresh('formActions');
    }
    
    void setTypeValue( data ) {
        type = getTypeList().find{ it.type == data.reftype }
        binding?.refresh('type');
    }
    
    def getTypeList() {
        def list = [];
        
        def invs = Inv.lookupOpeners('amnesty:update:plugin');
        invs?.each{ o->
            def props = o.properties;
            list << [caption: o.caption, type: props.reftype, index: props.index]
        }
        
        list?.sort{ it.index }
        
        return list;
    }
    
    def getOpener() {
        if (!entity.reftype) return;
        
        def op = Inv.lookupOpener('amnesty:update:' + entity.reftype, [entity: entity, mode: mode]);
        if (!op) return null;
        return op;
    }
    
    void submitForApproval() {
        if (!MsgBox.confirm("You are about to submit this document for approval. Continue?")) return;
        
        entity = service.submitForApproval(entity);
        checkEditable(entity);
        setTypeValue(entity);
    }
    
    void approveDocument() {
        if (!MsgBox.confirm("You are about to approve this document. Continue?")) return;
        
        entity = service.approveDocument(entity);
        checkEditable(entity);
        setTypeValue(entity);
    }
    
    void disapprove() {
        if (!MsgBox.confirm("You are about to disapprove this document. Continue?")) return;
        
        entity = service.disapprove(entity);
        checkEditable(entity);
        setTypeValue(entity);
    }
    
}

