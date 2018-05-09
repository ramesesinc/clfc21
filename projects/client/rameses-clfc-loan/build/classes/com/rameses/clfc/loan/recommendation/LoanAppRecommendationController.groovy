package com.rameses.clfc.loan.recommendation;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;

class LoanAppRecommendationController
{
    def loanapp, caller, selectedMenu;
    
    @Service('LoanAppRecommendationService')
    def service;
    
    def data;
    void init() {
        data = service.open([objid: loanapp.objid]);
        if ( data == null ) data = [:]; 
        if ( !data.objid ) data.objid = loanapp.objid;
    }
    
    boolean isCiVisible() {
        return true; 
    }
    
    boolean isCrecomVisible() {
        return true;
    }
    
    boolean isCanEditCI() {
        if (loanapp?.state.toString() != 'FOR_INSPECTION') return false; 
        return (Inv.lookup('ci-recommendation-allow-edit') ? true : false); 
    }
    
    boolean isCanEditCRECOM() {
        if (loanapp?.state.toString() != 'FOR_CRECOM') return false; 
        return (Inv.lookup('crecom-recommendation-allow-edit') ? true : false);         
    }
    
    def editCI() {
        return 'ci'; 
    }
    
    def editCRECOM() {
        return 'crecom'; 
    }

    def doCancelEdit() { 
        init(); 
        return 'default'; 
    }
    
    def doSaveCI() {
        service.updateCI( data );
        return 'default'; 
    }
    
    def doSaveCRECOM() {
        service.updateCRECOM( data );
        return 'default'; 
    }
}
