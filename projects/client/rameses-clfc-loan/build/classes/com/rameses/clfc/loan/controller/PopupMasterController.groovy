package com.rameses.clfc.loan.controller;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;

public class PopupMasterController
{
    @Binding
    def binding

    @ChangeLog
    def changeLog
    
    def entity = [:];
    def mode = 'read';
    def state;
    def handler;
    def caller;

    public def createEntity() {
        return [:]
    }

    public final init() {
        entity = createEntity()
    }
    
    public void afterCreate(data) {}
    public void afterOpen( data ) {}
    
    public def create() {
        init();
        mode = 'create';
        afterCreate(entity); 
        return null;
    }
    
    public def open() {
        entity = copyMap( entity );
        afterOpen( entity );
        return null; 
    }
    
    public def doOk() {
        if( handler ) handler(entity)
        if( caller ) caller.binding.refresh('htmlview');
        return "_close"
    }

    public def doCancel() {
        if( mode.toString().matches('create|edit')) {
            if( !MsgBox.confirm("Changes will be discarded. Continue?") ) return null

            if( changeLog.hasChanges() ) {
                changeLog.undoAll()
                changeLog.clear()
            }
        }
        return "_close"
    }
    
    public def copyMap( src ) {
        if (src instanceof Map) {
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
        return null;
    }
    
    public def copyList( src ) {
        if (src instanceof List) {
            def list = [];
            src?.each{ o->
                if (o instanceof Map) {
                    list << copyMap( o );
                } else if (o instanceof List) {
                    list << copyList( o );
                } else {
                    list << o;
                }
            }
            return list;
        }
        return null;
    }
}