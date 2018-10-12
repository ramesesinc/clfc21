package com.rameses.clfc.producttype2.constraint

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class PostingOptionConditionController {
	
    @Binding
    def binding;
    
    def entity, handler, mode = 'read';
    def selectedField, selectedExpr, vars, constraintControls = [];
    
    void open() {
        entity = copyMap( entity );
        
        if (entity.constraints) {
            entity.constraints.sort{ it.index }
            entity.constraints.each {
                addConstraintControl( it );
            }
        }
        
        fieldListHandler?.reload();
    }
    
    def copyMap( src ) {
        def data = [:];
        
        src?.each{ k, v->
            if (v instanceof Map) {
                data[k] = copyMap( v );
            } else if (v instanceof List) {
                data[k] = copyList( v );
            } else {
                data[k] = v;
            }
        }
        
        return data;
    }
    
    def copyList( src ) {
        def list = [];
        
        src?.each{ o->
            if (o instanceof Map) {
                list << copyMap( o );
            } else if (o instanceof List) {
                list << copyList( o );
            } else {
                list << o;
            }
        }
        
        return list;
    }
    
    def fieldListHandler = [
        fetchList: { o->
            if (!entity.fields) entity.fields = [];
            return entity.fields;
        }
    ] as BasicListModel;
        
    
    def addField() {
        def handler = { o->        
            def i = entity.fields?.find{ it.fact.objid==o.fact.objid && it.field.objid==o.field.objid }
            if (i) throw new RuntimeException("Field has already been selected.");
            
            o.title = o.fact.varname + "_" + o.field.name;
                        
            if (!entity.fields) entity.fields = [];
            entity.fields << o;
            
            fieldListHandler?.reload();
        }
        
        def params = [
            onselect: handler,
            category: "producttype"
        ];
        
        def op = Inv.lookupOpener("loan:producttype:field:select", params);
        if (!op) return null;
        return op;
    }
    
    void removeField() {
        if (!selectedField) return;
        
        if (entity.fields) {
            entity.fields.remove( selectedField );
            fieldListHandler?.reload();
        }
    }
    
    def expressionListHandler = [
        fetchList: { o->
            if (!entity.expressions) entity.expressions = [];
            return entity.expressions;
        }
    ] as BasicListModel;
    
    def addExpr() {
        
        def handler = { o->
            if (!o.objid) o.objid = "EXPR" + new UID();
            if (!entity.expressions) entity.expressions = [];
            def idx = entity.expressions?.size();
            if (!idx) idx = 0;
            o.index = idx;
            
            entity.expressions << o;
            expressionListHandler?.reload();
        }
        
        def params = [
            mode    : mode,
            handler : handler,
            vars    : vars
        ]
        
        def op = Inv.lookupOpener('loan:producttype:expression:create', params);
        if (!op) return null;
        return op;
    }
    
    void removeExpr() {
        if (!selectedExpr) return;
        
        if (entity.expressions) {
            entity.expressions.remove( selectedExpr );
            expressionListHander?.reload();
        }
    }
    
    def addConstraint() {
        def handler = { o->
            
            if (!entity?.constraints) entity?.constraints = [];
            
            def constraint = [objid: "CONS" + new UID()];
            def idx = entity?.constraints?.size();
            if (!idx) idx = 0;
            constraint.field = o;
            constraint.index = idx;
            
            entity.constraints << constraint;
            
            addConstraintControl( constraint );
            binding.refresh( "constraintControls" );
        }
        
        def fields = [];
        if (vars) {
            fields.addAll( vars );
        }
        
        fields.addAll( getFieldVarList() );
        
        /*
        if (entity.group) {
            def idx = 0;
            
            if (entity.index == null) {
                def list = postingsequence?.findAll{ it.group==entity.group }
                if (!list) list = [];
                idx = list.size();
            } else {
                idx = entity.index;
            }
            if (!idx) idx = 0;
            def vars2 = buildPostingSequenceVarList( idx, entity.group );
            fields.addAll( vars2 );

        }
        
        def flist = service.getFields( [category: 'PRODUCTTYPE', index: entity.index, group: entity.group, sequence: postingsequence] );
        if (flist) fields.addAll( flist );
        */
        
        def params = [
            onselect    : handler,
            fieldList   : fields
        ];
        def op = Inv.lookupOpener('producttype:postinginfo:option:condition:select', params);
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
            if (g) constraintControls.remove( g );
            
            resolveConstraintIndex();
            
            binding.refresh();
        }
        def h = field.handler;
        if(!field.handler) h = field.datatype;
        //m.handler = "ruleconstraint:handler:"+h;
       
        m.handler = 'producttype:postinginfo:option:condition:' + h;
        constraintControls << m;
    }
    
    void resolveConstraintIndex() {
        if (entity.constraints) {
            constraintControls = [];
            
            entity.constraints.sort{ it.index }
            entity.constraints.eachWithIndex{ itm, idx->
                itm.index = idx;
            }
            
            entity.constraints.sort{ it.index }
            entity.constraints.each{
                addConstraintControl( it );
            }
            
            binding?.refresh( "constraintControls" );
        }
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
    
    def getFieldVarList() {
        def list = [];
        entity.fields?.each{ o->
            def item = [
                caption     : o.title, 
                title       : o.title, 
                signature   : o.title, 
                handler     : o.field.handler
            ];
            
            if (item.handler == "expression") {
                item.description = "(decimal)";
            } else if (item.handler) {
                item.description = "(" + item.handler + ")";
            }
            list << item;
        }
        
        return list;
    }
    
    def doOk() {
        if (handler) handler( entity );
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
}

