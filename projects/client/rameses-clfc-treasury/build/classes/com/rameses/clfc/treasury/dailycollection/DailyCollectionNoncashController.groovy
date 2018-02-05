package com.rameses.clfc.treasury.dailycollection

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class DailyCollectionNoncashController 
{
    def entity, selectedItem;
    def allowEdit;
    
    void init() {
        if (!entity.noncash) entity.noncash = [];
    }
    
    def listHandler = [
        fetchList: { o->
            if (!entity.noncash) entity.noncash = [];
            return entity.noncash;
        }
    ] as BasicListModel;
    
    def getHtmlview() {
        if (!selectedItem) return "";
        def info = selectedItem;
        return """
            <html>
                <body>
                    <h1>Non-cash Information</h1>
                    <table>
                        <tr>
                            <td> <b>Date</b> </td>
                            <td> <b>:</b> </td>
                            <td> ${new java.text.SimpleDateFormat("MMM-dd-yyyy").format(info.txndate)} </td>
                        </tr>
                        <tr>
                            <td> <b>Collector</b> </td>
                            <td> <b>:</b> </td>
                            <td> ${info.collector.name} </td>
                        </tr>
                        <tr>
                            <td> <b>Route</b> </td>
                            <td> <b>:</b> </td>
                            <td> ${info.route.description} </td>
                        </tr>
                        <tr>
                            <td> <b>Amount:</b> </td>
                            <td> <b>:</b> </td>
                            <td> ${new java.text.DecimalFormat('#,##0.00').format(info.amount)} </td>
                        </tr>
                    </table>
                </body>
            </html>
        """;
    }
}

