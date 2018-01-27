package com.rameses.clfc.treasury.ledger.amnesty.smc.rule

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class SMCRuleTestController 
{
    @Binding
    def binding;
    
    @Service('SMCRuleTestService')
    def service;
    
    def smcLookup = Inv.lookupOpener('smc:ledgeramnesty:lookup', [
         onselect: { o->
             def data = service.getSMCAmnestyInfo(o);
             if (!smc) smc = [:];
             smc.refno = data?.refno;
             smc.conditions = data?.courtinfo?.conditions;
             binding?.refresh();
             listHandler?.reload();
         }
    ]);
    
    def smc;
   
    /*
    def getSmcLookup() {
        def handler = { o->            
            def data = service.getSMCAmnestyInfo(o);
            
            if (!smc) smc = [:];
            smc.refno = data?.refno;
            println 'conditions';
            data?.courtinfo?.conditions?.each{ println it }
            
            listHandler?.reload();
        }
        def op = Inv.lookupOpener('smc:ledgeramnesty:lookup', [onselect: handler]);
        if (!op) return null;
        return op;
    }
    */
    
    def listHandler = [
        fetchList: {
            if (!smc?.conditions) return [];
            return smc.conditions;
        }
    ] as BasicListModel;
    
    void executeRule() {
        service.executeRule(smc);
    }
    
}

