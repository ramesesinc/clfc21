package com.rameses.rulemgmt.constraint;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*

public class DateHandlerController
{
    def action;
    
    void init() {
        if (!action) action = [:];
    }
    
    def operatorList = [
      [caption: 'No Maturity', value: 'nomd'],
      [caption: 'Specify Date', value: 'specify'],
      [caption: 'Term Month(s)/Day(s)', value: 'term']
    ];

    /*
    def operatorList = [
        [caption:"before", symbol:"<"],
        [caption:"on or before", symbol:"<="],
        [caption:"after", symbol:">"],
        [caption:"on or after", symbol:">="],
        [caption:"on", symbol:"=="],
    ];
    */
    
}