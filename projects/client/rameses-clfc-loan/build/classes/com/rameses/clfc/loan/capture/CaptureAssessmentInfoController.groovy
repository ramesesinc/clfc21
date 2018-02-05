package com.rameses.clfc.loan.capture

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.text.DecimalFormat;

class CaptureAssessmentInfoController {

    @Binding
    def binding;
    
    @Service("CaptureLoanAppService")
    def service;
    
    def entity, mode = 'read';
    def decFormat = new DecimalFormat("#,##0.00");
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.assessmentinfo) entity.assessmentinfo = [charges: []];
        listHandler?.reload();
        binding?.refresh("total");
    }
    
    void assess() {
        entity.assessmentinfo = service.assess(entity);
        listHandler?.reload();
        binding?.refresh("total");
    }
    
    def selectedCharge;
    def listHandler = [
        fetchList: { o->
            if (!entity.assessmentinfo.charges) entity.assessmentinfo.charges = [];
            return entity.assessmentinfo.charges;
        }
    ] as BasicListModel;
    
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
    
    void removeCharge() {
        if (!MsgBox.confirm("You are about to remove this charge. Continue?")) return;
        
        entity.assessmentinfo.charges.remove(selectedCharge);
        
        def charges = service.removeCharges([objid: entity.objid, charges: entity.assessmentinfo.charges]);
        entity.assessmentinfo.charges = charges;
        listHandler?.reload();
        binding?.refresh("total");
    }
    
    def getTotal() {
        def amt = entity.assessmentinfo?.charges?.amount.sum();
        if (!amt) amt = 0;
        return decFormat.format(amt);
    }
}

