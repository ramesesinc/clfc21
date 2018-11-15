package com.rameses.clfc.producttype2

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class PostingInfoController {

    @Binding
    def binding;
    
    @Script('Template')
    def template
    
    @Service("NewLoanProducttypeService")
    def service;
    
    @PropertyChangeListener
    def listener = [
        "entity.header": {
            binding?.refresh();
        }
    ]
    
    def entity, mode = "read";
    def postinggroup;
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.postinginfo) entity.postinginfo = [title: "POSTINGINFO"];
        if (!entity.postinginfo.advanceposting) entity.postinginfo.advanceposting = [:]
        postinggroup = service.getPostingGroup().sort{ it.idx }.collect{ it.name };
        if (!postinggroup) postinggroup = [];
        binding?.refresh();
    }
    
    def selectedHeader;
    def headersHandler = [
        fetchList: { o->
            if (!entity.postinginfo.postingheader) entity.postinginfo.postingheader = [];
            if (entity.postinginfo?.postingheader) {
                entity.postinginfo.postingheader.sort{ it.seqno }
                entity.postinginfo.postingheader.each{ 
                    it.isfirst = false;
                    it.islast = false;
                }
                entity.postinginfo.postingheader[0].isfirst = true;
                entity.postinginfo.postingheader[entity.postinginfo.postingheader.size() - 1].islast = true;
            }
            
            return entity.postinginfo.postingheader;
        }
    ] as ListPaneModel; 
    
    void generateDefaultPosting() {
        def header = service.getDefaultPostingHeader();
        if (!entity.postinginfo.postingsequence) entity.postinginfo.postingsequence = [];

        entity.postinginfo.postingheader = header;
        /*
        
        entity.postinginfo.postingsequence = [];
        header.each{ o->
            def item = entity?.postinginfo.postingsequence?.find{ it.objid == o.objid }
            if (!item) {
                item = [
                    objid           : o.objid, 
                    title           : o.title, 
                    name            : o.name, 
                    index           : o.index,
                    seqno           : o.seqno,
                    isfirst         : o.isfirst, 
                    islast          : o.islast,
                    postingexpr     : o.postingexpr,
                    datatype        : o.datatype,
                    postonlastitem  : true,
                    isdefault       : true 
                ];
                entity?.postinginfo.postingsequence << item; 
            }
            
            item.fields = service.getFields( item );
        }
        */
        headersHandler?.reload();
        //binding?.refresh("postingHtml");
    }
    
    def addHeader() {
        def handler = { o->
            def h = entity.postinginfo.postingheader?.find{ it.objid==o.objid }
            if (h) throw new RuntimeException("Header has already been selected.");
            
            if (!entity.postinginfo.postingheader) entity.postinginfo.postingheader = [];
            
            o.index = entity.postinginfo.postingheader.size();
            o.sequence = o.index + 1;
            o.seqno = o.sequence;
            
            entity.postinginfo.postingheader << o;
            headersHandler?.reload();
            
        }
        def op = Inv.lookupOpener("posting:header:lookup", [category: "PRODUCTTYPE", onselect: handler]);
        if (!op) return null;
        return op;
    }
    
    void removeHeader() {
        if (!selectedHeader) return;
        
        entity.postinginfo.postingheader.remove(selectedHeader);;
        entity.postinginfo.postingheader.eachWithIndex{ itm, idx->
            itm.index = idx;
            itm.seqno = idx + 1;
        }
        headersHandler?.reload();
    }
    
    void moveUpHeader() {
        if (!selectedHeader) return;
        
        def idx = selectedHeader.index
        
        def item = entity.postinginfo.postingheader.find{ it.index == (idx - 1) }
        if (item) {
            item.index++;   
            item.sequence = item.index + 1;
            item.seqno = item.sequence;
        }
        selectedHeader.index--;
        selectedHeader.sequence = selectedHeader.index + 1;
        selectedHeader.seqno = selectedHeader.sequence
        headersHandler?.reload();
    }
    
    void moveDownHeader() {
        if (!selectedHeader) return;
        
        def idx = selectedHeader.index;
        
        def item = entity.postinginfo.postingheader.find{ it.index == (idx + 1) }
        if (item) {
            item.index--;
            item.sequence = item.index + 1;
            item.seqno = item.sequence;
        }
        selectedHeader.index++;
        selectedHeader.sequence = selectedHeader.index + 1;
        selectedHeader.seqno = selectedHeader.sequence
        headersHandler?.reload();
    }
    
    def getPostingHtml() {
        if (!entity?.postinginfo.postingsequence) entity.postinginfo.postingsequence = [];
        def params = [
            group   : postinggroup,
            list    : entity?.postinginfo.postingsequence,
            mode    : mode
        ];
        resolvePostingSequenceIndex();
        
        //println 'group';
        //postinggroup.each{ println it }
        //println 'list';
        //params.list.each{ println it }
        //println '';
        return template.render('html/producttype_posting_sequence_html.gtpl', params);
    }
    
    void resolvePostingSequenceIndex() {
        def list = entity?.postinginfo?.postingsequence;
        if (list) {
            postinggroup?.each{ g->
                def xlist = list?.findAll{ it.group==g }
                if (xlist) {
                    xlist.sort{ it.index }
                    xlist.eachWithIndex{ itm, idx->
                        itm.index = idx;
                        itm.sequence = idx + 1;
                    }
                }
            }
        }
    }
    
    void moveUpCondition( params ) {
        def list = entity.postinginfo.postingsequence;
        def item = list.find{ it.objid==params.objid }
        if (!item) return;
        
        def idx = item.index;

        def item2 = list.find{ it.index==(idx - 1) }
        if (item2) {
            item2.index++;
            //item2.sequence = item2.index + 1;
        }
        item.index--;
        //item.sequence = item.index + 1;
        binding?.refresh("postingHtml");
    }
    
    void moveDownCondition( params ) {
        def list = entity.postinginfo.postingsequence;
        def item = list.find{ it.objid==params.objid }
        if (!item) return;
        
        def idx = item.index;

        def item2 = list.find{ it.index==(idx + 1) }
        if (item2) {
            item2.index--;
            //item2.sequence = item2.index + 1;
        } 
        item.index++;
        //item.sequence = item.index + 1;
        binding?.refresh("postingHtml");
    }
    
    
    def addCondition() {        
        def handler = { o->

            def i = entity.postinginfo.postingsequence.find{ it.code==o.code && it.group==o.group }
            if (i) throw new RuntimeException("Posting condition has already been selected.");

            /*
            def list = entity.postinginfo.postingsequence?.findAll{ it.group==o.group }
            if (!o.sequence) {
                
            }
            o.sequence = list?.size();
            if (!o.sequence) o.sequence = 1;
            o.index = o.sequence - 1;
            */
                        
            entity.postinginfo.postingsequence << o;
            resolvePostingSequenceIndex();
            
            binding?.refresh('postingHtml');
        }
        
        def params = [
            mode            : mode,
            handler         : handler,
            vars            : buildVarList(),
            headerList      : buildHeaderList(),
            postingsequence : entity?.postinginfo?.postingsequence
        ];
        
        def op = Inv.lookupOpener("producttype:postinginfo:constraint:create", params);
        if (!op) return null;
        return op;
    }
    
    void removeCondition( params ) {
        //println 'params->' + params;
        //println 'sequence';
        //entity.postinginfo.postingsequence?.each{ println it }
        //println '';
        
        def item = entity.postinginfo.postingsequence.find{ it.objid==params.objid }
        if (!item) return;
        
        entity.postinginfo.postingsequence.remove( item );
        binding?.refresh("postingHtml");
    }
    
    def editCondition( params ) {
        //def item = entity.postinginfo.postingsequence.find{ it.code==params.code }
        def item = entity.postinginfo.postingsequence.find{ it.objid==params.objid }
        if (!item) return;
        
        def handler = { o->            
            item = entity.postinginfo.postingsequence.find{ it.objid==o.objid }
            if (!item) return;
            
            def i = entity.postinginfo.postingsequence.find{ it.objid != params.objid && it.code==o.code && it.group==o.group }
            if (i) throw new RuntimeException("Posting condition has already been selected.");
            
            item.putAll( o );            
            resolvePostingSequenceIndex();
            
            binding?.refresh('postingHtml');
        }
        
        def xprm = [
            entity              : item,
            mode                : mode,
            handler             : handler,
            vars                : buildVarList(), //buildVarList( item?.index ),
            forConstraintList   : [],
            headerList          : buildHeaderList(),
            postingsequence     : entity?.postinginfo?.postingsequence
        ];
        
        def op = Inv.lookupOpener("producttype:postinginfo:constraint:open", xprm);
        if (!op) return null;
        return op;
    }
    
    def editAllowAdvanceCondition() {
        def handler = { o->
            if (!entity.postinginfo.advanceposting) entity.postinginfo.advanceposting = [:];
                        
            entity.postinginfo.advanceposting = o;
        }
        
        def params = [
            entity  : entity?.postinginfo?.advanceposting,
            mode    : mode,
            handler : handler,
            vars    : buildVarList(),
        ];
        
        def op = Inv.lookupOpener("producttype:postinginfo:advanceposting:condition:open", params);
        if (!op) return null;
        return op;
    }
    
    
    
    def buildHeaderList() {
        def list = [];
        entity?.postinginfo?.postingheader?.each{
            list << [objid: it.objid, title: it.title, name: it.name, datatype: it.datatype];
        }
        /*
        def l = entity.postinginfo?.postingheader;
        l?.each{ 
            println it;
            list << [code: it.code, title: it.title, name: it.name];
        }
        */
        return list;
    }
    
    /*
    def buildVarList() {
        def idx = entity.postinginfo?.postingsequence?.size();
        if (!idx) idx = 0;
        return buildVarList( idx );
    }
    */
    
    def buildVarList() {
        //if (!idx) idx = 0;
        
        def list = [];
        /*
        entity.loaninfo?.fields?.each{ o->
            list << [caption: o.title, title: o.title, signature: o.title, handler: o.field.handler];
        }
        */
        def item, varname;
        
        entity?.generalinfo?.attributes?.each{ o->
            varname = "GENINFO_" + o.attribute?.varname;
            item = [
                caption     : varname,//o.attribute?.varname,
                title       : varname,//o.attribute?.varname,
                signature   : varname,//o.attribute?.varname,
                //handler     : o.attribute?.type
                //handler     : o.attribute?.handler
                handler     : o.attribute?.datatype
            ];
            
            if (item.handler == "expression") {
                item.description = "(decimal)";
            } else if (item.handler) {
                item.description = "(" + item.handler + ")";
            }
            
            list << item;
        }
        
        entity.loaninfo?.attributes?.each{ o->
            varname = "LOANINFO_" + o.attribute?.varname;
            item = [
                caption     : varname,//o.attribute?.varname,
                title       : varname,//o.attribute?.varname,
                signature   : varname,//o.attribute?.varname,
                //handler     : o.attribute?.type
                //handler     : o.attribute?.handler
                handler     : o.attribute?.datatype
            ];
            
            if (item.handler == "expression") {
                item.description = "(decimal)";
            } else if (item.handler) {
                item.description = "(" + item.handler + ")";
            }
            
            list << item;
        }
        
        //def xlist = entity.postinginfo?.postingsequence?.findAll{ it.index < idx && it.varname != null }
        /*
        def xlist = [];
        xlist?.each{ 
            item = [
                caption     : it.varname,
                title       : it.varname,
                signature   : it.varname,
                handler     : it.datatype,
                description : it.datatype
            ];
            
            list << item;
        } 
        */
        
        return list;
    }
}

