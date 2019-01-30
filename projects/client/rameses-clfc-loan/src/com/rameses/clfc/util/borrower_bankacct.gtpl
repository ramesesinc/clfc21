<font class="bold">General Information</font>
<table>
    <tr>
        <td class="nowrap">Bank Name : </td>
        <td>${ifNull(bankacct.bankname, '-')}</td>
    </tr>
    <tr>
        <td>Remarks : </td>
        <td> <p>${ifNull(bankacct.remarks, '-')}</p> </td>
    </tr>
</table>