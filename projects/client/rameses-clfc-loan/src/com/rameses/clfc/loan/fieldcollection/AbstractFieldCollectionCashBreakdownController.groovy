package com.rameses.clfc.loan.fieldcollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

abstract class AbstractFieldCollectionCashBreakdownController {

    @Caller
    def caller;
    
    Map entity = new HashMap(); 
    String mode = 'read';
    BigDecimal totalbreakdown;
    String cbSchemaName = "clfc:denomination:nopanel";
    
    abstract Map getParams();
    
    void init() {
        if (!entity.cashbreakdown?.items) entity.cashbreakdown = [items: []];
        totalbreakdown = entity?.cashbreakdown?.items?.amount?.sum();
        if (!totalbreakdown) totalbreakdown = 0;
    }
    
    Object getCashbreakdown() {
        def op = Inv.lookupOpener(cbSchemaName, getParams());
        if (!op) return null;
        return op;
    }
    
}

