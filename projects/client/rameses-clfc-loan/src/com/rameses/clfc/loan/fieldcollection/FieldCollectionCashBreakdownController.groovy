package com.rameses.clfc.loan.fieldcollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class FieldCollectionCashBreakdownController 
    extends AbstractFieldCollectionCashBreakdownController {
    
    Map getParams() {
        totalbreakdown = entity.cashbreakdown.items?.amount?.sum();
        if (!totalbreakdown) totalbreakdown = 0;
        
        def params = [
            entries         : entity?.cashbreakdown?.items,
            totalbreakdown  : totalbreakdown,
            editable        : (mode != 'read'? true : false),
            onupdate        : { o->
                //caller?.buildConsolidatedCashBreakdown(entity);
                 
            }
        ];
        return params;
    }
}

