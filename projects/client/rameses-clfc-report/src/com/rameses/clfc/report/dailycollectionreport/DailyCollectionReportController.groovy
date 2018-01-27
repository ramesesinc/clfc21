package com.rameses.clfc.report.dailycollectionreport

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;

class DailyCollectionReportController extends ReportModel {

    @Service("NewDailyCollectionReportService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    String title = "Daily Collection Report";
    def date, mode = "init";
    
    void init() {
        date = dateSvc.getServerDateAsString();
        mode = "init";
    }
    
    def close() {
        return "_close";
    }
    
    def back() {
        mode = 'init';
        return "default";
    }
    
    def rptdata;
    def preview() {
        rptdata = service.getReportData([date: date]);
        mode = 'preview';
        viewReport();
        return 'preview';
    }
    
    public Map getParameters() {
        return [:];
    }

    public Object getReportData() {
        return rptdata;
    }
    
    public String getReportName() {
        return "com/rameses/clfc/report/dailycollectionreport/DCRReport.jasper";
    }

    public SubReport[] getSubReports() {
        return [
            new SubReport('COLLECTION_DETAIL', 'com/rameses/clfc/report/dailycollectionreport/DCRCollectionDetail.jasper'),
            new SubReport('COLLECTION_ITEM_DETAIL', 'com/rameses/clfc/report/dailycollectionreport/DCRCollectionItemDetail.jasper'),
            new SubReport('OVERAGE', 'com/rameses/clfc/report/dailycollectionreport/DCROverageDetail.jasper'),
            new SubReport('CASH_ON_HAND', 'com/rameses/clfc/report/dailycollectionreport/DCRCashOnHandDetail.jasper')
        ];  
    }
}

