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
    
    @Service("NewLoanProductTypeService")
    def service;
    
    @PropertyChangeListener
    def listener = [
        'group': { o->
            entity.group = null;
            if (o) {
                entity.group = o.name;
            }
        },
        'ruleset': {o->
            entity.ruleset = null;
            if (o) {
                entity.ruleset = o.name;
            }
        },
        'rulegroup': { o->
            entity.rulegroup = null;
            if (o) {
                entity.rulegroup = o.name;
            }
        },
        'entity.header': {
            entity.overridevalue = null;
            if (it == null) {
                entity.isincrementafterposting = null;
                entity.allowoffset = null;
                entity.isexempted = null;
                entity.isdeductabletoamount = null;
                entity.deductabletoamountusevar = null;
                entity.deductabletoamountvar = null;
                entity.recalculateifnotenough = null;
                entity.postwithlacking = null;
                entity.applylacking = null;
                entity.allowoffset = null;
            }
            binding?.refresh();
        },
        "entity.postwithlacking": { o->
            if (!o) {
                entity.applylacking = false;
            }
            binding?.refresh();
        },
        "entity.isdeductabletoamount": { o->
            if (!o) {
                entity.deductabletoamountusevar = null;
                entity.deductabletoamountvar = null;
            }
            binding?.refresh();
        },
        "entity.deductabletoamountusevar": { o->
            if (!o) {
                entity.deductabletoamountvar = null;
            }
            binding?.refresh();
        }
        /*,
        'entity.posttoheader': { o->
            entity.code = null;
            entity.title = null;
            entity.name = null;
            entity.varname = null;
            entity.datatype = null;
            entity.attributeid = null;
            entity.attribute = null;
            entity.header = null;
            binding?.refresh();
        },
        'entity.header': { o->
            entity.code = null;
            entity.name = null;
            entity.datatype = null;
            entity.header = o;
            if (o != null) {
                entity.code = o.objid;
                entity.name = o.name;
                entity.title = o.title;
                entity.datatype = o.datatype;
            }
            
            //println 'entity';
            //entity?.each{ println it }
            binding?.refresh();
        }
        */
    ]
    
    def entity, postingsequence, mode = 'read';
    def handler, constraintControls = [];
    def _expr, vars, date;
    def forConstraintList, headerList;
    def group, ruleset, rulegroup;
    
    def attributeLookup = Inv.lookupOpener("loan:producttype:attribute:lookup", [
         onselect: { o->
             //println 'attribute'
             //o.each{ println it }
             entity.code = o.code;
             entity.title = o.title;
             entity.name = o.fieldname;
             entity.varname = o.varname;
             entity.datatype = o.datatype;
             entity.attributeid = o.code;
             entity.attribute = o;
             
             binding?.refresh();
         },
         category: "POSTING"
    ]);
    
    void init() {
        entity = [objid: "PC" + new UID(), fields: []];
        group = null;
        ruleset = null;
        rulegroup = null;
    }
    
    void open() {
        entity = copyMap( entity );
        
        entity?.constraints?.each {
            addConstraintControl( it );
        }
        
        group = null;
        if (entity.group) {
            def m =  getPostingGroupList()?.find{ it.name==entity.group }
            if (m) group = m;
        }
        
        ruleset = null;
        if (entity.ruleset) {
            def m = getRuleSetList()?.find{ it.name==entity.ruleset }
            if (m) ruleset = m;
        }
        
        rulegroup = null;
        if (entity.rulegroup) {
            def m = getRuleGroupList()?.find{ it.name==entity.rulegroup }
            if (m) rulegroup = m;
        }
        
        binding?.refresh();
    }
    
    def getRuleGroupList() {
        def list = service.getPostingRuleGroup();
        if (!list) list = [];
        return list;
    }
    
    def getRuleSetList() {
        def list = service.getPostingRuleSet();
        if (!list) list = [];
        return list;
    }
    
    def getPostingGroupList() {
        def list = service.getPostingGroup();
        if (!list) list = [];
        return list;
    }
    
    /*
    def getHeaderList() {
        def list = [];
        entity.postingheader.sort{ it.sequence }
        entity.postingheader.each{
            list << [code: it.code, title: it.title, name: it.name];
        }
        
        return list;
    }
    */
    
    /*
    def getPostingSequenceList() {
        def list = service.getPostingRuleGroup();
        if (!list) list = [];
        return list;
    }
    
    def getPostingGroupList() {
        def list = service.getPostingRuleSet();
        if (!list) list = [];
        return list;
    }
    */
    
    def copyMap( src ) {
        def map = [:];
        
        src?.each{ k,v-> 
            if (v instanceof List) {
                map[k] = copyList( v );
            } else if (v instanceof Map) {
                map[k] = copyMap( v );
            } else {
                map[k] = v;
            }
        }
        
        return map;
    }
    
    def copyList( src ) {
        def list = [];
        
        src?.each{ 
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
        
        def params = [
            onselect    : handler,
            fieldList   : fields
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
            constraintControls.remove( g );
            
            resolveConstraintIndex();
            
            binding.refresh();
        }
        def h = field.handler;
        if(!field.handler) h = field.datatype;
        //m.handler = "ruleconstraint:handler:"+h;
       
        m.handler = 'producttype:postinginfo:constraint:' + h;
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
    
    def getVarList() {
        def xlist = entity?.constraints?.findAll{ it.varname != null }
        def list = [];
        xlist?.each{ o->
            list << [objid: o.code, name: o.varname];
       }
        if (!list) list = [];
        return list;
    }
    
    def getDeductableToAmountVarList() {
        def list = [];
        
        list = vars?.findAll{ it.handler == entity.attribute?.datatype }?.collect{ it.signature }
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
                    //println 'group ' + entity.group;
                    
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
                        
                        //println "index->" + entity.index + " idx->" + idx;
                        
                        //println 'idx->' + idx + ' group->' + entity.group;
                                                
                        def vars2 = buildPostingSequenceVarList( idx, entity.group );
                        xvars.addAll( vars2 );
                        
                    }
                    return xvars;
                    /*
                    if (!vars) vars = [];
                    return vars;
                    */
                    //return [[caption: 'Caption', signature: 'Signature', description: '(Description)']];
                    /*
                    def vars = service.findAllVarsByType( [ruleid: action.parentid, datatype: type ] );
                    return vars.col lect{
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
    
    def buildPostingSequenceVarList( idx, group ) {
        
        def list = [];
        
        def groups = postingGroupList;
        def g = groups?.find{ it.name == group }
        if (g) {
            def xlist = groups.findAll{ it.idx < g.idx }
            xlist?.each{  o->
                postingsequence?.findAll{ it.group==o.name && it.varname != null }?.each{
                    def item = [
                        caption     : o.name + '_' + it.varname,
                        title       : o.name + '_' + it.varname,
                        signature   : o.name + '_' + it.varname,
                        handler     : it.datatype,
                        description : it.datatype
                    ];

                    list << item;
                }
            }
        }
        
        postingsequence?.findAll{ it.group==group && it.index < idx && it.varname != null }?.each{
            def item = [
                caption     : it.varname,
                title       : it.varname,
                signature   : it.varname,
                handler     : it.datatype,
                description : it.datatype
            ];
            
            list << item;
        }
        
        //println 'list';
        //list?.each{ println it }
        //println ''
        
        return list;
    }
    
    def doOk() {
        if (!entity.objid) entity.objid = "PC" + new UID();
        
        if (!entity.sequence) {
            def list = postingsequence?.findAll{ it.group==entity.group }
            list?.each{ println it }
            if (!list) list = [];
            entity.sequence = list.size() + 1;
        }
        if (!entity.sequence) entity.sequence = 1;
        entity.index = entity.sequence - 1;
        
        //throw new RuntimeException("stop");
        if (handler) handler( entity );
        return '_close'
    }
    
    def doCancel() {
        return '_close';
    }
}

