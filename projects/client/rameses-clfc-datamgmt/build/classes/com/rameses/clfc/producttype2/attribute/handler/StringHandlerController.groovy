package com.rameses.clfc.producttype2.attribute.handler

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;

class StringHandlerController {

    def entity, mode = 'read';
    
    void init() {
        if (!entity) entity = [:];
    }
}

