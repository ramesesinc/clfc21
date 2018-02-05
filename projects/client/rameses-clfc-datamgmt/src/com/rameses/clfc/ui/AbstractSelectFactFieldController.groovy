package com.rameses.clfc.ui

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

abstract class AbstractSelectFactFieldController {
    
    protected abstract String getServiceName();

    def onselect, category;
    def selectedFact, selectedField;
    
    public Object getService() {
        String name = getServiceName();
        if ((name == null) || (name.trim().length() == 0)) {
            throw new NullPointerException("Please specify a serviceName");
        }
        return InvokerProxy.getInstance().create(name);
    }
    
    def getFactList() {
        def params = [category: category];
        beforeGetFactList(params);;
        return getService().getFactList(params);
    }
    
    void beforeGetFactList( params ) {
    }
    
    def getFieldList() {
        if (!selectedFact) return [];
        
        def params = [factid: selectedFact.objid];
        beforeGetFieldList(params)
        return getService().getFieldList(params);
    }
    
    void beforeGetFieldList( params ) {
    }

    def doOk() {
        if(!selectedFact || !selectedField) 
            throw new Exception("Please select an item");
        
        if (onselect) {
            onselect([fact: selectedFact, field: selectedField]);
        }
        return "_close";
    }

    def doCancel() {
        return "_close";
    }

}

