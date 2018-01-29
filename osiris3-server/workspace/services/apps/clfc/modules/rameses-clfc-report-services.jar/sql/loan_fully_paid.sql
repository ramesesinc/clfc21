[getFullyPaidByStartdateAndEnddate]
select l.objid as ledgerid, a.borrower_objid, a.borrower_name, a.loanamount,
	l.dtmatured, ac.dtreleased, b.address as borrower_address, a.appno, l.dtlastpaid as dtfullypaid
from loan_ledger l
inner join loanapp a on l.appid = a.objid
inner join borrower b on a.borrower_objid = b.objid
left join loanapp_capture ac on a.objid = ac.objid
where l.dtlastpaid between $P{startdate} and $P{enddate}
	and l.state = 'close'
order by a.borrower_name