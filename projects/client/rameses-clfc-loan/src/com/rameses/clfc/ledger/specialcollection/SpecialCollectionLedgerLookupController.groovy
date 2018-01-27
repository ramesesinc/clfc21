package com.rameses.clfc.ledger.specialcollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class SpecialCollectionLedgerLookupController
{
    @Binding
    def binding;
    
    @Service('LoanSpecialCollectionLedgerLookupService')
    def service;

    def collectorid, billdate;
    def selectedItem, searchtext;
    def onselect, listSize = 0;

    def getListSize() {
        if (!listSize) return 0;
        return listSize;
    }
    
    def listHandler = [
        getRows: { 
            return 15;
        },
        fetchList: { o->
            o.collectorid = collectorid;
            o.billdate = billdate;
            o.type = selectedOption?.type;
            o.searchtext = searchtext;
            def list = service.getList(o);
            listSize = (list? list?.size() : 0);
            return list;
        },
        getColumns: { o->
            return service.getColumns(o);
        },
        onOpenItem: { item,colname-> 
            select();
        }
    ] as PageListModel;

    def selectedOption;
    def optionsHandler = [
        getItems: {
            return service.getOptions();
            //return svc.getStates();
        }, 
        onselect: { o->
            moveFirstPage();
            //listHandler?.reload();
            //reloadAll();
        }
    ] as ListPaneModel;
    
    def select() {
        if (onselect) onselect(selectedItem);
        return '_close';
    }
    
    def cancel() {
        return '_close';
    }
    
    void search() { 
        moveFirstPage();
        binding?.refresh('pagecount');
    }
    
    void refresh() {
        listHandler?.reload();
        binding?.refresh('pagecount');
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
        if (listSize > listHandler?.getRows()) {
            listHandler?.moveNextPage(); 
            binding?.refresh('pagecount');
        }
    } 
    
    void moveLastPage() {}     
   
    def getPagecount() {
        //def pi = listHandler?.getPageIndex();
        //def lpi = listHandler?.getLastPageIndex(); 
        //return 'Page ' + pi + ' of ' + (lpi? lpi : '?');
        //if (!list) return "Page 1 of ?";
        return 'Page ' + listHandler?.getPageIndex() + ' of ?';
    }
}

