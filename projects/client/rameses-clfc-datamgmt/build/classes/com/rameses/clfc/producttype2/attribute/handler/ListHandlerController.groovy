package com.rameses.clfc.producttype2.attribute.handler

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class ListHandlerController {

    def entity, mode = 'read';
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.defaultvalue) entity.defaultvalue = [];
        listHandler?.reload();
    }
    
    def selectedItem;
    def listHandler = [
        fetchList: {
            if (!entity.defaultvalue) entity.defaultvalue = [];
            return entity.defaultvalue;
        },
        onAddItem: { o->
            entity.defaultvalue << o;
        }
    ] as EditorListModel;
}

