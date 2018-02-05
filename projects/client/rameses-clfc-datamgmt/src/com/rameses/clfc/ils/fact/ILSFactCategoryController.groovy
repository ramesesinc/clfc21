package com.rameses.clfc.ils.fact

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class ILSFactCategoryController {

    def entity, mode = 'read';
    def handler;
    
    void create() {
        entity = [objid: "IRFC" + new UID()];
    }
    
    def doOk() {
        if (handler) handler(entity);
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}

