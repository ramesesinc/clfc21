package com.rameses.clfc.loan.collateral;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class CollateralOtherController
{
    def loanappid, collateral, caller, beforeSaveHandlers;
    def entity = [:];
    
    void init() {
        if( collateral?.other ) entity = collateral.other;
        if (entity.attachments == null) entity.attachments = [];
        beforeSaveHandlers.otherCollateralSaveHandler = { validate(); }
    }
    
    void validate() {
        if( !collateral?.other ) 
            collateral.other = entity
    }
    
    def getMode() {
        try { 
            return caller.mode; 
        } catch(Throwable t) {
            return null; 
        }
    }    
}