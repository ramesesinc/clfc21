package com.rameses.clfc.treasury.ledger.amnesty

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.text.*;

class AmnestyRecommendationController 
{
    @ChangeLog
    def changeLog
    
    def entity, mode = 'read', recommendationmode = 'read';
    def decFormat = new DecimalFormat('#,##0.00');
    def dateFormat = new SimpleDateFormat("MMM-dd-yyyy");
    
    def parseDate( date ) {
        if (!date) return null;
        if (date instanceof Date) {
            return date;
        } else {
            return java.sql.Date.valueOf(date);
        }
    }
    
    void init() {
        if (!entity) entity = [:];
    }
    
    def selectedItem;
    def listHandler = [
        fetchList: { o->
            if (!entity.items) entity.items = [];
            return entity.items;
        },
        onOpenItem: { itm, colName->
            if (itm) return openItemImpl(itm);
            return null;
        }
    ] as BasicListModel;
    
    
    def openItem() {
        return openItemImpl(selectedItem);
    }
    
    def openItemImpl( itm ) {
        def handler = { o->
            if (o.withmd == 1) {
                if (o.usedate == 0 && o.day == 0 && o.month == 0)
                    throw new Exception('Please specify term.');
            }
            
            if (entity?.txnstate) o.txnstate = entity?.txnstate;
            buildDescription(o);

            def i = entity.items?.find{ it.objid == o.objid }
            if (i) {
                entity?.items?.remove(i);
                o._edited = true;
                entity?.items?.add(o);
                //i.clear();
                //i._edited = true;
                //i.putAll(o);
            }

            if (entity?._addeditems) {
                i = entity?._addeditems?.find{ it.objid == o.objid }
                if (i) {
                    entity?._addeditems?.remove(i);
                    entity?._addeditems?.add(o);
                    //i = o;
                    //i.clear();
                    //i.putAll(o);
                }
            }

            listHandler?.reload();
        }
        def xmode = mode;
        if (entity?.txnstate == 'FOR_APPROVAL') {
            xmode = recommendationmode;
        }
        
        if ((entity.txnstate != 'DRAFT' && itm.txnstate != 'FOR_APPROVAL') || itm.allowedit==false) {
            xmode = 'read';
        }
        
        def params = [
            entity          : itm,
            mode            : xmode,
            approveHandler  : { o->
                itm.txnstate = o.txnstate;
                listHandler?.reload();
            }
        ];
        
        if (params.mode != 'read') {
            params.handler = handler;
        }
        
        def op = Inv.lookupOpener('ledgeramnesty:item:open', params);
        if (!op) return null;
        return op;
    }
    
    def addItem() {
        def handler = { o->
            if (o.withmd == 1) {
                if (o.usedate == 0 && o.day == 0 && o.month == 0)
                    throw new Exception('Please specify term.');
            }
            
            if (entity?.txnstate) o.txnstate = entity?.txnstate;
            buildDescription(o);
        
            if (!entity._addeditems) entity._addeditems = [];
            entity._addeditems << o;
            
            if (!entity.items) entity.items = [];
            entity.items << o;
            listHandler?.reload();
        }
        def op = Inv.lookupOpener('ledgeramnesty:item:create', [handler: handler]);
        if (!op) return null;
        return op;
    }
    
    void buildDescription( data ) {
        def description = decFormat.format(data.amount) + ' ';
        
        if (!data.withmd || data.withmd == 0) {
            description += ' No Maturity Date ';
        } else if (data?.withmd == 1) {
            if (data.usedate == 0) {
                if (data.month > 0) description += data.month + ' Month(s) ';
                if (data.day > 0) description += data.day + ' Day(s) ';
            } else if (data.usedate == 1) {
                description += 'until ' + dateFormat.format(parseDate(data.date));
            }
        }
        
        data.description = description;
    }
        
    void removeItem() {
        if (!MsgBox.confirm('You are about to remove this item. Continue?')) return;
        
        entity.items?.remove(selectedItem);
        if (entity._addeditems) entity._addeditems.remove(selectedItem);
        
        if (!entity._removeditems) entity._removeditems = [];
        entity._removeditems << selectedItem;
        
        listHandler?.reload();
    }
    
}

