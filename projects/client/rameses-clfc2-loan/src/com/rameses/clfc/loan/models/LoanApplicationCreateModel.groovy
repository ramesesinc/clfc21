package com.rameses.clfc.loan.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.*;

public class LoanApplicationCreateModel {

    @Service("LoanApplicationService")
    def service;
    
    @Binding 
    def binding;
    
    @FormId
    def formid;

    def entity, borrower, mode;
    def clientTypes, loanTypes; 
    def productTypes, producttype;

    def routeLookupHandler = InvokerUtil.lookupOpener('route:lookup', [:]);
    
    @PropertyChangeListener
    def listener = [
        "entity.clienttype": {o->
            if ( o != 'MARKETED' ) entity.marketedby = null
        }, 
        
        "producttype": { o-> 
            entity.producttype = [name: o.name, term: o.term]; 
        }
    ];
    
    void init() {
        mode = 'init';
        formid = 'loanapplication-create';
        entity = [:];
        clientTypes = LoanUtil.clientTypes; 
        loanTypes = service.getLoanTypes(); 
        productTypes = service.getProductTypes(); 
    } 
    
    def getTitle() {
        if ( mode == 'success' ) {
            return 'Create Application: Success'
        } else { 
            return 'Create Application: Initial'
        }
    }
    
    def doCancel() {
        if ( MsgBox.confirm('You are about to close this application. Proceed?')) {
            return '_close'; 
        } else {
            return null; 
        }
    }
        
    def doBackToInitial() { 
        mode = 'init';
        return 'default'; 
    }
    
    def doSubmit() {
        if ( entity.clienttype == 'MARKETED' && !entity.marketedby ) 
            throw new Exception('Marketed By is required'); 

        if ( MsgBox.confirm('You are about to submit this application. Proceed?')) {
            entity.loancount = appList? appList.size() : 0;
            entity.producttype = producttype; 
            entity.loanaccount = selectedApp;
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
    
    def getLookupBorrower() { 
        def onselect = { o-> 
            borrower = o; 
            appListHandler.reload(); 
        }
        def onempty = {
            borrower = null; 
            appListHandler.reload(); 
        }
        return Inv.lookupOpener('customer:search', [onselect: onselect, onempty: onempty]); 
    }
    
    def appList, selectedApp;
    def appListHandler = [
        fetchList: { o-> 
            appList = [];
            def borrowerid = borrower?.objid; 
            if ( !borrowerid ) return null; 
            
            appList = service.getLoanApps([ borrowerid: borrowerid ]); 
            return appList; 
        }
    ] as BasicListModel; 
    
    boolean isCanNewApp() {
        if ( mode != 'init' || !borrower?.objid ) return false; 
        return (appList != null && appList?.isEmpty());
    }
    boolean isCanRenewApp() {
        if ( mode != 'init' || !borrower?.objid ) return false; 
        return (appList != null && !appList?.isEmpty());
    }
    
    void buildEntity() {
        entity = [ objid: 'LOANAPP'+ new java.rmi.server.UID(), appmode: 'ONLINE' ];
        entity.borrower = borrower;
    }
    def doNewApp() {
        buildEntity();
        entity.apptype = 'NEW';
        mode = 'info';
        return mode; 
    }
    
    def doRenewApp() {
        buildEntity();
        entity.apptype = 'RENEW';
        mode = 'info'; 
        return mode;
    }
}