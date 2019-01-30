package com.rameses.clfc.loan.controller;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

public class LoanAppController { 
    
    @Service('LoanAppService')
    def service;
    
    @Binding
    def binding;
    
    def loanappid;
    def source = [:];
    def entity = [:];
    def mode = 'read';
    def handlers = [:];
    
    @FormId
    def getFormId() { 
        return 'LOAN-'+entity.appno;
    }

    @FormTitle
    def getFormTitle() { 
        return getFormId(); 
    } 
        
    void open() {
        source = entity;
        def data = service.open([objid: source?.objid]);
        entity = [:];
        entity.putAll(data);
        loanappid = entity.objid;
    }
    
    def getTitle() {
        def buffer = new StringBuffer();
        buffer.append('<html><body>');
        buffer.append('<font color="#483d8b" size="5">'+entity.appno+' - </font>');
        buffer.append('<font color="red" size="5">'+entity.state+'</font><br>');
        buffer.append('<font color="#444444" size="3"><b>'+entity.borrower?.name+'</b></font>');
        buffer.append('</body></html>');
        return buffer.toString();
    }
    
    def getMenuItems() {        
        def items = [];
        def type = entity?.borrower?.entitytype?.toString().toLowerCase();
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
        items << [name:'comment', caption:'Logs'];
        
        /*
        items << [name:'borrower', caption:'Principal Borrower'];
        items << [name:'loandetail', caption:'Loan Details'];
        items << [name:'business', caption:'Business'];
        items << [name:'collateral', caption:'Collateral'];
        items << [name:'otherlending', caption:'Other Lending'];
        items << [name:'jointborrower', caption:'Joint Borrower'];
        items << [name:'comaker', caption:'Co-Maker'];
        //items << [name:'attachment', caption:'Attachments'];
        items << [name:'comment', caption:'Comments'];
        items << [name:'recommendation', caption:'Recommendations'];
        */
            /*
            [name:'fla', caption:'FLA'],
            [name:'prevfla', caption:'Previous FLA'],
            [name:'summary', caption:'Summary'] 
             */
        return items;
    }
    
    def selectedMenu;
    def listHandler = [
        getDefaultIcon: {
            return 'Tree.closedIcon'; 
        },         
        getItems: { 
            return menuItems;
        },
        beforeSelect: {o-> 
            return (mode == 'read');
        }, 
        onselect: { o->
            binding?.refresh('opener');
            /*
            def data = service.open([objid: loanappid, name:o?.name]);
            entity.clear();
            entity.putAll(data);
            //println 'list handler borrower ' + entity?.borrower;
            if (o.opener != null && o.dataChangeHandler != null) {
                o.dataChangeHandler();
            }
            binding?.refresh('opener');
            */
            //subFormHandler.reload();
        } 
    ] as ListPaneModel;
    
    def copyMap( src ) {
        def data = [:];
        src?.each{ k, v->
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
    
    def copyList( src ) {
        def list = [];
        src?.each{
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
    
    def getOpener() {
        if (selectedMenu == null) {
            return new Opener(outcome:'blankpage'); 
        }

        def invtype = 'loanapp-' + selectedMenu?.name;
        def params = [
            caller: this,
            loanapp: copyMap(entity),
            menuitem: copyMap(selectedMenu),
            handlers: handlers
        ];
        if (selectedMenu?.name == 'borrower' && entity?.borrower?.entitytype) {
            invtype += entity.borrower.entitytype.toLowerCase();
        }

        def op = Inv.lookupOpener( invtype + ':open', params);
        if (!op) new Opener(outcome:'blankpage');
        return op;
    }
    
    /*
    def xsubFormHandler = [
        getOpener: {
            if (selectedMenu == null) {
                return new Opener(outcome:'blankpage'); 
            }
            
            def op = selectedMenu.opener;
            //println 'subform borrower ' + entity?.borrower;
            if (op == null) {
                def type = entity?.borrower?.entitytype?.toLowerCase();
                if (type) {      
                    //println 'subtform handler type ' + type;
                    //println 'subform handler name ' + selectedMenu?.name;
                    def invtype = 'loanapp-' + selectedMenu.name + type + ':open';
                    op = InvokerUtil.lookupOpener(invtype, [ 
                        caller      : this, 
                        loanapp     : entity, 
                        selectedMenu: selectedMenu
                    ]); 
                    selectedMenu.opener = op;
                }
            }
            return op;
        } 
    ] as SubFormPanelModel;
    */

    boolean getIsForEdit() {
        if(entity.state.matches('INCOMPLETE|PENDING|FOR_INSPECTION') && mode == 'read' && entity.appmode != 'CAPTURE') return true;
        return false;
    }
    
    boolean getIsEditable() {
        if(mode == 'edit' && entity.appmode != 'CAPTURE') return true;
        return false;
    }
    
    void edit() {
        mode = 'edit';
        binding?.refresh('opener');
        //println 'edit';
        //subFormHandler.refresh();
    }
    
    void save() {
        //selectedMenu.saveHandler(); 
        if (handlers.saveHandler) handlers.saveHandler();
        mode = 'read'; 
        binding?.refresh('title|opener');
        //subFormHandler.refresh();
    }
    
    void cancel() {
        if (!MsgBox.confirm('Are you sure you want to cancel all the changes made?')) return;
        
        mode = 'read'; 
        //selectedMenu.opener = null;
    }
    
    boolean getIsPending() {
        if(entity.state == 'PENDING' && mode == 'read' && entity.appmode != 'CAPTURE') return true;
        return false;
    }
    
    def submitForInspection() {
        def handler = {o->
            o.objid = loanappid;
            entity.state = service.submitForInspection(o).state;
            binding.refresh('title|formActions|opener');
        }
        return InvokerUtil.lookupOpener("application-forinspection:create", [handler:handler])
    }
    
    boolean getIsForInspection() {
        if(entity.state == 'FOR_INSPECTION' && mode == 'read' && entity.appmode != 'CAPTURE') return true;
        return false;
    }
    
    def submitForCrecom() { 
        if ( entity.appmode.toString().equalsIgnoreCase("ONLINE")) {
            //do nothing 
        } else {
            if (!entity.route.code) throw new Exception('Route for loan application is required.');
        }
        
        if (!entity.businesses) entity.businesses = service.getBusinesses([objid: entity.objid]);

        def business = entity.businesses.find{ it.ci?.evaluation == null }
        if (business) throw new Exception("CI Report for business $business.tradename is required.");
        def handler = {o->
            o.objid = loanappid;
            entity.state = service.submitForCrecom(o).state;
            binding.refresh('title|formActions|opener');
            /*
            if(selectedMenu.opener.properties.key.matches('recommendation')) {
                selectedMenu.opener = null;
                subFormHandler.refresh();
            }
            */
        } 
        return InvokerUtil.lookupOpener("application-forcrecom:create", [handler:handler]);
    }
    
    boolean getIsForCrecom() {
        if(entity.state == 'FOR_CRECOM' && mode == 'read' && entity.appmode != 'CAPTURE') return true;
        return false;
    }
    
    def submitForApproval() {
        def handler = {o->
            o.objid = loanappid;
            entity.state = service.submitForApproval(o).state;
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
            o.objid = loanappid;
            entity.state = service.returnForCI(o).state;
            binding.refresh('title|formActions|opener');
        }
        return InvokerUtil.lookupOpener("application-returnforci:create", [handler:handler]);
    }
    
    boolean getIsApproved() {
        if(entity.state == 'APPROVED' && mode == 'read' && entity.appmode != 'CAPTURE') return true;
        return false;
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
