package com.rameses.clfc.ledger.followupcollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
import java.rmi.server.UID;

abstract class AbstractFollowupCollectionController 
{	
    @Binding
    def binding;
    
    @Service("DateService")
    def dateSvc;
    
    @Service("LoanBillingGroupService")
    def billingGroupSvc;

    abstract String getServiceName();
    abstract Map createEntity();
    
    def getService() {
        String name = getServiceName();
        if ((name == null) || (name.trim().length() == 0)) {
          throw new NullPointerException("Please specify a serviceName");
        }
        return InvokerProxy.getInstance().create(name);
    }

    public String getTitle() {
        String text = "Follow-up Collection";

        if (mode == "create")
          text += " (New)";
        if (mode == "edit") {
          text += " (Edit)";
        }
        return text;
    }   
    
    String entityName = "followupcollection";
    def mode = 'read';
    def entity, showconfirmation = true;

    def prevledgers, preventity;

    
    void create() {
        entity = createEntity();
        mode = 'create';
        listHandler?.reload();
    }
    
    void open() {
        entity = service.open(entity);
        mode = 'read';
        listHandler?.reload();
    }
    
    /*
    Map createEntity() {
        def map = [
            objid       : 'FC' + new UID(),
            state       : 'DRAFT',
            billingid   : 'LB' + new UID(),
            txntype     : 'ONLINE',
            txndate     : dateSvc.getServerDateAsString().split(" ")[0],
            //itemtype    : 'followup'
        ];
        showconfirmation = true;
        return map;
    }
    */
    
    def collectorLookupHandler = Inv.lookupOpener('route-collector:lookup', [:]);
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

    
    void save() {
        if (!MsgBox.confirm("You are about to save this document. Continue?")) return;
        
        def msg;
        if (mode == 'create') {
            entity = service.create(entity);
            msg = "Follow-up collection created successfully.";
        } else if (mode == 'edit') {
            entity = service.update(entity);
            msg = "Follow-up collection updated successfully.";
        }
        
        if (entity._addedledger) entity.remove('_addedledger');
        if (entity._removedledger) entity.remove('_removedledger');
        
        mode = 'read';
        MsgBox.alert(msg);
        EventQueue.invokeLater({
            listHandler?.reload();
            binding?.refresh('formActions');
        });
    }
   
    void edit() {
        mode = 'edit';
        
        preventity = [:];
        if (entity) {
            preventity.putAll(entity);
        }
        
        prevledgers = [];
        if (entity.ledgers) {
            def item;
            entity?.ledgers?.each{ o->
                item = [:];
                item.putAll(o);
                prevledgers << item;
            }
        }
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

    def addLedgersFromGroup() {
        if (!entity.txndate) throw new Exception("Please specify date.");
        
        def handler = { o->
            def list = billingGroupSvc.getDetailsWithLedgerInfo([objid: o.objid]);
            def m;
            list.each{ i->
                m = entity.ledgers.find{ i.objid == it.objid }
                if (!m) {
                    i.scdetailid = 'FCD' + new UID();
                    
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
            type    : 'FOLLOWUP',
            date    : entity.txndate
        ]
        def op = Inv.lookupOpener('billinggroup:lookup', params);
        if (!op) return null;
        return op;
    }

    def addLedger() {
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

    def removeLedger() {
        if (!selectedLedger) return;
        
        if (!MsgBox.confirm('You are about to remove this ledger. Continue?')) return;
        
        if (!entity._removedledger) entity._removedledger = [];
        entity._removedledger << selectedLedger;
        
        if (entity._addedledger) {
            entity._addedledger.remove(selectedLedger);
        }
        
        entity.ledgers?.remove(selectedLedger);
        
        listHandler?.reload();
    }
    
    void resetBilling() {
        if (!MsgBox.confirm("You are about to reset this billing. Continue?")) return;
        
        entity = service.resetBilling(entity);
        MsgBox.alert("Resetting billing has been successfully processed.");
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
            MsgBox.alert("Follow-up collection billing created successfully!");
            EventQueue.invokeLater({
                binding?.refresh();
                listHandler?.reload();
            });
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
        def op = Inv.lookupOpener('remarks:create', [title: 'Reason for cancellation', handler: handler]);
        if (!op) return null;
        
        return op;
    }
    
    void disapprove() {
        if (!MsgBox.confirm("You are about to disapprove this document. Continue?")) return;
        
        entity = service.disapprove(entity);
    }
}

