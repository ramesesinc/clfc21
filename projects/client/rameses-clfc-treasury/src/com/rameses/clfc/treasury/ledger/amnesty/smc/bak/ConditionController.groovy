package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class ConditionController 
{
    @Binding
    def binding;
    
    def data, mode = 'read';
    def handler, xhandler;
    
    
    def conditionLookup = Inv.lookupOpener('smc:condition:lookup', [
         onselect: { o->
             data.conditionid = o.code;
             data.title = o.title;
             binding?.refresh();
         }
    ]);
    
    void init() {
        data = [:];
    }
    
    def handlerList = ["decimal", "integer", "expression", "boolean", "date"]

    def getOpener() {
        if (!xhandler) return null;
        
        def op = Inv.lookupOpener('smccondition:handler:' + xhandler, [action: data, mode: mode]);
        if (!op) return null;
        return op;
    }
    
    void open() {
        def xdata = [:]
        xdata.putAll(data);
        data = xdata;
    }
    
    def doOk() {
        if (handler) handler(data);
        return '_close';
    }
    
    def doCancel() {
        return '_close';
    }
}

