package com.rameses.clfc.loan.collateral;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.clfc.util.*;
import com.rameses.clfc.loan.controller.*;

class RealPropertyFormController extends PopupMasterController
{
    def classifications = LoanUtil.propertyClassificationTypes;
    def uomList = LoanUtil.propertyUomTypes;
    def modesOfAcquisition = LoanUtil.propertyModeOfAcquisitionTypes;
    
    def createEntity() {
        return [attachments: [], ci: [:]];
    }
    
    void afterOpen( data ) {
        if (!data.ci) data.ci = [:];
        if (data.attachments == null) data.attachments = [];    }
    
}
