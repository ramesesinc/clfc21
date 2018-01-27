package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class CourtInfoController 
{
    @Binding
    def binding;
    
    def entity, filingmode = 'read', courtmode = 'read';
    def defaultvarlist;
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.courtinfo) entity.courtinfo = [:];
    }
    
    def selectedOption;
    def optionsHandler = [
        fetchList: { 
            def xlist = Inv.lookupOpeners('amnesty:smc:court:plugin');
            def list = [], props;
            xlist?.each{ o->
                props = o.properties;
                list << [caption: o.caption, type: props.reftype, index: props.index]
                
            }
            
            list?.sort{ it.index }
            return list;
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
            entity          : entity?.courtinfo,
            mode            : courtmode,
            defaultvarlist  : defaultvarlist
        ];
        
        def op = Inv.lookupOpener('amnesty:smc:court:' + selectedOption?.type, params);
        if (!op) return null;
        return op;
    }
}

