package com.rameses.clfc.report.summaryofcollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;

class SummaryOfCollectionController extends ReportModel {

    @Service("SummaryOfCollectionReportService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    def startdate, enddate;
    def mode = "init";
    
    String title = "Summary of Collection";
    
    void init() {
        startdate = dateSvc.getServerDateAsString();
        enddate = startdate;
        mode = "init";
    }
    
    private def getParams() {
        return [
            startdate   : startdate, 
            enddate     : enddate
        ];
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
        rptdata = service.getReportData(getParams());
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
        return "com/rameses/clfc/report/summaryofcollection/SummaryOfCollectionReport.jasper";
    }

    public SubReport[] getSubReports() {
        return [
            new SubReport('DETAIL', 'com/rameses/clfc/report/summaryofcollection/SummaryOfCollectionDetailReport.jasper')
        ];
    }
}

