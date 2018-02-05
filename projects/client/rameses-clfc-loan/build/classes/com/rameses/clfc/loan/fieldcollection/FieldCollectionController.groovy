package com.rameses.clfc.loan.fieldcollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.*;
import com.rameses.clfc.util.LoanUtil;
import java.rmi.server.UID;

class FieldCollectionController 
{
    @Binding
    def binding;
    
    @Service("DateService")
    def dateSvc;
    
    @Service('LoanFieldCollectionService')
    def service;
    
    String title = 'Post Field Collection';
    
    def action = 'init', billdate, collector, route;
    def collectorList, entity, totalbreakdown = 0;
    def mode = 'read', prevcashbreakdown;
    
    void init() {
        action = 'init';
        mode = "read";
        billdate = dateSvc.getServerDateAsString().split(' ')[0];
        collectorList = service.getCollectors();
    }
    
    def close() {
        return '_close';
    }
    
    def next() {
        mode = 'read';
        action = 'view';
        getFieldCollection();
        return "main";
    }
    
    def back() {
        action = "init";
        return "default";
    }
    
    def getRouteList() {
        if (!collector) return [];
        def params = [
            collectorid : collector.objid,
            billdate    : billdate
        ];
        return service.getRoutes(params);
    }
 
    void save() {
        entity.cashbreakdown = service.updateCashBreakdown(entity.cashbreakdown);
        mode = 'read';
    }
    
    void edit() {
        prevcashbreakdown = [];
        def item;
        entity?.cashbreakdown?.items?.each{ o->
            item = [:];
            item.putAll(o);
            prevcashbreakdown.add(item);
        }
        mode = 'edit';
        //itemsModel?.reload();
        //binding?.refresh()
    }
    
    void cancel() {
        if (!MsgBox.confirm('Cancelling will undo changes made to cash breakdown. Continue?')) return;
        
        if (prevcashbreakdown) {
            entity.cashbreakdown.items = [];
            entity.cashbreakdown.items.addAll(prevcashbreakdown);
        }
        
        mode = 'read';
        //itemsModel?.reload();
    }
    
    void getFieldCollection() {
        entity = service.getFieldCollection([itemid: route.itemid]);

        entity.billdate = billdate;
        entity.collection = route.type;
        if (!entity.route) entity.route = route;
        
        buildConsolidatedCashBreakdown(entity);
        computeTotalBreakdown();
    }
    
    void computeTotalBreakdown() {
        totalbreakdown = entity.cashbreakdown?.items?.amount?.sum();
        if (!totalbreakdown) totalbreakdown = 0;
        //def tb = entity.cashbreakdown.items?.amount?.sum();
        //if (!tb) tb = 0;
        
        //def tb1 = entity.shortagebreakdown?.items?.amount?.sum();
        //if (!tb1) tb1 = 0;
        
        //totalbreakdown = tb + tb1;
    }
     
    /*
    def itemsModel = [
        getOpeners: {
            def list = Inv.lookupOpeners("fieldcollection:breakdown:plugin", [entity: entity, mode: mode]);
            list = [];
            
            return list;
        },
        getOpenerParams: { o->
            buildConsolidatedCashBreakdown(entity);
            computeTotalBreakdown();
            return [entity: entity, mode: mode];
        }
    ] as TabbedPaneModel;
    */
   
    def getConsolidatedCashbreakdown() {
        if (!entity.consolidatedbreakdown) entity.consolidatedbreakdown = [:];
        if (!entity.consolidatedbreakdown.items) entity.consolidatedbreakdown.items = [];
        
        def tb = entity.consolidatedbreakdown?.items?.amount?.sum();
        if (!tb) tb = 0;
        def params = [
            entries         : entity.consolidatedbreakdown?.items,//entity?.cashbreakdown?.items,
            totalbreakdown  : tb, //totalbreakdown,
            editable        : false, //(mode != 'read'? true: false),
        ];
        
        def op = Inv.lookupOpener("clfc:denomination:nopanel", params);
        if (!op) return null;
        return op;
    }
    
    def getCollectionCashbreakdown() {
        if (!entity.cashbreakdown) entity.cashbreakdown = [:];
        if (!entity.cashbreakdown.items) entity.cashbreakdown.items = [];
        
        def tb = entity.cashbreakdown?.items?.amount?.sum();
        if (!tb) tb = 0;
        def params = [
            entries         : entity.cashbreakdown?.items,//entity?.cashbreakdown?.items,
            totalbreakdown  : tb, //totalbreakdown,
            editable        : (mode != 'read'? true: false),
            onupdate        : { o->
                buildConsolidatedCashBreakdown(entity);
                computeTotalBreakdown()
                binding?.refresh("consolidatedCashbreakdown");
            }
        ];
        
        def op = Inv.lookupOpener("clfc:denomination:nopanel", params);
        if (!op) return null;
        return op;
    }
    
    def getShortageCashbreakdown() {
        if (!entity.shortagebreakdown) entity.shortagebreakdown = [:];
        if (!entity.shortagebreakdown.items) entity.shortagebreakdown.items = [];
        
        def tb = entity.shortagebreakdown?.items?.amount?.sum();
        if (!tb) tb = 0;
        def params = [
            entries         : entity.shortagebreakdown?.items,//entity?.cashbreakdown?.items,
            totalbreakdown  : tb, //totalbreakdown,
            editable        : false, //(mode != 'read'? true: false),
        ];
        
        def op = Inv.lookupOpener("clfc:denomination:nopanel", params);
        if (!op) return null;
        return op;
    }
    
    void buildConsolidatedCashBreakdown( params ) {
        entity.consolidatedbreakdown = [items: buildConsolidatedCashBreakdownImpl(params)];
    }
    
    def buildConsolidatedCashBreakdownImpl( params ) {
        def list = [];
        LoanUtil.denominations.each{ o->
            def map = [:];
            map.putAll(o);
            
            def i = params.cashbreakdown.items?.find{ it.denomination==o.denomination && it.qty > 0 }
            if (i) map.qty += i.qty;
            
            i = params.shortagebreakdown?.items?.find{ it.denomination==o.denomination && it.qty > 0 }
            if (i) map.qty += i.qty;
            
            map.amount = map.qty * map.denomination;
            
            list << map;
        }
        
        return list;
    }
    
    void submitCbsForVerification() {
        if (!MsgBox.confirm("You are about to submit CBS for this collection for verification. Continue?")) return;
        
        entity.cashbreakdown = service.submitCbsForVerification(entity);
        getFieldCollection();
    }
    
    def viewCbsSendbackRemarks() {
        return Inv.lookupOpener('remarks:open', [title: 'Reason for Send Back', remarks: entity.cashbreakdown.sendbackremarks])
    }
    
    def getTotalCollection() {
        //if (page == 'special') return entity.routes.total.sum();
        //return entity.route.total;
        return entity.totalamount;
    }
    
    def post() {
        if (!MsgBox.confirm('You are about to post this collection. Continue?')) return;
        
        service.post(entity);
        MsgBox.alert("Collection successfully posted!");
        entity = [:];
        mode = 'read';
        action = 'init';
        return 'default';
    }

    def viewCollectionSheets() {
        def title = "Field Collection: " + route.description + (route.area? " - " + route.area : "");
        def opener = InvokerUtil.lookupOpener('fcloan:open', [type: route.type, collectionid: route.itemid, title: title])
        opener.caption = title;
        return opener;
    }
    
    def overage() {
        //println 'overage';
        def handler = { o->
            getFieldCollection();
            binding.refresh();
        }

        def allowCreate = false;
        if (totalbreakdown == null) totalbreakdown = 0;
        if (entity.totalamount < totalbreakdown && entity?.hassendback==true && getIsCashier()) {
            allowCreate = true;
        }

        def params = [
            remittanceid: entity.remittance?.objid,
            collector   : collector,
            txndate     : billdate,
            handler     : handler,
            allowCreate : allowCreate
        ];
        return Inv.lookupOpener('overage:list', params);
    }
    
    def getIsCashier() {
        def roles = ClientContext.currentContext.headers.ROLES;
        
        def flag = false;
        if (roles.containsKey('LOAN.CASHIER')) {
            flag = true;
        }
        return flag;
        //return (ClientContext.currentContext.headers.ROLES.containsKey("LOAN.FIELD_COLLECTOR")? true : false);
    }

    def shortage() {
        //println 'shortage';
        def handler = { o->
            getFieldCollection();
            binding.refresh();
        }

        def allowCreate = false;
        if (!totalbreakdown) totalbreakdown = 0;
        if (entity.totalamount > totalbreakdown && entity?.hassendback==true && getIsCashier()) {
            allowCreate = true;
        }

        def params = [
            remittanceid: entity.remittance?.objid,
            collector   : collector,
            txndate     : entity.billdate,
            handler     : handler,
            allowCreate : allowCreate
        ];
        return Inv.lookupOpener('shortage:list', params);
    }

    def sendback() {
        def handler = { o->
            def params = [
                remittance  : entity?.remittance,
                itemid      : route.itemid
            ]
            entity = service.sendBack(params);
            binding.refresh('formActions');
        }

        def params = [
            remittanceid    : entity.remittance?.objid,
            action          : 'sendback',
            afterSaveHandler: handler
        ];

        return Inv.lookupOpener('sendback:open', params);
    }
    
    
}

