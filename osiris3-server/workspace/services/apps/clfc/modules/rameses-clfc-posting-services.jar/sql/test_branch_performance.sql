[getPaymentDates]
select distinct txndate as date
from loan_ledger_payment 
where txndate between $P{startdate} and $P{enddate}
order by txndate

[getLedgersWithPaymentsByDate]
select l.objid, l.acctname as borrower, l.appid, q.amount, q.refno, q.date, a.appno
from (
	select l.objid, p.amount, p.refno, p.txndate as date
	from loan_ledger l
	inner join loan_ledger_payment p on l.objid = p.parentid 
	where p.txndate = $P{date}
	group by l.objid
) q 
	inner join loan_ledger l on q.objid = l.objid
	inner join loanapp a on l.appid = a.objid
order by l.acctname

[getLedgersWithPaymentsByStartdateAndEnddate]
select l.objid, l.acctname as borrower, l.appid
from (
	select l.objid
	from loan_ledger l
	inner join loan_ledger_payment p on l.objid = p.parentid 
	where p.txndate between $P{startdate} and $P{enddate}
	group by l.objid
) q inner join loan_ledger l on q.objid = l.objid
order by l.acctname

[getPaymentsByStartdateAndEnddate]
select p.*
from loan_ledger_payment p
where p.parentid = $P{objid}
	and p.txndate between $P{startdate} and $P{enddate}
order by p.txndate

[getLedgerDetailByDate]
SELECT d.* 
FROM loan_ledger_detail d
INNER JOIN ledger_detail_state_type s ON d.state = s.name
WHERE d.parentid = $P{objid}
	AND d.amnestyid IS NULL
	AND d.dtpaid = $P{date}
ORDER BY d.day, d.dtpaid, d.refno, s.level, d.txndate

[getLedgerDetailByDate]
SELECT d.* 
FROM loan_ledger_detail d
INNER JOIN ledger_detail_state_type s ON d.state = s.name
WHERE d.parentid = $P{objid}
	AND d.amnestyid IS NULL
	AND d.dtpaid = $P{date}
ORDER BY d.day, d.dtpaid, d.refno, s.level, d.txndate

[getLedgerDetailByStartdateAndEnddate]
SELECT d.* 
FROM loan_ledger_detail d
INNER JOIN ledger_detail_state_type s ON d.state = s.name
WHERE d.parentid = $P{objid}
	AND d.amnestyid IS NULL
	AND d.dtpaid between $P{startdate} and $P{enddate}
ORDER BY d.day, d.dtpaid, d.refno, s.level, d.txndate

[getAmnestyDetailByStartdateAndEnddate]
SELECT d.* 
FROM ledgeramnesty_postingdetail d
INNER JOIN ledger_detail_state_type s ON d.state = s.name 
WHERE d.parentid = $P{objid}
	AND d.dtpaid between $P{startdate} and $P{enddate}
ORDER BY d.day, d.dtpaid, d.refno, s.level, d.txndate

[getRevenueByDateAndTag]
select * 
from loan_ledger_revenue 
where parentid = $P{objid}
	and txndate = $P{date}
	and tag = $P{tag}

[getRevenueByStartdateAndEnddateAndTag]
select * 
from loan_ledger_revenue 
where parentid = $P{objid}
	and txndate between $P{startdate} and $P{enddate}
	and tag = $P{tag}

[findActiveFixAmnestyByLedgeridAndDate]
select ac.*
from ledgeramnesty_active ac
where ac.ledgerid = $P{ledgerid}
	and $P{date} between ac.dtstarted and ifnull(ac.dtended, curdate())
	and ac.type = "fix"