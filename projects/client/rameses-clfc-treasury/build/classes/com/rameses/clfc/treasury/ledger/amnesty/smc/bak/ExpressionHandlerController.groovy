package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class ExpressionHandlerController
{
    @Binding
    def binding;
    
    def action, vars, _expr;
    def mode = 'read';
    
    void init() {
        if (!action) action = [:];
    }
    
    def editExpression() {
        def model, handle;
        try {
            _expr = action?.expr;
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
                    if (!vars) vars = [];
                    return vars;
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
                action.expr = _expr;
                action.exprtype = 'expression';
                binding?.refresh('action.expr');
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
        return InvokerUtil.lookupOpener("expression:editor", [model:model, updateHandler: handle] );
    }
}

