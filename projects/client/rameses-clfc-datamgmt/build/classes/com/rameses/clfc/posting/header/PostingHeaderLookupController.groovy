package com.rameses.clfc.posting.header

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class PostingHeaderLookupController 
{
    @Binding
    def binding;
    
    @Service('PostingHeaderLookupService')
    def service;
    
    def onselect, conditions;
    
    def selectedOption;
    def optionsHandler = [
        fetchList: { o->
            if (conditions) o.conditions = conditions;
            def list = service.getList(o);
            return list;
        },
        onselect: { o-> 
            //println 'selected option ' + selectedOption;
            //query.state = selectedOption?.state; 
            //reloadAll(); 
            //binding?.refresh('opener');
            binding?.refresh('formActions');
        }
    ] as ListPaneModel; 
        
    void reload() {
        optionsHandler?.reload();
    }
    
    def doOk() {
        if (onselect) onselect(selectedOption);
        return '_close';
    }
    
    def doCancel() {
        return '_close';
    }
}

