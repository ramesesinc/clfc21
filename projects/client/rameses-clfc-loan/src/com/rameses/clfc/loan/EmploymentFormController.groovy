package com.rameses.clfc.loan

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;
import com.rameses.clfc.loan.controller.PopupMasterController;

class EmploymentFormController extends PopupMasterController
{
    def statusList = LOV.LOAN_EMP_STATUS;
    
    def createEntity() {
        return [ objid:'EMP'+new UID() ];
    }
    
    def open() {
        entity = copyMap( entity );
    }
}

