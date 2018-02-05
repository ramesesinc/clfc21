package com.rameses.clfc.producttype2

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class ProductTypeController {

    @Caller
    def caller;
    
    @Binding
    def binding;
    
    @Service("NewLoanProductTypeService")
    def service;
    
    def entity, mode = "read";
    def preventity, prevgenattr, prevloanattr;
    def prevloanfields, prevpostingheader, prevpostingseq;
    
    void create() {
        entity = createEntity();
        mode = "create";
        binding?.refresh();
    }
    
    void open() {
        entity = service.open(entity);
        mode = "read";
        binding?.refresh();
    }
    
    Map createEntity() {
        def defaultgenattr = service.getDefaultGeneralInfoAttributes();
        def defaultloanattr = service.getDefaultLoanInfoAttributes();
        def data = [
            txnstate    : "DRAFT", 
            generalinfo : [
                title       : "GENERALINFO",
                attributes  : copyList(defaultgenattr),
                _addedattr  : copyList(defaultgenattr)
            ],
            loaninfo    : [
                title       : "LOANINFO",
                attributes  : copyList(defaultloanattr),
                _addedattr  : copyList(defaultloanattr)
            ],
            postinginfo : [
                title           : "POSTINGINFO",
                postingheader   : service.getDefaultPostingHeader()
            ]
        ];
        return data;
    }
    
    def copyList( src ) {
        def list = [];
        src?.each{ o->
            def d = [:];
            d.putAll(o);
            list << d;
        }
        
        return list;
    }
    
    def selectedOption;
    def optionListHandler = [
        getItems: {
            //return svc.getStates();
            def list = [];
            def l = Inv.lookupOpeners("producttype:plugin");
            l?.each{ o->
                def props = o.properties;
                list << [caption: o.caption, type: props.reftype, index: props.index];
            }
            list?.sort{ it.index }
            return list;
        }, 
        onselect: { o->
            binding?.refresh("opener");
            //query.state = o.state;
            //reloadAll();
        }
    ] as ListPaneModel;
    
    def getOpener() {
        if (!selectedOption) return;
        
        def op = Inv.lookupOpener("producttype:" + selectedOption.type, [entity: entity, mode: mode]);
        if (!op) return null;
        return op;
    }
    
    def validate( data ) {
        
        def msg = '';
        def flag = false;
        if (!data?.code) msg += 'Code is required.\n';
        if (!data?.title) msg += "Title is required.\n";
        if (!data?.paymentschedule) msg += "Payment Schedule is required.\n";
        
        if (msg) flag = true;
        return [msg: msg, haserror: flag];
    }
    
    void save() {
        def res = validate(entity);
        if (res.haserror == true) throw new Exception(res.msg);
        
        if (mode == "create") {
            entity = service.createData(entity);
        } else if (mode == "edit") {
            entity = service.updateData(entity);
        }
        
        mode = "read";
        entity._addedattr = [];
        entity._removedattr = [];
        
        EventQueue.invokeLater({    
            binding?.refresh();
            caller?.reload();
        });
    }
    
    def close() {
        return "_close";
    }
    
    def cancel() {
        if (mode == "edit") {
            
            if (preventity) {
                entity = preventity;
            }
            
            if (entity.generalinfo) {
                if (prevgenattr) {
                    entity.generalinfo.attributes = prevgenattr;
                }
                entity.generalinfo._addedattr = [];
                entity.generalinfo._removedattr = [];
            }
            
            if (entity.loaninfo) {
                if (prevloanattr) {
                    entity.loaninfo.attributes = prevloanattr;
                }
                entity.loaninfo._addedattr = [];
                entity.loaninfo._removedattr = [];
            }
            
            if (entity.postinginfo) {
                if (prevpostingheader) {
                    entity.postinginfo.postingheader = prevpostingheader;
                }
                if (prevpostingseq) {
                    entity.postinginfo.postingsequence = prevpostingseq;
                }
            }
            
            mode = "read";
            
            return null;
        }
        return "_close";
    }
    
    void edit() {
        preventity = [:];
        if (entity) {
            preventity = entity;
        }
        
        prevgenattr = [];
        if (entity.generalinfo?.attributes) {
            prevgenattr = copyList(entity.generalinfo.attributes);
        }
        
        prevloanattr = [];
        if (entity.loaninfo.attributes) {
            prevloanattr = copyList(entity.loaninfo.attributes);
        }
        
        prevloanfields = [];
        if (entity.loaninfo?.fields) {
            prevloanfields = copyList(entity.loaninfo.fields);
        }
        
        prevpostingheader = [];
        if (entity.postinginfo?.postingheader) {
            prevpostingheader = copyList(entity.postinginfo.postingheader)
        }
        
        prevpostingseq = [];
        if (entity.postinginfo.postingsequence) {
            prevpostingseq = copyList(entity.postinginfo.postingsequence);
        }
        
        mode = "edit";
        binding?.refresh();
    }
    
    
    void activate() {
        entity = service.activate(entity);
        EventQueue.invokeLater({    
            binding?.refresh();
            caller?.reload();
        });
    }
    
    void deactivate() {
        entity = service.deactivate(entity);
        EventQueue.invokeLater({    
            binding?.refresh();
            caller?.reload();
        });
    }
    
    void testExpression() {
        service.testExpression(entity);
    }
    
}

