package com.rameses.clfc.posting.header2

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class PostingHeaderLookupController 
{
    @Binding
    def binding;
    
    @Service('NewPostingHeaderLookupService')
    def service;
    
    def onselect, category;
    
    def selectedOption;
    def optionsHandler = [
        fetchList: { o->
            o.category = category;
            def list = service.getList(o);
            return list;
        },
        onselect: { o-> 
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

