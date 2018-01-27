package com.rameses.clfc.patch.ledger.edit.posting

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class EditLedgerPostingInitialController 
{
    @Binding
    def binding;
    
    String title = 'Edit Ledger Posting';
    
    def ledger;
    def borrowerLookup = Inv.lookupOpener('edit:posting:ledger:lookup', [
         onselect: { o->
             ledger = o;
             binding?.refresh();
         }
    ]);

    def editLedger() {
        if (!ledger) return;
        
        def op = Inv.lookupOpener('edit:ledger:posting', [ledger: ledger]);
        if (!op) return null;
        return op;
    }
    
    def close() {
        return '_close';
    }
}

