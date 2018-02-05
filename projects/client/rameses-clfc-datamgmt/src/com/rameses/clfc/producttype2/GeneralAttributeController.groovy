package com.rameses.clfc.producttype2

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class GeneralAttributeController {

    @Binding
    def binding;
    
    @PropertyChangeListener
    def listener = [
        "entity.usedefault": { o->
            entity.value = null;
            binding?.refresh("xhandler");
        },
        "xhandler": { o->
            entity.handler = o.handler;
            //entity.datatype = o.datatype;
            //entity.vardatatype = o.vardatatype;
            entity.expr = null;
            entity.value = null;
            binding?.refresh("opener");
        }
    ]
    
    def handler, entity, mode = "read";
    def xhandler, varlist, typeList = [];
    def attributeLookup = Inv.lookupOpener("loan:producttype:attribute:lookup", [
         onselect: { o->
             o.value = o.defaultvalue;
             entity.attributeid = o.code;
             entity.attribute = o;
             entity.usedefault = 1;
             entity.value = o.value;
             binding?.refresh();
         },
         category: "GENERAL"
    ]);
    
    void create() {
        resetTypeList();
        entity = [objid: "LATTR" + new UID()];
    }
    
    void open() {
        resetTypeList();
        if (mode != "read") {
            def d = [:];
            d.putAll(entity);
            entity = d;
        }
        xhandler = typeList.find{ it.handler == entity.handler }
    }
    
    void resetTypeList() {
        typeList = [];
        def xl = Inv.lookupOpeners("producttype:general:attribute:plugin");
        xl?.each{ o->
            def props = o.properties;
            def item = [
                caption     : o.caption,
                handler     : props.handler,
                datatype    : props.datatype,
                vardatatype : props.vardatatype
            ];
            typeList << item;
        }
        typeList.sort{ it.caption }
    }
    
    def getOpener() {
        def invtype = "";
        def type = "blank"
        def params = [entity: entity, mode: mode];
        if (!entity.usedefault || entity.usedefault==0) {
            invtype = "producttype:general:attribute:";
            if (entity.handler) type = entity.handler;
            invtype += type;
        } else if (entity.usedefault==1) {
            invtype = "producttype:general:attribute:default:";
            if (entity.attribute) type = entity.attribute.type;
            invtype += type;
            params.entity = entity.attribute;
            params.mode = "read";
        }
        if (entity.handler == 'expression') {
            if (!varlist) varlist = [];
            params.vars = varlist;
        }
        
        //def op = Inv.lookupOpener("producttype:general:attribute:" + invtype, [entity: entity, mode: mode]);
        def op = Inv.lookupOpener(invtype, params);
        if (!op) return null;
        return op;
    }
    
    def doOk() {
        if (handler) handler(entity);
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
}

