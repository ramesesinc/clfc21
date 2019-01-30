package com.rameses.clfc.loan

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris3.client.*;
import com.rameses.osiris3.common.*
import java.rmi.server.UID;
import com.rameses.clfc.loan.controller.PopupMasterController;

class EducationalBackgroundFormController extends PopupMasterController
{
    def createEntity() {
        return [ objid:'EDUC'+new UID() ];
    }
    
    def open() {
        entity = copyMap( entity );
    }
}

