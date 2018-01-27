package com.rameses.clfc.loan.billing;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
import java.rmi.server.UID;

class LoanLedgerBillingController
{
    @Caller
    def caller;
    
    @Binding
    def binding;
    
    @Service("DateService")
    def dateSvc;
    
    @Service("LoanLedgerBillingService")
    def service;
    
    def getTitle() {
        String text = "Collection Sheet";

        if (mode == "create")
          return text + " (New)";
        if (mode == "edit") {
          return text + " (Edit)";
        }
        return text;
    }
    
    def entity, mode = 'read';
    
    String entityName = 'ledgerbilling';
    
    def prevroutes, preventity, selectedItem;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    def collectorLookup = Inv.lookupOpener('route-collector:lookup', [
         onselect: { o->
             entity.collector = o;
             binding?.refresh();
         }
    ]);
        
    void create() {
        entity = createEntity();
        mode = 'create';
        listHandler?.reload();
    }
    
    void open() {
        entity = service.open(entity);
        mode = 'read';
        listHandler?.reload();
    }
    
    Map createEntity() {
        def data = [ 
            objid       : 'LB' + new UID(),//'LB'+new java.rmi.server.UID(), 
            routes      : [],
            billdate    : dateSvc.getServerDateAsString().split(" ")[0]
        ];
        
        return data;
    }
    
    def listHandler = [
        fetchList: { o->
            if (!entity.routes) entity.routes = [];
            return entity.routes; 
        }
    ] as BasicListModel;
    
    /*void beforeSave( data ) {
        if (!data.routes) throw new Exception('Please specify route(s) for this collector.');
        //if (mode == 'edit') allowEdit = false;
    } */       
    
    def save() {
        //if (!entity.routes)
        //    throw new Exception("Please specify route(s) for this collector.");
                    
        def handler
        if (!handler) {
            handler = [
                onMessage: { o->
                    println 'on message';
                    //println 'onMessage '  + o;
                    //println 'EOF ' + AsyncHandler.EOF;

                    if (o == AsyncHandler.EOF) {
                        loadingOpener.handle.binding.fireNavigation("_close");
                        return;
                    }        

                    loadingOpener.handle.binding.fireNavigation("_close");
                    entity.putAll(o);

                    if (entity._added) entity.remove('_added');
                    if (entity._removed) entity.remove('_removed');
                    //entity._added = [];
                    //entity._removed = [];

                    def msg = "Billing created successfully!";
                    if (mode == 'edit') msg = "Billing updated successfully!";

                    mode = 'read';
                
                    EventQueue.invokeLater({
                        binding?.refresh();
                        caller?.reload();
                    });
                
                    MsgBox.alert(msg);

                },
                onTimeout: {
                    println 'on timeout';
                    handler?.retry(); 
                },
                onCancel: {
                    println 'on cancel';
                    println 'processing cancelled.';
                    //fires when cancel() method is executed 
                }, 
                onError: { o->
                    println 'on error';
                    loadingOpener.handle.binding.fireNavigation("_close");
                    MsgBox.err(o.message);
                    /*if (o instanceof java.util.concurrent.TimeoutException) {

                    } else {
                        throw new Exception(o.message);
                    }*/
                }
            ] as AbstractAsyncHandler;
        }
        
        if (mode == 'create') {
            service.create(entity, handler);
        } else if (mode == 'edit') {
            service.update(entity, handler);
        }
        
        return loadingOpener;
        //svc.rebuild(entity, handler);
    }
    
    public boolean getEditable() {
        def flag = false;
        if (mode == 'read' && entity.editable) flag = true;
        return flag;
    }  

    public boolean getResetable() {
        def flag = false;
        if (mode == 'read' && entity.resetable) flag = true;
        return flag;
    }

    def addItem() {
        def handler = { r->
            //println 'route ' + r;
            
            def i = entity?.routes?.find{ it.code == r.code }
            if (i) throw new Exception('Route ' + r.description + ' - ' + r.area + ' already selected.');
            
            if (!r.itemid) r.itemid = 'LI' + new UID();
            
            if (!entity._added) entity._added = [];
            entity._added << r;
            
            if (!entity.routes) entity.routes = [];
            entity.routes << r;
            
            listHandler?.reload();
        }
        def op = Inv.lookupOpener('route:lookup', [onselect: handler]);
        if (!op) return null;
        
        return op;
    }
    
    void edit() {
        def item;
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        prevroutes = [];
        if (entity?.routes) {
            entity?.routes?.each{ o->
                item = [:];
                item.putAll(o);
                prevroutes << item;
            }
        }
        
        mode = 'edit';
    }
    
    def cancel() {
        if (mode == 'edit') {
            if (!MsgBox.confirm('Cancelling will undo changes made. Continue?')) return;
            
            if (preventity) {
                entity = preventity;
            }

            entity.routes = [];
            if (prevroutes) {
                entity.routes.addAll(prevroutes);
            }
            
            mode = 'read';
            
            EventQueue.invokeLater({
                 binding?.refresh();
                 listHandler?.reload();
            });
            
            return null;
        }
        return close();
    }
    
    def close() {
        return "_close";
    }
    
    void removeItem() {
        if (!selectedItem) return;
        
        if (!MsgBox.confirm('You are about to remove this item. Continue?')) return;
        
        if (!entity._removed) entity._removed = [];
        entity._removed << selectedItem;
        
        if (entity._added) entity._added.remove(selectedItem);
        
        entity.routes.remove(selectedItem);
        
        listHandler?.reload();
    }

    def reset() {
        if (!MsgBox.confirm("You are about to reset this billing. Continue?")) return;
        
        def handler;
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
                    EventQueue.invokeLater({ 
                        caller?.reload();
                        binding?.refresh();
                    });
                    MsgBox.alert("Resetting has been successfully processed.", true);
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
                    //throw new Exception(o.message);
                    //println 'o';
                    /*if (o instanceof java.util.concurrent.TimeoutException) {

                    } else {
                        throw new Exception(o.message);
                    }*/
                }
            ] as AbstractAsyncHandler;
        }
        service.resetBilling(entity, handler);
        return loadingOpener;
    }
    
    void setSubBiling( subbillingid ) {
        entity.subbillingid = subbillingid;
    }
    
    void refreshList() {
        EventQueue.invokeLater({
            caller?.reload();
        })
    }
    
    void cancelBilling() {
        if (!MsgBox.confirm('You are about to cancel this billing. Continue?')) return;
        
        entity = service.cancelBilling(entity);
        EventQueue.invokeLater({ 
            caller?.reload();
            binding?.refresh();
            listHandler?.reload();
        });
        
    }
    
    def createSubBilling() {
        def op = Inv.lookupOpener('ledger:subbilling:create', [entity: entity]);
        if (!op) return null;
        return op;
    }
    
    def openSubBilling() {
        def op = Inv.lookupOpener('ledger:subbilling:open', [entity: entity]);
        if (!op) return null;
        return op;
    }
}
