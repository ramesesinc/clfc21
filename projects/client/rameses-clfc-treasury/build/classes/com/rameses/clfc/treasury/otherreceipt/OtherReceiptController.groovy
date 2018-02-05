package com.rameses.clfc.treasury.otherreceipt

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class OtherReceiptController {

    @Binding
    def binding;
    
    @Service('OtherReceiptService')
    def service;
    
    @Service('DateService')
    def dateSvc;
        
    @PropertyChangeListener
    def listener = [
        'txndate': { o->
            binding?.refresh('collector|collection');
        },
        'collector': { o->
            binding?.refresh('collection');
        },
        'selectedPayment': { o->
            println 'selected payment ' + o;
            binding?.refres('formActions');
        }
    ];
    
    String title = 'Post Other Receipt Collection';
    
    def txndate, collector, collection;
    def action = 'default';
    def entity, mode = 'read', totalcollection = 0;
    
    def init() {
        txndate = dateSvc.getServerDateAsString().split(' ')[0];
        action = 'default';
        return 'default';
    }
    
    def getCollectorList() {
        def list = service.getCollectorList([txndate: txndate]);
        if (!list) list = [];
        return list;
    }
    
    /*
    void setCollector( collector ) {
        this.collector = collector;
        println 'collector ' + collector;
    }
    */
    
    def getCollectionList() {
        def params = [txndate: txndate, collectorid: collector?.objid];
        def list = service.getCollectionList(params);
        if (!list) list = [];
        return list;
    }
    
    def close() {
        return '_close';
    }
    
    def collectPayment() {
        def handler = { o->
            service.savePayment(o);
            MsgBox.alert('Payment successfully saved!');
            EventQueue.invokeLater({
                 binding?.refresh();
            });
        }
        
        def op = Inv.lookupOpener('otherreceipt:payment', [handler: handler]);
        if (!op) return null;
        return op;
    }
    
    def next() {
        entity = service.getOtherReceiptInformation(collection);
        if (!entity) entity = [:];
        
        def list = entity?.payments?.findAll{ it.state != 'VOIDED' }
        totalcollection = list?.amount?.sum();
        if (!totalcollection) totalcollection = 0;
        
        listHandler?.reload();
        mode = 'read';
        
        action = 'posting';
        return 'posting';
    }
    
    def back() {
        action = 'default';
        return 'default';
    }
    
    def selectedPayment;
    def listHandler = [
        fetchList: { o->
            if (!entity.payments) entity.payments = [];
            return entity.payments;
        }
    ] as BasicListModel;
        
    void setSelectedPayment( selectedPayment ) {
        this.selectedPayment = selectedPayment;
        binding?.refresh('formActions');
    }
    
    def getTotalcash() {
        def list = entity?.payments?.findAll{ it.payoption == 'cash' && it.state != 'VOIDED' };
        def amt = list?.amount?.sum();
        if (!amt) amt = 0;
        return amt;
    }
    
    def getTotalnoncash() {
        def list = entity?.payments?.findAll{ it.payoption == 'check' && it.state != 'VOIDED' };
        def amt = list?.amount?.sum();
        if (!amt) amt = 0;
        return amt;
    }
    
    def getCashbreakdown() {
        
        def params = [
            entries         : entity.cashbreakdown.items,
            totalbreakdown  : getTotalbreakdown(),
            editable        : mode != 'read',
            onupdate        : { o->
                //totalbreakdown = o;
            }
        ]
        def op = InvokerUtil.lookupOpener('clfc:denomination', params);
        if (!op) return null;
        return op;
    }
    
    void remitCollection() {
        if (!MsgBox.confirm('You are about to remit this collection. You cannot void any payment(s) after remitting. Continue?')) return;
        
        entity = service.remitCollection(entity);
    }
    
    def getTotalbreakdown() {
        def total = entity?.cashbreakdown?.items?.amount?.sum();
        if (!total) total = 0;
        
        return total;
    }
    
    def prevbreakdown;
    void editBreakdown() {
        prevbreakdown = [];
        entity?.cashbreakdown?.items?.each{ o->
            def item = [:];
            item.putAll(o);
            prevbreakdown << item;
        }
        
        mode = 'edit';
    }
    
    void cancelBreakdown() {
        if (prevbreakdown) {
            entity?.cashbreakdown?.items = prevbreakdown;
        }
        
        mode = 'read';
    }
    
    void saveBreakdown() {
        entity.cashbreakdown = service.saveBreakdown(entity.cashbreakdown);
        
        mode = 'read';
    }
    
    def postCollection() {
        if (!MsgBox.confirm('You about to post this collection. Continue?')) return;
        
        service.postCollection(entity);
        MsgBox.alert('Successfully posted collection!');
        action = 'default';
        return 'default';
    }
    
    void reloadPayments() {
        entity = service.getOtherReceiptInformation(entity);
        println 'has cash ' + entity.hascash;
        //entity.payments = service.getPayments(entity);
        listHandler?.reload();
        
        def list = entity?.payments?.findAll{ it.state != 'VOIDED' }
        totalcollection = list?.amount?.sum();
        if (!totalcollection) totalcollection = 0;
        binding?.refresh();
    }
    
    
    def voidPayment() {
        if (!selectedPayment) return;
        
        def invtype = 'otherreceipt:voidrequest';
        if (!selectedPayment?.voidpaymentid) {
            invtype += ':create';
        } else {
            invtype += ':open';
        }
        
        def params = [
            payment : selectedPayment,
            entity  : [objid: selectedPayment?.voidpaymentid]
        ];
        
        def op = Inv.lookupOpener(invtype, params);
        if (!op) return null;
        return op;
    }
}

