package com.rameses.clfc.report.loan.masterlist

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;

class LoanMasterListReportController extends ReportModel 
{
    @Service("LoanMasterListReportService")
    def service;

    @Service("DateService")
    def dateSvc;
    
    @Binding
    def binding;
    
    String title = "Loan Master List";

    def mode = 'init'
    def startdate, enddate, criteria;
    def reportCriteriaList = [];
    def handler;

    void init() {
        startdate = dateSvc.getServerDateAsString();
        enddate = startdate;
        //reportCriteriaList = service.getCriteria();
    }
    
    def routecode;
    def getRouteList() {
        return service.getRoutes();
    }
    
    def categoryid;
    def getCategoryList() {
        return service.getCategories();
    }
    
    def close() {
        return "_close";
    }
    
    private def getParams() {
        return [
            routecode   : routecode,
            categoryid  : categoryid
        ];
    }
    
    def rptdata;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    
    def preview() {
//        service.generate(getParams());

//        println rptdata;
//        println 'passing';
//        rptdata = service.getReportData(getParams());
//        viewReport();
//        mode = 'preview';
//        return 'preview';
        if (!handler) {
            handler = [
                onMessage   : { o->
                    if (o == AsyncHandler.EOF) {
                        return;
                    }
                    rptdata = o;
                    viewReport();
                    mode = 'preview';
                    loadingOpener.handle.closeForm();
                    binding.fireNavigation('preview');
                },
                onTimeout: {
                    handler?.retry();
                },
                onCancel: {
                    loadingOpener.handle.closeForm();
                },
                onError     : { o->
                    loadingOpener.handle.closeForm();
                    MsgBox.err(o.message);
                }
            ] as AbstractAsyncHandler;
        }
        service.getReportData(getParams(), handler);
        return loadingOpener;
        
    }
    
    def back() {
        mode = 'init';
        return "default";
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
        return "com/rameses/clfc/report/loan/masterlist/LoanMasterListReport.jasper";
    }
}

