package com.rameses.clfc.loan.collateral;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.clfc.loan.controller.*;

class ApplianceFormController extends PopupMasterController
{    
    def createEntity() {
        return [attachments: [], ci: [:]];
    }
    
    void afterOpen( data ) {
        if (!data.ci) data.ci = [:];
        if (data.attachments == null) data.attachments = [];
    }
}
