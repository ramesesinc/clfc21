package com.rameses.clfc.treasury.checkout;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.*;

class CheckoutController extends CRUDController {
    
    @Caller
    def caller;
    
    @Binding
    def binding;
    
    @Service('DateService')
    def dateSvc;
    
    String serviceName = 'CheckoutService';
    String entityName = 'checkout';
    
    boolean allowApprove = false;
    boolean allowDelete = false;
    boolean allowEdit = true;
    
    def selectedDepositSlip;
    def previtems, preventity;
    
    Map createEntity() {
        return [
            objid   : 'CO' + new UID(), 
            txndate : dateSvc.getServerDateAsString().split(' ')[0],
            txnstate: 'DRAFT'
        ];
    }
    
    void afterOpen( data ) {
        checkEditable(data);
    }
    
    def getAssigneeLookup() {
        def handler = { o->
            if (entity?.representative2?.objid == o.objid) {
                throw new Exception('Representative #1 must not be the same as Representative #2.');
            }

            entity.representative1 = o;
        }
        return assigneeImpl(handler);
    }
    
    void clearRep1() {
        entity?.representative1 = null;
        binding?.refresh('entity.representative1');
    }
    
    def getAssignee2Lookup() {
        def handler = { o->
            if (entity?.representative1?.objid == o.objid) {
                throw new Exception('Representative #2 must not be the same as Representative #1.');
            }
            
            entity.representative2 = o;
        }
        return assigneeImpl(handler);
    }
    
    void clearRep2() {
        entity?.representative2 = null;
        binding?.refresh('entity.representative2');
    }
    
    def assigneeImpl( handler ) {
        def op = Inv.lookupOpener('vaultrepresentative:lookup', [onselect: handler]);
        if (!op) return null;
        return op;
    }
    
    void checkEditable( data ) {
        allowEdit = false;
        if (data.txnstate == 'DRAFT') {
            allowEdit = true;
        }
        binding?.refresh('formActions');
    }
    
    void afterCreate( data ) {
        listHandler?.reload();
    }
    
    void afterEdit( data ) {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        previtems = [];
        if (entity.depositslips) {
            entity.depositslips?.each{ o->
                def i = [:];
                i.putAll(o);
                previtems << i;
            }
        }
    }
    
    void afterCancel() {
        
        if (preventity) {
            entity = preventity;
        }
        
        if (previtems) {
            entity.depositslips = previtems;
            listHandler?.reload();
        }
        entity?.remove('_addedds');
        entity?.remove('_removedds');
    }
    
    void afterSave( data ) {
        data?.remove('_addedds');
        data?.remove('_removedds');
        
        EventQueue.invokeLater({
             caller?.reload();
        });
    }
    
    def listHandler = [
        fetchList: { o->
            if (!entity.depositslips) entity.depositslips = [];
            return entity.depositslips;
        }
    ] as BasicListModel;
    
    def addDepositSlip() {
        def handler = { o->
            def item = entity.depositslips.find{ it.refid==o.objid }
            //def item = entity.depositslips.find{ it.depositslip.controlno == o.controlno }
            if (item) throw new Exception("This deposit slip has already been selected.");
           
            item = [
                objid       : 'COD' + new UID(),
                parentid    : entity.objid,
                refid       : o.objid,
                txndate     : o.txndate,
                depositslip : [
                    controlno   : o.controlno,
                    acctno      : o.passbook.acctno,
                    acctname    : o.passbook.acctname,
                    amount      : o.amount
                ]
            ]
            
            if (!entity._addedds) entity._addedds = [];
            entity._addedds << item;
            
            entity.depositslips << (item);
            listHandler?.reload();
        }
        
        def params = [
            state   : 'CLOSED',
            reftype : 'SAFE_KEEP',
            onselect: handler
        ];
        def op = Inv.lookupOpener("depositslip:lookup", params);
        if (!op) return null;
        return op;
    }
    
    void removeDepositSlip() {
        if (!MsgBox.confirm('You are about to remove this deposit slip. Continue?')) return;
        
        if (!entity._removedds) entity._removedds = [];
        entity._removedds << selectedDepositSlip;
        
        if (entity._addedds) entity._addedds.remove(selectedDepositSlip);
        entity.depositslips.remove(selectedDepositSlip);
        
        listHandler?.reload();
    }
    
    void confirm() {
        if (!MsgBox.confirm('You are about to confirm this check-out. Continue?')) return;
        
        entity = service.confirm(entity);
        checkEditable(entity);
        
        EventQueue.invokeLater({
             caller?.reload();
        });
    }
}