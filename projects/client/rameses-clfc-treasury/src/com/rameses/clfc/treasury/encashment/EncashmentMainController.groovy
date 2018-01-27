package com.rameses.clfc.encashment;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class EncashmentMainController extends CRUDController
{
    @Binding
    def binding;
    
    @Service('DateService')
    def dateSvc;

    String serviceName = "EncashmentService";
    String entityName = "encashment";
    
    boolean allowDelete = false, allowApprove = false, allowEdit = true;
    def prevcbs, prevbreakdown, prevreference, preventity;
    
    Map createPermission = [domain: 'TREASURY', role: 'CASHIER'];
    Map editPermission = [domain: 'TREASURY', role: 'CASHIER'];
    
    Map createEntity() {
        def data = [
            objid   : 'E' + new UID(),
            txnstate: 'DRAFT',
            txndate : dateSvc.getServerDateAsString().split(' ')[0],
            overage : 0
        ];
        data.check = [
            objid   : data.objid,//'EC' + new UID(),
            txndate : data.txndate,
            parentid: data.objid
        ];
        return data;
    }
    
    void afterCreate( data ) {
        optionsHandler.reload();
        optionsHandler.setSelectedIndex(0);
        checkEditable(data);
    }
    
    void checkEditable( data ) {
        allowEdit = false;
        if (data.txnstate == 'DRAFT') {
            allowEdit = true;
        }
        binding?.refresh('formActions');
    }
    
    void afterOpen( data ) {
        checkEditable(data);
    }
    
    void validateEntity( data ) {
        def msg = "";
        if (!data.txndate) msg += "Date is required.\n";
        if (!data.amount) msg += "Amount is required.\n";
        if (!data.remarks) msg += "Remarks is required.\n";
        if (!data.check.checkno) msg += "Check No. is required.\n";
        if (!data.check.txndate) msg += "Date is required.\n";
        if (!data.check.bank?.objid) msg += "Bank is required.\n";
        //if (!data.check.passbook?.objid) msg += "Passbook is required.\n";
        if (msg) throw new Exception(msg);
        
    }
    
    void beforeSave( data ) {
        validateEntity(data);  
    }

    void afterSave( data ) {
        checkEditable(data);
    }
    
    void afterEdit( data ) {
        if (data) {
            preventity = [:];
            preventity.putAll(data);
        }
        
        //def item;
        prevcbs = [];
        if (data?.cbs) {
            prevcbs = copyList(data?.cbs);
        }
        /*
        data?.cbs?.each{ o->
            item = [:];
            item.putAll(o);
            prevcbs << item;
        }
        */
        
        prevreference = [];
        if (data?.references) {
            prevreference = copyList(data?.references);
        }
        /*
        data?.references?.each{ o->
            item = [:];
            item.putAll(o);
            prevreference << item;
        }
        */
        
        prevbreakdown = [];
        if (data?.breakdown) {
            prevbreakdown = copyList(data?.breakdown);
        }
        /*
        data?.breakdown?.each{ o->
            item = [:];
            item.putAll(o);
            prevbreakdown << item;
        }
        */
    }
    
    def copyList( src ) {
        def list = [];
        
        def item;
        src?.each{ o->
            item = [:];
            item.putAll(o);
            list << item;
        }
        
        return list;
    }
    
    void afterCancel() {
        if (preventity) {
            entity = preventity;
        }
        
        if (prevcbs) {
            entity.cbs = prevcbs;
        }
        
        if (prevbreakdown) {
            entity.breakdown = prevbreakdown;
        }

        if (prevreference) {
            entity.references = prevreference;
        }
        
        entity.remove('_addedcbs');
        entity.remove('_removedcbs');
        binding?.refresh('opener');
    }
    
    def close() {
        return '_close';
    }
    
    def selectedOption;
    def optionsHandler = [
        fetchList: {
            def xlist = Inv.lookupOpeners('encashment-plugin');
            def list = [], props;
            xlist?.each{ o->
                props = o.properties;
                list << [caption: o.caption, name: props.name, index: props.index]
            }
            list.sort{ it.index }
            return list;
        }, 
        onselect: { o->
            binding.refresh('opener');
        }
    ] as ListPaneModel;

    def getOpener() {
        if (!selectedOption) return null;
        
        /*
        def params = [
            entity      : entity,
            allowEdit   : allowEdit //(mode!='edit'? true : false)
        ];
        */
       def params = [entity: entity, mode: mode];
        
        def op = Inv.lookupOpener('encashment:' + selectedOption.name, params);
        if (!op) return null;
        
        return op;
    }
    
    void submitForApproval() {
        if (!MsgBox.confirm('You are about to submit this document for approval. Continue?')) return;
        
        entity = service.submitForApproval(entity);
        checkEditable(entity);
    }
    
    void approveDocument() {
        if (!MsgBox.confirm('You are about to approve this document. Continue?')) return;
        
        entity = service.approveDocument(entity);
        checkEditable(entity);
    }

    void disapprove() {
        if (!MsgBox.confirm('You are about to disapprove this document. Continue?')) return;
        
        entity = service.disapprove(entity);
        checkEditable(entity);
    }
}