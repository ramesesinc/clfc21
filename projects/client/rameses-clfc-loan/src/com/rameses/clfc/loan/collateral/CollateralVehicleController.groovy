package com.rameses.clfc.loan.collateral;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.*;

class CollateralVehicleController
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
    
    def selectedVehicle;
    def vehicleHandler = [
        fetchList: {o->
            if( !collateral?.vehicles ) collateral.vehicles = [];
            collateral.vehicles.each{ it._filetype = "vehicle" }
            return collateral.vehicles;
        },
        onRemoveItem: {o->
            return removeVehicleImpl(o); 
        },
        getOpenerParams: {o->
            def handler = { c->
                def data = collateral.vehicles?.find{ it.objid == c.objid }
                if (data) {
                    data.putAll( c );
                    vehicleHandler?.reload();
                }
            }
            return [mode: mode, state: caller?.state, caller:this, handler: handler];
        }
    ] as EditorListModel;
    
    def addVehicle() {
        def handler = {vehicle->
            vehicle.parentid = loanappid;
            collateral.vehicles.add(vehicle);
            vehicleHandler.reload();
        }
        return InvokerUtil.lookupOpener("vehicle:create", [state: caller?.state, handler:handler]);
    }
    
    void removeVehicle() {
        removeVehicleImpl(selectedVehicle); 
    }
    
    boolean removeVehicleImpl(o) {
        if (mode == 'read') return false;
        if (MsgBox.confirm("You are about to remove this vehicle. Continue?")) {
            collateral.vehicles.remove(o);
            return true;
        } else { 
            return false; 
        } 
    }
    
    def getHtmlview() {
        def m = [:]; 
        if ( selectedVehicle ) m.putAll( selectedVehicle ); 
         
        return htmlbuilder.buildVehicle( m );
    }
    
    /*
    def addCiReport() {
        if ( collateral.ci == null ) collateral.ci = [:]; 
        
        def params = [mode: mode, caller: this]; 
        params.handler = { collateral.ci.vehicle = it } 
        params.entity = collateral.ci.vehicle; 
        if ( params.entity == null ) params.entity = [:]; 
        return InvokerUtil.lookupOpener("cireport:edit", params);
    } 
    */
}