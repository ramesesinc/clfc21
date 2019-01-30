package com.rameses.clfc.loan.controller;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.loan.controller.*;
import com.rameses.clfc.borrower.BorrowerContext;

class LoanAppPrincipalBorrowerController   
{
    //feed by the caller
    def caller, loanapp, menuitem, handlers; 
    
    @Service('PrincipalBorrowerService') 
    def service;
    
    private def beforeSaveHandlers = [:];
    private def dataChangeHandlers = [:];
    
    def mode;
    def snapshot;
    def base64;

    void init() {
        mode = 'read';
        if (loanapp.objid == null) return;

        base64 = new com.rameses.util.Base64Cipher();   
        handlers.saveHander = { save(); }
        handlers.dataChangeHandler = { dataChange(); }
        //menuitem.saveHandler = { save(); }
        //menuitem.dataChangeHandler = { dataChange(); }
        dataChange();
    }

    def createOpenerParams() {
        def ctx = new com.rameses.clfc.borrower.BorrowerContext(caller, this, service, loanapp);
        ctx.beforeSaveHandlers = beforeSaveHandlers;
        ctx.dataChangeHandlers = dataChangeHandlers;
        return [borrowerContext: ctx, mode: mode]; 
    }
    
    def tabHandler = [
        getOpeners: {
            if (!loanapp.borrower.type) return [];
                      
            def type = loanapp.borrower.type.toLowerCase();
            return InvokerUtil.lookupOpeners('loanapp-borrower:plugin', createOpenerParams());
        },
        getOpenerParams: {
            return createOpenerParams(); 
        },
        beforeSelect: {item,index-> 
            if (caller?.mode == 'read' || index == 0) return true; 
        
            return (loanapp.borrower?.objid != null); 
        }
    ] as TabbedPaneModel 
    
    void save() {
        beforeSaveHandlers.each{k,v-> 
            if (v != null) v(); 
        }

        def data = [objid: loanapp.objid, borrower: loanapp.borrower]; 
        service.update(data);
        mode = 'read'; 
    }
    
    void dataChange() {
        def data = service.open([objid: loanapp.objid, itemname: menuitem.name]);
        data.borrower.type = 'PRINCIPAL'
        loanapp.clear();
        loanapp.putAll(data); 
        tabHandler?.reload();
    }
    
    void edit() {
        snapshot = base64.encode( loanapp ); 
        mode = 'edit'; 
    }
    void cancelEdit() { 
        if (MsgBox.confirm('Are you sure you want to cancel any changes made?')) { 
            def o = base64.decode( snapshot ); 
            if ( o ) {
                loanapp.clear();
                loanapp.putAll( o ); 
                tabHandler?.reload();
            }
            mode = 'read'; 
        }
    }
}
