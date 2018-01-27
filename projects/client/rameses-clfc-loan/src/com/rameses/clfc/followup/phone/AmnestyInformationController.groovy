package com.rameses.clfc.followup.result

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class AmnestyInformationController 
{
    @Binding
    def binding;
    
    @PropertyChangeListener
    def listener = [
        'entity.amnestyoption': { o->
            switch (o) {
                case 'avail'    : entity?.rejectedamnesty = [:]; 
                                  break;
                case 'reject'   : entity?.availedamnesty = [:]; 
                                  break;
                default         : entity?.availedamnesty = [:];
                                  entity?.rejectedamnesty = [:];
                                  break;
            }
            binding?.refresh('opener');
        }
    ];
    
    def OPTION_PREFIX = 'followup:result:amnesty:';
    
    def entity, mode = 'read';
    def currentamnesty, availedamnesty;
    def selectedOption, amnestyOptionList = [];
    
    void init() {
        currentamnesty = entity.currentamnesty;
        if (!currentamnesty) currentamnesty = [:];
        
        resetOptionList();
        binding?.refresh();
    }
    
    void resetOptionList() {
        amnestyOptionList = [];
        def xlist = Inv.lookupOpeners('followup:result:amnesty:plugin');
        def list = [], props;
        xlist?.each{ o->
            props = o.properties;
            amnestyOptionList << [caption: o.caption, reftype: props?.reftype, index: props?.index];
        }
        amnestyOptionList?.sort{ it.index }
    }
    
    def getOpener() {
        if (!entity.amnestyoption) return null;
        
        def params = [
            entity  : entity,
            mode    : mode
        ];
        def op = Inv.lookupOpener(OPTION_PREFIX + entity?.amnestyoption, params);
        if (!op) return null;
        
        return op;
    }
}

