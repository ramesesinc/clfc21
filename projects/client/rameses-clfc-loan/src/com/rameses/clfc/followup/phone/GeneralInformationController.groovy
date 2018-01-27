package com.rameses.clfc.followup.phone

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class GeneralInformationController 
{
    @Binding
    def binding;
    
    void init() {}
        
    def entity, mode = 'read';
    
    def getBorrowerLookup() {
        def handler = { o->
            entity.borrower = o.borrower;
            entity.ledgerid = o.ledgerid;
            entity.loanapp = o.loanapp;
            binding?.refresh();
        }
        
        def params = [onselect: handler]
        def op = Inv.lookupOpener('phone:followup:borrower:lookup', params);
        if (!op) return null;
        return op;
    }
}

