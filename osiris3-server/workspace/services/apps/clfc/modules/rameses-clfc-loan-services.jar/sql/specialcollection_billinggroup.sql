[getList]
SELECT s.* FROM specialcollection_billinggroup s
WHERE s.name LIKE $P{searchtext}
ORDER BY s.dtcreated DESC

[getListByState]
SELECT s.*
FROM specialcollection_billinggroup s
WHERE s.name LIKE $P{searchtext}
	AND s.txnstate = $P{state}
ORDER BY s.dtcreated DESC

[xgetListByState]
SELECT s.* FROM specialcollection_billinggroup s
WHERE s.name LIKE $P{searchtext}
	AND s.txnstate = $P{txnstate}
ORDER BY s.dtcreated DESC

[getLookupList]
SELECT s.* FROM specialcollection_billinggroup s
${filter}
ORDER BY s.dtcreated DESC

[getDetails]
SELECT d.*
FROM specialcollection_billinggroup_detail d
WHERE d.parentid = $P{objid}


[xgetDetails]
SELECT s.*, l.state, r.area AS route_area, r.description AS route_description
FROM specialcollection_billinggroup_detail s
INNER JOIN loan_ledger l ON s.ledgerid = l.objid
INNER JOIN loanapp a ON l.appid = a.objid
INNER JOIN loan_route r ON a.route_code = r.code
WHERE s.parentid = $P{objid}

[getDetailsWithLedgerInfo]
SELECT l.*, r.code AS route_code, r.area AS route_area, r.description AS route_description,
	a.objid AS loanappid, a.appno, c.address_text AS homeaddress, a.dtcreated AS loandate,
	a.loanamount
FROM specialcollection_billinggroup_detail d 
INNER JOIN loan_ledger l ON d.ledgerid = l.objid
INNER JOIN loanapp a ON l.appid = a.objid
INNER JOIN customer c ON l.acctid = c.objid
INNER JOIN loan_route r ON a.route_code = r.code
WHERE l.state = 'OPEN'
	AND d.parentid = $P{objid}

[getAccountsForBillingGroup]
select b.objid as borrower_objid, b.name as borrower_name, b.address as borrower_address, a.objid as loanapp_objid,
	a.appno as loanapp_appno, a.loanamount as loanapp_amount, l.objid as ledger_objid, ac.dtreleased as ledger_dtreleased, l.dtmatured as ledger_dtmatured,
	(select txndate from followup_result where loanapp_objid=a.objid and txnstate='confirmed' order by txndate desc limit 1) as dtlastfollowup
from (
	select l.objid
	from loan_ledger l
	inner join loanapp a on l.appid=a.objid
	inner join borrower b on a.borrower_objid=b.objid
	where l.state='open'
		and b.name like $P{searchtext}
	union
	select l.objid
	from loan_ledger l
	inner join loanapp a on l.appid=a.objid
	where l.state='open'
		and a.appno like $P{searchtext}
) q inner join loan_ledger l on q.objid=l.objid
inner join loanapp a on l.appid=a.objid
inner join borrower b on a.borrower_objid=b.objid
left join loanapp_capture ac on a.objid=ac.objid
where a.loantype <> 'branch'
order by b.name, a.appno desc, ac.dtreleased desc

[getAccountsSMC]
select b.objid as borrower_objid, b.name as borrower_name, b.address as borrower_address, a.objid as loanapp_objid,
	a.appno as loanapp_appno, a.loanamount as loanapp_amount, l.objid as ledger_objid, ac.dtreleased as ledger_dtreleased, l.dtmatured as ledger_dtmatured,
	(select txndate from followup_result where loanapp_objid=a.objid and txnstate='confirmed' order by txndate desc limit 1) as dtlastfollowup
from (
	select l.objid
	from loan_ledger l
	inner join loanapp a on l.appid=a.objid
	inner join borrower b on a.borrower_objid=b.objid
	left join (
		select ac.*
		from (
			select *
			from ledgeramnesty_active ac
			where curdate() between dtstarted and ifnull(dtended, curdate())
			order by dtfiled desc
		) ac
		group by ac.ledgerid
	) ac on l.objid=ac.ledgerid
	where l.state='open'
		and b.name like $P{searchtext}
		and ac.type='SMC'
	union
	select l.objid
	from loan_ledger l
	inner join loanapp a on l.appid=a.objid
	left join (
		select ac.*
		from (
			select *
			from ledgeramnesty_active ac
			where curdate() between dtstarted and ifnull(dtended, curdate())
			order by dtfiled desc
		) ac
		group by ac.ledgerid
	) ac on l.objid=ac.ledgerid
	where l.state='open'
		and a.appno like $P{searchtext}
		and ac.type='SMC'
) q inner join loan_ledger l on q.objid=l.objid
inner join loanapp a on l.appid=a.objid
inner join borrower b on a.borrower_objid=b.objid
left join loanapp_capture ac on a.objid=ac.objid
where a.loantype <> 'branch'
order by b.name, a.appno desc, ac.dtreleased desc

[getAccountsFixed]
select b.objid as borrower_objid, b.name as borrower_name, b.address as borrower_address, a.objid as loanapp_objid,
	a.appno as loanapp_appno, a.loanamount as loanapp_amount, l.objid as ledger_objid, ac.dtreleased as ledger_dtreleased, l.dtmatured as ledger_dtmatured,
	(select txndate from followup_result where loanapp_objid=a.objid and txnstate='confirmed' order by txndate desc limit 1) as dtlastfollowup
from (
	select l.objid
	from loan_ledger l
	inner join loanapp a on l.appid=a.objid
	inner join borrower b on a.borrower_objid=b.objid
	left join (
		select ac.*
		from (
			select *
			from ledgeramnesty_active ac
			where curdate() between dtstarted and ifnull(dtended, curdate())
			order by dtfiled desc
		) ac
		group by ac.ledgerid
	) ac on l.objid=ac.ledgerid
	where l.state='open'
		and b.name like $P{searchtext}
		and ac.type='FIX'
	union
	select l.objid
	from loan_ledger l
	inner join loanapp a on l.appid=a.objid
	left join (
		select ac.*
		from (
			select *
			from ledgeramnesty_active ac
			where curdate() between dtstarted and ifnull(dtended, curdate())
			order by dtfiled desc
		) ac
		group by ac.ledgerid
	) ac on l.objid=ac.ledgerid
	where l.state='open'
		and a.appno like $P{searchtext}
		and ac.type='FIX'
) q inner join loan_ledger l on q.objid=l.objid
inner join loanapp a on l.appid=a.objid
inner join borrower b on a.borrower_objid=b.objid
left join loanapp_capture ac on a.objid=ac.objid
where a.loantype <> 'branch'
order by b.name, a.appno desc, ac.dtreleased desc

[getAccountsBadDebt]
select b.objid as borrower_objid, b.name as borrower_name, b.address as borrower_address, a.objid as loanapp_objid,
	a.appno as loanapp_appno, a.loanamount as loanapp_amount, l.objid as ledger_objid, ac.dtreleased as ledger_dtreleased, l.dtmatured as ledger_dtmatured,
	(select txndate from followup_result where loanapp_objid=a.objid and txnstate='confirmed' order by txndate desc limit 1) as dtlastfollowup
from (
	select l.objid
	from loan_ledger l
	inner join loanapp a on l.appid=a.objid
	inner join borrower b on a.borrower_objid=b.objid
	left join (
		select ac.*
		from (
			select *
			from ledgeramnesty_active ac
			where curdate() between dtstarted and ifnull(dtended, curdate())
			order by dtfiled desc
		) ac
		group by ac.ledgerid
	) ac on l.objid=ac.ledgerid
	where l.state='open'
		and b.name like $P{searchtext}
		and ac.type='BAD_DEBT'
	union
	select l.objid
	from loan_ledger l
	inner join loanapp a on l.appid=a.objid
	left join (
		select ac.*
		from (
			select *
			from ledgeramnesty_active ac
			where curdate() between dtstarted and ifnull(dtended, curdate())
			order by dtfiled desc
		) ac
		group by ac.ledgerid
	) ac on l.objid=ac.ledgerid
	where l.state='open'
		and a.appno like $P{searchtext}
		and ac.type='BAD_DEBT'
) q inner join loan_ledger l on q.objid=l.objid
inner join loanapp a on l.appid=a.objid
inner join borrower b on a.borrower_objid=b.objid
left join loanapp_capture ac on a.objid=ac.objid
where a.loantype <> 'branch'
order by b.name, a.appno desc, ac.dtreleased desc

[getConferenceFollowup]
SELECT q2.*
FROM (
	SELECT d.borrower_objid, d.borrower_name, d.borrower_address, d.loanapp_objid, d.loanapp_appno, d.loanapp_amount, d.ledger_objid, d.ledger_dtreleased, d.ledger_dtmatured,
		(SELECT txndate FROM followup_result WHERE loanapp_objid = d.loanapp_objid AND txnstate = 'CONFIRMED' ORDER BY txndate DESC LIMIT 1) AS dtlastfollowup,
		(SELECT a.txndate FROM conferenceaccount a INNER JOIN conferenceaccount_detail ca ON a.objid = ca.parentid WHERE a.txnstate = 'APPROVED' AND ca.loanapp_objid = d.loanapp_objid ORDER BY a.txndate DESC LIMIT 1) AS dtconferenced
	FROM (
		SELECT DISTINCT d.loanapp_objid, d.objid
		FROM conferenceaccount c
		INNER JOIN conferenceaccount_detail d ON c.objid = d.parentid
		WHERE c.txnstate = 'APPROVED'
	) q INNER JOIN conferenceaccount_detail d ON q.objid = d.objid
) q2
GROUP BY q2.loanapp_objid
HAVING q2.dtlastfollowup > q2.dtconferenced
ORDER BY q2.dtconferenced DESC, q2.borrower_name

[getConferenceForFollowup]
SELECT q2.*
FROM (
	SELECT d.borrower_objid, d.borrower_name, d.borrower_address, d.loanapp_objid, d.loanapp_appno, d.loanapp_amount, d.ledger_objid, d.ledger_dtreleased, d.ledger_dtmatured,
		(SELECT txndate FROM followup_result WHERE loanapp_objid = d.loanapp_objid AND txnstate = 'CONFIRMED' ORDER BY txndate DESC LIMIT 1) AS dtlastfollowup,
		(SELECT a.txndate FROM conferenceaccount a INNER JOIN conferenceaccount_detail ca ON a.objid = ca.parentid WHERE a.txnstate = 'APPROVED' AND ca.loanapp_objid = d.loanapp_objid ORDER BY a.txndate DESC LIMIT 1) AS dtconferenced
	FROM (
		SELECT DISTINCT d.loanapp_objid, d.objid
		FROM conferenceaccount c
		INNER JOIN conferenceaccount_detail d ON c.objid = d.parentid
		WHERE c.txnstate = 'APPROVED'
			AND d.borrower_name LIKE $P{searchtext}
	) q INNER JOIN conferenceaccount_detail d ON q.objid = d.objid
) q2
GROUP BY q2.loanapp_objid
HAVING q2.dtlastfollowup IS NULL OR q2.dtlastfollowup < q2.dtconferenced
ORDER BY q2.dtconferenced DESC, q2.borrower_name

[getSpecialBilling]
SELECT s.borrower_objid, s.borrower_name, s.borrower_address, s.loanapp_objid, s.loanapp_appno, s.loanapp_amount,
	s.ledger_objid, s.ledger_dtreleased, s.ledger_dtmatured,
	(SELECT txndate FROM followup_result WHERE loanapp_objid = s.loanapp_objid AND txnstate = 'CONFIRMED' ORDER BY txndate DESC LIMIT 1) AS dtlastfollowup
FROM (
	SELECT DISTINCT s.loanapp_objid, s.objid
	FROM specialbillingaccount s
	WHERE s.txnstate = 'APPROVED'
		AND s.borrower_name LIKE $P{searchtext}
) q INNER JOIN specialbillingaccount s ON q.objid = s.objid
ORDER BY s.borrower_name

[getDelinquent]
SELECT b.objid AS borrower_objid, b.name AS borrower_name, b.address AS borrower_address, a.objid AS loanapp_objid, a.appno AS loanapp_appno,
	a.loanamount AS loanapp_amount, l.objid AS ledger_objid, ac.dtreleased AS ledger_dtreleased, l.dtmatured AS ledger_dtmatured,
	(SELECT txndate FROM followup_result WHERE loanapp_objid = a.objid AND txnstate = 'CONFIRMED' ORDER BY txndate DESC LIMIT 1) AS dtlastfollowup
FROM loan_ledger l 
INNER JOIN loanapp a ON l.appid = a.objid
LEFT JOIN loanapp_capture ac ON a.objid = ac.objid
INNER JOIN borrower b ON a.borrower_objid = b.objid
WHERE l.state = 'OPEN'
	AND l.dtmatured < CURDATE()
	AND b.name LIKE $P{searchtext}
ORDER BY b.name

[getPreviousBillingGroupByDate]
select s.*
from specialcollection_billinggroup s
where s.dtstarted < $P{date}
	and s.txntype=$P{type}
	and s.txnstate="APPROVED"
order by s.dtstarted desc

[findBillingGroupByDateAndType]
select s.*
from specialcollection_billinggroup s
where s.dtstarted=$P{date}
	and s.txntype=$P{type}
	and s.objid <> $P{objid}

[changeState]
UPDATE specialcollection_billinggroup SET txnstate = $P{txnstate}
WHERE objid = $P{objid}
