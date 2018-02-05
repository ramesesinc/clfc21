package com.rameses.clfc.treasury.ledger.amnesty.preview

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class AmnestyPreviewController extends AbstractAmnestyPreviewController
{
    String serviceName = 'LedgerAmnestyPreviewService';
    def data, item;
    
    void init() {
        item = service.openAmnesty(data);
        super.init();
    }
    
    def getTitle() {
        def str = 'Amnesty ';
        str += (item?.type? item.type : '') + ': ';
        str += (item?.description? item.description : '');
        return str;
    }
    
    String getAmnestyid() {
        return item?.objid;
    }
    
    def getOpener() {
        def op = Inv.lookupOpener('amnesty:preview:' + item?.type.toLowerCase(), [entity: item]);
        if (!op) return null;
        return op;
    }
    
    void beforeFetchList( params ) {
        params.reftype = item?.type;
        //params.ledgerid = '';
    }
    
    int getRows() {
        if (!item?.rows) return super.getRows();
        return item.rows;
    }
    
    int getLastPageIndex() {
        if (!item?.lastpageindex) return getLastPageIndex();
        return item.lastpageindex;
    }
    
    Map getColumnParams() {
        return [type: item?.type];
        //return [type: item?.amnestytype?.value];
    }
}