package com.rameses.clfc.patch.loan.detail

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.*;

class AppInfoController {
    
    @Binding
    def binding;

    @Caller
    def caller
    
    def entity;
    def appTypes = LoanUtil.appTypes;
    def routeLookupHandler = InvokerUtil.lookupOpener('route:lookup', [
        onselect: { o->
            entity.route = o;
            binding?.refresh();
        }
    ]);
    def productTypeLookup = Inv.lookupOpener("product_type:lookup", [
        onselect: { o->
            entity.producttype = o;
            //entity.producttype = caller?.service.getProductTypeInfo(o);
            binding?.refresh();
        }
    ]);
    
    void init() {
        if (!entity) entity = [:];
    }

    def getLoanTypes() {
        def list = caller?.service.getLoanTypes();
        if (!list) list = [];
        return list;
    }
    
    /*
    def getAppTypes() {
        println LoanUtil.appTypes;
    }
    */
}

