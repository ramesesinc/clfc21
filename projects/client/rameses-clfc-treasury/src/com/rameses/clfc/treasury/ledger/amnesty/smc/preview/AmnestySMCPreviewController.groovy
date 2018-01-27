package com.rameses.clfc.treasury.ledger.amnesty.smc.preview

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class AmnestySMCPreviewController 
{
    @Binding
    def binding;
    
    @Service('LedgerAmnestySMCPostingPreviewService')
    def service;
 
    @Script('Template')
    def template;
    
    String title = '';
    
    def data, selectedItem, currpageindex;
    
    void init() {
        data = service.open(data);
        title = data?.description;
        reload();
    }
    
    void reload() {
        listHandler?.reload();
    }
    
    def getRuleHtml() {
        if (!data?.conditions) entity.conditions = [];
        //return template.render( "html/smc_condition_html.gtpl", [rule: entity, editable:true] );
                
        def params = [
            conditions  : data?.conditions,
            mode        : 'read'
        ]
        return template.render('html/smc_condition_html.gtpl', params);
    }
    
    int getRows() {
        if (!data?.rows) return 30;
        return data?.rows;
    }
    
    int getLastPageIndex() {
        if (!data?.lastpageindex) return -1;
        return data?.lastpageindex;
    }
    
    def listHandler = [
        getRows: {
            return getRows();
        },
        getColumns: {
            def params = [objid: data?.objid]
            def list = service.getColumns(params);
            return list;
        },
        fetchList: { o->
            o.objid = data?.objid;
            def list = service.getList(o);
            return list;
        },
        getLastPageIndex: { 
            //if (!entity.lastpageindex) entity.lastpageindex = -1;
            //return entity.lastpageindex;
            return getLastPageIndex();
            
        },
        onOpenItem: { itm, colName ->
            if (colName != 'remarks' || !itm.remarks) return null;
            
            def op = Inv.lookupOpener("remarks-preview", [remarks: itm.remarks]);
            if (!op) return null;
            return op;
        }
    ] as PageListModel;
    
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
        currpageindex = listHandler?.getLastPageIndex();
        listHandler?.moveLastPage();
        binding?.refresh('pagecount');
    }
    
    def getPagecount() {
        //if (!list) return "Page 1 of ?";
        return "Page " + listHandler.getPageIndex() + " of " + listHandler.getLastPageIndex();
    }
    
    def printLedger() {
        def op = Inv.lookupOpener('smc-ledger:report', [smcid: data?.objid]);
        if (!op) return null;
        return op;
    }
}

