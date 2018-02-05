package com.rameses.clfc.patch.loan.branch

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;

class ReconstructBranchLoanController {

    @Binding
    def binding;
    
    @Service('ReconstructBranchLoanService')
    def service;
    
    String title = 'Reconstruct Branch Loan';
    
    def entity, selectedItem;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    
    def branchLoanLookup = Inv.lookupOpener('reconstruct:branchloan:lookup', [
        onselect: { o->
            entity = service.getBranchLoanInfo(o);
            binding?.refresh();
            listHandler?.reload();
        }
    ]);
    
    void init() {
        entity = [:];
    }
    
    def close() {
        return '_close';
    }
    
    def listHandler = [
        fetchList: { o->
            if (!entity.items) entity.items = [];
            return entity.items;
        },
        onColumnUpdate: { itm, colName->
            if (colName == '_remove' && itm[colName] == true) {
                itm._removeledger = false;
            } else if (colName == '_removeledger' && itm[colName] == true) {
                itm._remove = false;
            }
        }
    ] as EditorListModel;
    
    /*
    void reconstructLoan() {
        entity = service.reconstructLoan(entity);
        binding?.refresh();
        listHandler?.reload();
    }
    */
   
    def reconstructLoan() {
        def handler
        if (!handler) {
            handler = [
                onMessage: { o->
                    //println 'onMessage '  + o;
                    //println 'EOF ' + AsyncHandler.EOF;
                    if (o == AsyncHandler.EOF) {
                        loadingOpener.handle.binding.fireNavigation("_close");
                        return;
                    }        

                    loadingOpener.handle.binding.fireNavigation("_close");
                    
                    def msg = "Successfully reconstructed branch loan!";
                    entity = o;
                    
                    binding?.refresh();
                    listHandler?.reload();
                    MsgBox.alert(msg);
                },
                onTimeout: {
                    handler?.retry(); 
                },
                onCancel: {
                    println 'processing cancelled.';
                    //fires when cancel() method is executed 
                }, 
                onError: { o->
                    loadingOpener.handle.binding.fireNavigation("_close");
                    MsgBox.err(o.message);
                    /*if (o instanceof java.util.concurrent.TimeoutException) {

                    } else {
                        throw new Exception(o.message);
                    }*/
                }
            ] as AbstractAsyncHandler;
        }
        service.reconstructLoan(entity, handler);
        return loadingOpener;
    }
    
    
    def addItem() {
        def handler = { o->
        
            //if (!entity._added) entity._added = [];
            //entity._added << o;
            entity.items << o;
            
            listHandler?.reload();
        }
        
        def op = Inv.lookupOpener('reconstruct:branchloan:item:create', [handler: handler]);
        if (!op) return null;
        return op;
    }
    
    def editItem() {
        if (!selectedItem) return;
        
        def handler = { o->
            def item = entity?.items?.find{ it.objid == o.objid }
            if (!item) throw new Exception('Item record does not exist or has already been deleted.');
            
            item.putAll(o);
            /*
            if (entity._added) {
                item = entity._added.find{ it.objid == o.objid }
                item.putAll(o);
            }
            */
           
            listHandler?.reload();
        }
        
        def op = Inv.lookupOpener('reconstruct:branchloan:item:edit', [entity: selectedItem, handler: handler]);
        if (!op) return null;
        return op;
    }
    
//    void removeItem() {
//        if (!selectedItem) return;
//        
//        if (!MsgBox.confirm('You are about to remove this item. Continue?')) return;
//        
//        if (!entity._removed) entity._removed = [];
//        entity._removed << selectedItem;
//        
//        if (entity._added) entity._added.remove(selectedItem);
//        entity.items.remove(selectedItem);
//        
//        listHandler?.reload();
//    }
}

