package com.rameses.clfc.producttype2

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class LoanAttributeController {

    @Binding
    def binding;
    
    @PropertyChangeListener
    def listener = [
        "entity.computeduringapplication": { o->
            switch (o) {
                case 1: entity.computeduringposting = 0; break;
                case 2: entity.computeduringposting = 1; break;
            }
            binding?.refresh("entity.computeduringposting");
        },
        "entity.computeduringposting": { o->
            switch (o) {
                case 1: entity.computeduringapplication = 0; break;
                case 2: entity.computeduringapplication = 1; break;
            }
            binding?.refresh("entity.computeduringapplication");
        }
    ]
    
    def handler, entity, mode = "read";
    def varlist, typeList = [];
    def attributeLookup = Inv.lookupOpener("loan:producttype:attribute:lookup", [
         onselect: { o->
             o.value = o.defaultvalue;
             entity.attributeid = o.code;
             entity.attribute = o;
             entity.handler = o.handler;
             entity.computeduringapplication = 1;
             entity.computeduringposting = 0;
             binding?.refresh();
         },
         category: "LOAN"
    ]);
    
    void create() {
        entity = [objid: "LATTR" + new UID()];
    }
    
    void open() {
        if (mode != "read") {
            def d = [:];
            d.putAll(entity);
            entity = d;
        }
    }
    
    def getOpener() {
        if (!entity.handler) return;
        
        def params = [entity: entity, mode: mode, vars: []];
        def invtype = "producttype:loan:attribute:" + entity.handler;
        if (entity.handler == 'expression') {
            if (!varlist) varlist = [];
            params.vars = varlist;
        }
        
        //def op = Inv.lookupOpener("producttype:general:attribute:" + invtype, [entity: entity, mode: mode]);
        def op = Inv.lookupOpener(invtype, params);
        if (!op) return null;
        return op;
    }
    
    def doOk() {
        if (handler) handler(entity);
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
}

