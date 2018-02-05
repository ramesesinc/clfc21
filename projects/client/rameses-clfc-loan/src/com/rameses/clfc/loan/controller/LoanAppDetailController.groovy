package com.rameses.clfc.loan.controller;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.*;

class LoanAppDetailController 
{
    //feed by the caller
    def loanapp, caller, menuitem;
    
    @Service('LoanAppService') 
    def service;

    def clientTypes = LoanUtil.clientTypes; 
    def productTypes;   
    def routeLookupHandler = InvokerUtil.lookupOpener("route:lookup", [:]);    
    def borrowerLookupHandler
    
    def data = [:];
    def mode;
    def snapshot;
    def base64;

    void init() {
        mode = "read";         
        menuitem.saveHandler = { save(); }
        menuitem.dataChangeHandler = { dataChange(); }
        base64 = new com.rameses.util.Base64Cipher(); 
        data = service.open([objid: loanapp.objid]);
        loanapp.clear();
        loanapp.putAll(data);
        productTypes = service.getProductTypes(); 

        data.producttype = productTypes.find{ it.name == data.producttype?.name } 
        if (data.producttype == null) data.producttype = [:];
        def handler = {o->            
            if (o.objid == data.borrower.objid) 
                throw new Exception('Cannot select prinpical borrower as next to.');
            data.nextto = o;
        }
        
        borrowerLookupHandler = InvokerUtil.lookupOpener("loanappborrower:lookup", [
            'query.routecode': data.route.code, 
            'query.borrowerid': data.borrower.objid, 
            onselect: handler
        ]);
    }

    void dataChange() {
        data = loanapp;
    }
    
    void save() {
        if(data.clienttype == 'MARKETED' && !data.marketedby) 
            throw new Exception('Interviewed by is required.');
        
        if(loanapp.state == 'FOR_INSPECTION' && !data.route?.code) 
            throw new Exception("Route is required.")
        
        service.update(data);
        mode = 'read'; 
        snapshot = null; 
    }   
    void edit() {
        snapshot = base64.encode( loanapp ); 
        mode = 'edit'; 
    }
    void cancelEdit() { 
        if (MsgBox.confirm('Are you sure you want to cancel any changes made?')) { 
            def o = base64.decode( snapshot ); 
            if ( o ) {
                data.clear();
                data.putAll( o ); 
            }
            mode = 'read'; 
        }
    }    
}
