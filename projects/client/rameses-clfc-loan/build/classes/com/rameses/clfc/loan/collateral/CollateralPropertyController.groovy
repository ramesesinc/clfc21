package com.rameses.clfc.loan.collateral;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.clfc.util.*;

class CollateralPropertyController
{
    @Binding
    def binding;
    def loanappid, collateral, caller, beforeSaveHandlers;
    
    def htmlbuilder = new CollateralHtmlBuilder();
    
    def getMode() {
        try { 
            return caller.mode; 
        } catch(Throwable t) {
            return null; 
        }
    }
    
    def selectedProperty;
    def propertyHandler = [
        fetchList: {o->
            if( !collateral?.properties ) collateral.properties = [];
            collateral.properties.each{ it._filetype = "realproperty" }
            return collateral.properties;
        },
        onRemoveItem: {o->
            return removeChildImpl(o); 
        },
        getOpenerParams: {o->
            def handler = { c->
                def data = collateral.properties?.find{ it.objid == c.objid }
                if (data) {
                    data.putAll( c );
                    propertyHandler?.reload();
                }
            }
            return [mode: mode, state: caller?.state, caller:this, handler: handler];
        }
    ] as EditorListModel;
    
    def addProperty() {
        def handler = {property->
            property.parentid = loanappid;
            collateral.properties.add(property);
            propertyHandler.reload();
        }
        return InvokerUtil.lookupOpener("realproperty:create", [state: caller?.state, handler:handler]);
    }
    
    void removeChild() {
        removeChildImpl(selectedProperty); 
    }
    
    boolean removeChildImpl(o) {
        if (mode == 'read') return false;
        if (MsgBox.confirm("You are about to remove this child. Continue?")) {
            collateral.properties.remove(o);
            return true;
        } else { 
            return false; 
        } 
    }
    
    def getHtmlview() {
        def m = [:]; 
        if ( selectedProperty ) m.putAll( selectedProperty ); 
        
        return htmlbuilder.buildProperty( m );
    }
    
    /*
    def addCiReport() {
        if ( collateral.ci == null ) collateral.ci = [:]; 
        
        def params = [mode: mode, caller: this]; 
        params.handler = { collateral.ci.property = it } 
        params.entity = collateral.ci.property; 
        if ( params.entity == null ) params.entity = [:]; 
        return InvokerUtil.lookupOpener("cireport:edit", params);
    } 
    */    
}