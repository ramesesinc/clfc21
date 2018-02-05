package com.rameses.clfc.loan.payment.capture

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.framework.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class CapturePaymentPendingController {
	
    @Binding
    def binding;
    
    @Service("LoanCapturePaymentService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    String title = "Pending Captured Payments";
    
    def date, collector;
    def mode = 'init', searchtext;
    def isCollector = false;
    def headers = ClientContext.currentContext.headers;

    @PropertyChangeListener
    def listener = [
        "collector": { o->
            if (isCollector == true) {
                collector = [
                    objid   : headers.USERID,
                    name    : headers.NAME
                ];
            }
        }
    ]
    
    void init() {
        date = dateSvc.getServerDateAsString();
        collector = [:];
        if (headers.ROLES.containsKey("LOAN.FIELD_COLLECTOR")) {
            isCollector = true;
        }
        mode = 'init';
        binding?.refresh();
    }
    
    def listHandler = [
        getColumns: { o->
            return service.getPendingColumns( o );
        },
        fetchList: { o->
            o.date = date;
            o.collectorid = collector?.objid;
            o.searchtext = searchtext;
            return service.getPendingPayments( o );
//            return [];
        }
    ] as BasicListModel;
    
    def close() {
        return "_close";
    }
    
    def next() {
        listHandler?.reload();
        mode = 'list';
        return "list";
    }
    
    def back() {
        mode = 'init';
        return "default";
    }
    
    def getCollectorList() {
        return service.getPendingCollectors( [date: date] );
    }
    
    void search() {
        listHandler?.reload();
    }
    
    def submitForMapping() {
        if (!MsgBox.confirm("You are about to submit all pending collection for remittance. Continue?")) return;
                
        println 'collector ' + collector;
        service.submitForMapping([date: date, collectorid: collector?.objid]);
        return back();
    }
}

