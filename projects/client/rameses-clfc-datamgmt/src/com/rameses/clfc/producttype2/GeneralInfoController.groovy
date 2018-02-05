package com.rameses.clfc.producttype2

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.rmi.server.UID;

class GeneralInfoController {

    def entity, mode = 'read';
    
    def paymentScheduleLookup = Inv.lookupOpener("paymentschedule:lookup", [
         onselect: { o->
             entity.paymentschedule = o.name;
         }
    ]);

    
    void init() {
        if (!entity) entity = [:];
        if (!entity.generalinfo) entity.generalinfo = [title: "GENERALINFO"]
        //if (!entity.attributes) entity.attributes = [];
        listHandler?.reload();
    }
    
    def selectedItem;
    def listHandler = [
        fetchList: { o->
            if (!entity.generalinfo.attributes) entity.generalinfo.attributes = [];
            entity.generalinfo.attributes.sort{ it.index }
            return entity.generalinfo.attributes;
        }
    ] as BasicListModel;
    
    def buildVarList() {
        def idx = entity.generalinfo?.attributes?.size();
        if (!idx) idx = 0;
        return buildVarList(idx);
    }
    
    def buildVarList( index ) {
        def list = [];
        def x = entity?.generalinfo?.attributes?.findAll{ it.index < index }
        x.each{ o->
            def i = [
                caption     : o.attribute?.varname,
                title       : o.attribute?.varname,
                signature   : o.attribute?.varname,
                handler     : o.handler
            ];
            if (i.handler == "expression") {
                i.description = "(decimal)";
            } else if (i.handler) {
                i.description = "(" + i.handler + ")";
            }
            list << i;
        }
        
        return list;
    }
    
    def addAttribute() {
        def handler = { o->
            
            def i = entity.generalinfo.attributes?.find{ it.attributeid==o.attributeid } 
            if (i) throw new RuntimeException("Attribute has already been selected.");
            
            if (!o.index) o.index = entity.generalinfo.attributes.size();
            
            if (!entity.generalinfo.addedattr) entity.generalinfo._addedattr = [];
            entity.generalinfo._addedattr << o;
            
            if (!entity.generalinfo.attributes) entity.generalinfo.attributes = [];
            entity.generalinfo.attributes << o;
            
            listHandler?.reload();
        }
        def params = [
            entity  : [:],
            handler: handler,
            mode    : mode,
            varlist : buildVarList()
        ];
        def op = Inv.lookupOpener("loan:producttype:general:attribute:popup:create", params);
        if (!op) return null;
        return op;
    }
    
    void removeAttribute() {
        if (!selectedItem) return;
        
        if (!entity.generalinfo._removedattr) entity.generalinfo._removedattr = [];
        entity.generalinfo._removedattr << selectedItem;
        
        if (entity.generalinfo._addedattr) entity.generalinfo._addedattr.remove(selectedItem);
        
        if (entity.generalinfo.attributes) entity.generalinfo.attributes.remove(selectedItem);
        
        listHandler?.reload();
    }
    
    def openAttribute() {
        if (!selectedItem) return;
        
        def params = [
            entity  : selectedItem,
            mode    : mode
        ];
        
        if (mode!='read') {
            params.handler  = { o->
                def i = entity.generalinfo.attributes?.find{ it.attributeid==o.attributeid && it.objid!=o.objid }
                if (i) throw new RuntimeException("Attribute has alread been selected.");

                def data = entity.generalinfo.attributes.find{ it.objid==o.objid }
                if (data) {
                    data._updated=true;
                    data.putAll(o);
                }

                data = entity.generalinfo._addedattr?.find{ it.objid==o.objid }
                if (data) {
                    data.putAll(o);
                }
                listHandler?.reload();
            }
            params.varlist = buildVarList(selectedItem.index);
        }
        def op = Inv.lookupOpener("loan:producttype:general:attribute:popup:open", params);
        if (!op) return null;
        return op;
    }
}

