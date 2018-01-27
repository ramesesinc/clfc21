package com.rameses.clfc.treasury.dailycollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class DailyCollectionOtherReceiptController {

    def entity;
    
    void init() {
        if (!entity.otherreceipts) entity.otherreceipts = [];
    }
    
    def selectedItem;
    def listHandler = [
        fetchList: { o->
            if (!entity.otherreceipts) entity.otherreceipts = [];
            return entity.otherreceipts;
        }
    ] as BasicListModel;
    
    def getHtmlview() {
        if (!selectedItem) return "";
        def info = selectedItem;
        return """
            <html>
                <body>
                    <h1>Other Receipt Information</h1>
                    <table>
                        <tr>
                            <td> <b>Description</b> </td>
                            <td> <b>:</b> </td>
                            <td> ${info.name} </td>
                        </tr>
                        <tr>
                            <td> <b>Date</b> </td>
                            <td> <b>:</b> </td>
                            <td> ${new java.text.SimpleDateFormat("MMM-dd-yyyy").format(info.txndate)} </td>
                        </tr>
                        <tr>
                            <td> <b>Amount</b> </td>
                            <td> <b>:</b> </td>
                            <td> ${info.amount} </td>
                        </tr>
                    </table>
                </body>
            </html>
        """;
    }
}

