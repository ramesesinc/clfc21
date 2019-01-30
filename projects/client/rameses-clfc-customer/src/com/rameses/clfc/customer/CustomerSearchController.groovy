package com.rameses.clfc.customer;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class CustomerSearchController extends BasicLookupModel 
{
    //to be feed by the caller
    
    @Service('CustomerService')
    def service;
    
    def mode = 'search';
    
    void init() { 
        
    } 

    boolean show(String searchtext) {
        customerlistHandler.searchtext = searchtext; 
        return true; 
    }
    
    def getTitle() {
        def s = (mode == 'search'? 'Search Customers': 'Customer');
        return '<font color="#808080" size="5"><b>'+s+'</b></font><br>'; 
    } 
    
    def getValue() { 
        return customerlistHandler.selectedValue; 
    } 
    
    def selectedCustomer; 
    def customerlistHandler = [ 
        getRows: { return 20; },             
        fetchList: {o-> 
            return service.getList(o);  
        }, 
        onOpenItem: {item,colname-> 
            select();
        }
    ] as PageListModel;

    void search() { 
        customerlistHandler.reload(); 
    } 
    
    void moveFirstPage() {  
        customerlistHandler.moveFirstPage(); 
    } 
    
    void moveBackPage() { 
        customerlistHandler.moveBackPage(); 
    } 
    
    void moveNextPage() {  
        customerlistHandler.moveNextPage(); 
    } 
    
    void moveLastPage() {} 

    Map createOpenerParams() {
        return [
            listModelHandler: this,
            callerContext   : createContextHandler()
        ];
    }    
   
    def createContextHandler() {
        def ctx = new CustomerSearchContext(this);
        ctx.selectHandler = {o-> 
            select(o);
            customerlistHandler.bindingObject.fireNavigation('_close');
        }
        return ctx;
    }
    
    def create() {
        def op = Inv.lookupOpener('customer:create', createOpenerParams());
        if (!op) return null;
        return op;
    }
    
    def view() {
        if (!selectedCustomer) return null;
        def params = createOpenerParams();
        params.entity = selectedCustomer;
        
        def op = Inv.lookupOpener('customer:open', params);
        if (!op) return null;
        return op;
    }
    
    def xcreate() {   
        def ctype = null;
        def params = [:];
        params.handler = { o-> ctype = o; }
        Modal.show('customer:selecttype', params); 
        if (!ctype) return null; 
        
        params.clear();
        //def op = Inv.lookupOpener('entity'+ ctype +':create', params);
        //op.target = 'popup';
        def op = Inv.lookupOpener('customer:' + ctype + ':create', params);
        if (!op) return null;
        return op;
    }
    
    def xview() { 
        if ( !selectedCustomer ) return null;
        
        def ctype = selectedCustomer.type.toString().toLowerCase();
        def params = [ entity: selectedCustomer ];
        //def op = Inv.lookupOpener('entity'+ ctype +':open', params ); 
        //op.target = 'popup'; 
        def op = Inv.lookupOpener('customer:' + ctype + ':open', params);
        if (!op) return null;
        return op;
    }    
} 