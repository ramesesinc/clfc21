package test.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class TestAttachmentModel {

    @Binding 
    def binding; 
    
    def entity = [:];
    
    void dumpInfo() {
        println entity; 
    }
    
    boolean editable = true, allowadd = true, allowremove = true;
    
    def attachments = [
        [objid: 'FILEab08fe0:161300a097d:-7e3b', title:'File 3'],
        [objid: 'sample4', title:'Sample 4'],
        [objid: 'FILE-13f30483:1613c8bf895:-7d8d', title: 'For Sale'],
        [objid: 'FILE-3cae8ee5:161471d4d53:-7e23', title: 'From Storage 2']
    ]; 
}
