package com.rameses.clfc.loan.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.*;

public class NewLoanApplicationModel {

    @Service("LoanApplicationService")
    def service;
    
    @Binding 
    def binding;
    
    @FormId
    def formid;

    def entity, mode;
    def clientTypes, appTypes, loanTypes, productTypes;

    def routeLookupHandler = InvokerUtil.lookupOpener('route:lookup', [:]);
    
    @PropertyChangeListener
    def listener = [
        "entity.clienttype": {o->
            if ( o != 'MARKETED' ) entity.marketedby = null
        }
    ];
    
    void init() {
        mode = 'init';
        formid = 'loanapplication-new';
        
        entity = [ objid: 'LOANNEW'+ new java.rmi.server.UID(), appmode: 'ONLINE' ];
        appTypes = LoanUtil.appTypes.findAll{( it.key == 'NEW' )} 
        clientTypes = LoanUtil.clientTypes; 
        loanTypes = service.getLoanTypes(); 
        productTypes = service.getProductTypes(); 
    } 

    def getCustomerLookupHandler() {
        def onselect = { o-> 
            service.checkBorrowerForExistingLoan([borrowerid: o.objid, objid: entity.objid]); 
            entity.borrower = o; 
            entity.borrower.address = o.address?.text;             
        }
        def onempty = {
            entity.borrower = [:];
        }
        
        return Inv.lookupOpener('customer:search', [onselect: onselect, onempty: onempty]);
    }
    
    def getTitle() {
        if ( mode == 'success' ) {
            return 'New Application: Success'
        } else { 
            return 'New Application: Initial Information'
        }
    }
    
    def doCancel() {
        if ( MsgBox.confirm('You are about to close this application. Proceed?')) {
            return '_close'; 
        } else {
            return null; 
        }
    }
    
    def doSubmit() {
        if ( entity.clienttype == 'MARKETED' && !entity.marketedby ) 
            throw new Exception('Marketed By is required'); 
        
        if ( MsgBox.confirm('You are about to submit this application. Proceed?')) {
            def o = service.create( entity ); 
            if ( o ) entity.putAll( o ); 

            mode = 'success';
            return mode; 
            
        } else {
            return null; 
        }
    } 
    
    def doClose() { 
        return '_close'; 
    } 
    
    def doAddAnother() {
        init(); 
        return 'default';  
    }
    
    def doOpen() {
        return Inv.lookupOpener('loanapplication:open', [entity: entity]); 
    }
}