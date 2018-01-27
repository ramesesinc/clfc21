package com.rameses.clfc.report.billing

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;

class BillingReportController extends ReportModel {

    @Service("BillingReportService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    String title = "Billing Report";
    def date, mode = "init";

    void init() {
        date = dateSvc.getServerDateAsString();
        mode = "init";
    }
    
    def close() {
        return "_close";
    }
    
    def back() {
        page = "init";
        return "default";
    }
    
    def rptdata;
    def preview() {
        rptdata = service.getReportData([date: date]);
        mode = "preview";
        viewReport();
        return "preview";
    }
    
    public Map getParameters() {
        return [:];
    }

    public Object getReportData() {
        return rptdata;
//        return service.getReportData([startdate: startdate, enddate: enddate]);
    }
    
    public String getReportName() {
        return "com/rameses/clfc/report/billing/BillingReport.jasper";
    }

    public SubReport[] getSubReports() {
        return [
            new SubReport("ROUTE_DETAIL", "com/rameses/clfc/report/billing/BillingReportPerRoute.jasper"),
            new SubReport("ITEM_DETAIL", "com/rameses/clfc/report/billing/BillingReportPerItem.jasper")
        ];
    }
    
}

