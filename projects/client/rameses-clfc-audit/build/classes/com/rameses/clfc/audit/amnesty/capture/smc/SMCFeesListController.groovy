package com.rameses.clfc.audit.amnesty.capture.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class FilingFeesListController extends SMCListController
{
    String entityName = 'smc:filing:fee';
    
    void init() {
        if (!entity.fees) entity.fees = [];
    }
    
    def getCreateParams() {
        def handler = { o->
            /*
            def i = entity?.docs?.find{ it.code == o.code && it.objid != o.objid }
            if (i) throw new Exception(o.title + ' has already been selected.');
            */
            
            if (o.amount <= 0) throw new Exception('Please specify amount for the fee.');
            
            if (!entity.fees) entity.fees = [];
            entity.fees << o;
            
            if (!entity._addedfees) entity._addedfees = [];
            entity._addedfees << o;
            
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
            
            if (o.amount <= 0) throw new Exception('Please specify amount for the fee.');
            
            def i = entity?.fees?.find{ it.objid == o.objid }
            if (i) i.putAll(o);
            
            i = entity?._addedfees?.find{ it.objid == o.objid }
            if (i) i.putAll(o);
            
            listHandler?.reload();
        }
        return [saveHandler: handler];
    }
    
    void remove() {
        if (!MsgBox.confirm('You are about to remove this fee. Continue?')) return;
        
        if (!entity?._removedfees) entity._removedfees = [];
        entity?._removedfees << selectedItem;
        
        def i = entity?.fees?.find{ it.objid == selectedItem?.objid }
        if (i) entity?.fees?.remove(i);
        
        i = entity?._addedfees?.find{ it.objid == selectedItem?.objid }
        if (i) entity?._addedfees?.remove(i);
        
        listHandler?.reload();
    }
    
    def getLastPageIndex() {
        if (!entity.fees) return 1;
        
        def a = entity.fees.size()/getRows();
        a = new BigDecimal(a + '').setScale(0, BigDecimal.ROUND_CEILING);
        
        return a;
    }
    
    def getRows() {
         return 5;
    }
    
    List fetchList( params ) {
        def list = [];
        if (entity.fees) {
            def xlist = [];
            xlist.addAll(entity.fees);
            
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

