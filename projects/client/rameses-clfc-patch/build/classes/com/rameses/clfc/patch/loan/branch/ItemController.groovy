package com.rameses.clfc.patch.loan.branch

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;
import com.rameses.util.*;

class ItemController {

    @Binding
    def binding;
        
    def entity, handler, mode = 'read';
    
    void create() {
        entity = [objid: 'LBD' + new UID(), unpaid: [:]];
        mode = 'create';
    }
    
    void edit() {
        println 'entity ' + entity; 
        entity = copyMap(entity);
        //entity = service.getItemInfo(entity);
        mode = 'edit';
    }
    
    void open() {
        entity = copyMap(entity);
        //entity = service.getItemInfo(entity);
        mode = 'read';
    }
    
    def copyMap( src ) {
        def data = [:];
        data.putAll(src);
        
        if (data.info) {
            def info = data.remove('info');
            info = ObjectDeserializer.getInstance().read(info);
            data.putAll(info);
        }
        
        return data;
    }
    
    def productTypeLookup = Inv.lookupOpener('reconstruct:branchloan:producttype:lookup', [
         onselect: { o->
             entity.producttype = o;
             binding?.refresh();
         }
    ]);

    def routeLookup = Inv.lookupOpener('reconstruct:branchloan:route:lookup', [
         onselect: { o->
             entity.route = o;
             binding?.refresh();
         }
    ]);
    
    def doOk() {
        if (handler) handler(entity);
        return '_close';
    }
    
    def doCancel() {
        return '_close';
    }
}

