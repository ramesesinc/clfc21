package com.rameses.clfc.posting.header2

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris3.client.*;
import com.rameses.osiris3.common.*;
import java.rmi.server.UID;

class FieldUpdateController {

    def entity, mode = 'read';
    def handler;
    
    void init() {
        entity = [objid: "FTU" + new UID()];
    }
    
    void open() {
        entity = copyMap( entity );
    }
    
    def copyMap( src ) {
        def data = [:];
        src?.each{ k, v->
            if (v instanceof Map) {
                data[k] = copyMap( v );
            } else if (v instanceof List) {
                data[k] = copyList( v );
            } else {
                data[k] = v;
            }
        }
        return data;
    }
    
    def copyList( src ) {
        def list = [];
        src?.each{ 
            if (it instanceof Map) {
                list << copyMap( it );
            } else if (it instanceof List) {
                list << copyList( it );
            } else {
                list << it;
            }
        }
    }
    
    def doOk() {
        if (handler) handler( entity );
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
}

