package com.rameses.clfc.treasury.ledger.amnesty.preview

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.text.*;

abstract class AbstractAmnestyPreviewController
{
    @Binding
    def binding;
    
    abstract String getServiceName();
    abstract String getAmnestyid();
    abstract def getOpener();
    
    def entity, currpageindex, pagenumber, pagecount;
    
    def getTitle() {
        def str = 'Amnesty ';
        /*
        str += (entity?.amnesty?.type? entity.amnesty.type : '') + ': ';
        str += (entity?.amnesty?.description? entity.amnesty.description : '');
        /*
        if (item?.availed) {
            str += ': ' + item?.availed?.description;
        }
        */
        return str;
    }
    
    def getService() {
        String name = getServiceName();
        if ((name == null) || (name.trim().length() == 0)) {
          throw new NullPointerException("Please specify a serviceName");
        }
        return InvokerProxy.getInstance().create(name);
    }
    
    void init() {
        //if (entity.amnesty) data = entity.amnesty;
        refresh();
        binding?.refresh('opener');
    }
    
    void refresh() {    
        listHandler?.reload();
        binding?.refresh('pagecount');
    }
    
    Map getColumnParams() {
        return [:];
    }
    
    int getRows() {
        return 30;
    }
    
    int getLastPageIndex() {
        return -1;
    }
    
    void beforeFetchList( params ) {
    }
    
    def listHandler = [
        getRows: {
            return getRows(); 
        },
        getColumns: {
            def list = service.getColumns(getColumnParams());
            return list;
            
        },
        getLastPageIndex: { 
            //if (!entity.lastpageindex) entity.lastpageindex = -1;
            //return entity.lastpageindex;
            return getLastPageIndex();
            
        },
        fetchList: { o->
            o.amnestyid = getAmnestyid();
            beforeFetchList(o);
            def list = service.getList(o);
            return list;
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

    void goToPageNumber() {
        currpageindex = pagenumber
        if (pagenumber > listHandler.getLastPageIndex()) currpageindex = listHandler.getLastPageIndex();
        listHandler.moveToPage(currpageindex);
        binding?.refresh('pagecount');
    }
    
    def close() {
        return '_close';
    }
}

/*
abstract class AbstractAmnestyPreviewController
{
    @Binding
    def binding;
    
    abstract String getServiceName();
    abstract String getLedgerid();
    abstract String getAmnestyid();
    
    def getService() {        
        String name = getServiceName();
        if ((name == null) || (name.trim().length() == 0)) {
          throw new NullPointerException("Please specify a serviceName");
        }
        return InvokerProxy.getInstance().create(name);
    }
    
    def entity, currpageindex, pagenumber, pagecount;
    def data, list;
    
    void init() {
        //if (entity.amnesty) data = entity.amnesty;
        refresh();
    }
    
    void refresh() {        
        listHandler.reload();
        binding?.refresh('pagecount');
    }
    
    Map getColumnParams() {
        return [:];
    }
    
    int getRows() {
        return 30;
    }
    
    int getLastPageIndex() {
        return -1;
    }
    
    def listHandler = [        
        getRows: {
            return getRows(); 
        },
        getColumns: { 
            //def params = [type: data.amnestyoption]
            def params = getColumnParams();
            //return [];
            return service.getColumns(params); 
            
        },
        getLastPageIndex: { 
            //if (!entity.lastpageindex) entity.lastpageindex = -1;
            //return entity.lastpageindex;
            return getLastPageIndex();
            
        },
        fetchList: { o->
            //println 'params ' + o;
            //if (entity) o.ledgerid = entity.objid; 
            //if (data) o.amnestyid = data.objid;
            o.ledgerid = getLedgerid();
            o.amnestyid = getAmnestyid();
            list = service.getList(o);
            //list = [];
            //buildHtmlview();
            return list;
        },
        onOpenItem: { itm, colName ->
            if (colName != 'remarks' || !itm.remarks) return null;
            
            return Inv.lookupOpener("remarks-preview", [remarks: itm.remarks]);
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
        currpageindex = pagenumber
        if (pagenumber > listHandler.getLastPageIndex()) currpageindex = listHandler.getLastPageIndex();
        listHandler.moveToPage(currpageindex);
        binding?.refresh('pagecount');
    }
    
    def close() {
        return '_close';
    }
}
*/