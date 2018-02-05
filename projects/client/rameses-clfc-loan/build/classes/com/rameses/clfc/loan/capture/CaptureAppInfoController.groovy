package com.rameses.clfc.loan.capture;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.*;

class CaptureAppInfoController {
    
    @Binding
    def binding;
    
    @Service("CaptureLoanAppService")
    def service;
    
    @PropertyChangeListener
    def listener = [
        "entity.clienttype": {o->
            if(o != 'MARKETED') entity.marketedby = null
            binding.refresh('entity.marketedby');
        }
        /*,
        "producttype": { o->
            entity.producttype = o;
        }
        /*,
        "entity.apptype": {o->
            if (o == 'NEW') { 
                entity.previousloans?.clear(); 
                previousLoansHandler.reload(); 
            } 
        }
        */
    ]
    
    def entity, mode = 'read';
    def loanTypes, productTypes;
    def producttype;
    def clientTypes = LoanUtil.clientTypes;
    def appTypes = LoanUtil.appTypes;
    
    def routeLookupHandler = Inv.lookupOpener('route:lookup', [:]);
    def customerLookupHandler = Inv.lookupOpener("customer:search", [
         onselect: { o-> 
            service.checkBorrowerForExistingLoan([borrowerid: o.objid, objid: entity.objid]); 
            entity.borrower = o; 
            entity.borrower.address = o.address?.text; 
         },
         onempty: {
            entity.borrower = [:];
         }
    ]);
    
    void init() {
        if (!entity) entity = [:];
        loanTypes = service.getLoanTypes();
        productTypes = service.getProductTypes();
        //producttype = productTypes.find{ it.code==entity.producttype.name }
    }
    
    /*
    void init() {
        if (!entity) entity = [:];
        loanTypes = service.getLoanTypes();
        productTypes = service.getProductTypes();
    }
    */
    
    /*
    def getCustomerLookupHandler() {
        def onselect = { o-> 
            service.checkBorrowerForExistingLoan([borrowerid: o.objid, objid: entity.objid]); 
            entity.borrower = o; 
            entity.borrower.address = o.address?.text;             
        }
        def onempty = {
            entity.borrower = [:];
        }
        def op = Inv.lookupOpener('customer:search', [onselect: onselect, onempty: onempty]);
        if (!op) return;
        return op;
    }
    */
    
}
