<% if ( vehicle._filetype ) { %> 
<font class="bold">General Information</font>
<table>
<tr>
    <td class="nowrap" width="120px">
        Kind of Vehicle : 
    </td>
    <td>${ifNull(vehicle.kind, '-')}</td>
</tr>
<tr>
    <td>Make : </td>
    <td>${ifNull(vehicle.make, '-')}</td>
</tr>
<tr>
    <td>Model : </td>
    <td>${ifNull(vehicle.model, '-')}</td>
</tr>
<tr>
    <td>Body Type : </td>
    <td>${ifNull(vehicle.bodytype, '-')}</td>
</tr>
<tr>
    <td>Use : </td>
    <td>${ifNull(vehicle.usetype, '-')}</td>
</tr>
<tr>
    <td>Date Acquired : </td>
    <td>${ifNull(vehicle.dtacquired, '-')}</td>
</tr>
<tr>
    <td>Acquired From : </td>
    <td>${ifNull(vehicle.acquiredfrom, '-')}</td>
</tr>
<tr>
    <td>Registered Name : </td>
    <td>${ifNull(vehicle.registeredname, '-')}</td>
</tr>
<tr>
    <td>Chassis No. : </td>
    <td>${ifNull(vehicle.chassisno, '-')}</td>
</tr>
<tr>
    <td>Plate No. : </td>
    <td>${ifNull(vehicle.plateno, '-')}</td>
</tr>
<tr>
    <td>Engine No. : </td>
    <td>${ifNull(vehicle.engineno, '-')}</td>
</tr>
<tr>
    <td>Market Value : </td>
    <td>${ifNull(vehicle.marketvalue, '-')}</td>
</tr>
<tr>
    <td>Remarks : </td>
    <td> <p>${ifNull(vehicle.remarks, '-')}</p> </td>
</tr>
</table>
<br>
<% } %>

<font class="bold">Credit Investigation Report</font>
<table style="width:100%;"> 
<tr>
    <td>
        ${ifNull(vehicle.ci?.evaluation, '-')}
    </td>
</tr>
</table>
<br/> 
