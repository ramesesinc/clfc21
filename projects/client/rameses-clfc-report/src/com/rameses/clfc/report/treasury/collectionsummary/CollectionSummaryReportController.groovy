package com.rameses.clfc.report.treasury.collection;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;

class CollectionSummaryReportController extends ReportModel
{    
    @Service("CollectionSummaryReportService")
    def service;
    
    @Service("DateService")
    def dateSvc;

    String title = "Collection Summary Report";

    def mode = 'init'
    def startdate, enddate;
    def reportdata;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    
    void init() {
        startdate = dateSvc.getServerDateAsString();
        enddate = startdate;
    }

    def close() {
        return "_close";
    }
    
    def xpreview() {
        if (java.sql.Date.valueOf(enddate).compareTo(java.sql.Date.valueOf(startdate)) < 0)
            throw new Exception("End date must not be less than start date.");
             
        def handler
        if (!handler) {
            handler = [
                onMessage: { o->
                    println 'on message';
                    //println 'onMessage '  + o;
                    //println 'EOF ' + AsyncHandler.EOF;

                    if (o == AsyncHandler.EOF) {
                        loadingOpener.handle.binding.fireNavigation("_close");
                        return;
                    }        

                    loadingOpener.handle.binding.fireNavigation("_close");
                    reportdata = o;
                
                    mode = 'preview';
                    viewReport();
                    return 'preview';
                },
                onTimeout: {
                    println 'on timeout';
                    handler?.retry(); 
                },
                onCancel: {
                    println 'on cancel';
                    println 'processing cancelled.';
                    //fires when cancel() method is executed 
                }, 
                onError: { o->
                    println 'on error';
                    loadingOpener.handle.binding.fireNavigation("_close");
                    MsgBox.err(o.message);
                    /*if (o instanceof java.util.concurrent.TimeoutException) {

                    } else {
                        throw new Exception(o.message);
                    }*/
                }
            ] as AbstractAsyncHandler;
        }
        
        service.getReportData([startdate: startdate, enddate: enddate]);
        
        return loadingOpener;
    }
    
    def preview() {
        if (java.sql.Date.valueOf(enddate).compareTo(java.sql.Date.valueOf(startdate)) < 0)
            throw new Exception("End date must not be less than start date.");

        reportdata = service.getReportData([startdate: startdate, enddate: enddate]);
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
        return reportdata;//service.getReportData([startdate: startdate, enddate: enddate]);
    }
    
    public String getReportName() {
        return "com/rameses/clfc/report/treasury/collectionsummary/CollectionSummaryReport.jasper";
    }

    public SubReport[] getSubReports() {
        return [
            new SubReport('DETAIL', 'com/rameses/clfc/report/treasury/collectionsummary/CollectionSummaryReportDetail.jasper'),
            new SubReport('DATE_DETAIL', 'com/rameses/clfc/report/treasury/collectionsummary/CollectionSummaryReportDateDetail.jasper'),
            new SubReport('OTHERRECEIPT_DETAIL', 'com/rameses/clfc/report/treasury/collectionsummary/CollectionSummaryReportOtherReceiptDetail.jasper'),
            new SubReport('DATE_GROUP_DETAIL', 'com/rameses/clfc/report/treasury/collectionsummary/CollectionSummaryReportDateDetailGroup.jasper')
        ];
    }
}