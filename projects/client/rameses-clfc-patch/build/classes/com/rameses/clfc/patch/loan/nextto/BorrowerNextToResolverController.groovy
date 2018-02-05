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
    
    def entity = [:], searchtext;
    def list, route;
    
    def routeLookup = Inv.lookupOpener('borrowernextto:route:lookup', [
         onselect: { o->
             o.name = o.description + ' - ' + o.area;
             entity = service.getInfo([route: o]);
             //route = o;
             binding?.refresh();
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
            if (!entity.list) entity.list = [];
            return entity.list;
            //o.routecode = route?.code;
            //o.searchtext = searchtext;
            //return service.getLedgers(o);
        }
    ] as BasicListModel
    
    def getParams() {
        return [searchtext: searchtext, routecode: entity.route.code]
    }
    
    void search() {
        entity.list = service.getLedgers(getParams());
        listHandler?.reload();
    }
    
    def addNextTo() {
        def handler = { o->
            def params = [
                borrowerid  : selectedItem?.borrower?.objid,
                nexttoid    : o?.objid,
                routecode   : entity.route?.code
            ];
            service.setNextTo(params);
            search();
        }
        def params = [
            borrowerid  : selectedItem?.borrower?.objid, 
            routecode   : entity?.route?.code,
            onselect    : handler
        ];
        def op = Inv.lookupOpener('borrowernextto:available:lookup', params);
        if (!op) return null;
        return op;
    }
    
    def removeNextTo() {
        if (!MsgBox.confirm('You are about to remove next to for this borrower. Continue?')) return;
        
        //service.removeNextTo([borrowerid: selectedItem?.borrower?.objid]);
        service.removeNextTo(selectedItem);
        search();
    }
    
    def refresh() {
        search();
    }
    
    def close() {
        return '_close';
    }
    
    def displayRouteLedgers() {
        def op = Inv.lookupOpener('borrower:nextto:report', [routecode: entity.route?.code]);
        if (!op) return null;
        return op;
    }
    /*
    void displayRouteLedgers() {
        service.displayRouteLedgers([routecode: route?.code, searchtext: searchtext]);
    }
    */
   
    void setAsStartingBorrower() {
        if (!selectedItem) return;
        
        def params = [
            startborrower       : selectedItem,
            prevstartborrower   : entity.startborrower
        ];
        entity.startborrower = service.setAsStartingBorrower( params );
        binding?.refresh();
        search();
    }
}

