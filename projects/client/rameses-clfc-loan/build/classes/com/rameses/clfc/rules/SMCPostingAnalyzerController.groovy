package com.rameses.clfc.rules

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class SMCPostingAnalyzerController 
{
    @Service('SampleSMCPostingRuleService')
    def ruleSvc;
    
    def entity = [:];
    
    void init() {
        //entity = [dtreleased: '2016-02-01', interest: 8.35, absentpenalty: 1.5, dailydue: 50];
        entity = [amount: 43000, balance: 43000, payment: 20000];
    }
    
    void runTest() {
        ruleSvc.execute(entity);
    }
}