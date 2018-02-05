package com.rameses.clfc.report.legal.overdue

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;

class LegalOverdueReportController extends ReportModel
{
    @Binding
    def binding;
    
    @Service("LegalOverdueReportService")
    def service;
    
    @Service("DateService")
    def dateSvc;
    
    String title = "Statement of Overdue Accounts";

    def mode = 'init'
    def txndate, criteria, notedby;
    def notedbyLookup = Inv.lookupOpener('report-user:lookup', [
         onselect: { o->
             notedby = o;
             if (!notedby.name) {
                 notedby.name = o.lastname + ', ' + o.firstname;
                 if (o.middlename) notedby.name += ' ' + o.middlename;
             }
             binding?.refresh('xnotedby');
         },
         roles  : 'BRANCH_MANAGER',
         domains: 'LOAN'
    ]);

    void init() {
        txndate = dateSvc.getServerDateAsString();
        //reportCriteriaList = service.getCriteria();
    }
    
    def getTypeList() {
        return service.getCriteria([:]);
    }
    
    def close() {
        return "_close";
    }
    
    void clearNotedBy() {
        notedby = [:];
        binding?.refresh('xnotedby');
    }
    
    private def getParams() {
        return [
            txndate : txndate, 
            criteria: criteria,
            notedby : notedby
        ];
    }
    
    def rptdata;
    def loadingOpener = Inv.lookupOpener("popup:loading", [:]);
    def handler;
    def preview() {
//        mode = 'preview';
        //service.generate(getParams());
//        rptdata = service.getReportData(getParams());
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
//        return rptdata;//service.getReportData(getParams());
        def data = rptdata.remove("items");
        return data;
    }
    
    public String getReportName() {
        return "com/rameses/clfc/report/legal/overdue/LegalOverdueReport.jasper";
    }

//    public SubReport[] getSubReports() {     
//        return [
//            new SubReport('DETAIL', 'com/rameses/clfc/report/legal/overdue/LegalOverdueReportDetail.jasper')
//        ];
//    }
}

