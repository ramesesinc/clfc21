package com.rameses.clfc.report.amnesty.expired

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;

class ExpiredAmnestyReportController extends ReportModel
{
    @Service("ExpiredAmnestySummaryReportService")
    def service;

    @Service("DateService")
    def dateSvc;
    
    @Binding
    def binding;
    
    String title = "Expired Amnesty Summary Report";

    def mode = 'init'
    def startdate;
    def enddate;

    void init() {
        startdate = dateSvc.getServerDateAsString();
        enddate = startdate;
    }
    
    def close() {
        return "_close";
    }
    private def getParams(){
        return [
            startdate: startdate,
            enddate: enddate
        ]
    }
    
    def rptdata;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    def handler;
    def preview() {
//        mode = 'preview';
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
//        return service.getReportData([startdate: startdate, enddate: enddate]);
        def data = rptdata.remove("items");
        return data;
    }
    
    public String getReportName() {
        return "com/rameses/clfc/report/amnesty/expired/ExpiredAmnestySummaryReport.jasper";
    }

//    public SubReport[] getSubReports() {
//        return [
//            new SubReport('DETAIL', 'com/rameses/clfc/report/amnesty/expired/ExpiredAmnestySummaryReportDetail.jasper')
//        ];
//    }
}

