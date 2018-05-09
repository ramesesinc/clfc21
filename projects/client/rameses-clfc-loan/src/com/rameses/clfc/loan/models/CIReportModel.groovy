package com.rameses.clfc.loan.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;

class CIReportController extends PopupMasterController {
    public def createEntity() {
        if( entity ) return entity;
        return [:]
    }
}