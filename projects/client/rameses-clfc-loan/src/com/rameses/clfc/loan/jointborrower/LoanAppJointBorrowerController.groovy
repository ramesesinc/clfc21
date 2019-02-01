package com.rameses.clfc.loan.jointborrower;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.*;
import com.rameses.clfc.borrower.BorrowerContext;

class LoanAppJointBorrowerController { 
    
    @Service('JointBorrowerService') 
    def service; 
    
    //feed by the caller
    def caller, loanapp, menuitem, handlers;
    
    private def beforeSaveHandlers = [:];
    private def dataChangeHandlers = [:];
    
    def mode;
    def snapshot;
    def base64;
        
    def borrowers = [];
    def selectedJointBorrower;
    
    void init() {
        mode = 'read';
        handlers.saveHandler = { save(); }
        //menuitem.saveHandler = { save(); }  
        base64 = new com.rameses.util.Base64Cipher();  
        
        def data = service.open([objid: loanapp.objid]);
        loanapp.clear();
        loanapp.putAll(data);
        borrowers = loanapp.jointborrowers;
    }
        
    void save() {
        def data = [ objid: loanapp.objid, borrowers: borrowers ]
        service.update(data); 
        borrowers.each { 
            it.remove('_isnew'); 
        }
        mode = 'read'; 
        snapshot = null; 
    }
    void edit() {
        snapshot = (borrowers ? base64.encode( borrowers ) : null ); 
        mode = 'edit'; 
    }
    void cancelEdit() { 
        if (MsgBox.confirm('Are you sure you want to cancel any changes made?')) { 
            def o = ( snapshot ? base64.decode( snapshot ) : null ); 
            if ( o ) {
                borrowers = o; 
                jointBorrowerHandler?.reload();
            }
            mode = 'read'; 
        }
    }    

    def addJointBorrower() { 
        def params = createOpenerParams()
        params.callBackHandler = {joint->
            joint._isnew = true;
            borrowers.add(joint);
            jointBorrowerHandler.reload();
        };
        return InvokerUtil.lookupOpener("jointborrower:create", params)
    }
    
    def createOpenerParams() { 
        return [
            loanapp: [:], 
            caller: this, service: service, 
            beforeSaveHandlers: beforeSaveHandlers,
            dataChangeHandlers: dataChangeHandlers
        ];
    }
    
    def jointBorrowerHandler = [
        fetchList: {o->
            if( !borrowers ) borrowers = []
            borrowers.each{ it._filetype = "jointborrower" }
            return borrowers;
        },
        onRemoveItem: {o->
            return removeJointBorrowerImpl(o);
        },
        getOpenerParams: {o->
            def params = createOpenerParams()
            params.loanapp.borrower = o;
            return params
        }
    ] as EditorListModel;
        
    void removeJointBorrower() {
        removeJointBorrowerImpl(selectedJointBorrower);
    }
            
    boolean removeJointBorrowerImpl(o) {
        if (mode == 'read') return false;
        if (MsgBox.confirm("You are about to remove this joint borrower. Proceed?")) {
            borrowers.remove(o);
            return true;
        } else { 
            return false; 
        } 
    } 
    
    def getHtmlview() {
        def htmlbuilder=new BorrowerInfoHtmlBuilder();
        return htmlbuilder.buildBorrower(selectedJointBorrower)
    }
}
