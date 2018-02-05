package com.rameses.clfc.ui

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class ExpressionHandlerController
{
    @Binding
    def binding;
    
    def entity, vars, _expr;
    def mode = 'read';
    def handler;
    
    void init() {
        if (!entity) entity = [:];
    }
    
    def editExpression() {
        def model, handle;
        try {
            _expr = entity?.expr;
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
                entity.expr = _expr;
                entity.exprtype = 'expression';
                if (handler) handler(_expr);
                binding?.refresh('entity.expr');
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