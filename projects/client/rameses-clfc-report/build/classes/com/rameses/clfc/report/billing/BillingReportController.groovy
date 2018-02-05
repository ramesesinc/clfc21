package com.rameses.clfc.report.billing

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;

class BillingReportController extends ReportModel {

    @Service("BillingReportService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    @Binding
    def binding;
    
    String title = "Billing Report";
    def date, mode = "init";
    
    def handler;

    void init() {
        date = dateSvc.getServerDateAsString();
        mode = "init";
    }
    
    def close() {
        return "_close";
    }
    
    def back() {
        mode = "init";
        return "default";
    }
    
    def rptdata;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    def preview() {
//        rptdata = service.getReportData([date: date]);
//        mode = "preview";
//        viewReport();
//        return "preview";
        if (!handler){
            handler = [
                onMessage : { o ->
                    if (o == AsyncHandler.EOF){
                        loadingOpener.handle.closeForm();
                        return;
                    }
                    rptdata = o;
                    viewReport();
                    loadingOpener.handle.closeForm();
                    mode = 'preview';
                    binding.fireNavigation('preview'); 
                },
                onTimeout : {
                    handler?.retry();
                },
                onCancel : {
                    loadingOpener.handle.closeForm();
                },
                onError : { p -> 
                    loadingOpener.handle.closeForm();
                    MsgBox.err(p.message);       
                }
            ] as AbstractAsyncHandler;
        } 
        service.getReportData([date:date], handler)
        return loadingOpener;
    }
    
    
    public Map getParameters() {
//        return [:];
        def data = [:];
        data.putAll(rptdata);
        if (data.items) data.remove("items");
        return data;
    }

    public Object getReportData() {
//        return rptdata;
//        return service.getReportData([startdate: startdate, enddate: enddate]);
        def data = rptdata.remove("items");
        return data;
    }
    
    public String getReportName() {
        return "com/rameses/clfc/report/billing/BillingReport.jasper";
    }

//    public SubReport[] getSubReports() {
//        return [
//            new SubReport("ROUTE_DETAIL", "com/rameses/clfc/report/billing/BillingReportPerRoute.jasper"),
//            new SubReport("ITEM_DETAIL", "com/rameses/clfc/report/billing/BillingReportPerItem.jasper")
//        ];
//    }
    
}

