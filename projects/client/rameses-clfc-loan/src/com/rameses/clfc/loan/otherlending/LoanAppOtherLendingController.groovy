package com.rameses.clfc.loan.otherlending;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.clfc.util.*;

class LoanAppOtherLendingController
{
    //feed by the caller
    def loanapp, caller, menuitem, handlers;

    def mode;
    def snapshot;
    def base64;
    
    @Binding
    def binding;
    
    @Service('LoanAppOtherLendingService') 
    def service; 
    
    def htmlbuilder = new OtherLendingHtmlBuilder();
    def otherlendings = [];
    def selectedOtherLending;
    
    void init() {
        mode = 'read';
        handlers.saveHandler = { save(); }
        //menuitem.saveHandler = { save(); }  
        base64 = new com.rameses.util.Base64Cipher(); 
        def data = service.open([objid: loanapp.objid]);
        loanapp.clear();
        loanapp.putAll(data);
        otherlendings = loanapp.otherlendings;
    }
        
    void save() {
        def data = [ objid: loanapp.objid, otherlendings: otherlendings ]
        service.update(data); 
        mode = 'read'; 
        snapshot = null;
    }

    def addOtherLending() {
        def handler = {otherlending->
            otherlending.parentid = loanapp.objid;
            otherlendings.add(otherlending);
            otherLendingHandler.reload();
        }
        return InvokerUtil.lookupOpener("otherlending:create", [handler:handler]);
    }
    
    def otherLendingHandler = [
        fetchList: {o->
            if( !otherlendings ) otherlendings = [];
            otherlendings.each{ it._filetype = "otherlending" }
            return otherlendings;
        },
        onRemoveItem: {o->
            return removeOtherLendingImpl(o);
        },
        getOpenerParams: {o->
            def handler = { l->
                def data = otherlendings?.find{ it.objid == l.objid }
                if (data) {
                    data.putAll( l );
                    otherLendingHandler?.reload();
                }
            }
            return [mode: mode, caller:this, handler: handler];
        }
    ] as EditorListModel
            
    void removeOtherLending() {
        removeOtherLendingImpl(selectedOtherLending);
    }
    
    boolean removeOtherLendingImpl(o) {
        if(caller.mode == 'read') return false;
        if(MsgBox.confirm("You are about to remove this lending. Continue?")) {
            otherlendings.remove(o);
            return true;
        }
        return false;
    }
            
    def getHtmlview() {
        return htmlbuilder.buildOtherLending(selectedOtherLending);
    }
    
    void edit() {
        snapshot = (otherlendings ? base64.encode( otherlendings ) : null); 
        mode = 'edit'; 
    }
    void cancelEdit() { 
        if (MsgBox.confirm('Are you sure you want to cancel any changes made?')) { 
            def o = (snapshot ? base64.decode( snapshot ): null); 
            if ( o ) {
                otherlendings = o; 
                otherLendingHandler?.reload();
            }
            mode = 'read'; 
        }
    }     
}