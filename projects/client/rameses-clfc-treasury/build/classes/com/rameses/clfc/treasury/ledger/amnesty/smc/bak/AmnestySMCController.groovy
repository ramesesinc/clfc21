package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class AmnestySMCController 
{
    @Binding
    def binding;
    
    @Service('LedgerAmnestySMCService')
    def service;
    
    def entity, mode = 'read', smcmode = 'read';
    def preventity, previtems;
    
    Map createEntity() {
        return [objid: 'SMC' + new UID(), txnstate: 'DRAFT'];
    }
    
    void create() {
        entity = createEntity();
        mode = 'create';
        smcmode = 'read';
    }
    
    void open() {
        entity = service.open(entity);
        mode = 'read';
        smcmode = 'read';
    }
    
    def close() {
        return '_close';
    }
    
    def cancel() {
        if (mode == 'edit') {
            
            mode = 'read';
            return null;
        }
        if (smcmode == 'edit') {
            
            smcmode = 'read';
            return null;
        }
        return '_close';
    }
    
    void edit() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        previtems = copyList(entity.payments);
        mode = 'edit';
    }
    
    void editSmc() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        previtems = copyList(entity.conditions);
        smcmode = 'edit';
    }
    
    def copyList( src ) {
        def list = [];
        def itm;
        src?.each{ o->
            itm = [:];
            itm.putAll(o);
            list << itm;
        }
        return list;
    }
    
    void save() {
        //def res = validate(entity);
        //if (res.haserror == true) throw new Exception(res.msg);
        
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        if (mode == 'create') {
            entity = service.create(entity);
        } else if (mode == 'edit') {
            entity = service.update(entity);
        }
        mode = 'read';
    }
    
    void saveSmc() {
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        entity = service.saveSmc(entity);
        smcmode = 'read';
    }
    
    def selectedOption;
    def optionsHandler = [
        fetchList: { 
            def xlist = Inv.lookupOpeners('amnesty:smc:plugin');
            def list = [], props;
            xlist?.each{ o->
                props = o.properties;
                list << [caption: o.caption, type: props.reftype, index: props.index]
                
            }
            
            list?.sort{ it.index }
            return list;
        },
        onselect: {o-> 
            //query.state = selectedOption?.state; 
            //reloadAll(); 
            binding?.refresh('opener');
        }
    ] as ListPaneModel; 
      
    def getOpener() {
        if (!selectedOption) return null;
        
        def params = [
            entity  : entity,
            mode    : mode,
            smcmode : smcmode
        ];
        
        def op = Inv.lookupOpener('amnesty:smc:' + selectedOption?.type, params);
        if (!op) return null;
        return op;
    }
    
    void submitForFiling() {
        if (!MsgBox.confirm('You are about to submit this document for filing. Continue?')) return;
        
        entity = service.submitForFiling(entity);
    }
    
    void fileDocument() {
        if (!MsgBox.confirm('You are about to file this document. Continue?')) return;
        
        entity = service.fileDocument(entity);
    }
    
    def getStatus() {
        def str = '';
        if (entity?.txnmode) str += entity?.txnmode;
        if (str) str += ' - ';
        if (entity?.txnstate) str += entity?.txnstate;
        return str;
    }
}

