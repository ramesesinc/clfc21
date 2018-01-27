package com.rameses.clfc.patch.loan.nextto.report

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;

class BorrowerNextToReportController extends ReportModel 
{
    @Service('BorrowerNextToResolverService')
    def service;
    
    def rptdata, routecode;
    void preview() {
        //service.generate(getParams());
        rptdata = service.getReportData([routecode: routecode]);
        viewReport();
    }

    public Map getParameters() {
        return [:];
    }

    public Object getReportData() {
        return rptdata;//service.getReportData(getParams());
    }
    
    public String getReportName() {
        return "com/rameses/clfc/patch/loan/nextto/report/BorrowerNextToReport.jasper";
    }

    public SubReport[] getSubReports() {
        return [
            new SubReport('DETAIL', 'com/rameses/clfc/patch/loan/nextto/report/BorrowerNextToReportDetail.jasper')
        ];
    }
}

