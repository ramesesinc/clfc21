package com.rameses.clfc.ledger.proceeds

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
import java.rmi.server.UID;

class CaptureLoanLedgerProceedsController extends CRUDController
{
    @Binding
    def binding;
    
    String serviceName = "CaptureLoanLedgerProceedsService"
    String entityName = "captureledgerproceed";
    
    boolean allowEdit = true;
    boolean allowDelete = false;
    boolean allowApprove = false;
    
    Map createPermission = [domain: 'LOAN', role: 'CAO_OFFICER'];
    Map editPermission = [domain: 'LOAN', role: 'CAO_OFFICER'];
    
    def borrowerLookupHandler = Inv.lookupOpener('ledgerborrower:lookup', [
        onselect: { o->
            //entity.ledger = o;
            entity.borrower = o.borrower;
            entity.parentid = o.objid;
        },
        pastdueledgers: true
    ]);
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    
    Map createEntity() {
        return [
            objid   : 'CLPROC' + new UID(),
            txnstate: 'FOR_SELLING',
            txntype : 'CAPTURE'
        ];
    }
    
    void checkEditable( data ) {
        allowEdit = false;
        if (data.txnstate=='FOR_SELLING') {
            allowEdit = true;
        }
        binding?.refresh();
    }
    
    void afterSave( data ) {
        checkEditable( data );
    }
    
    void afterOpen( data ) {
        checkEditable( data );
        /*
        if (data.txnstate == 'SOLD') {
            allowEdit = false;
            allowDelete = false;
        }
        */
    }
    
    def sold() {
        if (!MsgBox.confirm("You are about to sell this proceed. Continue?")) return;
        
        def handler = [
            onMessage: { o->
                //println 'onMessage '  + o;
                //println 'EOF ' + AsyncHandler.EOF;
                if (o == AsyncHandler.EOF) {
                    loadingOpener.handle.binding.fireNavigation("_close");
                    return;
                }                
                
                entity = o;
                checkEditable( entity );
                /*
                allowEdit = false;
                allowDelete = false;
                binding?.refresh();
                */
                loadingOpener.handle.binding.fireNavigation("_close");
                MsgBox.alert("Successfully sold proceed!");
            },
            onError: { o->
                loadingOpener.handle.binding.fireNavigation("_close");
                MsgBox.err(o);
                /*if (o instanceof java.util.concurrent.TimeoutException) {
                
                } else {
                    throw new Exception(o.message);
                }*/
            }
        ] as AsyncHandler;
        service.sold(entity, handler);
        return loadingOpener;
    }    
    
    def getStatus() {
        def str = (entity.txntype? entity.txntype : '') + (entity.txnstate? ' - ' + entity.txnstate : '');
        
        return str;
    }
}

