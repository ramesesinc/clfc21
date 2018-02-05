package com.rameses.clfc.producttype2.handler

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class DecimalHandler {

    def entity, mode = 'read';
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.value) entity.value = 0.00;
    }
}

