package com.rameses.clfc.followup.result

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class FollowupResultController
{
    @Binding
    def binding;
    
    @Service('LoanLedgerFollowupResultService')
    def service;
    
    @Service('DateService')
    def dateSvc;
    
    def entity, preventity, prevoverriderequest;
    def mode = 'read';
    
    Map createEntity() {
        return [
            objid   : 'FR' + new UID(), 
            txnstate: 'DRAFT', 
            txndate : dateSvc.getServerDateAsString()
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
    
    def close() {
        return '_close';
    }
    
    void edit() {
        preventity = [:];
        if (entity) preventity.putAll(entity);
        
        prevoverriderequest = [];
        if (entity?.overriderequest) {
            def item;
            entity?.overriderequest?.each{ o->
                item = [:];
                item.putAll(o);
                prevoverriderequest << item;
            }
        }
        mode = 'edit';
    }
    
    def cancel() {
        if (mode=='edit') {
            if (!MsgBox.confirm('Cancelling will undo changes made. Continue?')) return;
            
            if (preventity) {
                entity = [:];
                entity.putAll(preventity);
            }
            
            if (prevoverriderequest) {
                entity?.overriderequest = [];
                entity?.overriderequest.addAll(prevoverriderequest);
            }
            
            entity.remove('_removerequest');
            
            mode = 'read';
            binding?.refresh('opener');
            return null;
        }
        return '_close';
    }
    
    def validate( data ) {
        
        def msg = '';
        def flag = false;
        if (!data?.txndate) msg += 'Follow-up Date is required.\n';
        if (!data?.borrower) msg += 'Borrower is required.\n';
        if (!data?.remarks) msg += 'Remarks is required.\n';
        
        switch (data?.amnestyoption) {
            case 'avail'    : if (!data?.availedamnesty?.objid) msg += 'Amnesty availed is required.\n'; break;
            case 'reject'   : if (!data?.rejectedamnesty?.items) msg += 'At lest 1 amnesty to reject is required.\n'; break;
        }
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
        binding?.refresh('opener');
    }
    
    def selectedOption;
    def optionsHandler = [
        fetchList: { 
            def xlist = Inv.lookupOpeners('followup:result:plugin');
            def list = [], props;
            xlist?.each{ o->
                props = o.properties;
                list << [caption: o.caption, type: props.reftype, index: props.index]
            }
            
            list?.sort{ it.index }
            return list;
        },
        onselect: { o->  
            binding?.refresh('opener');
        }
    ] as ListPaneModel; 
    
    def getOpener() {
        if (!selectedOption) return null;
        
        def params = [
            entity  : entity,
            mode    : mode
        ];
        def op = Inv.lookupOpener('followup:result:' + selectedOption?.type, params);
        if (!op) return null;
        return op;
    }
    
    void confirm() {
        if (!MsgBox.confirm('You are about to confirm this document. Contiue?')) return;
        
        entity = service.confirm(entity);
    }
}

