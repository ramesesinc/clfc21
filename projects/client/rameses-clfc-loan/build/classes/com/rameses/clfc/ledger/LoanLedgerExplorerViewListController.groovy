package com.rameses.clfc.ledger

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class LoanLedgerExplorerViewListController extends ExplorerViewListController
{
    
    private LoanLedgerExplorerViewController parentController2;
    
    String getTitle() {
        def props = this.node.properties;
        def total = props.totalaccounts;
        if (!total) total = 0;
        
        return total + ' Accounts';
    }
    
    String getEntityName() {
        def str = '';
        if (parentController2) {
            str = parentCotnroller2.defaultFileType;
        }
        println 'entity name ' + str;
        return str;
    }
    
    private void resetTotalAccounts() {
        resetTotalAccounts([:]);
    }
    
    private void resetTotalAccounts( params ) {
        def total = this.parentController2.svc.getTotalAccounts(params);
        if (!total) total = 0;
        def props = node.properties;
        props.totalaccounts = total;
        binding?.refresh('title');
    }
    
    public void updateView() {
        def qrymap = [:];
        qrymap.putAll(this.parentController2.queryMap);
        super.updateView();
        getQuery().putAll(qrymap)
        reload();
    }
    
    void setNode(Node node) {
        super.setNode(node);
        resetTotalAccounts();
    }
    
    List fetchList(Map params) {
        //this.parentController2.setQuery(query);
        def list = super.fetchList(params);
        if (this.parentController2 && query) {
            this.parentController2.setQueryMap(query);
        }
        
        def prm = [typeid: params.typeid, rootid: params.rootid, searchtext: params.searchtext];
        resetTotalAccounts(prm);
        
        return list;
    }
    
    public void setParent(ExplorerViewController parentController) {
        this.parentController2 = parentController;
        super.setParent(parentController);
    }
}

