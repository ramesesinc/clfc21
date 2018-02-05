package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class SMCConditionController extends CRUDController
{
    @Binding
    def binding;
    
    String serviceName = 'SMCConditionService';
    
    boolean allowDelete = false;
    boolean allowApprove = false;
    boolean allowEdit = true;
    
    void afterOpen( data ) {
        if (!data._allowedit) data._allowedit = false;
        allowEdit = data._allowedit;
        binding?.refresh('formActions');
    }
    
    Map createEntity() {
        return [conditiontype: 'USER'];
    }
    
    def handler;
    
    @PropertyChangeListener
    def listener = [
        'entity.handler': { o->
            switch (o) {
                case "decimal" : 
                    entity.vardatatype = "decimal";
                    entity.datatype = "decimal";
                    break;
                case "integer" : 
                    entity.vardatatype = "integer";
                    entity.datatype = "integer";
                    break;
                case "string" : 
                    entity.vardatatype = "string";
                    entity.datatype = "string";
                    break;
                case "boolean" : 
                    entity.vardatatype = "boolean";
                    entity.datatype = "boolean";
                    break;   
                case "date" : 
                    entity.vardatatype = "date";
                    entity.datatype = "date";
                    break;                               
                default:
                    entity.vardatatype = null;
                    entity.datatype = null;
                    break;
            }
        }
    ];
    
    //def handlerList = ["expression", "date"]
    def getHandlerList() {
        def openers = Inv.lookupOpeners('plugin:smccondition:handler');
        def props, list = [];
        openers?.each{ o->
            props = o.properties;
            if (props?.value) {
                list << props.value;
            }
        }
        
        if (!list) return [];
        list.unique{ it }
        return list;
    }
    
}

