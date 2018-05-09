package com.rameses.clfc.loan.attachment;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;

class LoanAppAttachmentController { 
    
    //feed by the caller
    def caller, loanapp, menuitem;
    def attachments;
    
    def mode;
    def snapshot;
    def base64;
    
    void init() {
        mode = 'read';
        menuitem.saveHandler = { save(); }  
        base64 = new com.rameses.util.Base64Cipher();  
    }
    
    void save() {
        println 'saving attachment'
    }
    void edit() {
        snapshot = (attachments ? base64.encode( attachments ) : null ); 
        mode = 'edit'; 
    }
    void cancelEdit() { 
        if (MsgBox.confirm('Are you sure you want to cancel any changes made?')) { 
            def o = ( snapshot ? base64.decode( snapshot ) : null ); 
            if ( o ) {
                attachments = o; 
                attachmentHandler?.reload();
            }
            mode = 'read'; 
        }
    }     
    
    def selectedAttachment;
    def attachmentHandler = [
        fetchList: {o->
            if(!attachments) attachments = [];
            attachments.each{ it._filetype = "attachment" }
        },
        onRemoveItem: {o->
            return removeItemImpl(o);
        },
        getOpenerParams: {o->
            return [mode: caller.mode, caller:this];
        }
    ] as EditorListModel;
    
    def addAttachment() {
        def handler = {attachment->
            attachments.add(attachment);
        }
        return InvokerUtil.lookupOpener("attachment:create", [handler:handler]);
    }
    
    void removeAttachment() {
        removeItemImpl(selectedAttachment);
    }
    
    boolean removeItemImpl(o) {
        if(caller.mode == 'read') return false;
        if(MsgBox.confirm("You are about to remove this attachment. Continue?")) {
            attachments.remove(o);
            return true;
        }
        return false;
    }
    
    def getHtmlview() {
        return '';
    }
}