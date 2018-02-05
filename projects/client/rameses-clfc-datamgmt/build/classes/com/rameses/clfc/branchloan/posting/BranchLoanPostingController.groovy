package com.rameses.clfc.branchloan.postingsequence

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class BranchLoanPostingController 
{
    @Binding
    def binding;
    
    @Service('BranchLoanPostingService')
    def service;
    
    @Script('Template')
    def template
    
    String title = 'Branch Loan Posting Setting';
    
    def entity, mode = 'read';
    def preventity, prevheaders, prevposting;
    
    void init() {
        entity = service.open(entity);
        mode = 'read';
    }
    
    def close() {
        return '_close';
    }
    
    def cancel() {
        if (!MsgBox.confirm('Cancelling will undo changes made. Continue?')) return;
        
        if (preventity) {
            entity = preventity;
        }
        
        if (prevheaders) {
            entity.postingheader = prevheaders;
        }
        
        if (prevposting) {
            entity.postingsequence = prevposting;
        }
        
        mode = 'read';
        binding?.refresh();
        headersHandler?.reload();
    }
    
    void save() {
        if (!MsgBox.confirm('You are about to update branch loan posting setting. Continue?')) return;
        
        entity = service.save(entity);
        mode = 'read';
        binding?.refresh();
    }
    
    void edit() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        prevheaders = [];
        if (entity?.postingheader) {
            prevheaders = copyList(entity?.postingheader);
        }
        
        prevposting = [];
        if (entity?.postingsequence) {
            prevposting = copyList(entity?.postingsequence);
        }
        
        mode = 'edit';
        binding?.refresh('formActions');
    }
    
    def copyList( src ) {
        def list = [];
        def item;
        src?.each{ o->
            item = [:];
            item.putAll(o);
            list << item;
        }
        return list;
    }
    
    def getHeaderHtml() {
        if (!entity?.postingheader) entity.postingheader = [];
        def params = [
            list    : entity?.postingheader,
            mode    : mode
        ]
        return template.render('html/branch_posting_header_html.gtpl', params);
    }
    
    def getPostingHtml() {
        if (!entity?.postingsequence) entity.postingsequence = [];
        def params = [
            list    : entity?.postingsequence,
            mode    : mode
        ]
        return template.render('html/branch_posting_condition_html.gtpl', params);
    }
    
    void generateDefaultPosting() {
        def list = service.getDefaultPostingSequence();
        if (!entity.postingheader) entity.postingheader = [];
        if (!entity.postingsequence) entity.postingsequence = [];
        
        def size;
        list?.each{ o->
            def item = entity?.postingheader?.find{ it.code == o.code }
            if (!item) {
                size = entity?.postingheader?.size();
                if (!size) size = 0;
                
                o.index = size;
                o.sequence = size + 1;
                entity.postingheader << o;
            }
            
            item = entity?.postingsequence?.find{ it.code == o.code }
            if (!item) {
                item = [
                    code            : o.code, 
                    title           : o.title, 
                    name            : o.name, 
                    index           : o.index,
                    sequence        : o.sequence,
                    isfirst         : o.isfirst, 
                    islast          : o.islast,
                    postingexpr     : o.postingexpr,
                    datatype        : o.datatype,
                    postonlastitem  : true
                ];
                entity?.postingsequence << item; 
            }
        }
        
        entity?.postingheader?.sort{ it.sequence }
        entity?.postingsequence?.sort{ it.sequence }
        binding?.refresh();
        headersHandler?.reload();
    }
    
    def selectedHeader;
    def headersHandler = [
        fetchList: { o->
           if (!entity.postingheader) entity.postingheader = [];
           return entity.postingheader;
        },
        onselect: { o-> 
            binding?.refresh('formActions');
        }
    ] as ListPaneModel; 
    
    void moveUpHeader() {
        def item = entity?.postingheader?.find{ it.code == selectedHeader?.code }
        if (item) {
            def item2 = entity?.postingheader[item.index - 1];
            if (item2) {
                item2.index++;
                item2.sequence = (item2.index + 1);
                
                def ifs = item2.isfirst;
                item2.isfirst = item.isfirst;
                item.isfirst = ifs;
                
                def il = item2.islast;
                item2.islast = item.islast;
                item.islast = il;
            }
            item.index--;
            item.sequence = (item.index + 1);
        }
        
        entity?.postingheader?.sort{ it.sequence }
        headersHandler?.reload();
        binding?.refresh();
    }
    
    void moveDownHeader() {
        
        def item = entity?.postingheader?.find{ it.code == selectedHeader?.code }
        if (item) {
            def item2 = entity?.postingheader[item.index + 1];
            if (item2) {
                item2.index--;
                item2.sequence = (item2.index + 1);
                
                def ifs = item2.isfirst;
                item2.isfirst = item.isfirst;
                item.isfirst = ifs;
                
                def il = item2.islast;
                item2.islast = item.islast;
                item.islast = il;
            }
            
            item.index++;
            item.sequence = (item.index + 1);
        }
        
        entity?.postingheader?.sort{ it.sequence }
        headersHandler?.reload();
        binding?.refresh();
    }
    
    def addHeader() {
        def handler = { o->
            def i = entity?.postingheader?.find{ it.code==o.code }
            if (i) throw new Exception("Posting header already selected.");
            
            def last;
            if (entity?.postingheader) {
                last = entity.postingheader[entity.postingheader.size() - 1];
                last.islast = false;
            }
            
            def size = entity?.postingheader?.size();
            if (!size) size = 0;

            o.index = size;
            o.sequence = size + 1;
            o.isfirst = (size == 0)? true : false;
            o.islast = true;
            
            if (!entity?.postingheader) entity.postingheader = [];
            entity.postingheader << o;
            
            def item = [
                code            : o.code, 
                title           : o.title, 
                name            : o.name, 
                index           : o.index,
                sequence        : o.sequence,
                isfirst         : o.isfirst, 
                islast          : o.islast,
                postingexpr     : o.postingexpr,
                datatype        : o.datatype,
                postonlastitem  : true
            ];
            
            if (entity?.postingsequence) {
                last = entity.postingsequence[entity.postingsequence.size() - 1];
                last.islast = false;
            }
            if (!entity?.postingsequence) entity.postingsequence = [];
            entity?.postingsequence << item; 
            
            entity?.postingheader?.sort{ it.sequence }
            entity?.postingsequence?.sort{ it.sequence }
            binding?.refresh();
            headersHandler?.reload();
        }
        
        def op = Inv.lookupOpener("postingheader:lookup", [onselect: handler]);
        if (!op) return null;
        return op;
    }
    
    void removeHeader() {
        if (!selectedHeader) return;
        
        if (!MsgBox.confirm('You are about to remove this posting header. Continue?')) return;
        
        def item = entity?.postingheader?.find{ it.code == selectedHeader?.code }
        if (item) entity?.postingheader?.remove(item);
        
        entity?.postingheader?.sort{ it.sequence }
        entity?.postingheader?.eachWithIndex{ o,idx->
            o.index = idx;
            o.sequence = (idx + 1);
            o.isfirst = false;
            o.islast = false;
        }
        
        if (entity?.postingheader) {
            entity.postingheader[0].isfirst = true;
            entity.postingheader[entity.postingheader.size() - 1].islast = true;
        }
        
        item = entity?.postingsequence?.find{ it.code == selectedHeader?.code }
        if (item) entity?.postingsequence?.remove(item);
        
        entity?.postingsequence?.sort{ it.sequence }
        entity?.postingsequence?.eachWithIndex{ o,idx-> 
            o.index = idx;
            o.sequence = (idx + 1);
            o.isfirst = false;
            o.islast = false;
        }
        
        if (entity?.postingsequence) {
            entity.postingsequence[0].isfirst = true;
            entity.postingsequence[entity.postingsequence.size() - 1].islast = true;
        }
        
        binding?.refresh();
        headersHandler?.reload();
    }
    
    void moveUpCondition( params ) {
        def item = entity?.postingsequence?.find{ it.code == params.code } 
        if (item) {
            def item2 = entity?.postingsequence[item.index - 1];
            if (item2) {
                item2.index++;
                item2.sequence = (item2.index + 1);
                
                def ifs = item2.isfirst;
                item2.isfirst = item.isfirst;
                item.isfirst = ifs;
                
                def il = item2.islast;
                item2.islast = item.islast;
                item.islast = il;
            }
            item.index--;
            item.sequence = (item.index + 1);
        }
        entity?.postingsequence?.sort{ it.sequence }
        binding?.refresh();
    }

    void moveDownCondition( params ) {
        def item = entity?.postingsequence?.find{ it.code == params.code }
        if (item) {
            def item2 = entity?.postingsequence[item.index + 1];
            if (item2) {
                item2.index--;
                item2.sequence = (item2.index + 1);
                
                def ifs = item2.isfirst;
                item2.isfirst = item.isfirst;
                item.isfirst = ifs;
                
                def il = item2.islast;
                item2.islast = item.islast;
                item.islast = il;
            }
            
            item.index++;
            item.sequence = (item.index + 1);
        }
        
        entity?.postingsequence?.sort{ it.sequence }
        binding?.refresh();
    }
    
    def editCondition( params ) {
        def item = entity?.postingsequence?.find{ it.code == params.code }
        
        def handler = { o->
            item = entity?.postingsequence?.find{ it.code == o.code }
            if (item) {
                item.putAll(o);
            }
            binding?.refresh('postingHtml');
        }
        
        def xparams = [
            headers     : entity?.postingheader,
            onselect    : handler,
            entity      : item,
            mode        : mode
        ]
        
        def op = Inv.lookupOpener('branchloan:posting:condition:edit', xparams);
        if (!op) return null;
        return op;
    }
    
}

