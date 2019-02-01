package com.rameses.clfc.loan.controller;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class LoanCollateralController
{
    //feed by the caller
    def loanapp, caller, menuitem, handlers;
    
    def mode;
    def snapshot;
    def base64;
    
    @Service('LoanAppCollateralService') 
    def service;    
    
    def beforeSaveHandlers = [:];
    def collateral = [];
    
    void init() {
        mode = 'read';
        handlers.saveHandler = { save(); }
        //menuitem.saveHandler = { save(); }  
        base64 = new com.rameses.util.Base64Cipher();     
        def data = service.open([objid: loanapp.objid]); 
        loanapp.clear();
        loanapp.putAll(data);
        collateral = loanapp.collateral;
    }

    def createOpenerParams() {
        return [
            beforeSaveHandlers: beforeSaveHandlers, 
            service: service, 
            loanappid: loanapp.objid,
            collateral: collateral,
            caller: this
        ]; 
    }
    
    def getState() {
        return caller?.entity?.state;
    }
    
    def tabHandler = [
        getOpeners: {
            return InvokerUtil.lookupOpeners('loanapp-collateral:plugin', createOpenerParams());
        },
        getOpenerParams: {
            return createOpenerParams(); 
        }
    ] as TabbedPaneModel 
    
    void save() {
        beforeSaveHandlers.each{k,v-> 
            if (v != null) v(); 
        }
        
        def data = [objid: loanapp.objid, collateral: collateral];
        service.update(data);
        mode = 'read'; 
        snapshot = null; 
    }
    void edit() {
        snapshot = (collateral ? base64.encode( collateral ) : null); 
        mode = 'edit'; 
    }
    void cancelEdit() { 
        if (MsgBox.confirm('Are you sure you want to cancel any changes made?')) { 
            def o = (snapshot ? base64.decode( snapshot ): null); 
            if ( o ) {
                collateral = o; 
                tabHandler?.reload();
            }
            mode = 'read'; 
        }
    }    
}