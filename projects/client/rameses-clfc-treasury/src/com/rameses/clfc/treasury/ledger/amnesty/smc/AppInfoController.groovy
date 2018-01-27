package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class AppInfoController 
{
    @Binding
    def binding;
    
    @Service('LedgerAmnestySMCService')
    def service;
    
    def entity, mode = 'read';    
    void init() {
        if (!entity) entity = [:];
        if (!entity.payments) entity.payments = [];
    }
    
    def borrowerLookup = Inv.lookupOpener('amnestyledger:lookup', [
        onselect: { o->
            entity.ledgerid = o.objid;
            def item = service.getLedgerInfo([ledgerid: o.objid]);
            if (item) {
                if (!item.borrower?.address) item.borrower.address = o.borrower.address;
                entity.putAll(item);
            }
            binding?.refresh();
            listHandler?.reload();
        }
    ]);

    def getRows() {
        return 20;
    }

    def getLastPageIndex() {
        if (!entity.payments) return 1;
        
        def a = entity.payments.size()/getRows();
        a = new BigDecimal(a + '').setScale(0, BigDecimal.ROUND_CEILING);

        return a;
    }
    
    def selectedItem;
    def listHandler = [
        getRows: {
            return getRows(); 
        },
        getLastPageIndex: { 
            //if (!entity.lastpageindex) entity.lastpageindex = -1;
            //return entity.lastpageindex;
            return getLastPageIndex();
            
        },
        fetchList: { o->
            return fetchList(o);
        }
    ] as PageListModel;
    
    List fetchList( params ) {
        def list = [];
        if (entity.payments) {
            def xlist = [];
            xlist.addAll(entity.payments);
            
            def toidx = (params._start + params._rowsize);
            if (toidx > xlist.size()) {
                toidx = xlist.size() - 1;
            }
            list = xlist.subList(params._start, toidx);
            list?.each{ it.txndate = parseDate(it.txndate); }
        }
        
        return list;
    }
    
    def parseDate( date ) {
        if (!date) return null;
        
        if (date instanceof Date) {
            return date;
        } else {
            return java.sql.Date.valueOf(date);
        }
    }
    
    def getPagecount() {
        return "Page " + listHandler.getPageIndex() + " of " + listHandler.getLastPageIndex();
    }
    
    void moveFirstPage() {
        listHandler?.moveFirstPage();
        binding?.refresh('pagecount');
    }

    void moveBackPage() {
        listHandler?.moveBackPage();
        binding?.refresh('pagecount');
    }

    void moveNextPage() {
        listHandler?.moveNextPage();
        binding?.refresh('pagecount');
    }

    void moveLastPage() {
        //currpageindex = listHandler?.getLastPageIndex();
        listHandler?.moveLastPage();
        binding?.refresh('pagecount');
    }
}

