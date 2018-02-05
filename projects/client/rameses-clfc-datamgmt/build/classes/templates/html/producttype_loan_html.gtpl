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
            list?.sort{ it.index }
            list?.each{ o->
                if (o.islast != null) o.remove('islast');
                if (o.isfirst != null) o.remove('isfirst');
            }
            
            if (mode != 'read' && list?.size() > 0) {
                list[0].isfirst = true;
                list[list?.size() - 1].islast = true;
            }
            list?.each{ o-> %>                   
            <div class="block">
                <%
                    try {
                        out.print("<b>");
                        if (o?.attribute?.varname != null) {
                            out.print(o.attribute.varname + '&nbsp;:&nbsp;');
                        }
                        out.print(o.attribute.title + '&nbsp;&nbsp;');
                        out.print("</b>");

                        if (mode != 'read') {
                            out.print('<a href="editAttr" objid="' + o.objid + '">[Edit]</a>&nbsp;&nbsp;');
                            /*
                            if (cond?._allowremove == true) {
                                out.print('<a href="removeCondition" objid="' + cond.objid + '">[Remove]</a>&nbsp;&nbsp;');
                            }
                            */
                        }
                        if (mode != 'read') {
                            if (o?.isfirst == null || o?.isfirst == false) { %>
                                <a href="moveUpAttr" objid="${o.objid}">[Up]</a>&nbsp;&nbsp;
                            <% } 
                               if (o?.islast == null || o?.islast == false) { %>
                                <a href="moveDownAttr" objid="${o.objid}">[Down]</a>&nbsp;&nbsp;
                            <% } %>
                            <a href="removeAttr" objid="${o.objid}">[Remove]</a>&nbsp;&nbsp;
                            <%
                        }
                        if (o.computeduringapplication==1) {
                            out.print("<br/>&nbsp;&nbsp;&nbsp;Compute during application is <b>true</b>");
                        }
                        if (o.computeduringposting==1) {
                            out.print("<br/>&nbsp;&nbsp;&nbsp;Compute during posting is <b>true</b>");
                        }
                        if (o.computeuponmaturity==1) {
                            out.print("<br/>&nbsp;&nbsp;&nbsp;Compute upon maturity is <b>true</b>");
                        }
                        out.print( "<br>&nbsp;&nbsp;&nbsp;");
                        def handler = o?.handler;
                        switch (handler) {
                            case "expression":
                                String expr = o?.expr;
                                if(!expr) {
                                    expr = "Not specified";
                                } else {
                                    expr = expr.replace('\n','<br>').replace('\t', '&nbsp;'.multiply(5)).replace('\\s', '&nbsp;' );
                                }
                                out.print( expr );
                                break;
                            default: 
                                String stringvalue = o?.stringvalue;
                                if (!stringvalue) {
                                    stringvalue = "Not specified";
                                }
                                out.print( stringvalue );
                                break;
                        }
                        out.print("<br/><br/>");
                    } catch (e) {
                        out.print(e.message);
                    }

                    /*
                    try {
                        out.print('<b>' + o.sequence + ' ' + o.title + '</b>&nbsp;&nbsp;');
                        if (mode != 'read') { %>
                            <% if (o?.isfirst == false) { %>
                                <a href="moveUpHeader" code="${o.code}">[Move Up]</a>&nbsp;&nbsp;
                            <% } 
                               if (o?.islast == false) { %>
                                <a href="moveDownHeader" code="${o.code}">[Move Down]</a>&nbsp;&nbsp;
                            <% } %>  
                        <% }
                        out.print('<br/><br/>');
                    } catch (e) {
                        out.print(e.message);
                    }
                    */
                %>
            </div>
        <% } %>
    </body>
</html>