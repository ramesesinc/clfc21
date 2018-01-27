package com.rameses.clfc.audit.amnesty.update

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class AmnestyUpdateFixController {

    @Binding
    def binding;
    
    @Service('AmnestyUpdateService')
    def service;
    
    @PropertyChangeListener
    def listener = [
        'entity.withmd': { o->
            switch (o) {
                case 0: entity.update.usedate = 0;
                        entity.update.date = null;
                        entity.update.day = 0;
                        entity.update.month = 0;
                        break;
            }
            binding?.refresh();
        },
        'entity.usedate': { o->
            switch (o) {
                case 0: entity.update.date = null;
                        break;
                case 1: entity.update.day = 0;
                        entity.update.month = 0;
                        break;
            }
            binding?.refresh();
        }
    ];
    
    def entity, mode = 'read';
    def daysList = [], maxDay = 31;
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.availed) entity.availed = [:];
        if (!entity.update) entity.update = [:];
        resetDaysList();
    }
    
    void resetDaysList() {
        daysList = [];
        for (int i=0; i <= maxDay; i++) { daysList << i; }
        binding?.refresh();
    }
    
    def amnestyLookup = Inv.lookupOpener('amnesty:update:lookup', [
         onselect: { o->
             def item = service.getFixAmnestyInformation(o);
             if (item) {
                 entity.amnesty = item.amnesty;
                 entity.availed = item.availed;
                 entity.update = item.update;
                 binding?.refresh();
             }
         },
         type: 'FIX'
    ]);
}

