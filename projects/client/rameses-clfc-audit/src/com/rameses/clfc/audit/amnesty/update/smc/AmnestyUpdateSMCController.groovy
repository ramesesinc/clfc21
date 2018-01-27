package com.rameses.clfc.audit.amnesty.update.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class AmnestyUpdateSMCController {
    
    @Binding
    def binding;
    
    @Service('AmnestyUpdateService')
    def service;
    
    def entity, mode = 'read';
    def defaultvarlist, termlist;
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.availed) entity.availed = [:];
        if (!entity.update) entity.update = [:];
        defaultvarlist = service.getSMCDefaultVarList();
        termlist = service.getSMCTermList();
    }
    
    /*
    def amnestyLookup = Inv.lookupOpener('amnesty:update:lookup', [
         onselect: { o->
             def item = service.getSMCAmnestyInformation(o);
             if (item) {
                 entity.amnesty = item.amnesty;
                 entity.availed = item.availed;
                 entity.update = item.update;
                 binding?.refresh();
             }
         },
         type: 'SMC'
    ]);
    */
   
    def selectedOption;
    def optionsModel = [
        fetchList: { 
            def xlist = Inv.lookupOpeners('amnesty:update:smc:plugin');
            def list = [], props;
            xlist?.each{ o->
                props = o.properties;                 
                list << [caption: o.caption, type: props.reftype, index: props.index]
            }
            
            list?.sort{ it.index }
            return list;
        },
        onselect: {o->
            binding?.refresh('opener');
        }
    ] as ListPaneModel;
    
    def getOpener() {
        if (!selectedOption) return;
        
        def params = [
            entity          : entity,
            mode            : mode,
            defaultvarlist  : defaultvarlist,
            termlist        : termlist
        ];
        def op = Inv.lookupOpener('amnesty:update:smc:' + selectedOption?.type, params);
        if (!op) return null;
        return op;
    }
}

