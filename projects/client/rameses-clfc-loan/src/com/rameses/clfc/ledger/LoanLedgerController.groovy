package com.rameses.clfc.ledger;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.text.*;
import java.rmi.server.UID;

class LoanLedgerController
{
    @Binding
    def binding;
    
    @Service('LoanLedgerService')
    def service;
    
    @FormTitle
    def getFormTitle() {
        def str = "";
        if (entity) str = entity.name + " - " + entity.appno;
        return str;
    }
    
    @FormId
    def getFormId() {
        if (!entity) return new UID();
        return entity.objid;
    }

    String title = "General Information";
    def entity, optionlist;
    def page = 'default';
    def rows = 30;
    def df = new DecimalFormat("#,##0.00");
    
    def open() {
        entity.rows = rows;
        entity = service.open(entity);
        if (!entity.rows) entity.rows = rows;
        //entity.rows = rows;
        //entity.lastpageindex = getLastPageIndex(entity);
        //println 'entity ' + entity;
        page = 'default';
        return page;
    }

    def selectedOption;
    def optionsListModel = [
        getItems: {
            def xlist = Inv.lookupOpeners("loanledger-plugin");
            def list = [], props;
            xlist?.each{ o->
                props = o.properties;
                if (!entity.amnesty && props.reftype == 'amnestypreview') {
                    /*do nothing*/
                } else {
                    def item = [
                        caption : createCaption(o.caption, props.reftype), 
                        reftype : props.reftype, 
                        index   : props.index
                    ];
                    def flag = false;
                    if (entity?.loantype == 'BRANCH' && item.reftype == 'branchledgerpreview') {
                        flag = true;
                    } else if (entity?.loantype != 'BRANCH' && item.reftype == 'ledgerpreview') {
                        flag = true;
                    } else if (!item.reftype.matches('(ledgerpreview|branchledgerpreview)')) {
                        flag = true;
                    }
                    
                    if (flag == true) {
                        list << item;
                    }
                }
            }
            return list;
        }, 
        onselect: { o->
            //query.state = o.state;
            //reloadAll();
            binding?.refresh('opener');
        }
    ] as ListPaneModel;
    
    private def createCaption( caption, type ) {
        def str = '';
        //caption;
        switch (type) {
            case 'ledgerpreview'        : str = "Ledger - " + df.format(entity.loanamount); 
                                          break;
            case 'branchledgerpreview'  : str = "Ledger - " + df.format(entity.loanamount); 
                                          break;
            case 'amnestypreview'       : str += (entity?.amnesty?.type? entity.amnesty.type : '') + ': ';
                                          str += (entity?.amnesty?.description? entity.amnesty.description : ''); 
                                          break;
            default                     : str = caption; 
                                          break;
        }
        return str;
    }
    
    def getOpener() {
        def reftype = selectedOption?.reftype;
        def params = [entity: entity];
        def inv = 'loanledger:' + selectedOption?.reftype
        if (reftype == 'amnestypreview') {
            params.data = entity.amnesty;
            inv += ':' + params?.data?.type?.toLowerCase();
        }
        
        def op = Inv.lookupOpener(inv, params);
        if (!op) return null;
        
        op.caption = createCaption(op.caption, reftype);
        return op;
    }
    
    def close() {
        return '_close';
    }
    
}