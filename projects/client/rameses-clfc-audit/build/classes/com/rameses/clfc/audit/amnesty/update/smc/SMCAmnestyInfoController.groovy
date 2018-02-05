package com.rameses.clfc.audit.amnesty.update.smc

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class SMCAmnestyInfoController {
	
    @Script("Template")
    def template;
    
    @Binding
    def binding;
    
    @Service('AmnestyUpdateService')
    def service;
    
    def entity, mode = 'read';
    
    def amnestyLookup = Inv.lookupOpener('amnesty:update:lookup', [
         onselect: { o->
             def item = service.getSMCAmnestyInformation(o);
             if (item) {
                 entity.amnesty = item.amnesty;
                 entity.update = item.update;
                 binding?.refresh();
             }
         },
         type: 'SMC'
    ]);

    def getConditionHtml() {
        if (!entity?.amnesty?.conditions) entity?.amnesty?.conditions = [];
        //return template.render( "html/smc_condition_html.gtpl", [rule: entity, editable:true] );
        
        def params = [
            conditions  : entity?.amnesty?.conditions,
            mode        : 'read'
        ];
        return template.render('html/smc_condition_html.gtpl', params);
    }
}

