package com.rameses.clfc.audit.amnesty.capture.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class CourtInfoController 
{
    @Binding
    def binding;
    
    @Script("Template")
    def template;
    
    @Service('LedgerAmnestyCaptureSMCService')
    def service;
    
    def entity, mode = 'read';
    def defaultvarlist;
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.courtinfo) entity.courtinfo = [conditions: []];
        
        if (mode != 'read') {
            def list = service.getDefaultConditions([conditions: entity?.courtinfo?.conditions]);
            list?.each{ o->
                if (!o.objid) o.objid = 'LSMCCOND ' + new UID();
                if (!o.parentid) o.parentid = entity?.objid;
                entity?.courtinfo?.conditions << o;
                
                if (!entity?.courtinfo?._addedconditions) entity.courtinfo?._addedconditions = [];
                entity?.courtinfo?._addedconditions << o;
            }
            //entity.courtinfo.conditions.addAll(list);
        }
        
        binding?.refresh('conditionHtml');
    }    
    
    def selectedOption;
    def optionsHandler = [
        fetchList: { 
            def xlist = Inv.lookupOpeners('capture:amnesty:smc:court:plugin');
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
            mode            : mode,
            defaultvarlist  : defaultvarlist
        ];
        
        def op = Inv.lookupOpener('capture:amnesty:smc:court:' + selectedOption?.type, params);
        if (!op) return null;
        return op;
    }
    
    def getConditionHtml() {
        if (!entity?.courtinfo?.conditions) entity.courtinfo?.conditions = [];
        //return template.render( "html/smc_condition_html.gtpl", [rule: entity, editable:true] );
                
        def params = [
            conditions  : entity?.courtinfo?.conditions,
            mode        : mode
        ]
        return template.render('html/smc_condition_html.gtpl', params);
    }
    
    def editCondition( params ) {
        def ci = entity?.courtinfo;
        def item = ci?.conditions?.find{ it.objid == params.objid }
        
        def handler = { o->
            def data = [:];
            if (ci?._addedconditions) {
                item = entity?._addedconditions?.find{ it.objid == o.objid }
                if (item) item.putAll(o);
            }
            
            if (ci?.conditions) {
                item = ci?.conditions?.find{ it.objid == o.objid }
                if (item) item.putAll(o);
            }
            
            binding?.refresh('conditionHtml');
        }
        
        def xprm = [
            handler : handler, 
            mode    : mode, 
            data    : item, 
            varlist : getVarList(item.index)
        ];
        
        def op = Inv.lookupOpener('capture:smc:condition:open', xprm);
        if (!op) return null;
        return op;
    }
    
    def removeCondition( params ) {
        def ci = entity?.courtinfo;
        def item = ci?.conditions?.find{ it.objid == params.objid }
        
        if (!ci?._removedconditions) ci._removedconditions = [];
        ci._removedconditions << item;
        
        if (ci?._addedconditions) ci._addedconditions.remove(item);
        
        if (ci?.conditions) ci.conditions.remove(item);
        
        binding?.refresh('conditionHtml');
    }
    
    
    def getVarList() {
        def idx = (entity?.conditions? entity?.conditions?.size() : 0);
        return getVarList(idx);
    }
    
    def getVarList( currentindex ) {
        def list = [];
        if (defaultvarlist) list.addAll(defaultvarlist);
        if (entity?.ci?.conditions) {
            def xlist = entity?.ci?.conditions?.findAll{ it.index < currentindex && it.varname != null }
            
            def item;
            xlist?.each{ o->
                item = [:];
                item.caption = o.varname;
                item.title = o.varname;
                item.signature = o.varname;
                if (o.datatype) {
                    item.description = '(' + o.datatype + ')';
                } else {
                    item.description = '(' + o.handler + ')';
                }
                list << item;
            }
        }
        
        return list;
    }
    
}

