package com.rameses.clfc.audit.amnesty.capture.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class SMCAmnestyController 
{
    @Binding
    def binding;
    
    @Service('LedgerAmnestyCaptureSMCService')
    def service;
    
    def entity, mode = 'read', postingmode = 'read';
    def prevpostinginfo, prevpostingheaders;
    def preventity, previtems;
    def defaultvarlist, termlist;
    
    Map createEntity() {
        return [objid: 'SMC' + new UID(), txnstate: 'DRAFT'];
    }
    
    void create() {
        entity = createEntity();
        mode = 'create';
        postingmode = 'read';
        defaultvarlist = service.getDefaultVarList();
        termlist = service.getTermList();
    }
    
    void open() {
        entity = service.open(entity);
        mode = 'read';
        postingmode = 'read';
        defaultvarlist = service.getDefaultVarList();
        termlist = service.getTermList();
    }
    
    def close() {
        return '_close';
    }
    
    def cancel() {
        if (mode == 'edit' || postingmode=='edit') {
            if (!MsgBox.confirm('Cancelling will undo changes made. Continue?')) return;
            
            if (preventity) {
                entity = preventity;
            }
            
            if (mode == 'edit') {
                if (previtems) {
                    entity?.conditions = [];
                    entity?.conditions.addAll(previtems);
                }
                mode = 'read';
            }
            
            if (postingmode == 'edit') {
                if (prevpostinginfo) {
                    entity.postinginfo = prevpostinginfo;
                }
                
                if (prevpostingheaders) {
                    entity?.postinginfo?.postingheaders = [];
                    entity?.postinginfo?.postingheaders.addAll(prevpostingheaders);
                }
                
                postingmode = 'read';
            }
            
            return null;
        }
        return '_close';
    }
    
    void edit() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        previtems = [];
        if (entity.conditions) {
            previtems.addAll(entity.conditions);
        }
        
        mode = 'edit';
    }
    
    void editPosting() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
                
        prevpostinginfo = [:];
        if (entity?.postinginfo) {
            prevpostinginfo.putAll(entity?.postinginfo);
        }
        
        prevpostingheaders = [];
        if (entity?.postinginfo?.headers) {
            prevpostingheaders = copyList(entity?.postinginfo?.headers);
        }
        
        postingmode = 'edit';
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
    
    def selectedOption;
    def optionsHandler = [
        fetchList: { 
            def xlist = Inv.lookupOpeners('capture:amnesty:smc:plugin');
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
            entity          : entity,
            mode            : mode,
            postingmode     : postingmode,
            termList        : termlist,
            defaultvarlist  : defaultvarlist
        ];
        
        def op = Inv.lookupOpener('capture:amnesty:smc:' + selectedOption?.type, params);
        if (!op) return null;
        return op;
    }
    
    private def validate( data ) {
        def msg = '';
        def flag = false;
        if (!data?.borrower) msg += 'Borrower is required.\n';
        if (!data?.courtinfo?.dtstarted) msg += 'Effectivity Date is required.\n';
        /*
        if (!data?.dtstarted) msg += 'Start Date is required.\n';
        if (!data?.amount) msg += 'Amount is required.\n';
        if (data?.nomaturity == null) msg += 'Term is required.\n';
        if (data?.nomaturity == 0) {
            if (data?.usedate == null) msg += 'Term is required.\n';
            if (data?.usedate == 1 && !data?.date) msg += 'Date is required.\n';
            if (data?.usedate == 0 && (!data?.day || !data?.month)) msg += 'Day/Month is required.\n';
        }
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
    }
    
    void savePosting() {
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        entity = service.savePosting(entity);
        postingmode = 'read';
    }
    
    void submitForApproval() {
        if (!MsgBox.confirm('You are about to submit this document for approval. Continue?')) return;
        
        entity = service.submitForApproval(entity);
    }
    
    void disapprove() {
        if (!MsgBox.confirm('You are about to disapprove this document. Continue?')) return;
        
        entity = service.disapprove(entity);
    }
    
    void approveDocument() {
        if (!MsgBox.confirm('You are about to approve this document. Continue?')) return;
        
        entity = service.approveDocument(entity);
    }
    
    void submitForVerification() {
        if (!MsgBox.confirm('You are about to submit this document for verification. Continue?')) return;
        
        entity = service.submitForVerification(entity);
    }
    
    void verifyDocument() {
        if (!MsgBox.confirm('You are about to verify this document. Continue?')) return;
        
        entity = service.verifyDocument(entity);
    }
    
    def getStatus() {
        def str = '';
        if (entity?.txnmode) str += entity?.txnmode;
        if (str) str += ' - ';
        if (entity?.txnstate) str += entity?.txnstate;
        return str;
    }
}

