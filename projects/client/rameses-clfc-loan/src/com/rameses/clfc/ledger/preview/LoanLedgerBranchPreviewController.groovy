package com.rameses.clfc.ledger.preview

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class LoanLedgerBranchPreviewController 
{
    @Binding
    def binding;
    
    @Service('LoanLedgerBranchPostingPreviewService')
    def service;
    
    def entity;
    def lastpageindex = 1, currpageindex = 1, pagenumber = 1;
    
    void refresh() {
        lastpageindex = service.getLastPageIndex([objid: entity?.objid, rows: getRows()]);
        listHandler?.reload();
        binding?.refresh('pagecount');
    }
    
    def printLedger() {
        def op = Inv.lookupOpener('branch-ledger:report', [ledgerid: entity?.objid]);
        if (!op) return null;
        return op;
    }
    
    def getRows() {
        if (!entity.rows) entity.rows = 20;
        return entity.rows;
    }
    
    def getLastPageIndex() {
        if (!lastpageindex) lastpageindex = 1;
        return lastpageindex;
    }
        
    def selectedItem;
    def listHandler = [
        getRows: {
            return entity.rows; 
        },
        getColumns: { 
            return service.getColumns([objid: entity?.objid]); 
        },
        getLastPageIndex: { 
            return getLastPageIndex();
        },
        fetchList: { o->
            //println 'params ' + o;
            o.objid = entity?.objid;
            def list = service.getList(o);
            //list = service.getList(o);
            //buildHtmlview();
            return list;
        },
        onOpenItem: { itm, colName ->
            if (colName != 'remarks' || !itm.remarks) return null;
            
            def op = Inv.lookupOpener("remarks-preview", [remarks: itm.remarks]);
            if (!op) return null
            return op;
        }
    ] as PageListModel;
    
    void moveFirstPage() {
        listHandler.moveFirstPage();
        binding?.refresh('pagecount');
    }

    void moveBackPage() {
        listHandler.moveBackPage();
        binding?.refresh('pagecount');
    }

    void moveNextPage() {
        listHandler.moveNextPage();
        binding?.refresh('pagecount');
    }

    void moveLastPage() {
        currpageindex = listHandler.getLastPageIndex();
        listHandler.moveLastPage();
        binding?.refresh('pagecount');
    }
    
    def getPagecount() {
        //if (!list) return "Page 1 of ?";
        return "Page " + listHandler.getPageIndex() + " of " + listHandler.getLastPageIndex();
    }

    void goToPageNumber() {
        currpageindex = pagenumber;
        if (pagenumber > listHandler.getLastPageIndex()) currpageindex = listHandler.getLastPageIndex();
        listHandler.moveToPage(currpageindex);
        binding?.refresh('pagecount');
    }
}

