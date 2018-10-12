package com.rameses.clfc.ui

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class DateHandlerController extends ConstraintHandler {
	
    def varList;
    
    void init() {
        
    }
    
    @PropertyChangeListener
    def listener = [
        "constraint.usevar": { o->
            if( o == 1) {
                constraint.datevalue = null;
            } else {
                constraint.var = null;
            }
        }
    ]

    def operatorList = [
        [caption:"before", symbol:"<"],
        [caption:"on or before", symbol:"<="],
        [caption:"after", symbol:">"],
        [caption:"on or after", symbol:">="],
        [caption:"on", symbol:"=="],
    ];
}

