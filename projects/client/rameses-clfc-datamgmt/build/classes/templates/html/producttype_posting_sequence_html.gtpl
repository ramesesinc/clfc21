<%
import com.rameses.util.*;
%>
<html>
    <head>
        <style>
            .block {
                padding-left: 10px;
            }
        </style>
    </head>
    <body>
        <%
        group?.each{ g-> %>
            <div class="block">
                <%
                try {
                    def xlist = list?.findAll{ it.group==g }
                    if (xlist) {
                        out.print('<b>[' + g + ']</b><br/>');
                        xlist.sort{ it.index }
                        xlist.each{ o-> 
                            o.isfirst = false;
                            o.islast = false;
                        }
                        if (xlist.size() > 0) {
                            xlist[0].isfirst = true;
                            xlist[xlist.size() - 1].islast = true;
                        }
                        xlist.each{ o->
                            out.print('&nbsp;&nbsp;');
                            if (o.varname) {
                                out.print('<b>' + o.varname + '&nbsp;:&nbsp;</b>');
                            }
                            out.print('<u>' + o.title + '</u>&nbsp;&nbsp;');
                            if (mode!='read') { %>
                                <a href="editCondition" objid="${o.objid}">[Edit]</a>&nbsp;&nbsp;
                                <%  if (o?.isfirst == false) { %>
                                        <a href="moveUpCondition" objid="${o.objid}">[Move Up]</a>&nbsp;&nbsp;
                                <%  } 
                                    if (o?.islast == false) { %>
                                        <a href="moveDownCondition" objid="${o.objid}">[Move Down]</a>&nbsp;&nbsp;
                                <%  } 
                                    if (!o?.isdefault) { %>
                                        <a href="removeCondition" objid="${o.objid}">[Remove]</a>&nbsp;&nbsp;
                                <%  }
                            }

                            if (o?.header) {
                                out.print('<br/>&nbsp;&nbsp;&nbsp;&nbsp;Post to Header: <b>' + o.header.title + '</b>');
                            }

                            switch (o?.ruleset) {
                                case 'postperitem': out.print('<br/>&nbsp;&nbsp;&nbsp;&nbsp;Post per item is <b>true</b>'); break;
                                case 'postonlastitem': out.print('<br/>&nbsp;&nbsp;&nbsp;&nbsp;Post on last item is <b>true</b>'); break;
                            }

                            if (o?.isdeductabletoamount == true) {
                                out.print('<br/>&nbsp;&nbsp;&nbsp;&nbsp;Deductable to amount is <b>true</b>');
                            }

                            if (o?.isincrementafterposting == true) {
                                out.print('<br/>&nbsp;&nbsp;&nbsp;&nbsp;Increment after posting is <b>true</b>');
                            }

                            if (o?.allowoffset == true) {
                                out.print('<br/>&nbsp;&nbsp;&nbsp;&nbsp;Allow offset is <b>true</b>');
                            }

                            if (o?.isexempted == true) {
                                out.print('<br/>&nbsp;&nbsp;&nbsp;&nbsp;Exempted is <b>true</b>');
                            }

                            if (o?.recalculateifnotenough == true) {
                                out.print('<br/>&nbsp;&nbsp;&nbsp;&nbsp;Recalculate and decrease total days if not enough is <b>true</b>');
                            }

                            if (o?.applylacking == true) {
                                out.print('<br/>&nbsp;&nbsp;&nbsp;&nbsp;Apply lacking if has lacking is <b>true</b>');
                            }

                            if (o?.allowrepeat == true) {
                                out.print('<br/>&nbsp;&nbsp;&nbsp;&nbsp;Allow repeat posting is <b>true</b>');
                            }
                            
                            if (o?.constraints) {
                                out.print("<br/>&nbsp;&nbsp;<i><b>Posting Constraint(s)</b></i>:");
                                o?.constraints?.each{ con->
                                    out.print('<br/>&nbsp;&nbsp;&nbsp;&nbsp;');
                                    if (con.varname) {
                                        out.print('<b>' + con.varname + '</b>: ');
                                    }
                                    out.print(con?.field?.title);
                                    if (!con?.operator?.symbol) {

                                    } else {
                                        out.print('&nbsp;' + con?.operator?.caption + '&nbsp;');
                                        if (con.usevar == 1) {
                                            out.print('<b>' + con?.var?.name + '</b>');
                                        } else {
                                            def handler = con?.field?.handler;
                                            switch (handler) {
                                                case 'decimal'  : out.print(con?.decimalvalue); break;
                                                default         : out.print(con?.stringvalue); break;
                                            }
                                        }
                                    }
                                }
                            }

                            if (o?.postingexpr) {
                                out.print("<br/>&nbsp;&nbsp;<i><b>Posting Value</b></i>:");
                                out.print("<br/>&nbsp;&nbsp;&nbsp;&nbsp;<b>$o.postingexpr</b>");
                            }

                            out.print('<br/><br/>');
                        }
                    }
                } catch (e) {
                    out.print( e.message );
                }
                %>
            </div>
        <% }
        %>
    </body>
</html>