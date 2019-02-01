package com.rameses.clfc.billinggroup

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
import com.rameses.common.*;

class BillingGroupController extends CRUDController {
    
    @Caller
    def caller;
    
    @Binding
    def binding;
    
    @Service('DateService')
    def dateSvc;
    
    @PropertyChangeListener
    def listener = [
        "entity.dtstarted": { o->
            entity.dtended = o;
        }
    ]
    
    String serviceName = 'LoanBillingGroupService';
    String entityName = "billing:group";
    
    boolean allowApprove = false;
    boolean allowDelete = false;
    boolean allowEdit = true;
    def loadingOpener;
    
    def collectorLookup = Inv.lookupOpener("route-collector:lookup", [
         onselect: { o->
             //println 'o-> ' + o;
             entity.collector = o;
             binding?.refresh();
         }
    ])
    def options;
    
    Map createEntity() {
        def date = dateSvc.getServerDateAsString().split(' ')[0];
        return [
            objid               : "BG" + new UID(),
            txnstate            : 'DRAFT',
            dtstarted           : date
        ];
    }
    
    def selectedLedger, selectedLedgerToAdd = [:];
    def listHandler = [
        fetchList: { o->
            if (!entity.items) entity.items = [];
            return entity.items;
        }
    ] as BasicListModel;
    
    void addLedger() {
        if (!selectedLedgerToAdd) throw new Exception('Please select a ledger to add.');
        
        def i = entity?.items?.find{ it.ledgerid == selectedLedgerToAdd.ledger.objid }
        if (i) throw new Exception('This ledger has already been selected.');
                
        def item = [:];
        item.putAll(selectedLedgerToAdd);
        item.objid = 'BGD' + new UID();
        item.parentid = entity.objid;
        item.ledgerid = item.ledger.objid;
        
        if (!entity._addedledgers) entity._addedledgers = [];
        entity._addedledgers.add(item);
        
        if (!entity.items) entity.items = [];
        entity.items.add(item);
        
        listHandler?.reload();
    }
    
    void removeLedger() {
        if (!MsgBox.confirm('You are about to remove this ledger. Continue?')) return;
        
        if (!entity._removedledgers) entity._removedledgers = [];
        entity._removedledgers.add(selectedLedger);
        
        if (entity._addedledgers) entity._addedledgers.remove(selectedLedger);
        entity.items.remove(selectedLedger);
        
        listHandler?.reload();
    }
    
    void afterOpen( data ){
        checkEditable(data);
    }
    
    void checkEditable( data ) {
        allowEdit = false;
        if (data.txnstate == 'DRAFT') {
            allowEdit = true;
        }
        binding?.refresh('formActions');
    }
    
    void afterCreate( data ) {
        selectedLedgerToAdd = [:];
        listHandler?.reload();
        binding?.refresh('options');
    }
    
    void afterSave( data ) {
        data._removedledgers = [];
        data._addedledgers = [];
        EventQueue.invokeLater({ caller?.reload(); });
    }
    
    def getTypeList() {
        return service.getTypes();
    }
    
    void reloadOptionList() {
        binding?.refresh('opener');
    }
    /*
    def getOpener() {
        def handler = { o->
            if (!selectedLedgerToAdd) selectedLedgerToAdd = [:];
            selectedLedgerToAdd.clear();
            println 'entity ' + o;
            if (o) {
                selectedLedgerToAdd.putAll(o);
            }
            
        }
        
        def params = [
            selectedLedgerToAdd : selectedLedgerToAdd,
            addLedgerHandler    : handler
        ]
        def op = Inv.lookupOpener('billinggroup-option-opener', params);
        if (!op) return null;
        return op;
    }
    */
   
    def addLedgerHandler = { o->
        if (!selectedLedgerToAdd) selectedLedgerToAdd = [:];
        selectedLedgerToAdd.clear();
        if (o) {
            selectedLedgerToAdd.putAll(o);
        }

    }
    
    def optionsList = [
        fetchList: { o->
            if (!selectedLedgerToAdd) selectedLedgerToAdd = [:];

            def params = [
                selectedLedgerToAdd : selectedLedgerToAdd,
                addLedgerHandler    : addLedgerHandler
            ];
            def list = Inv.lookupOpeners("billinggroup-option", params);

            list?.sort{ it.properties.index }
            return list;
        }
    ] as TabbedPaneModel;
    
    /*
    def getOptionsList() {
        getOpeners: {
            if (!selectedLedgerToAdd) selectedLedgerToAdd = [:];

            def params = [
                selectedLedgerToAdd : selectedLedgerToAdd,
                addLedgerHandler    : addLedgerHandler
            ];
            def list = Inv.lookupOpeners("billinggroup-option", params);

            list?.sort{ it.properties.index }
            return list;
        }
    }
    */
    
    void submitForApproval() {
        if (!MsgBox.confirm('You are about to submit this document for approval. Continue?')) return;
        
        entity = service.submitForApproval(entity);
        checkEditable(entity);
        EventQueue.invokeLater({ caller?.reload(); });
    }
    
    def approveDocument() {
        if (!MsgBox.confirm('You are about to approve this document. Continue?')) return;
                
        loadingOpener = Inv.lookupOpener('popup:loading', [:]);
        def handler = [
            onMessage   : { o->
                //println 'onMessage '  + o;
                //println 'EOF ' + AsyncHandler.EOF;
                //loadingOpener.handle.binding.fireNavigation("_close");

                if (o == AsyncHandler.EOF) {
                    //loadingOpener.handle.binding.fireNavigation("_close");
                    loadingOpener.handle.closeForm();
                    return;
                }
                entity = o;
                //generatePDFFile();
                loadingOpener.handle.closeForm();
                //entity.putAll(o);
                //def msg = ;
                //if (mode == 'edit') msg = "Follow-up collection updated successfully!";
                //mode = 'read';
                EventQueue.invokeLater({ 
                    binding?.refresh();
                    caller?.reload(); 
                });
            },
            onError     : { p->
                loadingOpener.handle.closeForm();
                MsgBox.err(p.message);
            }
        ] as AsyncHandler;
        service.approveDocument(entity, handler);
        return loadingOpener;
    }
    
    /*
    void approveDocument() {
        if (!MsgBox.confirm('You are about to approve this document. Continue?')) return;
        
        entity = service.approveDocument(entity);
        checkEditable(entity);
        EventQueue.invokeLater({ caller?.reload(); });
    }
    */
    
    void returnToDraft() {
        entity = service.returnToDraft( entity );
        checkEditable( entity );
        EventQueue.invokeLater({ caller?.reload(); });
    }
    
    /*
    void disapprove() {
        if (!MsgBox.confirm('You are about to disapprove this document. Continue?')) return;
        
        entity = service.disapprove(entity);
        checkEditable(entity);
    }
    */
    
    def addLedgersFromPreviousBilling() {
        def handler = { o->
            def list = service.getBillingGroupLedgers( o );
            list?.each{ itm->
                
                itm.objid = "BGD" + new UID();
                itm.parentid = entity.objid;
                
                def i = entity?.items?.find{ it.ledgerid == itm.ledgerid }
                if (!i) {
                    if (!entity.items) entity.items = [];
                    entity.items << itm;
                }
                
                i = entity?._addedledgers?.find{ it.ledgerid == itm.ledgerid }
                if (!i) {
                    if (!entity?._addedledgers) entity._addedledgers = [];
                    entity._addedledgers << itm;
                }
            }
            listHandler?.reload();
        }
        def op = Inv.lookupOpener("billing:group:previous:billing", [onselect: handler, type: entity.txntype, date: entity.dtstarted]);
        if (!op)return null;
        return op;
    } 
}


/*
class BillingGroupController extends CRUDController
{
    @Binding
    def binding;
    
    @Service("DateService")
    def dateSvc;
    
    String serviceName = "LoanBillingGroupService";
    String entityName = "billinggroup";
    
    boolean allowApprove = false;
    boolean allowDelete = false;
    boolean allowEdit = true;
    
    Map createPermission = [domain: 'LOAN', role: 'LEGAL_OFFICER,ACCT_ASSISTANT'];
    Map editPermission = [domain: 'LOAN', role: 'LEGAL_OFFICER,ACCT_ASSISTANT'];
    
    def selectedLedger, prevledgers;
        
    Map createEntity() {
        def date = dateSvc.getServerDateAsString().split(" ")[0];
        return [
            objid       : 'BG' + new UID(),
            txnstate    : 'DRAFT',
            dtstarted   : date,
            dtended     : date
        ];
    }
    
    void afterCreate( data ) {
        listHandler?.reload();
    }
    
    def getTypeList() {
        def list = service.getTypes();
        if (!list) list = [];
        return list;
    }
    
    void afterOpen( data ) {
        checkEditable(data);
    }
        
    void afteSave( data ) {
        data._addedledger = [];
        data._removedledger = [];
        listHandler?.reload();
    }
    
    void afterEdit( data ) {
        prevledgers = [];
        def item;
        data.ledgers.each{ o->
            item = [:];
            item.putAll(o);
            prevledgers.add(item);
        }
    }
    
    void afterCancel() {
        entity.ledgers = [];
        entity.ledgers.addAll(prevledgers);
        listHandler?.reload();
    }
    
    def getBorrowerLookupHandler() {        
        def handler = { o->
            if (entity.ledgers.find{ o.objid == it.ledgerid }) {
                throw new Exception("Ledger has already been selected.");
            }
            
            if (!entity._addedledger) entity._addedledger = [];
            def item = [
                objid   : 'BGD' + new UID(),
                parentid: entity.objid,
                state   : o.state,
                ledgerid: o.objid,
                borrower: [objid: o.acctid, name: o.acctname],
                route   : o.route
            ];
            
            
            if (!selectedLedger) {
                selectedLedger = item;
                entity.ledgers.add(item);
                entity._addedledger.add(item);
            } else {
                item.objid = selectedLedger.objid;
                item._edited = true;
                def i = entity._addedledger?.find{ it.objid == selectedLedger.objid }
                if (i) i = item;
                
                selectedLedger.clear();
                selectedLedger.putAll(item);
            }
            listHandler.reload();
        }
       return Inv.lookupOpener('specialcollectionledger:lookup', [onselect: handler]);
    }
    
    void checkEditable( data ) {
        if (data.txnstate!='DRAFT') {
            allowEdit = false;
        }
        binding?.refresh('formActions');
    }
    
    def listHandler = [
        fetchList: { o->
            if (!entity.ledgers) entity.ledgers = [];
            return entity.ledgers;
        }
    ] as EditorListModel;
    
    void removeLedger() {        
        if (!MsgBox.confirm("You are about to remove this ledger. Continue?")) return;

        if (!entity._removedledger) entity._removedledger = [];
        entity._removedledger.add(selectedLedger);

        if (entity._addedledger) entity._addedledger.remove(selectedLedger);
        entity.ledgers.remove(selectedLedger);
        listHandler?.reload();
    }
    
    void submitForApproval() {
        if (!MsgBox.confirm("You are about to submit this document for approval. Continue?")) return;
        
        entity = service.submitForApproval(entity);
        checkEditable(entity);
    }
    
    void approveDocument() {
        if (!MsgBox.confirm("You are about to approve this document. Continue?")) return;
        
        entity = service.approveDocument(entity);
    }
    
    void disapprove() {
        if (!MsgBox.confirm("You are aobut to disapprove this document. Continue?")) return;
        
        entity = service.disapprove(entity);
    }
    
}
*/