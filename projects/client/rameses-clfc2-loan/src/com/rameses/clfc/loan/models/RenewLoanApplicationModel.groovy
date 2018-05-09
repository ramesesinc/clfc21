package com.rameses.clfc.loan.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.*;

public class RenewLoanApplicationModel {

    @Service("LoanApplicationService")
    def service;
    
    @Binding 
    def binding;
    
    @FormId
    def formid;

    def entity, mode;
    def clientTypes, appTypes, loanTypes; 
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
        formid = 'loanapplication-renewal';
        
        entity = [ objid: 'LOANREW'+ new java.rmi.server.UID(), appmode: 'ONLINE' ];
        appTypes = LoanUtil.appTypes.findAll{( it.key == 'RENEW' )} 
        clientTypes = LoanUtil.clientTypes; 
        loanTypes = service.getLoanTypes(); 
        productTypes = service.getProductTypes(); 
    } 
    
    def getTitle() {
        if ( mode == 'success' ) {
            return 'Renewal Application: Success'
        } else { 
            return 'Renewal Application: Initial Information'
        }
    }
    
    def doCancel() {
        if ( MsgBox.confirm('You are about to close this application. Proceed?')) {
            return '_close'; 
        } else {
            return null; 
        }
    }
    
    def doNextToInfo() {
        int count = appListHandler.getDataListSize(); 
        if ( count > 0 ) {
            if ( !MsgBox.confirm('There are still open applications from this borrower. Proceed?')) { 
                return null; 
            } 
        }
        def cloan = service.getCurrentLoan([ borrowerid: entity.borrower.objid ]);
        if ( cloan == null ) throw new Exception('No current loan found for this borrower');
        
        entity.loanaccount = [acctno: null]; 
        def appno = entity.appno.toString();
        int idx = appno.lastIndexOf('-'); 
        if ( idx > 0 ) { 
            entity.loanaccount.acctno = appno.substring(0, idx); 
        } 
        
        entity.route = cloan.route;
        entity.loantype = cloan.loantype; 
        entity.loancount = cloan.loancount; 
        entity.loanamount = cloan.loanamount; 
        producttype = productTypes.find{( it.name == cloan.producttype?.name )} 
        mode = 'info'; 
        return mode; 
    }
    
    def doBackToInitial() { 
        mode = 'init';
        return 'default'; 
    }
    
    def doSubmit() {
        if ( entity.clienttype == 'MARKETED' && !entity.marketedby ) 
            throw new Exception('Marketed By is required'); 

        if ( MsgBox.confirm('You are about to submit this application. Proceed?')) {
            entity.producttype = producttype; 
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
            entity.borrower = o; 
            appListHandler.reload(); 
        }
        def onempty = {
            entity.borrower = null; 
            appListHandler.reload(); 
        }
        return Inv.lookupOpener('customer:search', [onselect: onselect, onempty: onempty]); 
    }
    
    def appListHandler = [
        fetchList: { o-> 
            def borrowerid = entity.borrower?.objid; 
            if ( !borrowerid ) return null; 
            
            return service.getOpenLoans([ borrowerid: borrowerid ]); 
        }
    ] as BasicListModel; 
}