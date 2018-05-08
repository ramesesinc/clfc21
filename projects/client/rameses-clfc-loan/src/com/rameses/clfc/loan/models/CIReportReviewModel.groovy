package com.rameses.clfc.loan.models;

import com.rameses.rcp.annotations.*;

class CIReportReviewModel {

    //feed by caller 
    def loanapp;
    def handler;
    
    @Service('LoanApplicationCIReportService')
    def service;
    
    def data; 
    def htmlbuilder;
    
    void init() {
        data = service.open([objid: loanapp.objid]);
        htmlbuilder = new com.rameses.clfc.util.CIReportHtmlBuilder();
    }
    
    def getHtmlview() {
        def m = [:]; 
        if ( data ) m.putAll( data ); 
        return htmlbuilder.build( m );
    }    
    
    def doCancel() {
        return '_close'; 
    }
    
    def doOk() {
        if ( handler ) handler(); 
        return '_close'; 
    }
}
