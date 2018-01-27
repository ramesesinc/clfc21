package com.rameses.clfc.treasury.settings.smc.posting.constraint

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class PostingConstraintController 
{
    @Binding
    def binding;
    
    @Service('LedgerAmnestySMCService')
    def service;
    
    @PropertyChangeListener
    def listener = [
        'postingitem': { o->
            entity.code = o.code;
            entity.title = o.title;
        },
        'entity.postonlastitem': { o->
            entity.postperitem = false;
            binding?.refresh();
        },
        'entity.postperitem': { o->
            entity.postonlastitem = false;
            binding?.refresh();
        }
    ]
    
    def entity, mode = 'read', headers;
    def onselect, itemList, postingitem;
    def constraintControls = [];
    
    void init() {
        entity = [:];
    }
    
    void open() {
        def list = getItemList();
        postingitem = list?.find{ it.code == entity.code }
        entity?.constraints?.each { c->
            addConstraintControl(c);
        }
        binding?.refresh();
        //println 'entity ' + entity; 
    }
    
    def getItemList() {
        if (!headers) headers = [];
        return headers;
    }
    
    def addConstraint() {
        def handler = { o->
            //def constraint = [objid:"RCONST"+new UID(), pos: entity?.constraints?.size() ];
            def constraint = [:];
            def pos = entity?.constraints?.size();
            if (!pos) pos = 0;
            constraint.field = o;
            
            if (!entity?.constraints) entity?.constraints = [];
            entity.constraints << constraint;
            
            addConstraintControl(constraint);
            binding.refresh( "constraintControls" );
        }
        def params = [
            onselect    : handler,
            fieldList   : service.getFieldList()
        ];
        def op = Inv.lookupOpener('smc:posting:constraint:select', params);
        if (!op) return null;
        return op;
    }
    
    void addConstraintControl( constraint ) {
        def field = constraint.field;
        def m = [:];
        m.objid = constraint.objid;
        m.fieldname = field.name;
        m.caption = field.title;
        m.type = 'subform';
        m.properties = [:];
        m.properties.condition = entity;
        m.properties.constraint = constraint;
        m.properties.varList = getVarList();
        m.properties.field = field;
        m.properties.removehandler = { x-> 
            def g = constraintControls.find{ it.objid == x.objid };
            constraintControls.remove(g);
            
            binding.refresh();
        }
        def h = field.handler;
        if(!field.handler) h = field.datatype;
        //m.handler = "ruleconstraint:handler:"+h;
        m.handler = 'smc:posting:constraint:' + h;
        constraintControls << m;
    }
    
    def getVarList() {
        def xlist = entity?.constraints?.findAll{ it.varname != null }
        def list = [];
        xlist?.each{ o->
            list << [objid: o.code, name: o.varname];
       }
        if (!list) list = [];
        return list;
    }
    
    def doOk() {
        if (onselect) onselect(entity);
        return '_close'
    }
    
    def doCancel() {
        return '_close';
    }
}

