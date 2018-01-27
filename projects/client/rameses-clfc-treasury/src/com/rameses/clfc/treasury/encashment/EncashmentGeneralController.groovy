package com.rameses.clfc.treasury.encashment;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class EncashmentGeneralController
{
    @Binding
    def binding;

    @ChangeLog
    def changeLog;
    
    @PropertyChangeListener
    def listener = [
        'entity.overage': { o->
            if (o == null) {
                entity.overage = 0;
            }
            
            binding?.refresh('entity.overage');
        }
    ];

    String title = "General Information";

    def entity, mode = 'read', bank, passbook;

    def bankLookup = Inv.lookupOpener('bank:lookup', [
        onselect: { o->
            entity?.check?.bank = o;
            binding.refresh('bank');
        },
        state   : 'ACTIVE'
    ]);

    def passbookLookup = Inv.lookupOpener('passbook:lookup', [
        onselect: { o->
            entity?.check?.passbook = o;
            binding.refresh('passbook');
        },
        state   : 'ACTIVE'
    ]);

    void refresh() {
        binding?.refresh();
    }

}