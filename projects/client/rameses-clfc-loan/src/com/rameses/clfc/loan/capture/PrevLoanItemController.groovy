package com.rameses.clfc.loan.capture

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class PrevLoanItemController {

    def entity, mode = 'read';
    def preventity, handler;
    
    void init() {
        entity = [:];
    }
    
    void open() {
        preventity = entity;
        entity = [:];
        entity.putAll(preventity);
    }
    
    def doOk() {
        if (handler) handler(entity);
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}

