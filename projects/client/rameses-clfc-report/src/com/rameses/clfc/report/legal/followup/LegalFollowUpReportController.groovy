package com.rameses.clfc.report.legal.followup

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;
import java.rmi.server.UID;
import java.text.SimpleDateFormat

class LegalFollowUpReportController extends CRUDController{
    
    String serviceName = "LegalFollowUpReportService";
    String entityName = "followup_report";
    
    @Service("DateService")
    def dateService;
  
    String title = "Follow Up Note Details";
    
    boolean isAllowApprove(){
        return false;
    }
    
    void beforeCreate(entity){
        entity.txndate = new Date();
    }
    
    void beforeSave(entity){
        def today = dateService.getBasicServerDate();
        def notedate = java.sql.Date.valueOf(entity.txndate);
        if (notedate.after(today))throw new Exception("Cannot create note beyond current date!");
    }
    
    def preview(){
        def op = Inv.lookupOpener("followup_report:preview", [objid : entity.objid, txndate: entity.txndate]);
        return op;
    }
}