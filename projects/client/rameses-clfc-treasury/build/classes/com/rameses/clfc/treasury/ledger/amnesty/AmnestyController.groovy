package com.rameses.clfc.treasury.ledger.amnesty

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class AmnestyController
{   
    @Binding
    def binding;
    
    @Service('LedgerAmnestyService')
    def service;
    
    @ChangeLog
    def changeLog
    
    def opener, previtems, preventity, entity;
    def mode = 'read', recommendationmode = 'read';
    
    Map createEntity() {
        return [
            objid   : 'AM' + new UID(),
            txnstate: 'DRAFT',
            txnmode : 'ONLINE'
        ];
    }
    
    void create() {
        entity = createEntity();
        mode = 'create';
        recommendationmode = 'read';
    }
    
    void open() {
        entity = service.open(entity);
        mode = 'read';
        recommendationmode = 'read';
    }
    
    void edit() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        previtems = copyList(entity.items);
        mode = 'edit';
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
    
    def cancel() {
        if (mode == 'edit') {
            if (changeLog.hasChanges()) {
                changeLog.undoAll();
                changeLog.clear();
            }
            
            if (preventity) {
                entity = preventity;
            }
            
            if (previtems) {
                entity.items = [];
                entity.items.addAll(previtems);
            }
            entity._addeditems = [];
            entity._removeditems = [];
            
            mode = 'read';
            binding?.refresh('opener');
            return null;
        }
        return '_close';
    }
    
    def selectedOption;
    def optionsHandler = [
        fetchList: { 
            def xlist = Inv.lookupOpeners('amnesty:plugin');
            def list = [], props;
            xlist?.each{ o->
                props = o.properties;
                if (!entity.availed && props.reftype=='availedamnesty') {
                    /* do nothing */
                } else {
                    list << [caption: o.caption, type: props.reftype, index: props.index]
                }
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
            entity              : entity,
            mode                : mode,
            recommendationmode  : recommendationmode
        ];
        def op = Inv.lookupOpener('amnesty:' + selectedOption?.type, params);
        if (!op) return null;
        return op;
    }
    
    void save() {
        def res = validate(entity);
        if (res.haserror == true) throw new Exception(res.msg);
        
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        if (mode == 'create') {
            entity = service.create(entity);
        } else if (mode == 'edit') {
            entity = service.update(entity);
        }
        mode = 'read';
    }
    
    def validate( data ) {
        def msg = '';
        def flag = false;
        if (!data?.borrower) msg += 'Borrower is required.\n';
        if (!data?.remarks) msg += 'Remarks is required.\n';
        if (msg) flag = true;//throw new Exception(msg);
        return [msg: msg, haserror: flag];
    }
    
    void submitForApproval() {
        if (!MsgBox.confirm('You are about to submit this document for approval. Continue?')) return;
        
        entity = service.submitForApproval(entity);
    }
    
    void returnDocument() {
        if (!MsgBox.confirm('You are about to return this document. Continue?')) return;
        
        entity = service.returnDocument(entity);
    }
    
    void editRecommendation() {
        previtems = copyList(entity.items);
        recommendationmode = 'edit';
    }
    
    void cancelRecommendation() {
        if (previtems) {
            entity?.items = [];
            entity?.items.addAll(previtems);
        }
        
        entity._addeditems = [];
        entity._removeditems = [];
        recommendationmode = 'read';
        binding?.refresh('opener');
    }
    
    void saveRecommendation() {
        if (!MsgBox.confirm('You are about to remove recommendations. Continue?')) return;
        
        entity = service.saveRecommendations(entity);
        recommendationmode = 'read';
    }
    
    def close() {
        return '_close';
    }
    
    def getStatus() {
        def str = '';
        if (entity?.txnmode) str += entity?.txnmode;
        if (str) str += ' - ';
        if (entity?.txnstate) str += entity?.txnstate;
        return str;
    }
}

