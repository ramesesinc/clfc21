package com.rameses.clfc.loan

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;
import com.rameses.clfc.loan.controller.PopupMasterController;

class OtherSourcesOfIncomeFormController extends PopupMasterController
{
    def createEntity() {
        return [ objid:'OI'+new UID() ]
    }	
    
    def open() {
        entity = copyMap( entity );
    }
}

