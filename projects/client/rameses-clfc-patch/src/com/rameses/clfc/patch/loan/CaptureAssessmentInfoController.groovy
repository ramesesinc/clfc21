package com.rameses.clfc.patch.loan

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class CaptureAssessmentInfoController {

    @Service("LoanAppCaptureRenewService")
    def service;
    
    def entity, mode = 'read';
    
    void init() {
        if (!entity) entity = [:];
        if (!entity.assessmentinfo) entity.assessmentinfo = [:];
        listHandler?.reload();
    }
    
    void assess() {
        entity.assessmentinfo = service.assess(entity);
        listHandler?.reload();
    }
    
    def listHandler = [
        fetchList: { o->
            if (!entity.assessmentinfo.charges) entity.assessmentinfo.charges = [];
            return entity.assessmentinfo.charges;
        }
    ] as BasicListModel;
}

