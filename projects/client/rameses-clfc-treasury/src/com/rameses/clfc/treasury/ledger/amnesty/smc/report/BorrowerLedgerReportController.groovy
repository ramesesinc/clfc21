package com.rameses.clfc.treasury.ledger.amnesty.smc.report


import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;

class BorrowerLedgerReportController extends ReportModel
{
    @Service('LedgerAmnestySMCPostingPreviewService')
    def service;
    
    
    def smcid;
	
    void preview() {
        viewReport();
    }
    
    public Map getParameters() {
        return [:];
    }

    public Object getReportData() {
        def params = [objid: smcid];
        return service.getReportData(params);
    }

    public String getReportName() {
        return "com/rameses/clfc/treasury/ledger/amnesty/smc/report/BorrowerLedger.jasper";
    }

    public SubReport[] getSubReports() {
        return [
            new SubReport('BORROWERLEDGERPAYMENT', 'com/rameses/clfc/treasury/ledger/amnesty/smc/report/BorrowerLedgerPayment.jasper')
        ];
    }
}

