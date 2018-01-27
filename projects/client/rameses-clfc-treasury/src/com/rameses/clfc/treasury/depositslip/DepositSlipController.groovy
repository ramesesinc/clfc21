package com.rameses.clfc.treasury.depositslip;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class DepositSlipController extends CRUDController
{
    @Caller
    def caller;

    @Binding
    def binding;

    @Service("DateService")
    def dateSvc;

    @PropertyChangeListener
    def listener = [
        'entity.type': { o->
            binding?.refresh('opener');
        }
    ]
    
    String serviceName = "DepositSlipService";
    String entityName = "depositslip";

    boolean allowDelete = false;
    boolean allowApprove = false;
    boolean allowEdit = true;
    
    //Map createParams = [domain: 'TREASURY', role: 'ACCT_ASSISTANT'];
    //Map editParams = [domain: 'TREASURY', role: 'ACCT_ASSISTANT'];
    
    Map createPermission = [domain: 'TREASURY', role: 'CASHIER'];
    Map editPermission = [domain: 'TREASURY', role: 'CASHIER'];

    def typeList = ["cash", "check"];
    def passbook, currencytype, accounttype, deposittype, items = [];
    def passbookLookup = Inv.lookupOpener("passbook:lookup", [
        onselect: { o->
            entity.passbook = o;
            binding.refresh("passbook");
        },
        state   : 'ACTIVE'
    ]);    
    def currencyTypeLookup = Inv.lookupOpener("currencytype:lookup", [
        onselect: { o->
            entity.currencytype = o;
            binding.refresh('currencytype');
        },
        state   : 'ACTIVATED'
    ]);
    def accountTypeLookup = Inv.lookupOpener("accounttype:lookup", [
        onselect: { o->
            entity.accounttype = o;
            binding.refresh('accounttype');
        },
        state   : 'ACTIVATED'
    ]);
    def depositTypeLookup = Inv.lookupOpener("deposittype:lookup", [
        onselect: { o->
            entity.deposittype = o;
            binding.refresh('deposittype');
        },
        state   : 'ACTIVATED'
    ]);

    def totalbreakdown;

    Map createEntity() {
        totalbreakdown = 0;
        return [
            objid   : 'DS' + new UID(), 
            state   : 'DRAFT',
            checks  : [],
            cbs     : [],
            txndate : dateSvc.getServerDateAsString(),
            amount  : 0
            //cashbreakdown   : createCashBreakdown()
        ];
    }

    private def createCashBreakdown() {
        return [
            objid   : 'CB' + new UID(),
            items   : []
        ];
    }

    void afterOpen( data ) {
        /*if (!data.cashbreakdown) {
            data.cashbreakdown = [objid: 'CB' + new UID(), items: []];
        }
        if (data.cashbreakdown?.items) {
            totalbreakdown = data.cashbreakdown.items.amount.sum();
        }
        if (!totalbreakdown) totalbreakdown = 0;*/

        checkEditable(data);
        //if (data.state != 'DRAFT') allowEdit = false;
    }
    
    void afterSave( data ) {
        data._addedcbs = [];
        data._removedcbs = [];
        data._addedcheck = [];
        data._removedcheck = [];
    }
    
    void checkEditable( data ) {
        allowEdit = false;
        if (data.state == 'DRAFT') {
            allowEdit = true;
        }
        binding?.refresh('formActions');
    }
    
    def prevcbs, prevcheck, preventity;
    void afterEdit( data ) {
        preventity = [:];
        preventity.putAll(entity);
        prevcbs = copyList(data?.cbs);
        prevcheck = copyList(data?.check);
        /*
        def item;
        prevcbs = [];
        prevcheck = [];
        if (data.cbs) {
            data.cbs.each{ o->
                item = [:];
                item.putAll(o);
                prevcbs.add(item);
            }
        }
        
        if (data.checks) {
            data.checks.each{ o->
                item = [:];
                item.putAll(o);
                prevcheck.add(item);
            }
        }
        */
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
    
    void afterCancel() {
        if (preventity) {
            entity = preventity;
        }
        
        entity.cbs = [];
        if (prevcbs) {
            entity.cbs.addAll(prevcbs);
        }
        
        entity.checks = [];
        if (prevcheck) {
            entity.checks.addAll(prevcheck);
        }
        binding?.refresh();
    }

    def getOpener() {
        if (!entity.type) return;
        
        def op = Inv.lookupOpener('depositslip:' + entity?.type?.toLowerCase(), [entity: entity, mode: mode]);
        if (!op) return null;
        return op;
    }
    
    /*
    def getPluginList() {
        getOpeners: {
            return Inv.lookupOpeners("depositslip-plugin", [entity: entity, mode: mode]);
        }
    }
    */

    def getCashbreakdown() {
        if (!entity.items) entity.items = [];
        totalbreakdown = 0;
        if (entity.items) {
            totalbreakdown = entity.items.amount.sum();
        }
        def params = [
            entries         : entity.items,//entity.cashbreakdown.items,
            totalbreakdown  : totalbreakdown,
            editable        : false,//((mode != 'read' && entity.type == 'cash')? true : false),
        ];
        return Inv.lookupOpener('clfc:denomination', params);
    }
    
    /*
    boolean getAllowReinstate() {
        def date = dateSvc.getServerDateAsString().split(" ")[0];
        if (entity.state == 'CLOSED' && entity.txndate == date) return true;
        return false;
    }
    */

    void refresh() {
        binding.refresh();
    }

    void refresh( text ) {
        binding.refresh(text);
    }
    
    void submitForApproval() {
        if (!MsgBox.confirm('You are about to submit this document for approval. Continue?')) return;
        
        entity = service.submitForApproval(entity);
        checkEditable(entity);
        EventQueue.invokeLater({ caller?.reload(); }); 
    }
    
    void approveDocument() {
        if (!MsgBox.confirm('You are about to approve this document. Continue?')) return;
        
        entity = service.approveDocument(entity);
        checkEditable(entity);
        EventQueue.invokeLater({ caller?.reload(); }); 
    }
    
    void disapprove() {
        if (!MsgBox.confirm('You are about to disapprove this document. Continue?')) return;
        
        entity = service.disapprove(entity);
        checkEditable(entity);
        EventQueue.invokeLater({ caller?.reload(); }); 
    }
    
    def cancelDepositSlip() {
        def handler = { remarks->
            try {
                entity.cancelremarks = remarks;
                entity = service.cancelDepositSlip(entity);

                binding?.refresh(); 
                EventQueue.invokeLater({ caller?.reload(); }); 
            } catch (Throwable t) {
                MsgBox.err(t.message);
            }
        }
        def params = [title: 'Reason for Cancel', handler: handler];
        def op = Inv.lookupOpener('remarks:create', params);
        if (!op) return null;
        return op;
    }
    
    void approveCancel() {
        if (!MsgBox.confirm('You are about to approve cancellation for this document. Continue?')) return;
        
        entity = service.approveCancel(entity);
        checkEditable(entity);
        EventQueue.invokeLater({ caller?.reload(); }); 
    }
    
    void disapproveCancel() {
        if (!MsgBox.confirm('You are about to disapprove cancellation for this document. Continue?')) return;
        
        entity = service.disapproveCancel(entity);
        checkEditable(entity);
        EventQueue.invokeLater({ caller?.reload(); }); 
    }
    
    def viewCancelRemarks() {
        def params = [title: "Reason for Cancel", remarks: entity.cancelremarks];
        def op = Inv.lookupOpener("remarks:open", params);
        if (!op) return null;
        return op;
    }
}