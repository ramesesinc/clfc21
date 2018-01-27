package com.rameses.clfc.audit.amnesty.capture.smc.posting

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class PostingInfoController 
{
    @Binding
    def binding;
    
    @Service('LedgerAmnestyCaptureSMCService')
    def service;
    
    @Script('Template')
    def template
    
    def entity, postingmode = 'read';
    def termList;
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.postinginfo) entity.postinginfo = [:];
        entity.postinginfo.created = true;
    }
    
    def selectedHeader;
    def headersHandler = [
        fetchList: { o->
           if (!entity.postinginfo.headers) entity.postinginfo.headers = [];
           return entity.postinginfo.headers;
        },
        onselect: { o-> 
            binding?.refresh('formActions');
        }
    ] as ListPaneModel; 
    
    void reload() {
        headersHandler?.reload();
        binding?.refresh();
    }
    
    def getConditionHtml() {
        if (!entity?.postinginfo?.conditions) entity?.postinginfo?.conditions = [];
        //return template.render( "html/smc_condition_html.gtpl", [rule: entity, editable:true] );
        
        def params = [
            list    : entity?.postinginfo?.conditions,
            mode    : 'read'
        ]
        return template.render('html/smc_posting_condition_html.gtpl', params);
    }
}

