package com.rameses.clfc.loan.fieldcollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class FieldCollectionConsolidatedCashBreakdownController 
    extends AbstractFieldCollectionCashBreakdownController {

    void init() {
        if (!entity.consolidatedbreakdown) entity.consolidatedbreakdown = [items: []];
    }
    
    Map getParams() {
        totalbreakdown = entity.consolidatedbreakdown.items?.amount?.sum();
        if (!totalbreakdown) totalbreakdown = 0;
        def params = [
            entries         : entity.consolidatedbreakdown?.items,//entity?.cashbreakdown?.items,
            totalbreakdown  : totalbreakdown, //totalbreakdown,
            editable        : false, //(mode != 'read'? true: false),
        ];
        
        return params;
    }
    
}

