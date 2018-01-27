package com.rameses.clfc.report.ledger.branch

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;

class BorrowerBranchLedgerReportController extends ReportModel
{
    @Service('LoanLedgerBranchPostingPreviewService')
    def service;
    
    def ledgerid;
	
    void preview() {
        viewReport();
    }
    
    public Map getParameters() {
        return [:];
    }

    public Object getReportData() {
        def params = [objid: ledgerid];
        return service.getReportData(params);
    }

    public String getReportName() {
        return "com/rameses/clfc/report/ledger/branch/BorrowerLedger.jasper";
    }

    public SubReport[] getSubReports() {
        return [
            new SubReport('BORROWERLEDGERPAYMENT', 'com/rameses/clfc/report/ledger/branch/BorrowerLedgerPayment.jasper')
        ];
    }
}

