package com.rameses.clfc.treasury.ledger.amnesty.override

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.text.*;

class OverrideAmnestyRequestListController 
{
    @Binding
    def binding;
    
    @Service('OverrideAmnestyRequestService')
    def service;
    
    def decFormat = new DecimalFormat('#,##0.00');
    def dateFormat = new SimpleDateFormat("MMM-dd-yyyy");
    
    def entity, mode = 'read';
    def selectedItem;
    
    void init() {
        if (!entity.overriderequest) entity.overriderequest = [];
    }
    
    void setSelectedItem( selectedItem ) {
        this.selectedItem = selectedItem;
        binding?.refresh('formActions');
    }
    
    void refreshList() {
        if (!entity.overriderequest) entity.overriderequest = [];
//        entity.overriderequest = fetchList([ledgerid: entity?.ledgerid])
        entity.overriderequest = fetchList([refid: entity?.objid]);
        listHandler?.reload();
    }
    
    def fetchList( params ) {
        return service.getList(params);
    }
    
    def listHandler = [
        getColumnList: { o->
            return service?.getPluginColumns(o);
        },
        fetchList: { o->
            //entity.overriderequest = fetchList(o);
            if (!entity.overriderequest) entity.overriderequest = [];
            return entity.overriderequest;
        },
        onOpenItem: { itm, colName->
            if (itm) return openRequestImpl(itm);
            return null;
        }
    ] as BasicListModel;
    
    
    def addRequest() {
        if (!entity.borrower) throw new Exception('Please specify borrower');
        if (entity.availedamnesty) {
            throw new Exception('Cannot add override amnesty request. You have already availed an amnesty. Please remove availed amnesty so that you can add an override amnesty request.');
        }
        if (entity.rejectedamnesty) {
            throw new Exception('Cannot add override amnesty request. You have specified amnesties to be rejected. Please remove rejected amnesties so that you can add an override amnesty request.');
        }
        
        def handler = { o->
            if (!o.day) o.day = 0;
            if (!o.month) o.month = 0;
            if (!o.usedate) o.usedate = 0;
            
            if (o.usedate == 0) {
                if (o.day == 0 && o.month == 0) {
                    throw new Exception('Please specify term.');
                }
            }
            buildDescription(o);
            
            o._new = true;
            
            if (!entity.overriderequest) entity.overriderequest = [];
            entity.overriderequest << o;
            
            if (!entity._request) entity._request = [];
            entity._request << o;
            
            listHandler?.reload();
        };
        
        def params = [
            entity  : [:],
            mode    : mode,
            handler : handler,
            borrower: entity?.borrower,
            loanapp : entity?.loanapp,
            ledgerid: entity?.ledgerid
        ];
        
        def op = Inv.lookupOpener('override:amnesty:request:item:create', params);
        if (!op) return null;
        return op;
        //println 'add request';
    }
    
    void buildDescription( data ) {
        def description = decFormat.format(data.amount) + ' ';
        
        if (data.usedate == 0) {
            if (data.month > 0) description += data.month + ' Month(s) ';
            if (data.day > 0) description += data.day + ' Day(s) ';
        } else if (data.usedate == 1) {
            description += 'until ' + dateFormat.format(parseDate(data.date)) + ' ';
        }
        
        data.description = description;
    }
    
    def parseDate( date ) {
        if (!date) return null;
        
        if (date instanceof Date) {
            return date;
        } else {
            return java.sql.Date.valueOf(date);
        }
    }
    
    void removeRequest() {
        if (!MsgBox.confirm('You are about to remove this item. Continue?')) return;
        
        if (!entity?._removerequest) entity?._removerequest = [];
        entity?._removerequest << selectedItem;
        
        entity?.overriderequest?.remove(selectedItem);
        
        listHandler?.reload();
    }
    
    def openRequest() {
        return openRequestImpl(selectedItem);
    }
    
    def openRequestImpl( itm ) {
        def params = [
            entity  : itm,
            mode    : mode,
            borrower: entity?.borrower,
            loanapp : entity?.loanapp
        ];
        
        if (mode != 'read') {
            def handler = { o->
                def item = entity?.overriderequest?.find{ o.objid == it.objid }
                
                if (item) {
                    item.putAll(o);
                    
                    buildDescription(item);
                    item._edit = true;
                }
                
                binding?.refresh();
                listHandler?.reload();
            }
            params.handler = handler;
        }
        def op = Inv.lookupOpener('override:amnesty:request:item:open', params);
        if (!op) return null;
        
        return op;
        //println 'open request';
    }
    
}

