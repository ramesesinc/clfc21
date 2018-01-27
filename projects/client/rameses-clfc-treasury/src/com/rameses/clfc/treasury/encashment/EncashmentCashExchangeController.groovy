package com.rameses.clfc.treasury.encashment

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.*;

class EncashmentCashExchangeController 
{
    @Binding
    def binding;
    
    def entity, mode = 'read';
    def selectedCbs;
    
    String title = 'Cash Exchange';
    
    void init() {
        if (!entity.cbs) entity.cbs = [];
    }
    
    void setSelectedCbs( selectedCbs ) {
        this.selectedCbs = selectedCbs;
        breakdownListHandler?.reload();
        cashOutListHandler?.reload();
        cashInListHandler?.reload();
        binding?.refresh('totalbreakdown|totalcashin|totalcashout');
    }
    
    def listHandler = [
        fetchList: { o->
            return entity.cbs;
        }
    ] as BasicListModel;
        
    
    def breakdownListHandler = [
        fetchList: { o->
            def list = selectedCbs?.reference;
            if (!list) list = getDenominations();
            return list;
        }
    ] as BasicListModel;
    
    def getTotalbreakdown() {
        if (!selectedCbs) return 0;
        
        def amt = selectedCbs?.reference?.amount?.sum();
        if (!amt) amt = 0;
        return amt;
    }
    
    def cashInListHandler = [
        fetchList: { o->
            def list = selectedCbs?.cashin
            if (!list) list = getDenominations();
            return list;
        }
    ] as BasicListModel;
    
    
    def getTotalcashin() {
        if (!selectedCbs) return 0;
        
        def amt = selectedCbs?.cashin?.amount?.sum();
        if (!amt) amt = 0;
        return amt;
    }
    
    def cashOutListHandler = [
        fetchList: { o->
            def list = selectedCbs?.cashout;
            if (!list) list = getDenominations();
            return list;
        }
    ] as BasicListModel;
    
    def getTotalcashout() {
        if (!selectedCbs) return 0;
        
        def amt = selectedCbs?.cashout?.amount?.sum();
        if (!amt) amt = 0;
        return amt;
    }
    
    def getDenominations() {
        def list = [], itm;
        LoanUtil.denominations?.each{ o->
            itm = [:];
            itm.putAll(o);
            list << itm;
        }
        return list;
    }
    
    def editExchange() {
        def handler = { o->
            def co = o?.cashout?.amount?.sum();
            if (!co) co = 0;
            
            def ci = o?.cashin?.amount?.sum();
            if (!ci) ci = 0;
            
            
            if (ci != co) {
                throw new Exception('Total amount for cash in is not equal to total amount for cash out.');
            }
            /*
            def amt = o?.reference?.amount?.sum();
            if (!amt) amt = 0;
            
            if (itm?.amount != amt) {
                
            }
            println 'amount ' + o?.reference?.amount?.sum();
            println 'ci ' + o?.cashin?.amount?.sum();
            println 'co ' + o?.cashout?.amount?.sum();
            
            throw new Exception('stopping');
            */
           
            def itm = entity?.cbs?.find{ it.objid == o.objid }
            
            itm.cashout = o.cashout;
            itm.cashin = o.cashin;
            
            def xitm;
            def ref = itm?.reference?.findAll{ it.qty > 0 };
            ref?.each{ i->
                xitm = entity?.references?.find{ it.denomination == i.denomination }
                if (xitm) {
                    xitm.qty -= i.qty;
                    xitm.amount = xitm.qty * xitm.denomination;
                }
            }
            
            itm?.reference?.clear();
            itm?.reference?.addAll(getDenominations());
            
            ref = o?.reference?.findAll{ it.qty > 0 }
            ref?.each{ i->
                xitm = entity?.references?.find{ it.denomination == i.denomination }
                if (xitm) {
                    xitm.qty += i.qty;
                    xitm.amount = xitm.qty * xitm.denomination;
                }
                
                xitm = itm?.reference?.find{ it.denomination == i.denomination }
                if (xitm) {
                    xitm.qty += i.qty;
                    xitm.amount = xitm.qty * xitm.denomination;
                }
            }
            
            breakdownListHandler?.reload();
            cashOutListHandler?.reload();
            cashInListHandler?.reload();
            binding?.refresh('totalbreakdown|totalcashin|totalcashout');
        }
        
        def params = [entity: selectedCbs, handler: handler];
        def op = Inv.lookupOpener('cashexchange:item', params);
        if (!op) return null;
        return op;
    }
    
    void removeExchange() {
        if (!MsgBox.confirm('You are about to remove cash exchange for this cbs. Continue?')) return;
        
        def breakdown = selectedCbs?.reference;
        
        def list, ci, co, itm;
        list = breakdown?.findAll{ it.qty > 0 }
        list?.each{ o->
            itm = entity?.references?.find{ it.denomination == o.denomination }
            if (itm) {
                itm.qty -= o.qty;
                itm.amount = itm.qty * itm.denomination;
            }
        }
        
        if (selectedCbs?.cashin) ci = selectedCbs.remove('cashin');
        list = ci?.findAll{ it.qty > 0 }
        
        list?.each{ o->
            itm = breakdown?.find{ it.denomination == o.denomination }
            if (itm) {
                itm.qty -= o.qty;
                itm.amount = itm.qty * itm.denomination;
            }
        }
        
        if (selectedCbs.cashout) co = selectedCbs.remove('cashout');
        list = co?.findAll{ it.qty > 0 }
        
        list?.each{ o->
            itm = breakdown?.find{ it.denomination == o.denomination }
            if (itm) {
                itm.qty += o.qty;
                itm.amount = itm.qty * itm.denomination;
            }
        }
        
        list = breakdown?.findAll{ it.qty > 0 }
        list?.each{ o->
            itm = entity?.references?.find{ it.denomination == o.denomination }
            if (itm) {
                itm.qty += o.qty;
                itm.amount = itm.qty * itm.denomination;
            }
        }
        
        breakdownListHandler?.reload();
        cashOutListHandler?.reload();
        cashInListHandler?.reload();
        
        binding?.refresh('totalbreakdown|totalcashout|totalcashin');
    }
}

