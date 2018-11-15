package com.rameses.clfc.producttype2.constraint

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class ProductTypeExpressionController {

    @Binding
    def binding;
    
    def entity, mode = 'read', handler;
    def selectedField, vars, _expr;
    def datatypeList = ["boolean", "date", "decimal", "integer"];
    
    void init() {
        entity = [objid: "EXPR" + new UID(), fields: []];
    }
    
    void open() {
        entity = copyMap( entity );
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
        
        src?.each{
            if (it instanceof Map) {
                list << copyMap( it );
            } else if (it instanceof List) {
                list << copyList( it );
            } else {
                list << it;
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
    
    def editValue() {
        
        def model, handle;
        try {
            _expr = entity?.expr;
            model = [
                getValue: {
                    return _expr;
                },
                setValue: { o->
                    _expr = o;
                },
                getVariables: { type->
                    if (!vars) vars = []
                    def xvars = copyList( vars );
                    xvars.addAll( getFieldVarList() );
                    return xvars;
                }
            ] as ExpressionModel;
            handle = { o-> 
                entity.expr = _expr;
                binding?.refresh('entity.expr');
            };
        } catch (Exception e) {
            println e.message;
        }
        def op = Inv.lookupOpener("expression:editor", [model:model, updateHandler: handle] );
        if (!op) return null;
        return op;
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

