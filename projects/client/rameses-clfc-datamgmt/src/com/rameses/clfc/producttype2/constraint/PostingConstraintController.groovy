package com.rameses.clfc.producttype2.constraint

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class PostingConstraintController 
{
    @Binding
    def binding;
    
    def date;
    /*
    @Service('NewLoanProductTypePostingService')
    def service;
    */
    
    @Service("NewLoanProductTypeService")
    def service;
    
    @PropertyChangeListener
    def listener = [
        'entity.postonlastitem': { o->
            entity.postperitem = false;
            binding?.refresh();
        },
        'entity.postperitem': { o->
            entity.postonlastitem = false;
            binding?.refresh();
        }
    ]
    
    def entity, mode = 'read'
    def handler, constraintControls = [];
    def _expr, vars;
    def forConstraintList;
    
    def attributeLookup = Inv.lookupOpener("loan:producttype:attribute:lookup", [
         onselect: { o->
             println 'attribute'
             o.each{ println it }
             entity.code = o.code;
             entity.title = o.title;
             entity.name = o.fieldname;
             entity.varname = o.varname;
             entity.datatype = o.datatype;
             entity.attributeid = o.code;
             entity.postonlastitem = true;
             entity.sequence = entity.postinginfo?.postingsequence?.size();
             if (!entity.sequence) entity.sequence = 1;
             entity.index = entity.sequence - 1;
             
             binding?.refresh();
             /*
             o.value = o.defaultvalue;
             entity.attributeid = o.code;
             entity.attribute = o;
             entity.usedefault = 1;
             entity.value = o.value;
             binding?.refresh();
             */
         },
         category: "POSTING"
    ]);
    
    void init() {
        entity = [fields: []];
    }
    
    void open() {
        entity = copyMap( entity );
        entity?.constraints?.each { c->
            addConstraintControl(c);
        }
        
        binding?.refresh();
    }
    
    def getPostingGroupList() {
        def list = service.getPostingRuleGroup();
        if (!list) list = [];
        return list;
    }
    
    def copyMap( src ) {
        def map = [:];
        
        src.each{ k,v-> 
            if (v instanceof List) {
                map[k] = copyList( v );
            } else {
                map[k] = v;
            }
        }
        
        return map;
    }
    
    def copyList( src ) {
        def list = [];
        
        src.each{ 
            if (it instanceof Map) {
                def item = copyMap( it );
                list << item;
            } else {
                list << it;
            }
        }
        
        return list;
    }
    
    
    def selectedField;
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
        
        entity.fields.remove(selectedField);
        fieldListHandler?.reload();
    }    
    
    def addConstraint() {
        def handler = { o->
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
        def op = Inv.lookupOpener('producttype:postinginfo:constraint:select', params);
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
        m.handler = 'producttype:postinginfo:constraint:' + h;
        constraintControls << m;
    }
    
    def getFieldVarList() {
        def list = [];
        entity.fields?.each{ o->
            list << [caption: o.title, title: o.title, signature: o.title, handler: o.field.handler];
        }
        
        return list;
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
    
    def editValue() {
        
        def model, handle;
        try {
            _expr = entity?.postingexpr;
            /*
            if(actionParam.exprtype =="range" && actionParam.listvalue) {
                if( !MsgBox.confirm("You are about to replace the existing range values. Continue?") ) {
                    return;
                }
                _expr = null;
            } 
            */
            model = [
                getValue: {
                    return _expr;
                },
                setValue: { o->
                    _expr = o;
                },
                getVariables: { type->
                    def xvars = [];
                    if (!vars) vars = []
                    xvars.addAll( vars );
                    xvars.addAll( getFieldVarList() );
                    return xvars;
                    /*
                    if (!vars) vars = [];
                    return vars;
                    */
                    //return [[caption: 'Caption', signature: 'Signature', description: '(Description)']];
                    /*
                    def vars = service.findAllVarsByType( [ruleid: action.parentid, datatype: type ] );
                    return vars.collect{
                        [caption: it.name, title:it.name,  signature: it.name, description : "("+it.datatype +")" ]
                    };
                    */
                }
            ] as ExpressionModel;
            handle = { o-> 
                entity.postingexpr = _expr;
                binding?.refresh('entity.postingexpr');
                /*
                actionParam.listvalue = null;
                actionParam.expr = _expr;
                actionParam.exprtype = "expression";
                binding.refresh("actionParam.expr") 
                */
            };
        } catch (Exception e) {
            println e.message;
        }
        def op = InvokerUtil.lookupOpener("expression:editor", [model:model, updateHandler: handle] );
        if (!op) return null;
        return op;
    }
    
    def doOk() {
        if (handler) handler(entity);
        return '_close'
    }
    
    def doCancel() {
        return '_close';
    }
}

