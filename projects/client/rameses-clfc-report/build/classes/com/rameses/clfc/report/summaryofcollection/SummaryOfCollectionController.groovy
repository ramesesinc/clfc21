package com.rameses.clfc.report.summaryofcollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;

class SummaryOfCollectionController extends ReportModel {

    @Service("SummaryOfCollectionReportService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    @Binding
    def binding;
    
    def startdate, enddate;
    def mode = "init";
    def handler;
    
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
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    def preview() {
//        rptdata = service.getReportData(getParams());
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
    
    public Map getParameters() {
//        return [:];
        def data = [:];
        data.putAll(rptdata);
        if (data.items) data.remove("items");
        return data;
    }

    public Object getReportData() {
//        return rptdata;
        def data = rptdata.remove("items");
        return data;
    }
    
    public String getReportName() {
        return "com/rameses/clfc/report/summaryofcollection/SummaryOfCollectionReport.jasper";
    }

//    public SubReport[] getSubReports() {
//        return [
//            new SubReport('DETAIL', 'com/rameses/clfc/report/summaryofcollection/SummaryOfCollectionDetailReport.jasper')
//        ];
//    }
}

