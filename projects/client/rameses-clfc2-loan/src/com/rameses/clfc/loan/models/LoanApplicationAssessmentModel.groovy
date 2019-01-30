package com.rameses.clfc.loan.models

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class LoanApplicationAssessmentModel 
{
    @Binding
    def binding;
    
    @Service('LoanApplicationAssessmentService')
    def service;
    
    def caller, loanapp, menuitem, handlers;
    def charges;
	
    void init() {
        charges = service.getCharges([ appid: loanapp?.objid ]);
    }
    
    def getState() {
        return caller?.entity?.state;
    }
    
    def selectedCharge;
    def chargeHandler = [
        fetchList: {
            if (!charges) charges = [];
            return charges;
        }
    ] as BasicListModel;
    
    def assess() {
        charges = service.assess([ appid: loanapp?.objid ]);
        chargeHandler?.reload();
        binding?.refresh('total');
    }
    
    def getTotal() {
        if (!charges) return 0;
        return charges?.amount.sum();
    }
    
    def addCharge() {
        def handler = { o->
            def i = charges.find{ it.acctid == o.acctid }
            if (i) throw new RuntimeException(o.title + ' has already been added');
            
            charges = service.addCharge([ appid: loanapp?.objid, charge: o ]);
            chargeHandler?.reload();
            binding?.refresh('total');
        }
        
        def op = Inv.lookupOpener("loanapp:capture:application:othercharge", [handler: handler]);
        if (!op) return null;
        return op;
    }
    
    void removeCharge() {
        if (!selectedCharge) return;
        if (!MsgBox.confirm("You are about to remove this charge. Continue?")) return;
        
        charges = service.removeCharge([ appid: loanapp?.objid, chargeid: selectedCharge.objid ]);
        chargeHandler?.reload();
        binding?.refresh('total');
    }
    
    /*
    def addOtherCharges() {
        def handler = { o->
            o.allowDelete = true;
            
            //if (!entity.assessmentinfo.charges) entity.assessmentinfo.charges = [];
            //entity.assessmentinfo.charges << o;
            
            def charges = service.saveCharges([objid: entity.objid, charge: o]);
            entity.assessmentinfo.charges = charges;
            
            listHandler?.reload();
            binding?.refresh("total");
        }
        //def op = Inv.lookupOpener("loan:itemaccount:lookup", [onselect: handler]);
        def op = Inv.lookupOpener("loanapp:capture:application:othercharge", [handler: handler]);
        if (!op) return null;
        return op;
    }
    */
    
    /*
    void xremoveCharge() {
        if (!MsgBox.confirm("You are about to remove this charge. Continue?")) return;
        
        entity.assessmentinfo.charges.remove(selectedCharge);
        
        def charges = service.removeCharges([objid: entity.objid, charges: entity.assessmentinfo.charges]);
        entity.assessmentinfo.charges = charges;
        listHandler?.reload();
        binding?.refresh("total");
    }
    */
    
}

