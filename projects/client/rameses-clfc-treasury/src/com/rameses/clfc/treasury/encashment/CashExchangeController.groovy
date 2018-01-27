package com.rameses.clfc.treasury.encashment

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.util.*;

class CashExchangeController 
{
    @Binding
    def binding;
    
    def entity, data, handler;
    def selectedItem;
    
    void init() {
        data = [objid: entity?.objid, amount: entity?.amount];
        
        if (entity.reference) {
            data.reference = copyList(entity.reference);
        } else {
            data.reference = getDenominations();
        }
        
        if (entity.cashin) {
            data.cashin = copyList(entity.cashin);
        } else {
            data.cashin = getDenominations();
        }
        
        if (entity.cashout) {
            data.cashout = copyList(entity.cashout);
        } else {
            data.cashout = getDenominations();
        }
        
        breakdownListHandler?.reload();
        cashInListHandler?.reload();
        cashOutListHandler?.reload();
        binding?.refresh('total.(breakdown|cashin|cashout)');
    }
    
    def copyList( src ) {
        def list = [];
        
        def itm;
        src?.each{ o->
            itm = [:];
            itm.putAll(o);
            list << itm;
        }
        
        return list;
    }

    def breakdownListHandler = [
        fetchList: { o->
            if (!data?.reference) data.reference = getDenominations();
            return data?.reference;
        }
    ] as BasicListModel;
    
    def getTotalbreakdown() {
        if (!data) return 0;
        
        def amt = data?.reference?.amount?.sum();
        if (!amt) amt = 0;
        return amt;
    }
    
    def cashInListHandler = [
        fetchList: { o->
            if (!data?.cashin) data.cashin = getDenominations();
            return data?.cashin;
        },
        beforeColumnUpdate: { itm, colName, newVal->
            if (colName == 'qty') {
                def list = [];
                list.addAll(getDenominations());
                if (data?.cashin) {
                    list = [];
                    list.addAll(data?.cashin);
                }

                def i = list?.find{ it.denomination == itm.denomination }
                if (i) list.remove(i);

                def amt = list?.amount?.sum();
                if (!amt) amt = 0;

                def xamt = newVal * itm?.denomination;
                if (!xamt) xamt = 0;
                
                amt += xamt;
                
                if (amt > getTotalcashout()) {
                    throw new Exception('Total cash in exceeds total cash out.');
                    return false;
                }
                
                i = data?.reference?.find{ it.denomination == itm.denomination }
                if (i) {
                    i.qty -= itm.qty;
                    i.amount = i.qty * i.denomination;
                }
            }
            return true;
        },
        onColumnUpdate: { itm, colName->
            if (colName=='qty') {
                if (!itm.qty) itm.qty = 0;

                itm.amount = itm.denomination * itm.qty;
                
                def i = data?.reference?.find{ it.denomination == itm.denomination }
                if (i) {
                    i.qty += itm.qty;
                    i.amount = i.qty * i.denomination;
                }
                binding?.refresh('totalcashin|totalbreakdown');
                breakdownListHandler?.reload();
            }
        }
    ] as EditorListModel;
    
    def getTotalcashin() {
        if (!data?.cashin) return 0;
        
        def amt = data?.cashin?.amount?.sum();
        if (!amt) amt = 0;
        return amt;
    }
    
    def cashOutListHandler = [
        fetchList: { o->
            if (!data?.cashout) data.cashout = getDenominations();
            return data?.cashout;
        },
        beforeColumnUpdate: { itm, colName, newVal->
            if (colName == 'qty') {
                def i = data?.reference?.find{ it.denomination == itm.denomination }
                
                i.qty += itm.qty;
                i.amount = i.qty * i.denomination;
                breakdownListHandler?.reload();
                binding?.refresh('totalbreakdown');
                
                if (newVal > i.qty) {
                    itm.qty = 0;
                    itm.amount = itm.qty * itm.denomination;
                    throw new Exception('Qty inputted exceeds qty available.');
                    return false;
                }
                
            }
            return true;
        },
        onColumnUpdate: { itm, colName->
            if (colName=='qty') {
                if (!itm.qty) itm.qty = 0;
                def i = data?.reference?.find{ it.denomination == itm.denomination }
                
                itm.amount = itm.denomination * itm.qty;
                
                if (i) {
                    i.qty -= itm.qty;
                    i.amount = i.qty * i.denomination;
                }
                binding?.refresh('totalcashout|totalbreakdown');
                breakdownListHandler?.reload();
            }
        }
    ] as EditorListModel;
    
    def getTotalcashout() {
        if (!data?.cashout) return 0;
        
        def amt = data?.cashout?.amount?.sum();
        if (!amt) amt = 0;
        return amt;
    }
    
    def getDenominations() {
        return copyList(LoanUtil.denominations);
    }
    
    def doCancel() {
        return '_close';
    }
    
    def doOk() {
        if (handler) handler(data);
        return '_close';
    }
}

