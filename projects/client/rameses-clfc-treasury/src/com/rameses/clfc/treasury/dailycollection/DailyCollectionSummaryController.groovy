package com.rameses.clfc.treasury.dailycollection;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class DailyCollectionSummaryController
{
    def entity;
    def df = new java.text.SimpleDateFormat("MMM-dd-yyyy");
    def decformat = new java.text.DecimalFormat("#,##0.00");
    
    void init() {
    }

    def getHtmlview() {
        if (!entity) return "";
        def html = """
            <html>
                <body>
                    <h1>Summary</h1>
                    <table cellspacing='0'>
                        <tr>
                            <td>Collection Date</td>
                            <td>:</td>
                            <td>${df.format(entity.txndate)}</td>
                        </tr>
                        <tr>
                            <td>Status</td>
                            <td>:</td>
                            <td><b>${entity.state}</b></td>
                        </tr>
                        <tr>
                            <td>Total Collection</td>
                            <td>:</td>
                            <td>${decformat.format(entity.totalcollection)}</td>
                        </tr>
                    </table> <br/> <br/>
        """;
        
        if (entity.cbs) {
            html += """
                <h3>Cash Breakdown Sheets</h3>
                <table border='1'>
                    <thead>
                        <td><b>CBS No.   </b></td>
                        <td><b>Date      </b></td>
                        <td><b>Collector </b></td>
                        <td><b>Amount    </b></td>
                        <!--<td><b>Is Encashed</b></td>-->
                    </thead>
                    <tbody>
            """;
            
            try {
                entity?.cbs?.each{ o->
                    html += """
                        <tr>
                        <td valign='top'>${o.info.cbsno}</td>
                        <td valign='top'>${df.format(o.info.txndate)}</td>
                        <td valign='top'>${o.info.collector.name}</td>
                        <td valign='top' align='right'>${decformat.format(o.info.amount)}</td>
                        <!--<td valign='top'>${o.isencashed == 1? true : false}</td>-->
                        </tr>
                    """;
                }
                def s = entity?.cbs?.size();
                if (s > 0) {
                    def amt = entity?.cbs?.info?.amount?.sum();
                    if (!amt) amt = 0;
                    html += """
                        <tr>
                        <td colspan='3' valign='top' align='right'> Total : </td>
                        <td valign='top' align='right'> ${decformat.format(amt)} </td>
                        </tr>
                    """;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            html += """
                    </tbody>
                </table> <br/> <br/>
            """;
        }
        
        if (entity.noncash) {
            html += """
                <h3>Non-cashes</h3>
                <table border='1' width='500px'>
                    <thead>
                        <td><b> Date      </b></td>
                        <td><b> Collector </b></td>
                        <td><b> Route     </b></td>
                        <td><b> Amount    </b></td>
                    </thead>
                    <tbody>
            """;
            
            try {
                entity?.noncash?.each{ o->
                    html += """
                        <tr>
                        <td valign='top'>${df.format(o.txndate)}</td>
                        <td valign='top'>${o.collector.name}</td>
                        <td valign='top'>${o.route.description}</td>
                        <td valign='top' align='right'>${decformat.format(o.amount)}</td>
                        </tr>
                    """;
                }
                def s = entity?.noncash?.size();
                if (s > 0) {
                    def amt = entity?.noncash?.amount?.sum();
                    if (!amt) amt = 0;
                    html += """
                    <tr>
                    <td colspan='3' valign='top' align='right'> Total : </td>
                    <td valign='top' align='right'> ${decformat.format(amt)} </td>
                    </tr>
                    """;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            html += """
                    </tbody>
                 </table> <br/> <br/>
            """;
        }
        
        if (entity.shortages) {
            html += """
                <h3>Shortages</h3>
                <table border='1' width='500px'>
                    <thead>
                        <td><b> Date      </b></td>
                        <td><b> Ref. No.  </b></td>
                        <td><b> CBS No.   </b></td>
                        <td><b> Collector </b></td>
                        <td><b> Amount    </b></td>
                        <td width='100'><b> Remarks   </b></td>
                    </thead>
                    <tbody>
            """;
            
            try {
                entity?.shortages?.each{ o->
                    html += """
                        <tr>
                        <td valign='top'>${df.format(o.txndate)}</td>
                        <td valign='top'>${o.refno}</td>
                        <td valign='top'>${o.cbsno}</td>
                        <td valign='top'>${o.collector.name}</td>
                        <td valign='top' align='right'>${decformat.format(o.amount)}</td>
                        <td valign='top'>${o.remarks}</td>
                        </tr>
                    """;
                }
                
                def s = entity?.shortages?.size();
                if (s > 0) {
                    def amt = entity?.shortages?.amount?.sum();
                    if (!amt) amt = 0;
                    
                    html += """
                    <tr>
                    <td colspan='5' valign='top' right='right'> Total : </td>
                    <td valign='top' right='right'> ${decformat.format(amt)} </td>
                    </tr>
                    """;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            html += """
                    </tbody>
                 </table> <br/> <br/>
            """;
        }
        
        if (entity.overages) {
            html += """
                <h3>Overages</h3>
                <table border='1'>
                    <thead>
                        <td><b> Date      </b></td>
                        <td><b> Ref. No.  </b></td>
                        <td><b> Collector </b></td>
                        <td><b> Amount    </b></td>
                        <td width='100'><b> Remarks   </b></td>
                    </thead>
                    <tbody>
            """;
            
            try {
                entity?.overages?.each{ o->
                    html += """
                        <tr>
                        <td valign='top'>${df.format(o.txndate)}</td>
                        <td valign='top'>${o.refno}</td>
                        <td valign='top'>${o.collector.name}</td>
                        <td valign='top' align='right'>${decformat.format(o.amount)}</td>
                        <td valign='top'>${o.remarks}</td>
                        </tr>
                    """;
                }
                
                def s = entity?.overages?.size();
                if (s > 0) {
                    def amt = entity?.overages?.amount?.sum();
                    if (!amt) amt = 0;
                    
                    html += """"
                    <tr>
                    <td colspan='4' valign='top' align='right'> Total : </td>
                    <td valign='top' align='right'> ${decformat.format(amt)} </td>
                    </tr>
                    """;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            html += """
                    </tbody>
                 </table> <br/> <br/>
            """;
        }
        
        if (entity.otherreceipts) {
            html += """
                <h3>Encashments</h3>
                <table border='1'>
                    <thead>
                        <td><b> Description  </b></td>
                        <td><b> Date         </b></td>
                        <td><b> Amount       </b></td>
                    </thead>
                    <tbody>
            """;
            
            try {
                entity?.otherreceipts?.each{ o->
                    html += """
                        <tr>
                        <td valign='top'>${o.name}</td>
                        <td valign='top'>${df.format(o.txndate)}</td>
                        <td valign='top' align='right'>${decformat.format(o.amount)}</td>
                        </tr>
                    """;
                }
                
                def s = entity?.otherreceipts?.size();
                if (s > 0) {
                    def amt = entity?.otherreceipts?.amount?.sum();
                    if (!amt) amt = 0;
                    
                    html += """
                    <tr>
                    <td colspan='2' valign='top' align='right'> Total : </td>
                    <td valign='top' align='right'> ${decformat.format(amt)} </td>
                    </tr>
                    """;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            html += """
                    </tbody>
                 </table> <br/> <br/>
            """;
        }
        
        if (entity.encashments) {            
            html += """
                <h3>Encashments</h3>
                <table border='1'>
                    <thead>
                        <td><b> Date         </b></td>
                        <td><b> Amount       </b></td>
                        <td><b> Check No.    </b></td>
                        <td><b> Check Date   </b></td>
                        <td><b> Bank         </b></td>
                        <!--<td><b> Passbook No. </b></td>-->
                        <td><b> Overage      </b></td>
                    </thead>
                    <tbody>
            """;
            
            try {
                entity?.encashments?.each{ o->
                    html += """
                        <tr>
                        <td valign='top'>${df.format(o.info.txndate)}</td>
                        <td valign='top' align='right'>${decformat.format(o.info.amount)}</td>
                        <td valign='top'>${o.info.check.checkno}</td>
                        <td valign='top'>${df.format(o.info.check.txndate)}</td>
                        <td valign='top'>${o.info.check.bank.objid}</td>
                        <!--<td valign='top'>${o.info.check.passbook.passbookno}</td>-->
                        <td valign='top' align='right'>${decformat.format(o.info.overage)}</td>
                        </tr>
                    """;
                }
                
                def s = entity?.encashments?.size();
                if (s > 0) {
                    def amt = entity?.encashments?.info?.amount?.sum();
                    if (!amt) amt = 0;
                    
                    html += """
                    <tr>
                    <td colspan='5' valign='top' align='right'> Total : </td>
                    <td valign='top' align='right'> ${decformat.format(amt)} </td>
                    </tr>
                    """;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            html += """
                    </tbody>
                 </table> <br/> <br/>
            """;
        }
        
        if (entity.depositslips) {    
            html += """
                <h3>Deposit Slips</h3>
                <table border='1'>
                    <thead>
                        <td><b> Date          </b></td>
                        <td><b> Control No.   </b></td>
                        <td><b> Amount        </b></td>
                        <td><b> Passbook No.  </b></td>
                        <td><b> Acct. No.     </b></td>
                        <td><b> Acct. Name    </b></td>
                        <td><b> Currency Type </b></td>
                        <td><b> Account Type  </b></td>
                        <td><b> Deposit Type  </b></td>
                    </thead>
                    <tbody>
            """;
            
            try {
                entity?.depositslips?.each{ o->
                    html += """
                        <tr>
                        <td valign='top'>${df.format(o.info.txndate)}</td>
                        <td valign='top'>${o.info.controlno}</td>
                        <td valign='top' align='right'>${decformat.format(o.info.amount)}</td>
                        <td valign='top'>${o.info.passbook.passbookno}</td>
                        <td valign='top'>${o.info.passbook.acctno}</td>
                        <td valign='top'>${o.info.passbook.acctname}</td>
                        <td valign='top'>${o.info.currencytype?.name? o.info.currencytype.name : ''}</td>
                        <td valign='top'>${o.info.accounttype?.name? o.info.accounttype.name : ''}</td>
                        <td valign='top'>${o.info.deposittype?.name? o.info.deposittype.name : ''}</td>
                        </tr>
                    """;                
                }
                
                def s = entity?.depositslips?.size();
                if (s > 0) {
                    def amt = entity?.depositslips?.info?.amount?.sum();
                    if (!amt) amt = 0;
                    
                    html += """
                    <tr>
                    <td colspan='8' valign='top' align='right'> Total : </td>
                    <td valign='top' align='right'> ${decformat.format(amt)}</td>
                    </tr>
                    """;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            html += """
                    </tbody>
                 </table> <br/> <br/>
            """;
        }
        
        html += """
                </body>
            </html>
        """;
        return html;
    }
}