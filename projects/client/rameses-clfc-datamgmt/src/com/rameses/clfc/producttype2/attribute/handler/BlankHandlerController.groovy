package com.rameses.clfc.producttype2.attribute.handler

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class BlankHandlerController {

    def entity;
    
    void init() {
        if (!entity) entity = [:];
        entity.defaultvalue = null;
    }
}

