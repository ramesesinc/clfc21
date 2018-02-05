package com.rameses.clfc.report.treasury.overagesummary;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;

class OverageSummaryReportController extends ReportModel
{    
    @Service("OverageSummaryReportService")
    def service;

    @Service('DateService')
    def dateSvc;
    
    @Binding
    def binding;
    
    String title = "Overage Summary Report";

    def mode = 'init'
    def startdate;
    def enddate;
    
    void init() {
        startdate = dateSvc.getServerDateAsString().split(' ')[0];
        enddate = startdate;
    }

    def close() {
        return "_close";
    }
    
    def parseDate( date ) {
        if (!date) return null;
        
        if (date instanceof Date) {
            return date;
        } else {
            return java.sql.Date.valueOf(date);
        }
    }
    
    private def getParams(){
        return [
            startdate : startdate,
            enddate : enddate
        ]
    }
    
    def rptdata;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    def handler;
    def preview() {
        def sd = parseDate(startdate);
        def ed = parseDate(enddate);
        if (ed.compareTo(sd) < 0)
            throw new Exception("End date must not be less than start date.");

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
        return "com/rameses/clfc/report/treasury/overagesummary/OverageSummaryReport.jasper";
    }

//    public SubReport[] getSubReports() {
//        return [
//            new SubReport('DETAIL', 'com/rameses/clfc/report/treasury/overagesummary/OverageSummaryReportDetail.jasper'),
//            new SubReport('ITEMDETAIL', 'com/rameses/clfc/report/treasury/overagesummary/OverageSummaryReportItemDetail.jasper')
//        ];
//    }
}