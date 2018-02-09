package com.rameses.clfc.currency;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class CurrencyController extends CRUDController {
    
    @Caller
    def caller;
    
    @Binding
    def binding;

    String serviceName = 'CurrencyTypeService';
    String entityName = 'currencytype';

    String createFocusComponent = 'entity.code';
    String editFocusComponent = 'entity.name';             
    boolean showConfirmOnSave = false;
    boolean allowApprove = false;
    boolean allowDelete = false;
    boolean allowEdit = true;
    
    Map createPermission = [domain:'DATAMGMT', role:'DATAMGMT_AUTHOR', permission:'curencytype.create']; 
    Map editPermission = [domain:'DATAMGMT', role:'DATAMGMT_AUTHOR', permission:'curencytype.edit']; 

    Map createEntity() { 
        def uid = new UID().toString();
        return [objid: 'CURRTYPE' + uid, code: 'CT' + uid.hashCode()]; 
    }
    
    void checkEditable( data ) {
        allowEdit = true;
        if (data.txnstate.matches("ACTIVATED")) {
            allowEdit = false;
        }
        binding?.refresh('formActions');
    }
    
    void afterSave( data ) {
        checkEditable( data );
        EventQueue.invokeLater({ caller?.reload(); });
    }
    
    void afterOpen( data ) {
        checkEditable( data );
    }
    
    void activate() {
        if (!MsgBox.confirm('You are about to activate this record. Continue?')) return
        //entity = service.changeState([objid: entity.objid, txnstate:'ACTIVATED']); 
        entity = service.activate( entity );
        checkEditable( entity );
        EventQueue.invokeLater({ caller?.reload() }); 
    } 
    
    void deactivate() {
        if (!MsgBox.confirm('You are about to deactivate this record. Continue?')) return
        //entity = service.changeState([objid: entity.objid, txnstate:'DEACTIVATED']); 
        entity = service.deactivate( entity );
        checkEditable( entity );
        EventQueue.invokeLater({ caller?.reload() }); 
    } 
    
    def viewLogs() {
        return Inv.lookupOpener('txnlog:open', [query: [txnid: entity.objid]]); 
    }    
}
