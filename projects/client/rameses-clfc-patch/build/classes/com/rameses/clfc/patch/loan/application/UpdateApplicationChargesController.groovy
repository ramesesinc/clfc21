package com.rameses.clfc.patch.loan.application

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;

class UpdateApplicationChargesController {

    @Binding
    def binding;
    
    String title = "Update Application";
    
    def entity;
    def page = "default";
    def applicationLookup = Inv.lookupOpener("update:application:lookup", [
         onselect: { o->
             entity = o;
             binding?.refresh();
         }
    ]);

    void init() {
        page = "default";
        entity = [:];
    }
    
    def close() {
        return "_close";
    }
    
    def next() {
        page = "main";
        return page;
    }
    
    def back() {
        page = "default";
        return page;
    }
    
    
}

