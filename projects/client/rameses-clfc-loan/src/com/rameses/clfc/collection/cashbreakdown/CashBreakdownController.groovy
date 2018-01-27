package com.rameses.clfc.collection.cashbreakdown;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class CashBreakdownController
{
    @Caller
    def caller;
    
    @Binding
    def binding;
    
    @Service('CollectionCashbreakdownService')
    def service;
    
    @Service('DateService')
    def dateSvc;
    
    def entity, mode = 'read', breakdownmode = 'read';
    
    def totalbreakdown, preventity, prevbreakdown, collector, route;
    def routeList = [];
    String entityName = "cashbreakdown";
    
    def collectorLookup = Inv.lookupOpener('collector:lookup', [
        onselect: { o->
            entity.collector = o;
            if (o) {
                def params = [txndate: entity.txndate];
                params.collectorid = entity.collector.objid;

                routeList = service.getRoutes(params);
                binding.refresh('collector|route');
            }
        }
    ]);

    void create() {
        entity = createEntity();
        routeList = [];
        route = null;
        collector = null
        mode = 'create';
        breakdownmode = 'read';
    }

    Map createEntity() {
        totalbreakdown = 0;
        return [
            objid   : 'CB' + new UID(),
            state   : 'DRAFT',
            txndate : dateSvc.getServerDateAsString().split(" ")[0],
            amount  : 0,
            items   : []
        ]
    }

    public void setRoute( route ) {
        this.route = route;
        entity.collection = route.collection;
        entity.group = route.group;
    }

    void verify() {
        if (MsgBox.confirm("You are about to verify this document. Continue?")) {
            entity = service.verify(entity);
        }
    }

    void open() {
        entity =  service.open(entity);
        totalbreakdown = (entity.items? entity.items.amount.sum() : 0);
        routeList = service.getRoutes([txndate: entity.txndate, collectorid: entity.collector.objid]);
        route = routeList?.find{ it.itemid == entity.group?.objid }
        mode = 'read';
        breakdownmode = 'read';
    }

    def copyList( src ) {
        def list = [];
        def item;
        src?.each{ o->
            item = [:];
            item.putAll(o);
            list << item;
        }
        
        return list;
    }
    
    void edit() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        prevbreakdown = copyList(entity?.items);
        
        mode = 'edit';
        breakdownmode = 'edit';
    }
    
    void editBreakdown() {
        prevbreakdown = copyList(entity?.items);
        breakdownmode = 'edit';
    }
    
    def cancel() {
        if (mode == 'edit' || breakdownmode == 'edit') {
            if (!MsgBox.confirm('Cancelling will undo changes made. Continue?')) return;
            
            if (mode == 'edit') {
                if (preventity) {
                    entity = preventity;
                }
                mode = 'read';
            }
            
            if (breakdownmode == 'edit') {
                entity.items = [];
                if (prevbreakdown) {
                    entity.items.addAll(prevbreakdown);
                }
                breakdownmode = 'read';
            }
            
            binding?.refresh();
            
            return null;
        }
        return '_close';
    }
    
    def close() {
        return '_close';
    }
        
    void save() {
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        if (mode == 'create') {
            entity = service.create(entity);
        } else if (mode == 'edit') {
            entity = service.update(entity);
        }
        
        mode = 'read';
        binding?.refresh();
    }
    
    void saveBreakdown() {
        if (!MsgBox.confirm('You are about to save this document. Continue?')) return;
        
        entity = service.saveBreakdown(entity);
        breakdownmode = 'read';
        binding?.refresh();
    }
    
    def getCashbreakdown() { 
        def params = [
            entries         : entity.items,
            totalbreakdown  : totalbreakdown,
            editable        : (breakdownmode == 'edit'? true : false),
            onupdate        : {o->
                totalbreakdown = o;
            }
        ]
        def op = InvokerUtil.lookupOpener('clfc:denomination', params);
        if (!op) return null;
        return op;
    }
    
    def sendBack() {
        if (!MsgBox.confirm("You are about to send back this document. Continue?")) return;
        
        def handler = { remarks->
            try {
                entity = service.sendBack([objid: entity.objid, remarks: remarks]);
                EventQueue.invokeLater({
                     caller?.reload();
                     binding?.refresh();
                });
            } catch (Throwable t) {
                MsgBox.err(t.message);
            }
        }
        return Inv.lookupOpener("remarks:create", [title: "Reason for Send Back", handler: handler]);
    }
    
    def viewSendBackRemarks() {
        return Inv.lookupOpener('remarks:open', [title: 'Reason for Send Back', remarks: entity?.sendbackremarks]);
    }

    void submitForVerification() {
        if (!MsgBox.confirm('You are about to submit this document for verification. Continue?')) return;
        
        entity = service.submitForVerification(entity);
    }

    def viewLogs() {
        return Inv.lookupOpener('txnlog:open', [query: [txnid: entity.objid]]); 
    } 
}

/*
class CashBreakdownController extends CRUDController
{
    @Caller
    def caller;

    @Binding
    def binding;

    @Service("DateService")
    def dateSvc;

    String serviceName = "CollectionCashbreakdownService";
    String entityName = "cashbreakdown";

    boolean allowDelete = false;
    boolean allowCreate = false;
    boolean allowApprove = false;
    boolean allowEdit = false;
    
    Map createPermission = [domain: 'LOAN', role: 'CASHIER'];
    //Map editPermission = [domain: 'LOAN', role: 'CASHIER'];

    def totalbreakdown, prevbreakdown, collector, route;
    def routeList = [];

    def collectorLookup = Inv.lookupOpener('collector:lookup', [
        onselect: { o->
            entity.collector = o;
            
            def params = [txndate: entity.txndate];  
            if (!entity.collector) return [];
            params.collectorid = entity.collector.objid;

            routeList = service.getRoutes(params);
            binding.refresh('collector|route');
        }
    ]);

    Map createEntity() {
        totalbreakdown = 0;
        return [
            objid   : 'CB' + new UID(),
            state   : 'DRAFT',
            txndate : dateSvc.getServerDateAsString().split(" ")[0],
            amount  : 0,
            items   : []
        ]
    }

    public void setRoute( route ) {
        this.route = route;
        entity.collection = route.collection;
        entity.group = route.group;
    }

    void verify() {
        if (MsgBox.confirm("You are about to verify this document. Continue?")) {
            entity = service.verify(entity);
        }
    }

    void afterOpen( data ) {
        totalbreakdown = (data.items? data.items.amount.sum() : 0);
        if (data.allowEdit == true) {
            allowEdit = true;
        }
        binding?.refresh('formActions');
        routeList = service.getRoutes([txndate: data.txndate, collectorid: data.collector.objid]);
        route = routeList.find{ it.itemid == data.group.objid }
    }

    void afterEdit( data ) {
        prevbreakdown = [];
        def item;
        data.items.each{ o->
            item = [:];
            item.putAll(o);
            prevbreakdown.add(item);
        }
    }

    void afterCancel() {
        entity.items = prevbreakdown;
    }
    
    def getCashbreakdown() { 
        def params = [
            entries         : entity.items,
            totalbreakdown  : totalbreakdown,
            editable        : (mode == 'edit'? true : false),
            onupdate        : {o->
                totalbreakdown = o;
            }
        ]
        return InvokerUtil.lookupOpener('clfc:denomination', params);
    }

    def reconcile() {
        if (MsgBox.confirm("You are about to reconcile this record. Continue?")) {
            return Inv.lookupOpener('remarks:create', [
                title: 'Reason for reconciliation', 
                handler: {remarks-> 
                    entity = service.reconcile([objid: entity.objid, remarks: remarks]); 
                    if (entity.allowEdit==true) allowEdit = true;
                    EventQueue.invokeLater({ 
                        caller?.reload(); 
                        binding?.refresh();
                    }); 
                } 
            ]); 
        }
    }
 
    def sendBack() {
        if (!MsgBox.confirm("You are about to send back this document. Continue?")) return;
        
        def handler = { remarks->
            try {
                entity = service.sendBack([objid: entity.objid, remarks: remarks]);
                EventQueue.invokeLater({
                     caller?.reload();
                     binding?.refresh();
                });
            } catch (Throwable t) {
                MsgBox.err(t.message);
            }
        }
        return Inv.lookupOpener("remarks:create", [title: "Reason for Send Back", handler: handler]);
    }
    
    def viewSendBackRemarks() {
        return Inv.lookupOpener('remarks:open', [title: 'Reason for Send Back', remarks: entity?.sendbackremarks]);
    }

    void submitForVerification() {
        if (!MsgBox.confirm('You are about to submit this document for verification. Continue?')) return;
        
        entity = service.submitForVerification(entity);
    }

    def viewLogs() {
        return Inv.lookupOpener('txnlog:open', [query: [txnid: entity.objid]]); 
    }   

}
*/