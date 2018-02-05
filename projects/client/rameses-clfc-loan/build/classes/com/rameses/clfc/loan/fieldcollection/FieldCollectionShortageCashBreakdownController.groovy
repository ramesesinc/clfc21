package com.rameses.clfc.loan.fieldcollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class FieldCollectionShortageCashBreakdownController 
    extends AbstractFieldCollectionCashBreakdownController {

    void init() {
        if (!entity.shortagebreakdown?.items) entity.shortagebreakdown = [items: []];
    }
    
    
    Map getParams() {
        totalbreakdown = entity.shortagebreakdown?.amount?.sum();
        if (!totalbreakdown) totalbreakdown = 0;
        def params = [
            entries         : entity?.shortagebreakdown?.items,
            totalbreakdown  : totalbreakdown,
            editable        : false,
        ];
        
        return params;
    }
}

