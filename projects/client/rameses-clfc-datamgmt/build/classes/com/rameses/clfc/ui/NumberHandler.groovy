package com.rameses.clfc.ui

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class NumberHandler extends ConstraintHandler {
    def varList;

    void init() {}


    @PropertyChangeListener
    def listener = [
        "constraint.usevar": { o->
            if( o == 1) {
                constraint.intvalue = null;
                constraint.decimalvalue = null;
            } else {
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

