package com.rameses.clfc.loan.models

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.clfc.loan.html.ApprovedRecommendationHtmlBuilder;

class LoanApplicationApprovedRecommendationModel
{
    @Binding
    def binding;
    
    def caller, loanapp, menuitem, handlers;
    def recommendations;
    
    @Service('LoanApplicationApprovedRecommendationService')
    def service;
    
    def htmlbuilder = new ApprovedRecommendationHtmlBuilder();
    def entity
    
    void init() {
        //recommendations = service.getRecommendations([ appid: loanapp?.objid ]);        
        entity = service.getRecommendationInfo([ appid: loanapp?.objid ]);
        if (!entity) entity = [:];
    }
    
    def getState() {
        return caller?.entity?.state;
    }
    
    def selectedRecom;
    def recommendationHandler = [
        fetchList: {
            if (!entity.recommendations) entity.recommendations = [];
            return entity.recommendations;
        }
    ] as BasicListModel;
    
    def getHtmlview() {        
        return htmlbuilder.getApprovedRecommendation( selectedRecom );
    }
    
    void select() {
        if (!selectedRecom) throw new Exception('Please selected recommendation to proceed.');
        
        entity = service.selectRecommendation([ appid: loanapp?.objid, recommendation: selectedRecom ]);
        binding?.refresh();
        recommendationHandler?.reload();
    }
    
    def getApproveRecomInfo() {
        def str = '';
        if (entity?.approvedrecommendation) {
            str += 'Amount: ' + entity.approvedrecommendation.amount + '\n';
            str += 'Remarks: ' + entity.approvedrecommendation.remarks;
        }
        
        return str;
    }
}

