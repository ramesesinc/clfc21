package com.rameses.clfc.loan.collection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;

class PostCollectionController {

    @Service("LoanPostCollectionService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    String title = "Post Collection";
    def date, selectedItem;
    
    @PropertyChangeListener
    def listener = [
        "date": { o->
            listHandler?.reload();
        }
    ];
    
    void init() {
        date = dateSvc.getServerDateAsString().split(" ")[0];
        listHandler?.reload();
    }
    
    def listHandler = [
        getColumns: { o->
            return service.getCollectionColumns( o );
        },
        fetchList: { o->
            o.date = date;
            return service.getCollections( o );
        }
    ] as BasicListModel;
    
    def next() {
        if (!selectedItem) throw new RuntimeException("Please select a collection to post.");
        
        //println "open collection";
        def op = Inv.lookupOpener("post:" + selectedItem.type.toLowerCase(), [collection: selectedItem]);
        if (!op) return null;
        return op;
    }
    
    void refresh() {
        listHandler?.reload();
    }
    
    def close() {
        return "_close";
    }
}

