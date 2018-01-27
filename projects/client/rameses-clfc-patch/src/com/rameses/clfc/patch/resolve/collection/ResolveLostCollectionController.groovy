package com.rameses.clfc.patch.resolve.collection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class ResolveLostCollectionController extends CRUDController
{
    @Binding
    def binding;
    
    String serviceName = 'ResolveLostCollectionService';
    
    Map createPermission = [domain: 'ADMIN', role: 'ADMIN_SUPPORT'];
    Map editPermission = [domain: 'ADMIN', role: 'ADMIN_SUPPORT'];
    
    boolean allowDelete = false;
    boolean allowApprove = false;
    boolean allowEdit = true;
    /*
    @Service('ResolveLostCollectionService')
    def service;
    */
    
    def routeLookup = Inv.lookupOpener('lostcollection:route:lookup', [
         onselect: { o->
             entity.route = o;
             binding?.refresh();
         }
    ]);

    def paymentLookup = Inv.lookupOpener('lostcollection:loanpayment:lookup', [
         onselect: { o->
             def info = service.getPaymentInfo(o);
             if (info) {
                 entity.putAll(info);
                 listHandler?.reload();
             }
             binding?.refresh();
         }
    ]);

    def collectorLookup = Inv.lookupOpener('route-collector:lookup', [
         onselect: { o->
             entity.collector = o;
             binding?.refresh();
         }
    ]);

    def getTypeList() {
        def list = service.getTypeList();
        if (!list) list = [];
        return list;
    }

    Map createEntity() {
        def data = [
            objid   : 'RLC' + new UID(), 
            txnstate: 'DRAFT',
            cbs     : [objid: 'CB' + new UID()],
            billing : [objid: 'LB' + new UID()]
        ];
    }
    
    void afterCreate( data ) {
        listHandler?.reload();
    }
    
    void afterOpen( data ) {
        checkEditable(data);
    }
    
    void checkEditable( data ) {
        allowEdit = false;
        if (data.txnstate == 'DRAFT') {
            allowEdit = true;
        }
    }
    
    def selectedItem;
    def listHandler = [
        fetchList: { o->
            if (!entity.payments) entity.payments = [];
            return entity.payments;
        }
    ] as BasicListModel;
    
    void submitForApproval() {
        if (!MsgBox.confirm('You are about to submit this document for approval. Continue?')) return;
        
        entity = service.submitForApproval(entity);
        checkEditable(entity);
    }
    
    void approveDocument() {
        if (!MsgBox.confirm('You are about to approve this document. Continue?')) return;
        
        entity = service.approveDocument(entity);
        checkEditable(entity);
    }
    
    void disapprove() {
        if (!MsgBox.confirm('You are about to disapprove this document. Continue?')) return;
        
        entity = service.disapprove(entity);
        checkEditable(entity);
    }
}

