package com.rameses.clfc.ledger.adjustment

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;
import java.rmi.server.UID;

class NewLedgerAdjustmentController {

    @Caller
    def caller;
    
    @Binding
    def binding;
    
    @Service("NewLedgerAdjustmentService")
    def service;
    
    def entity, mode = 'read';
    def ledger, preventity;
    def prevdebit, prevcredit;
    def prevmodify, prevmodifydebit, prevmodifycredit;
    def tabs;
    
    def borrowerLookupHandler = Inv.lookupOpener('ledgerborrower:lookup', [
        onselect: { o->
            entity.ledgerid = o.objid;
            entity.borrower = o.borrower;
        }
    ]);

    def createEntity() {
        def data = [
            objid       : 'LLA' + new UID(),
            txnstate    : 'DRAFT',
            ledgerid    : ledger?.objid,
            requesttype : 'ADJUSTMENT',
            debit       : [:],
            credit      : [:]
        ];
        
        def info = service.getLedgerInfo([ledgerid: data.ledgerid]);
        if (info) {
            data.loanapp = info.app;
            data.borrower = info.borrower;
        }
        
        return data;
    }
    
    void create() {
        entity = createEntity();
        mode = "create";
    }
    
    void open() {
        entity = service.open(entity);
        mode = "read";
        binding?.refresh('tabs');
    }
    
    def close() {
        return "_close";
    }
    
    def cancel() {
        if (mode == "edit") {
            
            if (preventity) {
                entity = preventity;
            }
            
            if (prevdebit) {
                entity.debit = prevdebit;
            }
            
            if (prevcredit) {
                entity.credit = prevcredit;
            }
            mode = "read";
            
            return null;
        }
        
        return "_close";
    }
    
    void cancelModify() {
        
        if (mode == "modify") {
            entity.txnstate = "APPROVED";
            entity.requesttype = "ADJUSTMENT";
            entity.modify = [:];
        } else if (mode == "editmodify") {
            if (prevmodify) {
                entity.modify = prevmodify;
            }

            if (prevmodifydebit) {
                entity.modify.debit = prevmodifydebit;
            }

            if (prevmodifycredit) {
                entity.modify.credit = prevmodifycredit;
            }

            prevmodifydebit = [:];
            if (entity.modify.debit) {
                prevmodifydebit.putAll(entity.modify.debit);
            }

            prevmodifycredit = [:];
            if (entity.modify.credit) {
                prevmodifycredit.putAll(entity.modify.credit);
            }
        }
        
        mode = "read";
    }
    
    void edit() {
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        prevdebit = [:];
        if (entity.debit) {
            prevdebit.putAll(entity.debit);
            def item = [:];
            if (entity.debit.item) {
                item.putAll(entity.debit.item);
            }
            prevdebit.item = item;
        }
        
        prevcredit = [:];
        if (entity.credit) {
            prevcredit.putAll(entity.credit);
            def item = [:];
            if (entity.credit.item) {
                item.putAll(entity.credit.item);
            }
            prevcredit.item = item;
        }
        
        mode = "edit";
    }
    
    void editModify() {
        prevmodify = [:];
        if (entity.modify) {
            prevmodify.putAll(entity.modify);
        }
        
        prevmodifydebit = [:];
        if (entity.modify.debit) {
            prevmodifydebit.putAll(entity.modify.debit);
        }
        
        prevmodifycredit = [:];
        if (entity.modify.credit) {
            prevmodifycredit.putAll(entity.modify.credit);
        }
        
        mode = "editmodify";
    }
    
    void validate( data ) {
        def msg = '';
        
        if (!data.txndate) msg += "Date is required.\n";
        if (!data.debit.item.name) msg += "Debit type is required.\n";
        if (!data.debit.amount) msg += "Debit amount is required.\n";
        if (!data.credit.item.name) msg += "Credit type is required.\n";
        if (!data.credit.amount) msg += "Credit amount is required.\n";
        if (!data.remarks) msg += "Remarks is required.\n";
        
        if (msg) throw new RuntimeException(msg);
    }
    
    void validateModify( data ) {
        def msg = "";
        
        def mod = data.modify;
        if (!mod.txndate) msg += "Modify Date is required.\n";
        if (!mod.debit.item?.name) msg += "Modify debit type is required.\n";
        if (!mod.debit.amount) msg += "Modify debit amount is required.\n";
        if (!mod.credit.item?.name) msg += "Modify credit type is required.\n";
        if (!mod.credit.amount) msg += "Modify credit amount is required.\n";
        if (!mod.remarks) msg += "Modify remarks is required.\n";
        
        if (msg) throw new RuntimeException(msg);
    }
    
    void save() {
        validate(entity);
        
        if (entity.debit.item.name == entity.credit.item.name) {
            throw new RuntimeException("Type credited must not be the same with type debited.");
        }
        
        if (!MsgBox.confirm("You are about to save this document. Continue?")) return;
        
        if (mode == "create") {
            entity = service.create(entity);
        } else if (mode == "edit") {
            entity = service.update(entity);
        }
        
        mode = "read";
        
        EventQueue.invokeLater({
             caller?.reload();
             binding?.refresh();
        });
    }
    
    void saveModify() {
        validateModify(entity);
        
        if (entity.modify.debit.item?.name == entity.modify.credit.item?.name) {
            throw new RuntimeException("Type credited must not be the same with type debited.");
        }
        
        if (!MsgBox.confirm("You are about to save this document. Continue?")) return;
        
        entity = service.saveModify(entity);
        
        mode = "read";
        
        EventQueue.invokeLater({
             caller?.reload();
             binding?.refresh();
        });
    }
    
    def tabHandler = [
        getOpeners: {
            def opts = Inv.lookupOpeners('ledger:adjustment:tab', [entity: entity, mode: mode]);
            
            def list = [];
            list.addAll(opts);
            if (entity.requesttype != 'MODIFY') {
                opts?.each{ o->
                    def props = o.properties;
                    if (props.reftype=='modify') {
                        list.remove(o);
                    }
                }
            }
            
            return list;
        }
    ] as TabbedPaneModel 
    
    
    void submitForApproval() {
        if (!MsgBox.confirm("You are about to submit this document for approval. Continue?")) return;
        
        entity = service.submitForApproval(entity);
        EventQueue.invokeLater({
             caller?.reload();
             binding?.refresh();
        });
    }
    
    void submitForApprovalModify() {
        if (!MsgBox.confirm("You are about to submit this docuemnt for approval. Continue?")) return;
        
        entity = service.submitForApprovalModify(entity);
        EventQueue.invokeLater({
             caller?.reload();
             binding?.refresh();
        });
    }
    
    void approveDocument() {
        if (!MsgBox.confirm("You are about approve this document. Continue?")) return;
        
        entity = service.approveDocument(entity);
        EventQueue.invokeLater({
             caller?.reload();
             binding?.refresh();
        });
    } 
    
    void disapproveDocument() {
        if (!MsgBox.confirm("You are about to disapprove this document. Continue?")) return;
        
        entity = service.disapproveDocument(entity);
        EventQueue.invokeLater({
             caller?.reload();
             binding?.refresh();
        });
    }
    
    void approveModify() {
        if (!MsgBox.confirm("You are about to approve delete request for this document. Continue?")) return;
        
        entity = service.approveModify(entity);
        EventQueue.invokeLater({
            caller?.reload();
            binding?.refresh('formActions');
        });
    }
    
    void disapproveModify() {
        if (!MsgBox.confirm("You are about to disapprove delete request for this document. Continue?")) return;
        
        entity = service.disapproveModify(entity);
        EventQueue.invokeLater({
            caller?.reload();
            binding?.refresh('formActions');
        });
        
    }
    
    void modify() {
        //ismodify = true;
        entity.requesttype = 'MODIFY';
        entity.txnstate = 'DRAFT';
        entity.modify = [
            txndate : entity.txndate,
            debit   : [:],
            credit  : [:]
        ]
        entity.modify.debit.putAll(entity.debit);
        entity.modify.credit.putAll(entity.credit);
        mode = 'modify';
    }
    
    def requestForDelete() {
        def handler = { remarks->
            try {
                entity = service.requestForDelete([objid: entity.objid, remarks: remarks]);
                //allowEdit = false;
                EventQueue.invokeLater({
                    caller?.reload();
                    binding?.refresh();
                });
            } catch (Throwable t) {
                MsgBox.err(t.message);
            }
        }
        return Inv.lookupOpener("remarks:create", [title: "Reason for Delete", handler: handler]);
    }
    
    def viewDeleteRequest() {
        return Inv.lookupOpener("remarks:open", [title: "Reason for Delete", remarks: entity.deleteremarks]);
    }
    
    void approveDelete() {
        if (!MsgBox.confirm("You are about to approve delete request for this document. Continue?")) return;
        
        entity = service.approveDelete(entity);
        EventQueue.invokeLater({
            caller?.reload();
            binding?.refresh('formActions');
        });
    }
    
    void disapproveDelete() {
        if (!MsgBox.confirm("You are about to disapprove delete request for this document. Continue?")) return;
        
        entity = service.disapproveDelete(entity);
        EventQueue.invokeLater({
            caller?.reload();
            binding?.refresh('formActions');
        });
        
    }
}

