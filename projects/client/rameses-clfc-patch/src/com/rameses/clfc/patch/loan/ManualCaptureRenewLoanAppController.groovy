package com.rameses.clfc.loan.capture

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class ManualCaptureRenewLoanAppController {
	
    @Caller
    def caller;
    
    @Binding
    def binding;
    
    @Service("LoanAppCaptureRenewService")
    def service;
    
    def entity, mode = 'read';
    def preventity;
    
    void create() {
        entity = service.initEntity([preventityid: entity.objid]);
        mode = 'create';
    }
    
    void open() {
        entity = service.open(entity);
        mode = 'read';
    }
    
    def selectedOption;
    def optionsHandler = [
        fetchList: {
            def xlist = Inv.lookupOpeners('loanapp:capture:renew:plugin');
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
        if (!selectedOption) return;
        
        def inv = 'loanapp:capture:renew:' + selectedOption?.type;
        def op = Inv.lookupOpener(inv, [entity: entity, mode: mode]);
        if (!op) return null;
        return op;
    }
    
    def getStatus() {
        if (!entity.txnstate) return;
        
        return entity.txnstate;
    }
    
    def cancel() {
        if (mode=='edit') {
            
            if (!MsgBox.confirm("Cancelling will undo changes made. Continue?")) return;
            
            if (preventity) {
                entity = preventity;
            }
            
            mode = 'read';
            binding?.refresh();
            
            return null;
        }
        return '_close';
    }
    
    def close() {
        return '_close';
    }
    
    void edit() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
            
            def item, list = [];
            entity?.previousloans?.each{ o->
                item = [:];
                item.putAll(o);
                list << item;
            }
            preventity.previousloans = list;
        }
        mode = "edit";
        binding?.refresh();
    }
    
    
    def validate( data ) {
        def msg = '';
        def flag = false;
        if (!data?.appno) msg += 'Loan No. is required.\n';
        //if (!data?.loantype) msg += 'Loan Type is required.\n';
        //if (!data?.apptype) msg += 'App. Type is required.\n';
        if (!data?.clienttype) msg += 'Client Type is required.\n';
        if (data?.clienttype=='MARKETED' && !data.marketedby) msg += 'Interviewed by is required.\n';
        if (!data?.producttype) msg += 'Product Type is required.\n';
        if (!data?.amount) msg += 'Amount Released is required.\n';
        if (!data?.dtreleased) msg += 'Release Date is required.\n';
        if (!data.route) msg += 'Route is required.\n';
        if (!data.purpose) msg += 'Purpose of loan is required.\n';
        
        if (msg) flag = true;//throw new Exception(msg);
        return [msg: msg, haserror: flag];
    }
    
    void save() {
        def res = validate(entity);
        if (res.haserror) {
            throw new RuntimeException(res.msg);
        }
        
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        if (mode=='create') {
            entity = service.create(entity);
        } else if (mode=='edit') {
            entity = service.update(entity);
        }
        
        mode = 'read';
        EventQueue.invokeLater({
            binding?.refresh();
            caller?.reload();
        });
    }
    
    void submitForAssessment() {
        entity = service.submitForAssessment(entity);
        EventQueue.invokeLater({
            binding?.refresh();
            caller?.reload();
        });
    }
    
    void returnFromAssessment() {
        entity = service.returnFromAssessment(entity);
        EventQueue.invokeLater({
            binding?.refresh();
            caller?.reload();
        });
    }
    
    void assess() {
        entity = service.assess(entity);
        EventQueue.invokeLater({
            binding?.refresh();
            caller?.reload();
        });
    }
    
    void submitForVerification() {
        entity = service.submitForVerification(entity);
        EventQueue.invokeLater({
            binding?.refresh();
            caller?.reload();
        });
    }
    
    void returnFromForVerification() {
        entity = service.returnFromForVerification(entity);
        EventQueue.invokeLater({
            binding?.refresh();
            caller?.reload();
        });
    }
    
    void verify() {
        entity = service.verify(entity);
        EventQueue.invokeLater({
            binding?.refresh();
            caller?.reload();
        });
    }
}

