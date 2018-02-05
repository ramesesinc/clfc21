package com.rameses.clfc.treasury.ledger.amnesty.preview

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class LedgerAmnestyPreviewController extends AbstractAmnestyPreviewController 
{
    String serviceName = 'LedgerAmnestyPreviewService';
        
    String getAmnestyid() {
        return item?.objid;
    }
    
    def getOpener() {
        def invtype = 'amnesty:preview:' + item?.type.toLowerCase();
        def op = Inv.lookupOpener(invtype, [entity: item]);
        if (!op) return null;
        return op;
    }
    
    def getTitle() {
        def str = 'Amnesty ';
        str += (item?.type? item.type : '') + ': ';
        str += (item?.description? item.description : '');
        return str;
    }
    
    def item;
    void preview() {
        item = service.openAmnestyByRefid(entity);
        init();
    }
    
    int getRows() {
        if (!item?.rows) return super.getRows();
        return item.rows;
    }
    
    int getLastPageIndex() {
        if (!item?.lastpageindex) return getLastPageIndex();
        return item.lastpageindex;
    }
    
    void beforeFetchList( params ) {
        params.reftype = item?.type;
        //params.ledgerid = '';
    }
    
    Map getColumnParams() {
        return [type: item?.type];
        //return [type: item?.amnestytype?.value];
    }
}