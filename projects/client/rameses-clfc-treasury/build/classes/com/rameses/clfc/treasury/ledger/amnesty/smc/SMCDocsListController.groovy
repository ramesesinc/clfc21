package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class SMCDocsListController extends com.rameses.clfc.treasury.ledger.amnesty.smc.ListController 
{
    String entityName = 'smc:filing:doc';
    
    void init() {
        if (!entity.docs) entity.docs = [];
    }
    
    def rows = 5;
    
    def getCreateParams() {
        def handler = { o->
            /*
            def i = entity?.docs?.find{ it.code == o.code && it.objid != o.objid }
            if (i) throw new Exception(o.title + ' has already been selected.');
            
            */
            if (o.qty <= 0) throw new Exception('Please specify quantity for the document.');
            
            if (!entity.docs) entity.docs = [];
            entity.docs << o;
            
            if (!entity._addeddocs) entity._addeddocs = [];
            entity._addeddocs << o;
            
            listHandler?.reload();
        }
        
        return [saveHandler: handler];
    }
    
    def getOpenParams() {
        def handler = { o->
            /*
            def i = entity?.docs?.find{ it.code == o.code && it.objid != o.objid }
            if (i) throw new Exception(o.title + ' has already been selected.');
            */
            
            if (o.qty <= 0) throw new Exception('Please specify quantity for the document.');
            
            def i = entity?.docs?.find{ it.objid == o.objid }
            if (i) i.putAll(o);
            
            i = entity?._addeddocs?.find{ it.objid == o.objid }
            if (i) i.putAll(o);
            
            listHandler?.reload();
        }
        
        def params = super.getOpenParams();
        params.saveHandler = handler
        return params;
        //return [saveHandler: handler];
    }
    
    void remove() {
        if (!MsgBox.confirm('You are about to remove this document. Continue?')) return;
        
        if (!entity._removeddocs) entity._removeddocs = [];
        entity._removeddocs << selectedItem;
        
        def i = entity?.docs?.find{ it.objid == selectedItem?.objid }
        if (i) entity?.docs?.remove(i);
        
        i = entity?._addeddocs?.find{ it.objid == selectedItem?.objid }
        if (i) entity?._addeddocs?.remove(i);
        
        listHandler?.reload();
    }
        
    def getLastPageIndex() {
        if (!entity.docs) return 1;
        
        def a = entity.docs.size()/getRows();
        a = new BigDecimal(a + '').setScale(0, BigDecimal.ROUND_CEILING);
        
        return a;
    }
    
    def getRows() {
         return 5;
    }
    
    List fetchList( params ) {
        def list = [];
        if (entity.docs) {
            def xlist = [];
            xlist.addAll(entity.docs);
            
            def toidx = (params._start + params._rowsize);
            if (toidx > xlist.size()) {
                toidx = xlist.size();
            }
            
            list = xlist.subList(params._start, toidx);
        }
        
        return list;
    }
    
    void reload() {
        binding?.refresh();
        listHandler?.reload();
    }
}

