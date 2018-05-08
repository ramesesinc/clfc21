[getList]
select n.*, a.objid as loanapp_objid, a.appno as loanapp_appno
from (
	select objid
	from loan_ledger_noncash
	where borrower_name like $P{searchtext}
	union
	select objid
	from loan_ledger_noncash 
	where refno like $P{searchtext}
	union
	select n.objid
	from loan_ledger_noncash n
	inner join loan_ledger l on n.parentid=l.objid
	inner join loanapp a on l.appid=a.objid
	where a.appno like $P{searchtext}
) q inner join loan_ledger_noncash n on q.objid=n.objid
inner join loan_ledger l on n.parentid=l.objid
inner join loanapp a on l.appid=a.objid
order by n.dtcreated desc

[getListByState]
select n.*, a.objid as loanapp_objid, a.appno as loanapp_appno
from (
	select objid
	from loan_ledger_noncash
	where borrower_name like $P{searchtext}
		and txnstate = $P{state}
	union
	select objid
	from loan_ledger_noncash 
	where refno like $P{searchtext}
		and txnstate = $P{state}
	union
	select n.objid
	from loan_ledger_noncash n
	inner join loan_ledger l on n.parentid=l.objid
	inner join loanapp a on l.appid=a.objid
	where a.appno like $P{searchtext}
		and n.txnstate = $P{state}
) q inner join loan_ledger_noncash n on q.objid=n.objid
inner join loan_ledger l on n.parentid=l.objid
inner join loanapp a on l.appid=a.objid
order by n.dtcreated desc

[xxxgetList]
SELECT n.*
FROM (
	SELECT n.objid
	FROM loan_ledger_noncash n
	WHERE n.borrower_name LIKE $P{searchtext}
	UNION
	SELECT n.objid
	FROM loan_ledger_noncash n
	WHERE n.refno LIKE $P{searchtext}
) q INNER JOIN loan_ledger_noncash n ON q.objid = n.objid
ORDER BY n.dtcreated DESC

[xxgetList]
SELECT n.*
FROM (
	SELECT n.objid
	FROM loan_ledger_noncash n
	WHERE n.borrower_name LIKE $P{searchtext}
		AND n.tag IS NULL
	UNION
	SELECT n.objid
	FROM loan_ledger_noncash n
	WHERE n.refno LIKE $P{searchtext}
		AND n.tag IS NULL
) a INNER JOIN loan_ledger_noncash n ON a.objid = n.objid
ORDER BY n.dtcreated DESC

[xgetList]
SELECT n.*
FROM (
	SELECT n.objid
	FROM loan_ledger_noncash n
	WHERE n.borrower_name LIKE $P{searchtext}
	UNION
	SELECT n.objid
	FROM loan_ledger_noncash n
	WHERE n.refno LIKE $P{searchtext}
) a INNER JOIN loan_ledger_noncash n ON a.objid = n.objid
ORDER BY n.dtcreated DESC

[xxgetListByState]
SELECT n.*
FROM (
	SELECT n.objid
	FROM loan_ledger_noncash n
	WHERE n.borrower_name LIKE $P{searchtext}
		AND n.txnstate = $P{state}
	UNION
	SELECT n.objid
	FROM loan_ledger_noncash n
	WHERE n.refno LIKE $P{searchtext}
		AND n.txnstate = $P{state}
) q INNER JOIN loan_ledger_noncash n ON q.objid = n.objid
ORDER BY n.dtcreated DESC

[xgetListByState]
SELECT n.*
FROM (
	SELECT n.objid
	FROM loan_ledger_noncash n
	WHERE n.borrower_name LIKE $P{searchtext}
		AND n.txnstate = $P{state}
		AND n.tag IS NULL
	UNION
	SELECT n.objid
	FROM loan_ledger_noncash n
	WHERE n.refno LIKE $P{searchtext}
		AND n.txnstate = $P{state}
		AND n.tag IS NULL
) a INNER JOIN loan_ledger_noncash n ON a.objid = n.objid
ORDER BY n.dtcreated DESC

[findByRefid]
SELECT a.* FROM loan_ledger_noncash a
WHERE a.refid = $P{refid}

[findCollectionNoncashByRefid]
SELECT n.* FROM collection_noncash n
WHERE n.refid = $P{refid}

[findByIdWithInfo]
SELECT p.*, l.acctid AS borrower_objid, l.acctname AS borrower_name,
	a.objid AS loanapp_objid, a.appno AS loanapp_appno, r.code AS route_code,
	r.description AS route_description, r.area AS route_area, l.paymentmethod
FROM loan_ledger_noncash p
INNER JOIN loan_ledger l ON p.parentid = l.objid
INNER JOIN loanapp a ON l.appid = a.objid
INNER JOIN loan_route r ON a.route_code = r.code
WHERE p.objid = $P{objid}

[changeState]
UPDATE loan_ledger_noncash SET txnstate = $P{txnstate}
WHERE objid = $P{objid}