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
    
    def entity, mode = 'read', filingmode = 'read', courtmode = 'read', postingmode = 'read';
    def preventity, prevfilingdocs, prevfilingfees;
    def prevcourtdocs, prevcourtfees, prevcourtconditions;
    def prevfilinginfo, prevcourtinfo, prevpostinginfo;
    def prevpostingheaders;
    def defaultvarlist, termlist;
    
    
    Map createEntity() {
        return [objid: 'SMC' + new UID(), txnstate: 'DRAFT'];
    }
    
    void create() {
        entity = createEntity();
        mode = 'create';
        filingmode = 'read';
        courtmode = 'read';
        postingmode = 'read';
        defaultvarlist = service.getDefaultVarList();
        termlist = service.getTermList();
        //smcmode = 'read';
    }
    
    void open() {
        entity = service.open(entity);
        mode = 'read';
        filingmode = 'read';
        courtmode = 'read';
        postingmode = 'read';
        defaultvarlist = service.getDefaultVarList();
        termlist = service.getTermList();
        //smcmode = 'read';
    }
    
    def close() {
        return '_close';
    }
    
    def cancel() {
        if (mode=='edit' || filingmode=='edit' || courtmode=='edit' || postingmode == 'edit') {
            if (!MsgBox.confirm('Cancelling will undo changes made. Continue?')) return;
            
            if (preventity) {
                entity = preventity;
            }
            
            if (mode == 'edit') {
                mode = 'read';
            }
            
            if (filingmode == 'edit') {
                if (prevfilinginfo) {
                    entity?.filinginfo = prevfilinginfo;
                }
                
                if (prevfilingdocs) {
                    entity?.filinginfo?.docs = [];
                    entity?.filinginfo?.docs.addAll(prevfilingdocs);
                }

                if (prevfilingfees) {
                    entity?.filinginfo?.fees = [];
                    entity?.filinginfo?.fees.addAll(prevfilingfees);
                }
                filingmode = 'read';
            }
            
            if (courtmode == 'edit') {
                if (prevcourtinfo) {
                    entity?.courtinfo = prevcourtinfo;
                }
                
                if (prevcourtdocs) {
                    entity?.courtinfo?.docs = [];
                    entity?.courtinfo?.docs.addAll(prevcourtdocs);
                }

                if (prevcourtfees) {
                    entity?.courtinfo?.fees = [];
                    entity?.courtinfo?.fees.addAll(prevcourtfees);
                }

                if (prevcourtconditions) {
                    entity?.courtinfo?.conditions = [];
                    entity?.courtinfo?.conditions.addAll(prevcourtconditions);
                }
                
                courtmode = 'read';
            }
            
            if (postingmode == 'edit') {
                if (prevpostinginfo) {
                    entity?.postinginfo = prevpostinginfo;
                }
                
                if (prevpostingheaders) {
                    entity?.postinginfo?.headers = [];
                    entity?.postinginfo?.headers.addAll(prevpostingheaders);
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
        mode = 'edit';
    }
    
    void editFiling() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        prevfilinginfo = [:];
        if (entity?.filinginfo) {
            prevfilinginfo.putAll(entity?.filinginfo);
        }
        
        prevfilingdocs = [];
        if (entity?.filinginfo?.docs) {
            prevfilingfees = copyList(entity?.filinginfo?.docs);
        }
        
        prevfilingfees = [];
        if (entity?.filinginfo?.fees) {
            prevfilingfees = copyList(entity?.filinginfo?.fees);
        }
        
        filingmode = 'edit';
    }
    
    void editCourtDecision() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        prevcourtinfo = [:];
        if (entity?.courtinfo) {
            prevcourtinfo.putAll(entity?.courtinfo);
        }
        
        prevcourtdocs = [];
        if (entity?.courtinfo?.docs) {
            prevcourtdocs = copyList(entity?.courtinfo?.docs);
        }
        
        prevcourtfees = [];
        if (entity?.courtinfo?.fees) {
            prevcourtfees = copyList(entity?.courtinfo?.fees);
        }
        
        prevcourtconditions = [];
        if (entity?.courtinfo?.conditions) {
            prevcourtconditions = copyList(entity?.courtinfo?.conditions);
        }
        
        courtmode = 'edit';
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
    
    void saveFiling() {
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        entity = service.saveFiling(entity);
        filingmode = 'read';
    }
    
    void saveCourtDecision() {
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        entity = service.saveCourtDecision(entity);
        courtmode = 'read';
    }
    
    void savePosting() {
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        entity = service.savePosting(entity);
        postingmode = 'read';
    }
    
    /*
    void saveSmc() {
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        entity = service.saveSmc(entity);
        smcmode = 'read';
    }
    */
    
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
        onselect: { o-> 
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
            defaultvarlist  : defaultvarlist,
            filingmode      : filingmode,
            courtmode       : courtmode,
            postingmode     : postingmode,
            termList        : termlist
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
    
    void submitForCourtDecision() {
       if (!MsgBox.confirm('You are about to submit this document for court decision. Continue?')) return;
       
        entity = service.submitForCourtDecision(entity);
    }
    
    void courtDecided() {
        if (!MsgBox.confirm('You are about to confirm court decision for this document. Continue?')) return;
        
        entity = service.courtDecided(entity);
    }
    
    void dismissed() {
        if (!MsgBox.confirm('You are about to dismiss this document. Continue?')) return;
        
        entity = service.dismissed(entity);
    }
    
    void submitForVerification() {
        if (!MsgBox.confirm('You are about to submit this document for verification. Continue?')) return;
        
        entity = service.submitForVerification(entity);
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
        
        entity = service.verifyDocument(entity);
    }
    
    
    def getStatus() {
        def str = '';
        if (entity?.txnmode) str += entity?.txnmode;
        if (str) str += ' - ';
        if (entity?.txnstate) str += entity?.txnstate;
        if (entity?._issendback == true) str += ' (SEND BACK)';
        return str;
    }
}

