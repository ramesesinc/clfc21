package com.rameses.clfc.treasury.deposit

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class DepositBankController {
    
    @Binding
    def binding;
    
    def entity, mode = 'read';
    def passbookLookup = Inv.lookupOpener('deposit:passbook:lookup', [
         onselect: { o->
             entity.bank = o.bank;
             entity.passbook = o;
             binding?.refresh();
         },
         state: 'ACTIVE'
    ]);
    
    void init() {
        listHandler?.reload();
    }
    
    def selectedDepositSlip;
    def listHandler = [
        fetchList: { o->
            if (!entity.depositslips) entity.depositslips = [];
            return entity.depositslips;
        }
    ] as BasicListModel;
        
    def addDepositSlip() {
        def handler = { o->
            def i = entity.depositslips.find{ it.depositslip.controlno == o.controlno }
            if (i) throw new Exception("This deposit slip has already been selected.");

            def item = [
                objid       : 'DD' + new UID(),
                parentid    : entity.objid,
                refid       : o.objid,
                txndate     : o.txndate,
                depositslip : [
                    controlno   : o.controlno,
                    type        : o.type,
                    acctno      : o.passbook.acctno,
                    acctname    : o.passbook.acctname,
                    amount      : o.amount
                ]
            ];
            
            if (!entity._addedds) entity._addedds = [];
            entity._addedds << item;
            
            //entity.depositslips.add(item);
            entity.depositslips << item;
            listHandler?.reload();
        }
        def op = Inv.lookupOpener("depositslip:lookup", [state: 'APPROVED', onselect: handler]);
        if (!op) return null;
        return op;
    }
    
    void removeDepositSlip() {
        if (!MsgBox.confirm('You are about to remove this deposit slip. Continue?')) return;
        
        if (!entity._removedds) entity._removedds = [];
        entity._removedds << selectedDepositSlip;
        
        if (entity._addedds) entity._addedds.remove(selectedDepositSlip);
        
        entity.depositslips.remove(selectedDepositSlip);
        listHandler?.reload();
    }
    
}

