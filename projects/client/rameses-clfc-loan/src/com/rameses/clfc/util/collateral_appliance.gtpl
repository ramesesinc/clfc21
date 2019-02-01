<% if ( appliance._filetype ) { %> 
<font class="bold">General Information</font>
<table>
<tr>
    <td width="100px">Type : </td>
    <td>${ifNull(appliance.type, '-')}</td>
</tr>
<tr>
    <td>Brand : </td>
    <td>${ifNull(appliance.brand, '-')}</td>
</tr>
<tr>
    <td>Date Acquired : </td>
    <td>${ifNull(appliance.dtacquired, '-')}</td>
</tr>
<tr>
    <td>Model No. : </td>
    <td>${ifNull(appliance.modelno, '-')}</td>
</tr>
<tr>
    <td>Serial No. : </td>
    <td>${ifNull(appliance.serialno, '-')}</td>
</tr>
<tr>
    <td>Market Value : </td>
    <td>${ifNull(appliance.marketvalue, '-')}</td>
</tr>
<tr>
    <td>Remarks : </td>
    <td>
        <p>${ifNull(appliance.remarks, '-')}</p>
    </td>
</tr>
</table>
<br/> 
<% } %>

<font class="bold">Credit Investigation Report</font>
<table style="width:100%;"> 
<tr>
    <td>
        ${ifNull(appliance.ci?.evaluation, '-')}
    </td>
</tr>
</table>
<br/> 
