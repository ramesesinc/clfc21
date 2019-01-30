<% if ( property._filetype ) { %> 
<font class="bold">General Information</font>
<table>
<tr>
    <td width="100px">Classification : </td>
    <td>${ifNull(property.classification, '-')}</td>
</tr>
<tr>
    <td>Location : </td>
    <td>${ifNull(property.location, '-')}</td>
</tr>
<tr>
    <td>Area : </td>
    <td>${ifNull(property.areavalue, '-')} ${ifNull(property.areauom, '-')}</td>
</tr>
<tr>
    <td>Zonal Value : </td>
    <td>${ifNull(property.zonalvalue, '-')}</td>
</tr>
<tr>
    <td>Date Acquired : </td>
    <td>${ifNull(property.dtacquired, '-')}</td>
</tr>
<tr>
    <td>Acquired From : </td>
    <td>${ifNull(property.acquiredfrom, '-')}</td>
</tr>
<tr>
    <td class="nowrap">Mode of Acquisition : </td>
    <td>${ifNull(property.modeofacquisition, '-')}</td>
</tr>
<tr>
    <td>Registered Name : </td>
    <td>${ifNull(property.registeredname, '-')}</td>
</tr>
<tr>
    <td>Market Value : </td>
    <td>${ifNull(property.marketvalue, '-')}</td>
</tr>
<tr>
    <td>Remarks : </td>
    <td> <p>${ifNull(property.remarks, '-')}</p> </td>
</tr>
</table>
<br/>
<% } %> 

<font class="bold">Credit Investigation Report</font>
<table style="width:100%;"> 
<tr>
    <td>
        ${ifNull(property.ci?.evaluation, '-')}
    </td>
</tr>
</table>
<br/> 