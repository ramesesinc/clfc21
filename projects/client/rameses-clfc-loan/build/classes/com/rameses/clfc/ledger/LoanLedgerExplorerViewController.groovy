package com.rameses.clfc.ledger

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class LoanLedgerExplorerViewController extends ExplorerViewController
{
    @Service('LoanLedgerService')
    def svc;
    
    String serviceName = 'LoanLedgerService';
    String getDefaultFileType() {
        return 'loanledger';
    }
    //String defaultFileType = 'loanledger';
    String entityName = 'loanledger';
    private LoanLedgerExplorerViewListController listHandler;
    private Map queryMap = [:];
    
    public void setQueryMap( queryMap ) {
        this.queryMap = queryMap;
    }
    
    public Map getQueryMap() {
        if (!this.queryMap) queryMap = [:];
        return this.queryMap;
    }
    
    public ExplorerViewListController getListHandler() {
        if (this.listHandler == null) {
            this.listHandler = new LoanLedgerExplorerViewListController();
            this.listHandler.setParent(this);
        }
        
        return this.listHandler;
    }
}

