package com.rameses.clfc.report.collection.nac

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;

class CollectionNACReportController extends ReportModel
{
    @Binding
    def binding;
    
    @Service('CollectionNACReportService')
    def service;
    
    @Service('DateService')
    def dateSvc;
    
    String title = "NAC Report";

    
    def startdate, enddate, route, collector;    
    def mode = 'init'
    
    void init() {
        def date = dateSvc.getServerDateAsString().split(" ")[0];
        startdate = date;
        enddate = date;
        mode = 'init';
    }
    
    def getRouteList() {
        def params = [
            startdate   : startdate,
            enddate     : enddate
        ]
        return service.getRoutes(params);
    }
    
    def close() {
        return "_close";
    }
    private def getParams(){
        return [
            startdate  : startdate,
            enddate    : enddate,
            route      : route,
            collector  : collector
        ];
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
//        def params = [
//            startdate       : startdate,
//            enddate         : enddate,
//            route           : route,
//            collector       : collector
//        ];
//        return service.getReportData(params);
        def data = rptdata.remove("items");
        return data;
    }
    
    public String getReportName() {
        return "com/rameses/clfc/report/collection/nac/CollectionNACReport.jasper";
    }

//    public SubReport[] getSubReports() {
//        return [
//            new SubReport('DETAIL', 'com/rameses/clfc/report/collection/nac/CollectionNACReportDetail.jasper'),
//            new SubReport('DATE_DETAIL', 'com/rameses/clfc/report/collection/nac/CollectionNACReportDateDetail.jasper')
//        ];
//    }
}

