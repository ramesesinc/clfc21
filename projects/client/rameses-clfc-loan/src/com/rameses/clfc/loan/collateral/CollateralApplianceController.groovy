package com.rameses.clfc.loan.collateral;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.*;

class CollateralApplianceController
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
    
    def selectedAppliance;
    def applianceHandler = [
        fetchList: {o->
            if( !collateral?.appliances ) collateral.appliances = [];
            collateral.appliances.each{ it._filetype = "appliance"; } 
            return collateral.appliances;
        },
        onRemoveItem: {o->
            return removeApplianceImpl(o); 
        },
        getOpenerParams: {o-> 
            return [mode: mode, caller:this];
        }
    ] as EditorListModel;
    

    def addAppliance() {
        def handler = {appliance->
            appliance.parentid = loanappid;
            collateral.appliances.add(appliance);
            applianceHandler.reload();
        }
        return InvokerUtil.lookupOpener("appliance:create", [handler:handler]);
    }
    
    void removeAppliance() {
        removeApplianceImpl(selectedAppliance); 
    }
    
    boolean removeApplianceImpl(o) {
        if (mode == 'read') return false;
        if (MsgBox.confirm("You are about to remove this appliance. Continue?")) {
            collateral.appliances.remove(o);
            return true;
        } else { 
            return false; 
        } 
    }
    
    def getHtmlview() {
        def m = [:]; 
        if ( selectedAppliance ) m.putAll( selectedAppliance ); 
        
        m.ci = collateral.ci; 
        return htmlbuilder.buildAppliance( m );
    }
    
    def addCiReport() {
        if ( collateral.ci == null ) collateral.ci = [:]; 
        
        def params = [mode: mode, caller: this]; 
        params.handler = { collateral.ci.appliance = it } 
        params.entity = collateral.ci.appliance; 
        if ( params.entity == null ) params.entity = [:]; 
        return InvokerUtil.lookupOpener("cireport:edit", params);
    }    
}