package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.client.*;

class FilingInfoController 
{
    @Binding
    def binding;
    
    def entity, filingmode = 'read';
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.filinginfo) entity.filinginfo = [:];
    }
    
    def selectedOption;
    def optionsHandler = [
        fetchList: { 
            def xlist = Inv.lookupOpeners('amnesty:smc:filing:plugin');
            def list = [], props;
            xlist?.each{ o->
                props = o.properties;
                list << [caption: o.caption, type: props.reftype, index: props.index]
                
            }
            
            list?.sort{ it.index }
            return list;
            return [];
        },
        onselect: {o-> 
            //query.state = selectedOption?.state; 
            //reloadAll(); 
            binding?.refresh('opener');
        }
    ] as ListPaneModel; 
    
    def getOpener() {
        if (!selectedOption) return null;
        
        def params = [
            entity  : entity?.filinginfo,
            mode    : filingmode
        ];
        
        def op = Inv.lookupOpener('amnesty:smc:filing:' + selectedOption?.type, params);
        if (!op) return null;
        return op;
    }
}

