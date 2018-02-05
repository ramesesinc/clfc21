package com.rameses.clfc.report.treasury.branch.pastdue

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;

class BranchPastDueReportController extends ReportModel
{    
    @Service("BranchPastDueReportService")
    def service;

    @Service("DateService")
    def dateSvc;
    
    @Binding
    def binding;
    
    String title = "Branch Past Due Report";

    def mode = 'init'
    def startdate, enddate, criteria;
    def reportCriteriaList = [];

    void init() {
        startdate = dateSvc.getServerDateAsString();
        enddate = startdate;
        //reportCriteriaList = service.getCriteria();
    }
    
    def close() {
        return "_close";
    }
    
    private def getParams() {
        return [
            startdate   : startdate, 
            enddate     : enddate, 
            criteria    : criteria
        ];
    }
    def rptdata;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    def handler;
    def preview() {
//        mode = 'preview';
//        service.generate(getParams());
//        viewReport();
//        return 'preview';
        if (!handler){
            handler = [
                onMessage : { o ->
                    loadingOpener.handle.closeForm();
                    if (o == AsyncHandler.EOF){
                        return;
                    }
                    rptdata = o;
                    viewReport();
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
        service.getReportData(getParams(), handler)
        return loadingOpener;
    }
    
    def back() {
        mode = 'init';
        return "default";
    }
    public Map getParameters() {
//        return [:];
        def data = [:];
        data.putAll(rptdata);
        if (data.items) data.remove("items");
        return data;
    }

    public Object getReportData() {
//        return service.getReportData(getParams());
        def data = rptdata.remove("items");
        return data;
    }
    
    public String getReportName() {
        return "com/rameses/clfc/report/treasury/branch/pastdue/BranchPastDueReport.jasper";
    }

//    public SubReport[] getSubReports() {
//        return [
//            new SubReport('DETAIL', 'com/rameses/clfc/report/treasury/branch/pastdue/BranchPastDueReportDetail.jasper'),
//            new SubReport('DATE_DETAIL', 'com/rameses/clfc/report/treasury/branch/pastdue/BranchPastDueReportDateDetail.jasper'),
//            new SubReport('ROUTE_DETAIL', 'com/rameses/clfc/report/treasury/branch/pastdue/BranchPastDueReportRouteDetail.jasper')
//        ];
//    }
}

