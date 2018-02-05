package com.rameses.clfc.ui

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class ConstraintHandler {
	
    @Binding
    def binding;

    @Caller
    def caller;

    def condition;
    def field;
    def constraint;
    def removehandler;

    void removeConstraint() {
        def z = condition.constraints.find{ it.objid == constraint.objid };
        condition.constraints.remove( z );
        if(removehandler) removehandler(z);
    }
}

