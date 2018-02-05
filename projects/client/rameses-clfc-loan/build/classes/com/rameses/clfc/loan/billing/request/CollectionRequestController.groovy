package com.rameses.clfc.ledger.specialcollection;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.framework.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
import java.rmi.server.UID;

class CollectionRequestController
{
    @Binding
    def binding;

    @Service("LoanSpecialCollectionRequestService")
    def service;

    @Service("LoanBillingGroupService")
    def billingGroupSvc;
    
    def getTitle() {
        String str
        str = 'Special';
        if (entity?.isfollowup == true) {
            str = 'Follow-up';
        }
        str += ' Collection Request';
        
        return str;
    }
    
    def entity, showconfirmation = true;
    def mode = 'read';
    def prevledgers, preventity;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);


    def selectedLedger;
    def listHandler = [
        fetchList: {
            if (!entity.ledgers) entity.ledgers = [];
            return entity.ledgers;
        },
        onRemoveItem: { o->
            removeLedger();
        }
    ] as BasicListModel;
    
    void open() {
        entity = service.open(entity);
        if (entity.iscreatemode==true) mode = 'create';
        binding?.refresh('title');
        //if (entity.state == 'PENDING' && ClientContext.currentContext.headers.ROLES.containsKey("LOAN.ACCT_ASSISTANT")) 
        //    mode = 'create';
    }
    
    void save() {
        if (!MsgBox.confirm("You are about to save this document. Continue?")) return;
        
        entity = service.update(entity);
        
        if (entity._addedledger) entity.remove('_addedledger');
        if (entity._removedledger) entity.remove('_removedledger');
        
        mode = 'read';
        MsgBox.alert("Special collection updated successfully.");
        EventQueue.invokeLater({
            listHandler?.reload();
            binding?.refresh('formActions');
        });
    }
    
    def cancel() {
        if (mode == 'edit') {
            if (!MsgBox.confirm('Cancelling will undo changes made. Continue?')) return;
            
            if (preventity) {
                entity = preventity;
            }
            
            if (prevledgers) {
                entity.ledgers = [];
                entity.ledgers.addAll(prevledgers);
            }
            
            if (entity._addedledger) entity.remove('_addedledger');
            if (entity._removedledger) entity.remove('_removedledger');
            
            mode = 'read';
            EventQueue.invokeLater({
                listHandler?.reload();
                binding?.refresh();
            });
        
            return null;
        }
        return close();
    }
 
    def close() {
        return "_close";
    }

    void edit() {
        mode = 'edit';
        prevledgers = [];
        if (entity?.ledgers) {
            def item;
            entity?.ledgers?.each{ o->
                item = [:];
                item.putAll(o);
                prevledgers << item;
            }
        }
        
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
    }

    def addLedgersFromGroup() {
        if (!entity.txndate) throw new Exception("Please specify date.");
        
        def handler = { o->
            def list = billingGroupSvc.getDetailsWithLedgerInfo([objid: o.objid]);
            def m;
            list?.each{ i->
                m = entity.ledgers.find{ i.objid == it.objid }
                if (!m) {
                    i.scdetailid = 'SCD' + new UID();
                    
                    if (!entity._addedledger) entity._addedledger = [];
                    //entity._addedledger.add(i);
                    entity._addedledger << i;
                    
                    if (!entity.ledgers) entity.ledgers = [];
                    //entity.ledgers.add(i);
                    entity.ledgers << i;
                }
            }
            listHandler?.reload();
        }
        def params = [
            onselect: handler,
            state   : 'APPROVED',
            type    : entity?.isfollowup == true? 'FOLLOWUP' : 'SPECIAL',
            date    : entity.txndate
        ]
        def op = Inv.lookupOpener('billinggroup:lookup', params);
        if (!op) return null;
        return op;
    }
    
    def addLedger() {
        //if (!entity.collector) throw new Exception("Please specify collector.");

        def handler = { o->
            def itm = entity?.ledgers?.find{ o.objid == it.objid }
            if (itm) throw new Exception("Ledger has already been selected.");

            if (!o.scdetailid) o.scdetailid =  'SCD' + new UID();
            
            if (!entity._addedledger) entity._addedledger = [];
            entity._addedledger << o;
            
            if (!entity.ledgers) entity.ledgers = [];
            entity?.ledgers << o;
            
            listHandler.reload();
        }
        
        def params = [onselect: handler];
        def op = Inv.lookupOpener('specialcollectionledger:lookup', params);
        if (!op) return null;
        
        return op;
    }
    
    void removeLedger() {
        if (!selectedLedger) return;
        
        if (!MsgBox.confirm('You are about to remove this ledger. Continue?')) return;
        
        if (!entity._removedledger) entity._removedledger = [];
        entity._removedledger << selectedLedger;
        
        if (entity._addedledger) {
            entity._addedledger.remove(selectedLedger);
        }
        
        entity.ledgers?.remove(selectedLedger);
        
        /*
        if (entity.routes) {
            entity.routes?.remove(selectedLedger.route);
        }
        */
        
        listHandler?.reload();
    }
    
    void submitForVerification() {
        if (!MsgBox.confirm("You are about to submit this document for verification. Continue?")) return;
        
        entity = service.submitForVerification(entity);
    }
    
    void verify() {
        if (!MsgBox.confirm("You are about to verify this document. Continue?")) return;
        
        entity = service.verify(entity);
    }
    
    void submitForApproval() {
        if (!MsgBox.confirm("You are about to submit this document for approval. Continue?")) return;
        
        entity = service.submitForApproval(entity);
    }
    
    void approveDocument() {
        if (!MsgBox.confirm("You are about to approve this document. Continue?")) return;
        
        entity = service.approveDocument(entity);
    }
    
    def createBilling() {
        if (!MsgBox.confirm("You are about to create billing for this document. Continue?")) return;
                
        def onMessageImpl = { o->
            //println 'onMessage '  + o;
            //println 'EOF ' + AsyncHandler.EOF;
            //loadingOpener.handle.binding.fireNavigation("_close");
            loadingOpener.handle.closeForm();

            if (o == AsyncHandler.EOF) {
                //loadingOpener.handle.binding.fireNavigation("_close");
                return;
            }
            entity = o;
            //entity.putAll(o);
            //def msg = ;
            //if (mode == 'edit') msg = "Follow-up collection updated successfully!";
            //mode = 'read';
            MsgBox.alert("Special collection request billing created successfully!");
            //println "Special collection billing created successfully!";
            binding?.refresh();
            listHandler?.reload();  
        }
        
        loadingOpener = Inv.lookupOpener('popup:loading', [:]);
        def handler = [
            onMessage   : onMessageImpl,
            onError     : { p->
                //loadingOpener.handle.binding.fireNavigation('_close');
                loadingOpener.handle.closeForm();
                
                if (showconfirmation==true) {
                    def msg = p.message;
                    msg += '\nDo you still want to continue to create this billing?';
                    if (MsgBox.confirm(msg)) {
                        showconfirmation = false;
                        def xhandler = { i->
                            def handler2 = [
                                onMessage   : onMessageImpl,
                                onError     : { o->
                                    //loadingOpener.handle.binding.fireNavigation('_close');  
                                    loadingOpener.handle.closeForm();
                                    MsgBox.err(o.message); 
                                }
                            ] as AsyncHandler;
                            service.createNewBillingWithoutLedgerValidation(entity, handler2);
                        }
                        loadingOpener = Inv.lookupOpener('popup:loading', [handler: xhandler]);
                        binding.fireNavigation(loadingOpener);
                    }
                    //def confirmhandler = {                        
                        
                    //}
                    //Modal.show('popup:confirmation', [handler: confirmhandler, text: msg], [title: "Confirmation"]);
                } else {
                    MsgBox.err(p.message);
                }
            }
        ] as AsyncHandler;
        service.createNewBilling(entity, handler);
        return loadingOpener;
    }
    
    def cancelBilling() {
        if (!MsgBox.confirm("You are about to cancel creation for this billing. Continue?")) return;
        
        def handler = { remarks->
            entity.cancelremarks = remarks;
            entity = service.cancelBilling(entity);
            
            MsgBox.alert("Billing successfully cancelled!");
            binding?.refresh();
        }
        return Inv.lookupOpener('remarks:create', [title: 'Reason for cancellation', handler: handler]);
    }
    
    void disapprove() {
        if (!MsgBox.confirm("You are about to disapprove this document. Continue?")) return;
        
        entity = service.disapprove(entity);
    }
}