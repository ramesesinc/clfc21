package com.rameses.clfc.ils.fact

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class ILSFactFieldController {

    @Binding
    def binding;
    
    @PropertyChangeListener
    def listener = [
        "entity.handler": { o->
            switch (o) {
                case 'decimal'  : entity.datatype = 'decimal'; break;
                case 'integer'  : entity.datatype = 'integer'; break;
                case 'boolean'  : entity.datatype = 'boolean'; break;
                case 'date'     : entity.datatype = 'date'; break;
                default: entity.datatype = 'string'; break;
            }
        },
        "entity.source": { o->
            if (o != 'database') {
                entity.schemaname = null;
                entity.subschemaname = null;
                entity.fieldname = null;
            }
            binding?.refresh("entity.*");
        }
    ]
    
    def entity, mode = 'read';
    def handler;
    
    def handlerList = [ "lookup", "decimal", "integer", "string", "boolean","date" ];
    def sourceList = ["user", "database"];
    
    void create() {
        entity = [objid: "IRFF" + new UID()]
        mode = 'create';
    }
    
    void open() {
        def preventity = [:];
        preventity.putAll(entity);
        entity = preventity;
        println 'entity';
        entity.each{ println it }
    }
    
    def getOpener() {
        if(!entity.handler) return null;
        return new Opener( outcome: entity.handler );
    }
    
    def doOk() {
        if (handler) handler(entity);
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}

