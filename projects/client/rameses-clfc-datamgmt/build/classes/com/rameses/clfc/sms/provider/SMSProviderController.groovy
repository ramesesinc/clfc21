package com.rameses.clfc.sms.provider

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class SMSProviderController extends CRUDController 
{
    String serviceName = 'SMSProviderService';
    String entityName = 'smsprovider';
    
    boolean allowApprove = false;
    boolean allowDelete = false;
    
    def listHandler = [
        fetchList: { o->
            if (!entity.prefixes) entity.prefixes = [];
            return entity.prefixes;
        },
        createItem: {
            return [provider: [code: entity.code]];
        },
        onAddItem: { o->
            if (!entity.prefixes) entity.prefixes = [];
            entity.prefixes << o;
        },
        removeItem: { o->
            removeItemImpl(o);
        }
    ] as EditorListModel;
    
    def prevprefixes, selectedPrefix;
    void afterEdit( data ) {
        def item;
        prevprefixes = [];
        if (data.prefixes) {
            data?.prefixes?.each{ o->
                item = [:];
                item.putAll(o);
                prevprefixes << item;
            }
        }
    }
    
    void afterCancel() {
        if (prevprefixes) {
            entity?.prefixes = prevprefixes;
        }
        listHandler?.reload();
    }
    
    void removeItem() {
        removeItemImpl(selectedPrefix);
    }
    
    void removeItemImpl( item ) {
        if (!MsgBox.confirm('You are about to remove this item. Continue?')) return; 
        
        def i = entity?.prefixes?.find{ it.code == item.code }
        println 'prefix ' + i;
        if (i) entity?.prefixes?.remove(i);
        listHandler?.reload();
    }
    
}

