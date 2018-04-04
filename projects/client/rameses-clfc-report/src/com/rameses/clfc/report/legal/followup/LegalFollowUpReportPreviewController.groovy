package com.rameses.clfc.report.legal.followup

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;

class LegalFollowUpReportPreviewController extends ReportModel {
    
    @Service("LegalFollowUpReportService")
    def service;
    
    @Binding
    def binding;
    
    String title = "Follow Up Report List";
    
    def txndate, objid, rptdata;
    
    void preview(){
        rptdata = service.getReportData([objid : objid, date : txndate]);
        viewReport();
    }
    
    public Map getParameters() {
        def data = [:];
        data.putAll(rptdata);
        if (data.items) data.remove("items");
        return data;
    }

    public Object getReportData() {        
        def data = rptdata.remove("items");
        return data;
    }
    
    public String getReportName() {
        return "com/rameses/clfc/report/legal/followup/FollowUpReport.jasper" 
    }
}

