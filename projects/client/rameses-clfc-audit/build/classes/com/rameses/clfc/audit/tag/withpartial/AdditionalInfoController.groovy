package com.rameses.clfc.audit.tag.withpartial


class AdditionalInfoController 
{
    def postingdate;
    def handler;
    
    def doOk() {
        if (handler) handler(postingdate);
        return '_close';
    }
    
    def doCancel() {
        return '_close';
    }
}

