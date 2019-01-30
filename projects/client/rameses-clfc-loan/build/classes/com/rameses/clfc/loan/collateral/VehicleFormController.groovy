package com.rameses.clfc.loan.collateral;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osirsi2.client.*;
import com.rameses.clfc.util.*;
import com.rameses.clfc.loan.controller.*;

class VehicleFormController extends PopupMasterController
{
    def kinds = LoanUtil.vehicleKinds;
    def uses = LoanUtil.vehicleUses;
    def orcr = true

    @PropertyChangeListener
    def listener = [
        "orcr": {o->
            if(o == false) entity.orcr = [:];
        }
    ]
    
    def createEntity() {
        return [orcr: [:], attachments: [], ci: [:]];
    }
             
   void afterOpen( data ) {
        if (data.orcr == null) data.orcr = [:];
        if (!data.orcr.crno) orcr = false;
        if (!data.ci) data.ci = [:];
        if (data.attachments == null) data.attachments = [];
    }
}
