package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.text.*;

class ConditionController 
{
    @Binding
    def binding;
    
    @Service('LedgerAmnestySMCService')
    def service;
    
    def data, mode = 'read';
    def handler, xhandler;
    def varlist;
    
    
    def conditionLookup = Inv.lookupOpener('smc:condition:lookup', [
        onselect: { o->
           data.conditionid = o.code;
           data.title = o.title;
           data.datatype = o?.datatype;
           data.vardatatype = o?.vardatatype;
           data.handler = o?.handler;
           binding?.refresh();
        }
    ]);
    
    void init() {
        data = [:];
        //termList = service.getTermList();
    }
    
    def handlerList = ["decimal", "integer", "expression", "boolean", "date"]

    def getOpener() {
        if (!data?.handler) return null;
        
        def params = [
            action  : data,
            mode    : mode,
            vars    : varlist
        ]
        def op = Inv.lookupOpener('smccondition:handler:' + data?.handler, params);
        if (!op) return null;
        return op;
    }
    /*
    def getOpener() {
        if (!xhandler) return null;
        
        def op = Inv.lookupOpener('smccondition:handler:' + xhandler, [action: data, mode: mode]);
        if (!op) return null;
        return op;
    }
    */
   
    def getTermList() {
        def list = service.getTermList();
        return list;
    }
    
    void open() {
        def xdata = [:]
        xdata.putAll(data);
        data = xdata;
    }
    
    def doOk() {
        def h = data?.handler;
        switch (h) {
            case 'date'     : data.stringvalue = buildStringValue(data);
                              break;
        }
        
        if (handler) handler(data);
        return '_close';
    }
    
    def doCancel() {
        return '_close';
    }
    
    def buildStringValue( data ) {
        def val = data?.operator?.value;
        def str = '';
        switch (val) {
            case 'nomd'     : str = 'No Maturity Date';
                              break;
            case 'specify'  : data?.date = data?.datevalue;
                              str = 'until ' + parseDate(data?.datevalue, 'MMM-dd-yyyy');
                              break;
            case 'term'     : if (data?.year && data?.year > 0) str += data?.year + ' Year(s) ';
                              if (data?.month && data?.month > 0) str += data?.month + ' Month(s) ';
                              if (data?.day && data?.day > 0) str += data?.day + ' Day(s) ';
                              break;
        }
        
        return str;
    }
    
    def parseDate( date ) {
        if (!date) return null;
        
        if (date instanceof Date) {
            return date;
        } else {
            return java.sql.Date.valueOf(date);
        }
    }
    
    def parseDate( date, pattern ) {
        if (!date) return null;
        if (!pattern) pattern = 'MMM-dd-yyyy';
        
        def dt;
        if (date instanceof Date) {
            dt = date;
        } else {
            dt = java.sql.Date.valueOf(date);
        }
        
        return new SimpleDateFormat(pattern).format(dt);
    }
}

