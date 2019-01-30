package com.rameses.clfc.loan.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

public class LoanApplicationModelImpl { 
    
    @Service('LoanApplicationService')
    def appSvc;
    
    @Binding
    def binding;
        
    def entity = [:];
    def menuitems = [];
    def mode = 'read';
    def handlers = [:];
        
    @FormId
    def getFormId() { 
        return 'LOAN: '+ entity?.appno;
    }
    @FormTitle
    def getFormTitle() { 
        return getFormId(); 
    }
    
    def getTitle() {
        if ( !entity ) return "";
        
        def buffer = new StringBuffer();
        buffer.append('<html><body>');
        buffer.append('<font color="#483d8b" size="5">'+entity.appno+' - </font>');
        buffer.append('<font color="red" size="5">'+entity.state+'</font><br>');
        buffer.append('<font color="#444444" size="3"><b>'+entity.borrower?.name+'</b></font>');
        buffer.append('</body></html>');
        return buffer.toString();
    }
    
    def copyMap( src ) {
        if (src instanceof Map) {
            def data = [:];
            src.each{ k, v->
                if (v instanceof Map) {
                    data[k] = copyMap( v );
                } else if (v instanceof List) {
                    data[k] = copyList( v );
                } else {
                    data[k] = v;
                }
            }
            return data;
        }
        return null;
    }
    
    def copyList( src ) {
        if (src instanceof List) {
            def list = [];
            src.each{ 
                if (it instanceof Map) {
                    list << copyMap( it );
                } else if (it instanceof List) {
                    list << copyList( it );
                } else {
                    list << it;
                }
            }
            return list;
        }
        return null;
    }
    
    void open() { 
        if ( !entity?.objid ) throw new Exception('loan application objid is required'); 

        entity = appSvc.open([ objid: entity.objid ]); 
        entity._consumed = true; 
        buildMenuItems(); 
    } 
    
    void buildMenuItems() {
        def items = [];
        def type = entity?.borrower?.type?.toString().toLowerCase();
        items << [name:'borrower', caption:'Principal Borrower'];
        if ( type == 'individual') { 
            items << [name:'jointborrower', caption:'Joint Borrower'];
            items << [name:'comaker', caption:'Co-Maker'];
        }
        
        items << [name:'loandetail', caption:'Loan Details'];

        if ( type == 'individual') { 
            items << [name:'business', caption:'Business'];
            items << [name:'collateral', caption:'Collateral'];
            items << [name:'otherlending', caption:'Other Lending'];
            items << [name:'attachment', caption:'Attachments'];
        }
        items << [name:'cireport', caption:'CI Reports'];
        items << [name:'recommendation', caption:'Recommendations'];
        items << [name:'approved-recommendation', caption:'Approved Recommendations'];
        if (entity.state.toString().matches('FOR_PROCESSING|FOR_RELEASE|RELEASED|CLOSED')) {
            items << [name:'assessment', caption:'Assessment'];
        }
        items << [name:'comment', caption:'Logs'];
        /*
        [name:'fla', caption:'FLA'],
        [name:'prevfla', caption:'Previous FLA'],
        [name:'summary', caption:'Summary'] 
         */
        menuitems = items;
    }
    
    def selectedMenu;
    def listHandler = [
        getDefaultIcon: {
            return 'Tree.closedIcon'; 
        },         
        getItems: { 
            return menuitems;
        },
        beforeSelect: {o-> 
            return (mode == 'read');
        }, 
        onselect: {o-> 
            def consumed = entity.remove('_consumed');
            if ( consumed.toString() != 'true' ) {
                def res = appSvc.open([objid: entity.objid, itemname: o?.name]);
                if ( res ) { 
                    entity.clear(); 
                    entity.putAll( res );  
                }
            } 
            
            if (handlers.dataChangeHandler) handlers.dataChangeHandler;
            binding?.refresh('opener');
            /*
            if (o.opener != null && o.dataChangeHandler != null) {
                o.dataChangeHandler();
            }
            */
            //subFormHandler.reload();
        } 
    ] as ListPaneModel;
    
    
    def getOpener() {
        if (selectedMenu == null) {
            return new Opener(outcome:'blankpage'); 
        }
        handlers = [:];
        
        def invtype = 'loanapp-' + selectedMenu?.name;
        def params = [
            caller: this,
            loanapp: copyMap( entity ),
            menuitem: copyMap( selectedMenu ),
            handlers: handlers
        ];
        if (selectedMenu?.name == 'borrower' && entity?.borrower?.type) {
            invtype += entity?.borrower?.type?.toString().toLowerCase();
        }

        def op = Inv.lookupOpener( invtype + ':open', params);
        if (!op) new Opener(outcome:'blankpage');
        return op;
    }
    
    /*
    def subFormHandler = [
        getOpener: {
            if (selectedMenu == null) {
                return new Opener(outcome:'blankpage'); 
            }            
            
            def op = selectedMenu.opener;
            if (op == null) {
                def invtype = 'loanapp-'+ selectedMenu.name +':open';
                println 'invtype->' + invtype;
                def invparam = [:]; 
                invparam.caller = this; 
                invparam.loanapp = entity; 
                invparam.menuitem = selectedMenu;
                op = InvokerUtil.lookupOpener(invtype, invparam); 
                selectedMenu.opener = op; 
            }
            return op; 
        } 
    ] as SubFormPanelModel; 
    */
    
    
    boolean getIsPending() {
        return ( entity.state == 'PENDING' && entity.appmode != 'CAPTURE') 
    }
    void submitForInspection() { 
        if ( MsgBox.confirm('You are about to submit this application for inspection. Continue?')) {
            def p = [ objid: entity.objid ];
            entity.state = appSvc.submitForInspection(p).state;
            binding.refresh('title|formActions|opener');
        }
    }
    
    boolean getIsForInspection() {
        return (entity.state == 'FOR_INSPECTION' && entity.appmode != 'CAPTURE');
    }
    def submitForCrecom() { 
        if ( entity.appmode.toString().equalsIgnoreCase("ONLINE")) {
            //do nothing 
        } else {
            if (!entity.route.code) throw new Exception('Route for loan application is required.');
        }

        appSvc.verifyCIReport([ objid: entity.objid ]); 
        
        def p = [ loanapp: entity ]; 
        p.handler = { 
            entity.state = appSvc.submitForCrecom([ objid: entity.objid ]).state;
            binding.refresh('title|formActions|opener');
            /*
            if ( selectedMenu ) {
                selectedMenu.opener = null;
                subFormHandler.refresh();
            }
            */
        } 
        return Inv.lookupOpener("cireport:review", p);
    }
    
    boolean getIsForCrecom() {
        return (entity.state == 'FOR_CRECOM' && entity.appmode != 'CAPTURE');
    }
    
    boolean getIsSendBack() {
        return (entity.state == 'SEND_BACK' && entity.appmode != 'CAPTURE');
    }
    
    void submitForApproval() {
        if ( MsgBox.confirm('You are about to submit this application for approval. Continue?')) {
            entity.state = appSvc.submitForApproval([ objid: entity.objid ]).state;
            binding.refresh('title|formActions|opener');
            /*
            if ( selectedMenu ) {
                selectedMenu.opener = null;
                subFormHandler.refresh();
            }
            */
        } 
    }
    
    def returnForCi() {
        def handler = {o->
            o.objid = entity.objid; 
            entity.state = appSvc.returnForCI(o).state;
            binding.refresh('title|formActions|opener');
        }
        return InvokerUtil.lookupOpener("application-returnforci:create", [handler:handler]);
    }
    
    def viewSendBackRemarks() {
        def op = Inv.lookupOpener('remarks:open', [remarks: entity.sendbackremarks]);
        if (!op) return null;
        return op;
    }
    
    def viewDisapproveRemarks() {
        def op = Inv.lookupOpener('remarks:open', [remarks: entity.disapproveremarks]);
        if (!op) return null;
        return op;
    }
    
    boolean isForApproval() {
        return (entity.state == 'FOR_APPROVAL' && entity.appmode != 'CAPTURE');
    }
    
    void sendBack() {
        if ( MsgBox.confirm('You are about to send back the application. Continue?')) {
            entity.state = appSvc.sendBack([ objid: entity.objid])?.state; 
            binding.refresh('title|formActions|opener');
        }
    }
    
    void approve() {
        if ( MsgBox.confirm('You are about to approve this application. Continue?')) {
            entity.state = appSvc.approve([ objid: entity.objid])?.state; 
            binding.refresh('title|formActions|opener');
        }
    }
    
    void disapprove() {
        if ( MsgBox.confirm('You are about to disapprove this application. Continue?')) {
            entity.state = appSvc.disapprove([ objid: entity.objid])?.state; 
            binding.refresh('title|formActions|opener');
        }
    }
    
    void disqualifiedOut() {
        if ( MsgBox.confirm('You are about to disqualify this application. Continue?')) {
            entity.state = appSvc.disqualifiedOut([ objid: entity.objid])?.state; 
            binding.refresh('title|formActions|opener');
        }
    }
    
    void cancelledOut() {
        if ( MsgBox.confirm('You are about to cancel out this application. Continue?')) {
            entity.state = appSvc.cancelledOut([ objid: entity.objid])?.state; 
            binding.refresh('title|formActions|opener');
        }
    }
    
    boolean getIsDisapproved() {
        return (entity.state == 'DISAPPROVED' && entity.appmode != 'CAPTURE');
    }
    
    boolean isApproved() {
        return (entity.state == 'APPROVED' && entity.appmode != 'CAPTURE');
    }
    
    boolean isForRequirement() {
        return (entity.state == 'FOR_REQUIREMENT' && entity.appmode != 'CAPTURE');
    }
    void submitForRequirement() {
        if ( MsgBox.confirm('You are about to submit this application for requirement. Continue?')) {
            entity.state = appSvc.submitForRequirement([ objid: entity.objid])?.state; 
            binding.refresh('title|formActions|opener');
        }
    }

    boolean isForProcessing() {
        return (entity.state == 'FOR_PROCESSING' && entity.appmode != 'CAPTURE');
    }
    void submitForProcessing() {
        if ( MsgBox.confirm('You are about to submit this application for processing. Continue?')) {
            entity.state = appSvc.submitForProcessing([ objid: entity.objid])?.state; 
            binding.refresh('title|formActions|opener');
        }
    }

    boolean isForRelease() {
        return (entity.state == 'FOR_RELEASE' && entity.appmode != 'CAPTURE');
    }
    void submitForRelease() {
        if ( MsgBox.confirm('You are about to submit this application for release. Continue?')) {
            entity.state = appSvc.submitForRelease([ objid: entity.objid])?.state; 
            binding.refresh('title|formActions|opener');
        }
    }    

    boolean isReleased() {
        return (entity.state == 'RELEASED' && entity.appmode != 'CAPTURE');
    }
    void release() {
        if ( MsgBox.confirm('You are about to release this application. Continue?')) {
            entity.state = appSvc.release([ objid: entity.objid])?.state; 
            binding.refresh('title|formActions|opener');
        }
    }    
}