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
            conditions?.sort{ it.sequence }
            conditions?.each{ cond-> %>                   
            <div class="block">
                <%
                    try {
                        out.print('<u>' + cond.title + '</u>&nbsp;&nbsp;');
                        if (mode!='read') { %>
                            <a href="editPostingCondition" code="${cond.code}">[Edit]</a>&nbsp;&nbsp;
                            <% if (cond?.isfirst == false) { %>
                                <a href="moveUpCondition" code="${cond.code}">[Move Up]</a>&nbsp;&nbsp;
                            <% } 
                               if (cond?.islast == false) { %>
                                <a href="moveDownCondition" code="${cond.code}">[Move Down]</a>&nbsp;&nbsp;
                            <% } %>
                            <!--
                            <a href="removePostingCondition" code="${cond.code}">[Remove]</a>&nbsp;&nbsp;
                            -->
                        <% }
                        out.print('<br/>&nbsp;&nbsp;');
                        if (cond?.postonlastitem == true) {
                            out.print('Post on last item is <b>true</b>');
                        } else if (cond?.postperitem == true) {
                            out.print('Post per item is <b>true</b>');
                        }

                        if (cond?.isdeductabletoamount == true) {
                            out.print('<br/>&nbsp;&nbsp;Deductable to amount is <b>true</b>');
                        }

                        if (cond?.isincrementafterposting == true) {
                            out.print('<br/>&nbsp;&nbsp;Increment after posting is <b>true</b>');
                        }

                        if (cond?.allowoffset == true) {
                            out.print('<br/>&nbsp;&nbsp;Allow offset is <b>true</b>');
                        }

                        cond?.constraints?.each{ con->
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
                        out.print('<br/><br/>');
                    } catch (e) {
                        out.print(e.message);
                    }
                %>
            </div>
        <% } %>
    </body>
</html>