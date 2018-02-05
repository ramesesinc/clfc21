package com.rameses.clfc.report.loan.forapplication

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;

class LoanForApplicationReportController  extends ReportModel
{   
    @Service("LoanForAdvanceApplicationReportService")
    def service;

    
    String title = "For Advance Application";

    def mode = 'init'
    def percent, days;

    void init() {
        percent = 0.10;
        days = 15;
        //reportCriteriaList = service.getCriteria();
    }
    
    def close() {
        return "_close";
    }
    
    private def getParams() {
        return [
            percent : percent, 
            days    : days
        ];
    }
    
    def rptdata;
    def preview() {
        //service.generate(getParams());
        rptdata = service.getReportData(getParams());
        viewReport();
        mode = 'preview';
        return 'preview';
    }
    
    def back() {
        mode = 'init';
        return "default";
    }
    public Map getParameters() {
        return [:];
    }

    public Object getReportData() {
        return rptdata;//service.getReportData(getParams());
    }
    
    public String getReportName() {
        return "com/rameses/clfc/report/loan/forapplication/LoanForAdvanceApplicationReport.jasper";
    }

    public SubReport[] getSubReports() {
        return [
            new SubReport('DETAIL', 'com/rameses/clfc/report/loan/forapplication/LoanForAdvanceApplicationReportDetail.jasper')
        ];
    }
}

