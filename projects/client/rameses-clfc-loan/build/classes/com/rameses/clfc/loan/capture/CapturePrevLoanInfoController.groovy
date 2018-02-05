package com.rameses.clfc.loan.capture

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class CapturePrevLoanInfoController {

    def entity, mode = 'read';
    def selectedPrevLoan;
    
    void init() {
        if (!entity) entity = [:];
        if (entity.apptype == 'NEW') {
            entity.previousloans = [];
        }
        previousLoansHandler?.reload();
    }
    
    def previousLoansHandler = [
        fetchList: { o->
               if (!entity.previousloans) entity.previousloans = [];
            return entity.previousloans;
        }
    ] as BasicListModel
    
    void updateLoancount( list ) {
        if (list) {
            list.sort{ it.dtreleased }
            list?.eachWithIndex{ itm, idx->
                itm.loancount = idx + 1;
            }
            
            list = list.reverse();
        }
    }
    
    def addItem() {
        def handler = { o->
            if (!o.objid) o.objid = 'PREVL' + new UID();
            o.loancount = entity.previousloans.size()+1;
            
            if (!entity.previousloans) entity.previousloans = [];
            entity.previousloans << o;
            
            updateLoancount(entity.previousloans);
                        
            previousLoansHandler?.reload();
        }
        
        def params = [
            entity  : [:],
            mode    : mode,
            handler : handler
        ]
        def op = Inv.lookupOpener("capture:prevloaninfo:item:create", params);
        if (!op) return;
        return op;
    }
    
    def openItem() {
        def handler = { o->
            def item = entity.previousloans.find{ it.objid == o.objid }
            if (!item) return;
            
            item.putAll(o);
            updateLoancount(entity.previousloans);
            
            previousLoansHandler?.reload();
        }
        
        def params = [
            entity  : selectedPrevLoan,
            mode    : mode,
            handler : handler
        ]
        
        def op = Inv.lookupOpener("capture:prevloaninfo:item:open", params);
        if (!op) return;
        return op;
    }
    
    void removeItem() {
        if (!selectedPrevLoan) return;
        
        if (!MsgBox.confirm("You are about to remove this item. Continue?")) return;
        
        entity.previousloans.remove(selectedPrevLoan);
        updateLoancount(entity.previousloans);
        
        previousLoansHandler?.reload();
    }
    /*
    def previousLoansHandler = [
        fetchList: {o->
            if(!entity.previousloans) entity.previousloans = [];
            return entity.previousloans
        },
        createItem: {
            return [loancount: entity.previousloans.size()+1];
        },
        onAddItem: {o->
            entity.previousloans.add(o)
        },
        onRemoveItem: {o->
            if( MsgBox.confirm("You are about to remove this loan. Continue?") ) {
                entity.previousloans.remove(o)
                return true
            }
            return false
        },
        onColumnUpdate: {o, colName->
            def item = entity.previousloans.find{ it == o }
            if( item ) item = o
        }
    ] as EditorListModel; 
    */
}

