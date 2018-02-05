package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class PostingHeaderGroupWithController 
{
    def onselect, header;
    def items, selectedItem;
    
    void init() {
        if (!header) header = [:];
        header?.groupwith?.each{ o->
            def item = items?.find{ it.code == o.code }
            if (item) item.selected = true;
        }
        listHandler?.reload();
    } 
    
    def listHandler = [
        fetchList: { o->
            if (!items) items = [];
            return items;
        }
    ] as EditorListModel;
    
    def getSelectedItems() {
        if (!items) items = [];
        
        def list = items?.findAll{ it.selected == true }
        return list;
    }
    
    def doOk() {
        def data = [items: getSelectedItems()];
        if (onselect) onselect(data);
        return '_close';
    }
    
    def doCancel() {
        return '_close';
    }
}

