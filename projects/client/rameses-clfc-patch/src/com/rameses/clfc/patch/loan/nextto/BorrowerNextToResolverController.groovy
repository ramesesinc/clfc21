package com.rameses.clfc.patch.loan.nextto

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class BorrowerNextToResolverController 
{
    @Binding
    def binding;
    
    @Service('BorrowerNextToResolverService')
    def service;
    
    String title = 'Borrower Next to Resolver';
    
    def route, searchtext;
    
    def routeLookup = Inv.lookupOpener('borrowernextto:route:lookup', [
         onselect: { o->
             o.name = o.description + ' - ' + o.area;
             route = o;
             listHandler?.reload();
         }
    ]);
    
    void setSelectedItem( selectedItem ) {
        this.selectedItem = selectedItem;
        binding?.refresh('addNextTo|removeNextTo');
    }
    
    def selectedItem;
    def listHandler = [
        fetchList: { o->
            o.routecode = route?.code;
            o.searchtext = searchtext;
            return service.getLedgers(o);
        }
    ] as BasicListModel
    
    void search() {
        listHandler?.reload();
    }
    
    def addNextTo() {
        def handler = { o->
            def params = [
                borrowerid  : selectedItem?.borrower?.objid,
                nexttoid    : o?.objid
            ];
            service.setNextTo(params);
            listHandler?.reload();
        }
        def params = [
            borrowerid  : selectedItem?.borrower?.objid, 
            routecode   : route?.code,
            onselect    : handler
        ];
        def op = Inv.lookupOpener('borrowernextto:available:lookup', params);
        if (!op) return null;
        return op;
    }
    
    def removeNextTo() {
        if (!MsgBox.confirm('You are about to remove next to for this borrower. Continue?')) return;
        
        service.removeNextTo([borrowerid: selectedItem?.borrower?.objid]);
        listHandler?.reload();
    }
    
    def refresh() {
        listHandler?.reload();
    }
    
    def close() {
        return '_close';
    }
    
    def displayRouteLedgers() {
        def op = Inv.lookupOpener('borrower:nextto:report', [routecode: route?.code]);
        if (!op) return null;
        return op;
    }
    /*
    void displayRouteLedgers() {
        service.displayRouteLedgers([routecode: route?.code, searchtext: searchtext]);
    }
    */
}

