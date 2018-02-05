package com.rameses.clfc.treasury.ledger.amnesty.smc.preview

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class AmnestySMCPreviewPopupController extends AmnestySMCPreviewController 
{
    def entity;
    void init() {
        data = [objid: entity?.refid];
        super.init();
    }
    
    def close() {
        return '_close';
    }
}

