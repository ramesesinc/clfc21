package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class ItemController 
{
    def entity, mode = 'read';
    def saveHandler;
    def preventity, previtems;
    
    String getPrefix() {
        return 'ITM';
    }
    
    Map createEntity() {
        return [objid: getPrefix() + new UID(), items: []];
    }
    
    void create() {
        entity = createEntity();
        mode = 'create';
    }
    
    void open() {
        preventity = [:];
        preventity.putAll(entity);
        
        previtems = [];
        def item;
        entity?.items?.each{ o->
            item = [:];
            item.putAll(o);
            previtems << item;
        }
        mode = 'read';
        println 'open';
    }    
    
    def doOk() {
        if (saveHandler) saveHandler(entity);
        return '_close';
    }
    
    def doCancel() {
        if (preventity) {
            entity.clear();
            entity.putAll(preventity);
        }
        
        if (previtems) {
            entity.items = [];
            entity.items?.addAll(previtems);
        }
        
        return '_close';
    }
}

