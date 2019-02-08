package com.rameses.clfc.loan.onlinecollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.framework.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.LoanUtil;

class PostOnlineCollectionController {

    @Binding
    def binding;
    
    @Caller
    def caller;
    
    @Service("LoanOnlineCollectionService")
    def service;
    
    String title = "Online Collection";
    
    def collection, mode = 'read';
    def entity, prevcashbreakdown;
    def selectedPayment, breakdownPanel;
    
    void init() {
        getCollectionInfo();
    }
    
    void getCollectionInfo() {
        entity = service.getCollection( [collector: collection.collector, collection: [objid: collection.itemid]] );
        buildConsolidatedCashBreakdown( entity );
        
        binding?.refresh();
        listHandler?.reload();
    }
    
    def back() {
        return "_close";
    }
    
    def getTotalcashbreakdown() {
        def amt = entity.cashbreakdown?.items?.amount?.sum();
        if (!amt) amt = 0;
        
        return amt;
    }
    
    def getTotalbreakdown() {
        def amt = entity?.consolidatedbreakdown?.items?.amount?.sum();
        if (!amt) amt = 0;
        
        return amt;
    }
    
    def getState() {
        return entity?.state;
    }
    
    def listHandler = [
        getColumns: { o->
            return service.getCollectionColumns( o );
        },
        fetchList: { o->
            if (!entity.payments) entity.payments = [];
            return entity.payments;
        }
    ] as BasicListModel;
        
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
    
    void save() {        
        if (mode != 'read') {
            entity.cashbreakdown = service.updateCashBreakdown(entity.cashbreakdown);
        }
        mode = 'read';
        binding?.refresh();
    }
    
    void edit() {
        prevcashbreakdown = [];
        
        def item;
        entity?.cashbreakdown?.items?.each{ o->
            item = [:];
            item.putAll(o);
            prevcashbreakdown << item;
        }
        
        mode = 'edit';
        binding?.refresh();
    }
    
    void cancel() {
        entity?.cashbreakdown?.items = [];
        if (prevcashbreakdown) {
            entity?.cashbreakdown?.items = prevcashbreakdown;
        }
        buildConsolidatedCashBreakdown( entity );
        
        mode = 'read';
        binding?.refresh();
    }
        
    void remit() {
        if (!MsgBox.confirm('You are about to remit this collection. Continue?')) return;
        
        entity.objid = collection?.groupid;
        entity = service.remit( entity );
        buildConsolidatedCashBreakdown( entity );
        
        EventQueue.invokeLater({
             binding?.refresh("breakdownPanel");
             caller?.refresh();
        });
    }
    
    void returnToDraft() {
        entity = service.returnToDraft( entity );
        
        EventQueue.invokeLater({
             binding?.refresh();
             caller?.refresh();
        });
    }
    
    void verify() {
        entity = service.verify( entity );
        buildConsolidatedCashBreakdown( entity );
        
        EventQueue.invokeLater({
             binding?.refresh();
             caller?.refresh();
        });
    }
    
    def post() {
        if (!MsgBox.confirm('You are about to post this collection. Continue?')) return;
        
        service.post( entity );
        EventQueue.invokeLater({ caller?.refresh(); });
        MsgBox.alert("Collection successfully posted!");
        return "_close";
    }
    
    def overage() {
        def handler = { o->
            getCollectionInfo();
            binding.refresh();
        }
        
        def allowCreate = false;
        if (entity.totalcashamount < getTotalcashbreakdown() && getIsAA()) {
            allowCreate = true;
        }

        def params = [
            remittanceid: entity.remittance?.objid,
            collector   : entity.collector,
            txndate     : entity.txndate,
            handler     : handler,
            allowCreate : allowCreate
        ];
        def op = Inv.lookupOpener('overage:list', params);
        if (!op) return null;
        return op;
    }

    def shortage() {
        def handler = { o->
            getCollectionInfo();
            binding.refresh();
        }
        
        def allowCreate = false;
        if (entity.totalcashamount > getTotalcashbreakdown() && getIsAA()) {
            allowCreate = true;
        }

        def params = [
            remittanceid: entity.remittance?.objid,
            collector   : entity.collector,
            txndate     : entity.txndate,
            handler     : handler,
            allowCreate : allowCreate
        ];
        def op = Inv.lookupOpener('shortage:list', params);
        if (!op) return null;
        return op;
    }
    
    def getIsAA() {
        def roles = ClientContext.currentContext.headers.ROLES;
        
        def flag = false;
        if (roles.containsKey('LOAN.ACCT_ASSISTANT')) {
            flag = true;
        }
        return flag;
    }
    
    def voidPayment() {
        if (!selectedPayment) return;
        
        if (selectedPayment?.allowvoid==false) {
            MsgBox.alert("Cannot void this payment.");
            return;
        }
        
        def op;
        if (selectedPayment.voidid) {
            op = openVoidRequest(selectedPayment);
        } else {
            op = createVoidRequest(selectedPayment);
        }
        
        if (!op) return null;
        return op;
    }
    
    def getVoidRequestParameters( payment ) {
        def handler = { o->
            getCollection();
            binding?.refresh();
        }
        
        def params = [
            txncode                 : 'ONLINE',
            collectionid            : payment.parentid,
            afterSaveHandler        : handler,
            afterApproveHandler     : handler,
            afterDisapproveHandler  : handler
        ];
        
        return params;
    }
    
    def createVoidRequest( payment ) {
        def params = getVoidRequestParameters( payment );
        params.route = payment.route;
        params.collectionsheet = [
            loanapp : payment.loanapp,
            borrower: payment.borrower
        ];
        params.payment = payment;
        params.collector = [
            objid   : ClientContext.currentContext.headers.USERID,
            name    : ClientContext.currentContext.headers.NAME
        ];
        
        def op = Inv.lookupOpener('voidrequest:create', params);
        if (!op) return null;
        return op;
    }

    def openVoidRequest( payment ) {
        def params = getVoidRequestParameters( payment );        
        params.payment = payment;
        def op = Inv.lookupOpener('voidrequest:open', params);
        if (!op) return null;
        return op;
    }
    
    def viewCbsSendbackRemarks() {
        def param = [
            title: 'Reason for Send Back',
            remarks: entity.cashbreakdown?.sendbackremarks
        ];
        def op = Inv.lookupOpener('remarks:open', param);
        if (!op) return null;
        return op;
        //return Inv.lookupOpener('remarks:open', [title: 'Reason for Send Back', remarks: entity.cashbreakdown.sendbackremarks])
    }
    
    void submitCbsForVerification() {
        if (!MsgBox.confirm("You are about to submit CBS for this collection for verification. Continue?")) return;
        
        entity.cashbreakdown = service.submitCbsForVerification( entity );
        getCollectionInfo();
    }
}

