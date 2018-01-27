package com.rameses.clfc.treasury.ledger.amnesty.fix

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class AmnestyFixController 
{
    @Service('LedgerAmnestyFixService')
    def service;
    
    @Binding
    def binding;
    
    def entity, mode = 'read';
    def preventity, amnesty;
    
    void open() {
        entity = service.open(entity);
        mode = 'read';
    }
    
    void edit() {
        preventity = [:];
        preventity.putAll(entity);
        
        mode = 'edit';
    }
    
    void save() {
        if (mode == 'create') {
            entity = service.create(entity);
        } else if (mode == 'edit') {
            entity = service.update(entity);
        }
        
        mode = 'read';
        binding?.refresh();
    }
    
    def cancel() {
        if (mode == 'edit') {
            entity = preventity;
            
            mode = 'read';
            binding?.refresh();
            return null;
        }
        return '_close';
    }
    
    def close() {
        return '_close';
    }
    
    def getLookupAmnesty() {
        def params = [
            onselect    : { o->
                def params = [objid: o.objid, txndate: entity?.txndate];
                def item = service.getAmnestyRecommendationInfo(params);
                if (item) {
                    amnesty = item;
                    entity.refid = item.objid;
                    entity.refno = item.refno;
                    entity.description = item.description;
                    entity.dtstarted = item.dtstarted;
                    entity.dtended = item.dtended;
                }
                
                binding?.refresh();
                /*
                def params = [objid: o.objid, txndate: entity?.txndate];
                def item = service.getAmnestyRecommendationInfo(params);
                if (item) {
                    entity.availedamnesty = item;
                    availedamnesty = item;
                    binding?.refresh();
                }
                */
            },
            loanappid   : entity?.loanapp?.objid,
            txndate     : entity?.txndate
        ]
        def op = Inv.lookupOpener('followup:amnesty:lookup', params);
        if (!op) return null;
        return op;
    }
    
    void removeAmnesty() {
        entity.refid = null;
        entity.refno = null;
        entity.dtstarted = null;
        entity.dtended = null;
        entity.description = null;
        amnesty = null;
        
        binding?.refresh();
    }
    
    void submitForVerification() {
        if (!MsgBox.confirm('You are about to submit this document for verification. Continue?')) return;
        
        entity = service.submitForVerification(entity);
        binding?.refresh();
    }
    
    def sendBack() {
        if (!MsgBox.confirm('You are about to send back this document. Continue?')) return;
        
        def params = [
            title   : 'Send Back remarks',
            handler : { remarks->
                try {
                    entity.sendbackremarks = remarks;
                    entity = service.sendBack(entity);
                    
                    binding?.refresh();
                } catch (Throwable t) {
                    MsgBox.err(t.message);
                }
            }
        ]
        
        def op = Inv.lookupOpener('remarks:create', params);
        if (!op) return null;
        return op;
    }
    
    def viewSendBackRemarks() {
        def params = [
            title   : 'Reason for Send Back',
            remarks : entity?.sendbackremarks
        ];
        def op = Inv.lookupOpener('remarks:open', params);
        if (!op) return null;
        return op;
    }
    
    void verify() {
        if (!MsgBox.confirm('You are about to verify this document. Continue?')) return;
        
        entity = service.verify(entity);
        binding?.refresh();
    }
    
    def getStatus() {
        def str = '';
        if (entity?.txnmode) str += entity?.txnmode;
        if (str) str += ' - ';
        if (entity?.txnstate) str += entity?.txnstate;
        return str;
    }
}

