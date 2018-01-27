package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

abstract class ListController 
{
    @Binding
    def binding;
    
    def entity, mode = 'read';
    
    def getRows() {
        return 20;
    }
    
    abstract def getLastPageIndex();
    abstract List fetchList( params );
    abstract String getEntityName();
    
    def getCreateParams() {
        return [:];
    }
    
    def create() {
        def op = Inv.lookupOpener(getEntityName() + ":create", getCreateParams());
        if (!op) return null;
        
        return op;
    }
    
    def getOpenParams() {
        return [mode: mode];
    }
    
    def open() {
        def params = getOpenParams();
        params.entity = selectedItem
        def op = Inv.lookupOpener(getEntityName() +  ':open', params);
        if (!op) return null;
        
        return op;
    }
    
    void remove() {}
    
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
    
    void setSelectedItem( selectedItem ) {
        this.selectedItem = selectedItem;
        binding?.refresh('formActions');
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

