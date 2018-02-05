package com.rameses.clfc.migrationsupport.borrowermerge

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class BorrowerMergeController {
    
    @Caller
    def caller;
    
    @Binding
    def binding;
    
    @Service("MigrationBorrowerMergeService")
    def service;
    
    String title = "Merge Request";
    
    def entity, selectedBorrower;
    def mode = 'read';
    def preventity, prevlist;
    
    void open() {
        entity = service.open(entity);
        mode = 'read';
        listHandler?.reload();
    }
    
    void edit() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        prevlist = [];
        if (entity.items) {
            def item;
            entity.items.each{ o->
                item = [:];
                item.putAll(o);
                prevlist << item;
            }
        }
        
        mode = 'edit';
    }
    
    def void cancel() {
        if (preventity) {
            entity = preventity;
        }
        
        if (prevlist) {
            entity.items = prevlist;
        }
        mode = 'read';
    }
    
    def close() { 
        return '_close'; 
    }
    
    def listHandler = [
        fetchList: { o->
            if (!entity.items) entity.items = [];
            return entity.items;
        }
    ] as BasicListModel;
    
    void save() {
        entity = service.save(entity);
        mode = 'read';
        EventQueue.invokeLater({
             caller?.reload();
        });
    }
    
    void selectBorrowerToRetain() {
        if (!selectedBorrower) return;
        
        entity.borrower = selectedBorrower.borrower;
        binding?.refresh();
    }
    
    void submitForApproval() {
        if (!MsgBox.confirm("You are about to submit this request for approval. Continue?")) return;
        
        entity = service.submitForApproval(entity);
        EventQueue.invokeLater({
             caller?.reload();
        });
    }
    
    void approveDocument() {
        if (!MsgBox.confirm("You are about to approve this request. Continue?")) return;
        
        entity = service.approveDocument(entity);
    }
    
    void disapprove() {
        if (!MsgBox.confirm("You are about to disapprove this request. Continue?")) return;
        
        entity = service.disapprove(entity);
        EventQueue.invokeLater({
             caller?.reload();
        });
    }
    
}

