package com.rameses.clfc.audit.amnesty.capture.fix

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class FixAmnestyController
{
    @Binding
    def binding;
    
    @ChangeLog
    def changeLog;
    
    @Service('LedgerAmnestyCaptureFixService')
    def service;
    
    def entity, mode = 'read';
    def preventity, prevamnesty;
    
    Map createEntity() {
        return [
            objid   : 'CAF' + new UID(),
            txnstate: 'DRAFT',
            txnmode : 'CAPTURE'
        ];
    }
    
    void create() {
        entity = createEntity();
        mode = 'create';
    }
    
    void open() {
        entity = service.open(entity);
        mode = 'read';
    }
    
    void edit() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        prevamnesty = [:];
        if (entity?.availed) {
            prevamnesty.putAll(entity.availed);
        }
        
        mode = 'edit';
    }
    
    def validate( data ) {
        def msg = '';
        def flag = false;
        if (!data?.borrower) msg += 'Borrower is required.\n';
        if (!data?.remarks) msg += 'Remarks is required.\n';
        
        def av = data?.availed;
        if (!av?.dtstarted) msg += 'Start Date is required.\n';
        if (!av?.amount) msg += 'Fix Amount is required.\n';
        //println 'withmd ' + av?.withmd;
        if (av?.withmd == 1) {
            if (av?.usedate == 0 || !av?.usedate) {
                if (!av?.day == null) msg += 'Day is required.\n';
                if (!av?.month == null) msg += 'Month is required.\n';
            } else if (av?.usedate == 1 && !av?.date) {
                msg += 'Date is required.\n';
            }            
        }
        /*
        if (!data?.availed?.dtstarted) msg += 'Start Date is required.\n';
        if (!data?.availed?.amount) msg += 'Fix Amount is required.\n';
        */
        if (msg) flag = true;//throw new Exception(msg);
        return [msg: msg, haserror: flag];
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
        binding?.refresh();
    }
    
    def close() {
        return '_close';
    }
    
    def cancel() {
        if (mode=='edit') {
            if (!MsgBox.confirm('Cancelling will undo changes made. Do you want to continue?')) return;
            
            if (changeLog.hasChanges()) {
                changeLog.undoAll();
                changeLog.clear();
            }
            
            if (preventity) {
                entity = preventity;
            }
            
            if (prevamnesty) {
                entity.availed = prevamnesty;
            }
            
            mode = 'read';
            binding?.refresh('opener');
            return null;
        }
        return '_close';
    }
    
    def selectedOption;
    def optionsHandler = [
        fetchList: { 
            def xlist = Inv.lookupOpeners('capture:amnesty:fix:plugin');
            def list = [], props;
            xlist?.each{ o->
                props = o.properties;
                if (!entity.availed && props.reftype=='availedamnesty') {
                    //do nothing
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
    
    
    def getStatus() {
        def str = '';
        if (entity?.txnmode) str += entity?.txnmode;
        if (str) str += ' - ';
        if (entity?.txnstate) str += entity?.txnstate;
        return str;
    }
    
    def getOpener() {
        if (!selectedOption) return null;
        def params = [
            entity              : entity,
            mode                : mode
        ];
        def op = Inv.lookupOpener('capture:amnesty:fix:' + selectedOption?.type, params);
        if (!op) return null;
        return op;
    }

    void submitForVerification() {
        if (!MsgBox.confirm('You are about to submit this document for verification. Continue?')) return;
        
        entity = service.submitForVerification(entity);
    }
    
    void verify() {
        if (!MsgBox.confirm('You are about to verify this document. Continue?')) return;
        
        entity = service.verify(entity);
    }
    
    def sendBack() {
        //if (!MsgBox.confirm('You are about send back this document. Continue?')) return;
        
        def handler = { remarks->
            try {
                entity.sendbackremarks = remarks;
                entity = service.sendBack(entity);

                binding?.refresh();
            } catch (Throwable t) {
                MsgBox.err(t.message);
            }
        }
        def op = Inv.lookupOpener('remarks:create', [title: 'Reason for send back', handler: handler]);
        if (!op) return null;
        return op;
    }
    
    def viewSendBackRemarks() {
        def op = Inv.lookupOpener("remarks:open", [title: "Reason for Send Back", remarks: entity.sendbackremarks]);
        if (!op) return null;
        return op;
    }
}

