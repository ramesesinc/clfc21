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
            list?.sort{ it.sequence }
            list?.each{ o-> %>                   
            <div class="block">
                <%
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
                %>
            </div>
        <% } %>
    </body>
</html>