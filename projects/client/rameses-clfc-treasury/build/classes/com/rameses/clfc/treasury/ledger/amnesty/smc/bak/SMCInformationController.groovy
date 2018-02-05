package com.rameses.clfc.treasury.ledger.amnesty.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;
import java.text.*;

class SMCInformationController 
{
    @Binding
    def binding;
    
    def entity, smcmode = 'read';
    
    def decFormat = new DecimalFormat('#,##0.00');
    def dateFormat = new SimpleDateFormat('MMM dd, yyyy');
                
    @Script("Template")
    def template;
    
    @PropertyChangeListener
    def listener = [
        'entity.nomaturity': { o->
            if (o == 1) {
                entity.usedate = 0;
                entity.day = 0;
                entity.month = 0;
                entity.date = null;
            }
            buildDescription();
        },
        'entity.usedate': { o->
            if (o == 1) {
                entity.day = 0;
                entity.month = 0;
            } else if (o == 0) {
                entity.date = null;
            }
            buildDescription();
        },
        'entity.amount': { o->
            buildDescription();
        },
        'entity.date': { o->
            buildDescription();
        },
        'entity.day': { o->
            buildDescription();
        },
        'entity.month': { o->
            buildDescription();
        }
    ];
    
    def daysList = [], maxdays = 31;
    
    void init() {
        if (entity.nomaturity == null) entity.nomaturity = 1;
        if (entity.usedate == null) {
            entity.usedate = 0;
            entity.day = 0;
            entity.month = 0;
        }
        if (!entity.conditions) entity.conditions = [];
        
        resetDaysList();
    }
    
    void resetDaysList() {
        daysList = [];
        for (int i = 0; i <= maxdays; i++) {
            daysList << i;
        }
        daysList.sort{ it }
    }
    
    void buildDescription() {
        def str = '';
        if (entity.amount) {
            str = decFormat.format(entity.amount) + ' ';
            
            if (entity.nomaturity == 1) {
                str += 'No Maturity ';
            } else if (!entity.nomaturity) {
                if (entity.usedate == 1) {
                    str += 'until ' + dateFormat.format(parseDate(entity.date))  + ' ';
                } else if (!entity.usedate) {
                    if (entity.month > 0 || entity.day > 0) str += 'in ';
                    if (entity.month > 0) str += entity.month + ' Month(s) ';
                    if (entity.day > 0) str += entity.day + ' Day(s) ';
                }
            }
        }
        entity.description = str;
        binding?.refresh();
    }
    
    def parseDate( date ) {
        if (!date) return null;
        
        if (date instanceof Date) {
            return date;
        } else {
            return java.sql.Date.valueOf(date);
        }
    }
    
    def getRuleHtml() {
        println 'get rule html';
        //return template.render( "html/smc_condition_html.gtpl", [rule: entity, editable:true] );
        def params = [
            conditions  : entity?.conditions,
            mode        : smcmode
        ]
        return template.render('html/smc_condition_html.gtpl', params);
    }
    
    def selectedItem;
    def listHandler = [
        fetchList: { o->
            if (!entity.conditions) entity.conditions = [];
            return entity.conditions;
        }
    ] as BasicListModel;
    
    def addCondition() {
        def handler = { o->
            o.objid = 'SMCCOND' + new UID();
            o.parentid = entity.objid;
            
            if (!entity._addedcondition) entity._addedcondition = [];
            entity._addedcondition << o;
            
            if (!entity.conditions) entity.conditions = [];
            entity.conditions << o;
            
            listHandler?.reload();
        }
        
        def op = Inv.lookupOpener('smc:condition:create', [handler: handler, mode: smcmode]);
        if (!op) return null;
        return op;
    }
    
    def openCondition() {
        def params = [data: selectedItem, mode: smcmode];
        if (smcmode != 'read') {
            params.handler = { o->
                def itm = entity.conditions.find{ it.objid == o.objid }
                if (itm) {
                    o._edited = true;
                    itm.putAll(o);
                }
                
                if (entity._addedcondition) {
                    itm = entity._addedcondition.find{ it.objid == o.objid }
                    if (itm) itm.putAll(o);
                }
                listHandler?.reload();
            }
        }
        def op = Inv.lookupOpener('smc:condition:open', params);
        if (!op) return null;
        return op;
    }
    
    void removeCondition() {
        if (!MsgBox.confirm('You are about to remove this condition. Continue?')) return;
        
        entity.conditions.remove(selectedItem);
        if (entity._addedcondition) entity._addedcondition.remove(selectedItem);
        
        if (!entity._removedcondition) entity._removedcondition = [];
        entity._removedcondition << selectedItem;
        
        listHandler?.reload();
    }
}