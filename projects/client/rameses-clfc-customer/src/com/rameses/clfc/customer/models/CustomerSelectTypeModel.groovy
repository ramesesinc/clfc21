package com.rameses.clfc.customer.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class CustomerSelectTypeModel {
    
    def handler; 
    def custtype;
    
    void init() {  
        custtype = 'individual';
    }
    
    def doOk() { 
        if ( !custtype )
            throw new Exception('Please select the type of customer'); 
        
        if ( handler ) {
            handler( custtype ); 
        }
        return '_close';
    }
    
    def doCancel() { 
        return '_close';
    }    
} 