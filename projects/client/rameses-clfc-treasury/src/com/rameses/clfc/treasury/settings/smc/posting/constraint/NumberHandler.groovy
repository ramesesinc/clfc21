package com.rameses.clfc.treasury.settings.smc.posting.constraint

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class NumberHandler extends ConstraintHandler {
    def varList;

    void init() {
        //if(constraint.usevar == 1 ) buildVarList();
    }

    /*
    void buildVarList() {
        varList = [];
        varList = service.findAllVarsByType( [ruleid:condition.parentid, datatype:field.vardatatype, pos: condition.pos ] ).collect{  
            [objid: it.objid, name: it.name]
        };
    }
    */

    @PropertyChangeListener
    def listener = [
        "constraint.usevar": { o->
            if( o == 1) {
                constraint.intvalue = null;
                constraint.decimalvalue = null;
                //buildVarList();
            }
            else {
                constraint.var = null;
            }
        }
    ]

    def operatorList = [
        [caption:"greater than", symbol:">"],
        [caption:"greater than or equal to", symbol:">="],
        [caption:"less than", symbol:"<"],
        [caption:"less than or equal to", symbol:"<="],
        [caption:"equal to", symbol:"=="],
        [caption:"not equal to", symbol:"!="],
    ];
}

