package com.rameses.clfc.treasury.ledger.amnesty.smc.handler;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*
import java.text.*;

public class DateHandlerController
{
    @Binding
    def binding;
    
    def action, operator;
    
    @PropertyChangeListener
    def listener = [
        'operator': { o->
            if (!action) action = [:];
            action.operator = o;
            binding?.refresh()
        }  
    ];
    
    void init() {
        if (!action) action = [:];
        operator = action?.operator;
    }
    
    def operatorList = [
        [caption: 'No Maturity', value: 'nomd'],
        [caption: 'Specify Date', value: 'specify'],
        [caption: 'Term', value: 'term']
    ];
    
    def getOpener() {
        if (!action?.operator) return null;
        
        def op = Inv.lookupOpener('smc:date:handler:' + action?.operator?.value, [action: action]);
        if (!op) return null;
        return op;
    }

    /*
    def operatorList = [
        [caption:"before", symbol:"<"],
        [caption:"on or before", symbol:"<="],
        [caption:"after", symbol:">"],
        [caption:"on or after", symbol:">="],
        [caption:"on", symbol:"=="],
    ];
    */
    
}