package com.rameses.clfc.report.dailycashreceipt

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;

class DailyCashReceiptController extends ReportModel {

    @Service("DailyCashReceiptReportService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    String title = "Daily Cash Receipt";
    
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
        return "com/rameses/clfc/report/dailycashreceipt/DCRReport.jasper";
    }

    public SubReport[] getSubReports() {
        return [
            new SubReport('COLLECTION_DETAIL', 'com/rameses/clfc/report/dailycashreceipt/DCRCollectionDetail.jasper'),
            new SubReport('COLLECTION_ITEM_DETAIL', 'com/rameses/clfc/report/dailycashreceipt/DCRCollectionItemDetail.jasper'),
            new SubReport('OTHER_RECEIPT', 'com/rameses/clfc/report/dailycashreceipt/DCROtherReceiptDetail.jasper'),
            new SubReport('DEPOSIT', 'com/rameses/clfc/report/dailycashreceipt/DCRDepositDetail.jasper'),
            new SubReport('CASH_BREAKDOWN', 'com/rameses/clfc/report/dailycashreceipt/DCRCashBreakdownDetail.jasper')
        ];  
    }
}

