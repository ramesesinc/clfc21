[getList]
select n.*, a.objid as loanapp_objid, a.appno as loanapp_appno
from (
	select objid
	from loan_ledger_proceeds
	where borrower_name like $P{searchtext}
	union
	select objid
	from loan_ledger_proceeds 
	where refno like $P{searchtext}
	union
	select n.objid
	from loan_ledger_proceeds n
	inner join loan_ledger l on n.parentid=l.objid
	inner join loanapp a on l.appid=a.objid
	where a.appno like $P{searchtext}
) q inner join loan_ledger_proceeds n on q.objid=n.objid
inner join loan_ledger l on n.parentid=l.objid
inner join loanapp a on l.appid=a.objid
order by n.dtcreated desc

[xgetList]
SELECT a.*, b.acctid AS borrower_objid, b.acctname AS borrower_name
FROM loan_ledger_proceeds a
	INNER JOIN loan_ledger b ON a.parentid = b.objid
WHERE a.borrower_name LIKE $P{searchtext}
ORDER BY a.dtcreated DESC

[getListByState]
select n.*, a.objid as loanapp_objid, a.appno as loanapp_appno
from (
	select objid
	from loan_ledger_proceeds
	where borrower_name like $P{searchtext}
		and txnstate = $P{state}
	union
	select objid
	from loan_ledger_proceeds 
	where refno like $P{searchtext}
		and txnstate = $P{state}
	union
	select n.objid
	from loan_ledger_proceeds n
	inner join loan_ledger l on n.parentid=l.objid
	inner join loanapp a on l.appid=a.objid
	where a.appno like $P{searchtext}
		and n.txnstate = $P{state}
) q inner join loan_ledger_proceeds n on q.objid=n.objid
inner join loan_ledger l on n.parentid=l.objid
inner join loanapp a on l.appid=a.objid
order by n.dtcreated desc

[xgetListByState]
SELECT a.*, b.acctid AS borrower_objid, b.acctname AS borrower_name
FROM loan_ledger_proceeds a
	INNER JOIN loan_ledger b ON a.parentid = b.objid
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnstate = $P{state}
ORDER BY a.dtcreated DESC

[getListByLedgerid]
SELECT a.* FROM loan_ledger_proceeds a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.parentid = $P{ledgerid}
ORDER BY a.dtcreated DESC

[getListByLedgeridAndState]
SELECT a.* FROM loan_ledger_proceeds a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.parentid = $P{ledgerid}
	AND a.txnstate = $P{state}
ORDER BY a.dtcreated DESC

[findByRefid]
SELECT a.* FROM loan_ledger_proceeds a
WHERE a.refid = $P{refid}

[findCollectionProceedByRefid]
SELECT p.* FROM collection_proceed p
WHERE p.refid = $P{refid}

[findByIdWithInfo]
SELECT p.*, l.acctid AS borrower_objid, l.acctname AS borrower_name,
	a.objid AS loanapp_objid, a.appno AS loanapp_appno, r.code AS route_code,
	r.description AS route_description, r.area AS route_area, l.paymentmethod
FROM loan_ledger_proceeds p
INNER JOIN loan_ledger l ON p.parentid = l.objid
INNER JOIN loanapp a ON l.appid = a.objid
INNER JOIN loan_route r ON a.route_code = r.code
WHERE p.objid = $P{objid}

[changeState]
UPDATE loan_ledger_proceeds SET txnstate = $P{txnstate}
WHERE objid = $P{objid}