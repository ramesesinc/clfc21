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
            list.sort{ it.index }
            list.each{ o->
                o.isfirst = false;
                o.islast = false;
            }
            if (list.size() > 0) {
                list[0].isfirst = true;
                list[list.size() - 1].islast = true;
            }
            list?.each{ o-> %>                   
            <div class="block">
                <%
                    try {
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
                        out.print('<br/>&nbsp;&nbsp;');
                        if (o?.postonlastitem == true) {
                            out.print('Post on last item is <b>true</b>');
                        } else if (o?.postperitem == true) {
                            out.print('Post per item is <b>true</b>');
                        } 

                        if (o?.isdeductabletoamount == true) {
                            out.print('<br/>&nbsp;&nbsp;Deductable to amount is <b>true</b>');
                        }

                        if (o?.isincrementafterposting == true) {
                            out.print('<br/>&nbsp;&nbsp;Increment after posting is <b>true</b>');
                        }

                        if (o?.allowoffset == true) {
                            out.print('<br/>&nbsp;&nbsp;Allow offset is <b>true</b>');
                        }

                        if (o?.isexempted == true) {
                            out.print('<br/>&nbsp;&nbsp;Exempted is <b>true</b>');
                        }

                        if (o?.recalculateifnotenough == true) {
                            out.print('<br/>&nbsp;&nbsp;Recalculate and decrease total days if not enough is <b>true</b>');
                        }

                        o?.constraints?.each{ con->
                            out.print('<br/>&nbsp;&nbsp;');
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
                                    def handler = con?.field?.datatype;
                                    switch (handler) {
                                        case 'decimal'  : out.print(con?.decimalvalue); break;
                                        default         : out.print(con?.stringvalue); break;
                                    }
                                }
                            }
                        }

                        if (o?.postingexpr) {
                            out.print("<br/>&nbsp;&nbsp;Posting Value:");
                            out.print("<br/>&nbsp;&nbsp;&nbsp;&nbsp;<b>$o.postingexpr</b>");
                        }

                        out.print('<br/><br/>');
                    } catch (e) {
                        out.print(e.message);
                    }
                %>
            </div>
        <% } %>
    </body>
</html>