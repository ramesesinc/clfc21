package com.rameses.clfc.loan.fieldcollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.framework.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.LoanUtil;

class PostFieldCollectionController {
    
    @Binding
    def binding;
    
    @Caller
    def caller;
    
    @Service("LoanFieldCollectionService")
    def service;
    
    String title = "Field Collection";
    
    def collection, mode = 'read';
    def entity, prevcashbreakdown;
    
    void init() {
        getCollectionInfo();
    }
    
    void getCollectionInfo() {
        entity = service.getFieldCollection( [itemid: collection.itemid] );
        entity.collection = collection.type;
        if (!entity.route) entity.route = [objid: collection.groupid, type: collection.grouptype, description: collection.description];
        
        buildConsolidatedCashBreakdown( entity );
    }
    
    def back() {
        return "_close";
    }
 
    void save() {
        entity.cashbreakdown = service.updateCashBreakdown(entity.cashbreakdown);
        mode = 'read';
    }
    
    void edit() {
        prevcashbreakdown = [];
        entity?.cashbreakdown?.items?.each{ o->
            def item = [:];
            item.putAll(o);
            prevcashbreakdown << item;
        }
        mode = 'edit';
        //itemsModel?.reload();
        //binding?.refresh()
    }
    
    void cancel() {
        if (!MsgBox.confirm('Cancelling will undo changes made to cash breakdown. Continue?')) return;
        
        if (prevcashbreakdown) {
            entity.cashbreakdown.items = prevcashbreakdown;
        }
        buildConsolidatedCashBreakdown( entity );
        
        mode = 'read';
        binding?.refresh();
    }

    def viewCollectionSheets() {
        def title = "Field Collection: " + entity.route.description + (entity.route.area? " - " + entity.route.area : "");
        def op = Inv.lookupOpener('fcloan:open', [type: collection.grouptype, collectionid: collection.itemid, title: title])
        if (!op) return null;
        op.caption = title;
        return op;
    }
    
    def getTotalbreakdown() {
        def amt = entity.cashbreakdown?.items?.amount?.sum();
        if (!amt) amt = 0;
        return amt;
    }
    
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
                buildConsolidatedCashBreakdown( entity );
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
    def post() {
        if (!MsgBox.confirm('You are about to post this collection. Continue?')) return;
        
        service.post( entity );
        EventQueue.invokeLater({ caller?.refresh(); });
        MsgBox.alert("Collection successfully posted!");
        return "_close";
    }
    
    def overage() {
        //println 'overage';
        def handler = { o->
            getCollectionInfo();
            binding.refresh();
        }

        def allowCreate = false;
        if (totalbreakdown == null) totalbreakdown = 0;
        if (entity.totalamount < totalbreakdown && entity?.hassendback==true && getIsECA()) {
            allowCreate = true;
        }

        def params = [
            remittanceid: entity.remittance?.objid,
            collector   : entity.remittance?.collector,
            txndate     : entity.billdate,
            handler     : handler,
            allowCreate : allowCreate
        ];
        def op = Inv.lookupOpener('overage:list', params);
        if (!op) return null;
        return op;
    }
    
    def shortage() {
        //println 'shortage';
        def handler = { o->
            getCollectionInfo();
            binding.refresh();
        }

        def allowCreate = false;
        if (!totalbreakdown) totalbreakdown = 0;
        if (entity.totalamount > totalbreakdown && entity?.hassendback==true && getIsECA()) {
            allowCreate = true;
        }

        def params = [
            remittanceid: entity.remittance?.objid,
            collector   : entity.remittance?.collector,
            txndate     : entity.billdate,
            handler     : handler,
            allowCreate : allowCreate
        ];
        def op = Inv.lookupOpener('shortage:list', params);
        if (!op) return null;
        return op;
    }
    
    def getIsECA() {
        def roles = ClientContext.currentContext.headers.ROLES;
        
        def flag = false;
        if (roles.containsKey('LOAN.CASHIER')) {
            flag = true;
        }
        return flag;
    }

    def sendback() {
        def handler = { o->
            def params = [
                remittance  : entity?.remittance,
                itemid      : collection.itemid
            ]
            entity = service.sendBack( params );
            EventQueue.invokeLater({
                caller?.refresh();
                binding.refresh('formActions');
            });
        }

        def params = [
            remittanceid    : entity.remittance?.objid,
            action          : 'sendback',
            afterSaveHandler: handler
        ];

        def op = Inv.lookupOpener('sendback:open', params);
        if (!op) return null;
        return op;
    }
}

