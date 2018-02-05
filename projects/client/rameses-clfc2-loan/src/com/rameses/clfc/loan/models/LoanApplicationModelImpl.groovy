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
    
    void open() { 
        if ( !entity?.objid ) throw new Exception('loan application objid is required'); 

        entity = appSvc.open([ objid: entity.objid ]); 
        entity._consumed = true; 
        buildMenuItems(); 
    } 
    
    void buildMenuItems() {
        def items = [];
        def type = entity?.borrower?.type?.toString().toLowerCase();
        items << [name:'principalborrower', caption:'Principal Borrower'];
        items << [name:'loandetail', caption:'Loan Details'];

        if ( type == 'individual') { 
            items << [name:'business', caption:'Business'];
            items << [name:'collateral', caption:'Collateral'];
            items << [name:'otherlending', caption:'Other Lending'];
            items << [name:'jointborrower', caption:'Joint Borrower'];
            items << [name:'comaker', caption:'Co-Maker'];
            items << [name:'attachment', caption:'Attachments'];
        }
        items << [name:'comment', caption:'Comments'];
        items << [name:'recommendation', caption:'Recommendations'];
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
            
            if (o.opener != null && o.dataChangeHandler != null) {
                o.dataChangeHandler();
            }
            subFormHandler.reload();
        } 
    ] as ListPaneModel;
    
    def subFormHandler = [
        getOpener: {
            if (selectedMenu == null) {
                return new Opener(outcome:'blankpage'); 
            }            
            
            def op = selectedMenu.opener;
            if (op == null) {
                def invtype = 'loanapp-'+ selectedMenu.name +':open';
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
    
    
    boolean getIsPending() {
        return ( entity.state == 'PENDING' && entity.appmode != 'CAPTURE') 
    }
    def submitForInspection() {
        def handler = {o->
            o.objid = entity.objid; 
            entity.state = appSvc.submitForInspection(o).state;
            binding.refresh('title|formActions|opener');
        }
        return InvokerUtil.lookupOpener("application-forinspection:create", [handler:handler])
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
        
        if (!entity.businesses) entity.businesses = appSvc.getBusinesses([objid: entity.objid]);

        def business = entity.businesses.find{ it.ci?.evaluation == null }
        if (business) throw new Exception("CI Report for business $business.tradename is required.");
        def handler = {o->
            o.objid = entity.objid; 
            entity.state = appSvc.submitForCrecom(o).state;
            binding.refresh('title|formActions|opener');
            if(selectedMenu.opener.properties.key.matches('recommendation')) {
                selectedMenu.opener = null;
                subFormHandler.refresh();
            }
        } 
        return InvokerUtil.lookupOpener("application-forcrecom:create", [handler:handler]);
    }
    
    boolean getIsForCrecom() {
        return (entity.state == 'FOR_CRECOM' && entity.appmode != 'CAPTURE');
    }
    def submitForApproval() {
        def handler = {o->
            o.objid = entity.objid; 
            entity.state = appSvc.submitForApproval(o).state;
            binding.refresh('title|formActions|opener');
            if(selectedMenu.opener.properties.key.matches('recommendation')) {
                selectedMenu.opener = null;
                subFormHandler.refresh();
            }
        }
        return InvokerUtil.lookupOpener("application-forapproval:create", [handler:handler]);
    }
    
    def returnForCi() {
        def handler = {o->
            o.objid = entity.objid; 
            entity.state = appSvc.returnForCI(o).state;
            binding.refresh('title|formActions|opener');
        }
        return InvokerUtil.lookupOpener("application-returnforci:create", [handler:handler]);
    }
    
    boolean getIsApproved() {
        return (entity.state == 'APPROVED' && mode == 'read' && entity.appmode != 'CAPTURE');
    }
    
    def backOut() {
        def handler = {o->
            println o
        }
        return InvokerUtil.lookupOpener("backout:create", [handler:handler]);
    }
    
    def submitForRelease() {
        println 'submit for release'
    }    
}