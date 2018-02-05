package com.rameses.clfc.patch.loan.detail;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class ChangeLoanAppDetailController {
    
    @Binding
    def binding;
    
    @Service("ChangeLoanAppDetailService")
    def service;

    String title = 'Change Loan App Detail';
    
    def entity;
    def borrowerLookupHandler = Inv.lookupOpener("loanapp_capture:lookup", [
        onselect: { o->
            entity = o;
            binding?.refresh();
        }
    ]);
    def mode;
    def data;
    
    def init() {
        entity = [:];
        mode = 'init';
        return "default"
    }
    
    def selectedOption;
    def listModel = [
        getItems: {
            //return svc.getOptions();
            def list = [];
            
            def ops = Inv.lookupOpeners("change:loanapp:detail:plugin");
            ops?.each{ o->
                def props = o.properties;
                if (props) {
                    list << [reftype: props.reftype, caption: o.caption, idx: props.index];
                }
            }
            list.sort{ it.idx }
            
            return list;
        }, 
        onselect: { o->
            binding?.refresh("opener");
        }
    ] as ListPaneModel;
    
    def getOpener() {
        if (!selectedOption) return;
        
        def inv = "change:loanapp:detail:" + selectedOption.reftype;
        
        def op = Inv.lookupOpener(inv, [entity: data]);
        if (!op) return null;
        return op;
    }
 
    def close() {
        return "_close";
    }

    def next() {
        mode = 'update';
        data = service.open(entity);
        //data = [:];
        //println 'entity ' + entity;
        /*
        entity.each{k, v->
            data[k] = v;
            if (v instanceof Map) {
                def m = [:];
                v.each{k2, v2->
                    m[k2] = v2;
                }  
                data[k] = m;
            }
        }
        */
        return "main"
    }

    def back() {
        mode = 'init';
        return "default";
    }

    def validate( data ) {
        def result = [:];
        def msg = "";
        
        if (!data.appno) msg += "App. No. is required.\n";
        if (!data.loantype) msg += "Loan Type is required.\n";
        if (!data.dtreleased) msg += "Release Date is required.\n";
        if (!data.loanamount) msg += "Amount Released is required.\n";
        if (!data.borrower.name) msg += "Borrower is required.\n";
        if (!data.route) msg += "Route is required.\n";
        if (!data.producttype) msg += "Product Type is required.\n";
        
        if (msg) {
            result.haserror = true;
        }
        result.msg = msg;
        return result;
    }
    
    def save() {
        def res = validate(data);
        if (res.haserror == true) throw new RuntimeException(res.msg);
        
        String msg = "<html>Changes made will affect the corresponding ledger for Application <b>" + data.appno + "</b>. Do you want to continue?</html>";
        if (MsgBox.confirm(msg)) {
            service.save(data);
            msg = "<html>Application <b>" + data.appno + "</b> updated successfully.";
            MsgBox.alert(msg);
            return init();
        }
    }
}