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
            conditions?.each{ o->
                if (o.islast != null) o.remove('islast');
                if (o.isfirst != null) o.remove('isfirst');
            }
            conditions?.sort{ it.index }
            
            if (mode != 'read' && conditions?.size() > 0) {
                conditions[0].isfirst = true;
                conditions[conditions?.size() - 1].islast = true;
            }

            conditions?.eachWithIndex{ cond, i-> %>            
            <div class="block">
                <%
                    try {
                        if (cond?.varname != null) {
                            out.print('<b>' + cond.varname + '</b>&nbsp;:&nbsp;');
                        }
                        out.print('<u>' + cond.title + '</u>&nbsp;&nbsp;');
                        if (mode != 'read') {
                            out.print('<a href="editCondition" objid="' + cond.objid + '">[Edit]</a>&nbsp;&nbsp;');
                            if (cond?._allowremove == true) {
                                out.print('<a href="removeCondition" objid="' + cond.objid + '">[Remove]</a>&nbsp;&nbsp;');
                            }
                        }

                        if (mode != 'read') {
                            if (!cond?.isfirst || cond?.isfirst == false) {
                                out.print('<a href="moveUp" objid="' + cond.objid + '">[Move Up]</a>&nbsp;');
                            }

                            if (!cond?.islast || cond?.islast == false) {
                                out.print('<a href="moveDown" objid="' + cond.objid + '">[Move Down]</a>&nbsp;');
                            }
                        }
                        
                        out.print( "<br>&nbsp;&nbsp;&nbsp;");
                        def handler = cond?.handler;
                        if(!handler) handler = cond?.datatype;
                        switch (handler) {
                            case "expression":
                                String expr = cond?.expr;
                                if(!expr) {
                                    expr = "Not specified";
                                } else {
                                    expr = expr.replace('\n','<br>').replace('\t', '&nbsp;'.multiply(5)).replace('\\s', '&nbsp;' );
                                }
                                out.print( expr );
                                break;
                            default: 
                                String stringvalue = cond?.stringvalue;
                                if (!stringvalue) {
                                    stringvalue = "Not specified";
                                }
                                out.print( stringvalue );
                                break;
                        }

                        if (cond?.computationterm) {
                            out.print( "<br>&nbsp;&nbsp;&nbsp;Computation term:: " + cond?.computationterm);
                        }

                        if (cond?.term) {
                            out.print( "<br>&nbsp;&nbsp;&nbsp;Posting term: " + cond?.term);
                        }
                    } catch(e) {
                        out.print( e.message );
                    }
                %>
            </div>
        <% } %>
    </body>
</html>

