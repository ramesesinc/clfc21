<font class="bold">General Information</font>                
<table>
<tr>
    <td>Tradename : </td>
    <td>${ifNull(business.tradename, '-')}</td>
</tr>                
<tr>
    <td class="nowrap">Kind of Business : </td>
    <td>${ifNull(business.kind, '-')}</td>
</tr>
<tr>
    <td>Stall Size(in mtrs.) : </td>
    <td>${ifNull(business.stallsize, '-')}</td>
</tr>
<tr>
    <td>Address : </td>
    <td>${ifNull(business.address, '-')}</td>
</tr>
<tr>
    <td>Occupancy : </td>
    <td>${ifNull(business.occupancy?.type, '-')}</td>
</tr>

<% if ( business.occupancy?.type == 'RENTED' ) { %>
<tr>
    <td>&nbsp;&nbsp;&nbsp; Rent Type : </td>
    <td>${ifNull(business.occupancy?.renttype, '-')}</td>
</tr>
<tr>
    <td>&nbsp;&nbsp;&nbsp; Rent Amount : </td>
    <td>${ifNull(business.occupancy?.rentamount, '-')}</td>
</tr>
<% } %>

<tr>
    <td>Business Started : </td>
    <td>${ifNull(business.dtstarted, '-')}</td>
</tr>
<tr>
    <td>Ownership : </td>
    <td>${ifNull(business.ownership, '-')}</td>
</tr>
<tr>
    <td>Capital Invested : </td>
    <td>${ifNull(business.capital, '-')}</td>
</tr>
<tr>
    <td class="nowrap">Esimated Daily Sales : </td>
    <td>${ifNull(business.avgsales, '-')}</td>
</tr>
<tr>
    <td>Business Hours : </td>
    <td>${ifNull(business.officehours, '-')}</td>
</tr> 
</table>
<br/> 

<font class="bold">Credit Investigation Report</font>
<table style="width:100%;"> 
<tr>
    <td>
        ${ifNull(business.ci?.evaluation, '-')}
    </td>
</tr>
</table>
<br/> 
