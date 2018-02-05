package com.rameses.clfc.audit.amnesty.update.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class SMCUpdateInfoController {

    @Binding
    def binding;
    
    @Script("Template")
    def template;
    
    def entity, mode = 'read';
    def defaultvarlist, termlist;
    
    def getConditionHtml() {
        if (!entity?.update?.conditions) entity?.update?.conditions = [];
        //return template.render( "html/smc_condition_html.gtpl", [rule: entity, editable:true] );
        
        def params = [
            conditions  : entity?.update?.conditions,
            mode        : mode
        ]
        return template.render('html/smc_condition_html.gtpl', params);
    }
    
    
    def getVarList() {
        def idx = (entity?.update?.conditions? entity?.update?.conditions?.size() : 0);
        return getVarList(idx);
    }
    
    def getVarList( currentindex ) {
        def list = [];
        if (defaultvarlist) list.addAll(defaultvarlist);
        if (entity?.update?.conditions) {
            def xlist = entity?.update?.conditions?.findAll{ it.index < currentindex && it.varname != null }
            
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
    
    void moveUp( params ) {
        def item = entity?.update?.conditions?.find{ it.objid == params.objid }
        
        if (item) {
            def idx = item.index;
            
            def item2 = entity?.update?.conditions?.find{ it.index == (idx - 1) }
            if (item2) {
                item2.index++;
            }
            item.index--;
        }
        
        binding?.refresh('conditionHtml');
    }
    
    void moveDown( params ) {
        def item = entity?.update?.conditions?.find{ it.objid == params.objid }
        
        if (item) {
            def idx = item.index;
            
            def item2 = entity?.update?.conditions?.find{ it.index == (idx + 1) }
            if (item2) {
                item2.index--;
            }
            item.index++;
        }
        
        binding?.refresh('conditionHtml');
    }
    
    def addCondition() {
        def handler = { o->
            o.objid = 'SMCCOND' + new UID();
            o.parentid = entity.amnesty.objid;
            o._allowremove = true;
            o.index = (entity?.update?.conditions? entity?.update?.conditions?.size() : 0) + 1;
            
            //if (!entity?.update?._addedconditions) entity.update?._addedconditions = [];
            //entity.update?._addedconditions << o;
            
            if (!entity?.update?.conditions) entity.update?.conditions = [];
            entity.update?.conditions << o;
            
            binding?.refresh('conditionHtml');
        }
        
        def op = Inv.lookupOpener('smc:condition:create', [handler: handler, mode: mode, varlist: getVarList()]);
        if (!op) return null;
        return op;
    }
    
    def editCondition( params ) {
        def item = entity?.update?.conditions?.find{ it.objid == params.objid }
        
        def handler = { o->
            def data = [:];
            //if (entity?.update?._addedconditions) {
            //   item = entity?.update?._addedconditions?.find{ it.objid == o.objid }
            //    if (item) item.putAll(o);
            //}
            
            if (entity?.update?.conditions) {
                item = entity?.update?.conditions?.find{ it.objid == o.objid }
                if (item) item.putAll(o);
            }
            
            binding?.refresh('conditionHtml');
        }
        
        def op = Inv.lookupOpener('smc:condition:open', [handler: handler, mode: mode, data: item, varlist: getVarList(item.index)]);
        if (!op) return null;
        return op;
    }
    
    def removeCondition( params ) {
        def item = entity?.update?.conditions?.find{ it.objid == params.objid }
        
        //if (!entity?.update?._removedconditions) entity.update?._removedconditions = [];
        //entity?.update?._removedconditions << item;
        
        //if (entity?.update?._addedconditions) entity.update?._addedconditions.remove(item);
        
        if (entity?.update?.conditions) entity.update?.conditions.remove(item);
        
        binding?.refresh('conditionHtml');
    }
}

