package com.rameses.clfc.branchloan.interest

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class BranchLoanInterestController extends CRUDController
{
    @Binding
    def binding;
    
    String serviceName = 'BranchLoanInterestService';
    String entityName = 'branchloan:interest';
    
    boolean allowDelete = false;
    boolean allowApprove = false;
    boolean allowEdit = true;
    
    Map createEntity() {
        return [objid: 'BLI' + new UID(), txnstate: 'DRAFT'];
    }
    
    void afterCreate( data ) {
        allowEdit = true;
        binding?.refresh('formActions');
    }
    
    void checkEditable( data ) {
        allowEdit = true;
        if (data?.txnstate == 'ACTIVE') {
            allowEdit = false;
        }
        binding?.refresh('formActions');
    }
    
    void afterOpen( data ) {
        checkEditable(data);
    }
    
    void activate() {
        if (!MsgBox.confirm("You are about to activate this setting. Continue?")) return;
        
        entity = service.activate(entity);
        checkEditable(entity);
    }
    
    void deactivate() {
        if (!MsgBox.confirm("You are about to deactivate this setting. Continue?")) return;
        
        entity = service.deactivate(entity);
        checkEditable(entity);
    }
    
    /*
    @Binding
    def binding;
    
    String serviceName = "BranchLoanSettingsService";
    String entityName = 'brancloansettings';
    String prefix = 'BLS';
    
    boolean allowDelete = false;
    boolean allowApprove = false;
    boolean allowEdit = true;
    
    void beforeCreate(data) {
        allowEdit = true;
        binding?.refresh('formActions');
    }
    
    void afterOpen( data ) {
        checkEditable(data);
    }
    
    void checkEditable( data ) {
        allowEdit = true;
        if (data.txnstate == 'ACTIVE') {
            allowEdit = false;
        }
        println 'allow edit ' + allowEdit;
        binding?.refresh('formActions');
    }
    
    void activate() {
        if (!MsgBox.confirm("You are about to activate this setting. Continue?")) return;
        
        entity = service.activate(entity);
        checkEditable(entity);
    }
    
    void deactivate() {
        if (!MsgBox.confirm("You are about to deactivate this setting. Continue?")) return;
        
        entity = service.deactivate(entity);
        checkEditable(entity);
    }
    */
}

