[getList]
SELECT d.*,
	CASE
		WHEN d.reftype = 'DEPOSIT' THEN 'Deposited to BANK'
		WHEN d.reftype = 'SAFE_KEEP' THEN 'Deposited to VAULT'
	END AS depositstatus
FROM depositslip d
WHERE d.controlno LIKE $P{searchtext}
ORDER BY d.txndate DESC, d.dtcreated DESC

[xgetList]
SELECT * FROM depositslip
WHERE controlno LIKE $P{searchtext}
ORDER BY txndate DESC

[getListByState]
SELECT d.*,
	CASE
		WHEN d.reftype = 'DEPOSIT' THEN 'Deposited to BANK'
		WHEN d.reftype = 'SAFE_KEEP' THEN 'Deposited to VAULT'
	END AS depositstatus
FROM depositslip d
WHERE d.controlno LIKE $P{searchtext}
	AND d.state = $P{state}
ORDER BY d.txndate DESC, d.dtcreated DESC

[xgetListByState]
SELECT * FROM depositslip
WHERE controlno LIKE $P{searchtext}
	AND state = $P{state}
ORDER BY txndate DESC

[getListForDailyCollection]
select d.*
from (
	select d.objid
	from depositslip d
	left join dailycollection_depositslip dds on d.objid = dds.refid
	where d.controlno like $P{searchtext}
		and d.state in ('approved','closed')
		and dds.objid is null
	union
	select d.objid
	from depositslip d
	inner join dailycollection_depositslip dds on d.objid = dds.refid
	where d.controlno like $P{searchtext}
		and d.state in ('approved','closed')
		and dds.parentid = $P{dailycollectionid}
) q inner join depositslip d on q.objid = d.objid
order by d.txndate desc

[getActiveList]
SELECT * FROM depositslip
WHERE controlno LIKE $P{searchtext}
	AND state IN ('APPROVED', 'CLOSED')
ORDER BY txndate DESC

[getListByStateAndReftype]
SELECT * FROM depositslip
WHERE controlno LIKE $P{searchtext}
	AND state = $P{state}
	AND reftype = $P{reftype}
ORDER BY txndate DESC

[getChecks]
SELECT * FROM depositslip_check
WHERE parentid = $P{objid}

[getCbs]
SELECT d.*, c.collector_objid, c.collector_name,
	CASE WHEN e.objid IS NULL THEN 0 ELSE 1 END AS isencashed
FROM depositslip_cbs d
INNER JOIN collection_cb c ON d.cbsid = c.objid
LEFT JOIN collection_cb_encash e ON d.cbsid = e.objid
WHERE d.parentid = $P{objid}

[getCheckouts]
SELECT * FROM depositslip_checkout

[getCheckoutByTxndate]
SELECT dc.dtcheckedout, d.controlno AS slipno, p.bank_objid AS bank, d.amount,
	c.cbsno, dc.representative1_objid, dc.representative2_objid
FROM depositslip d
INNER JOIN depositslip_checkout dc ON d.objid = dc.objid
INNER JOIN passbook p ON d.passbook_objid = p.objid
LEFT JOIN collection_cb c ON d.objid = c.collection_objid AND d.objid = c.group_objid
WHERE dc.dtcheckedout BETWEEN $P{starttime} AND $P{endtime}
ORDER BY dc.dtcheckedout

[findCurrentCancelRequestByState]
SELECT c.*
FROM depositslip_cancel c
WHERE c.parentid = $P{objid}
	AND c.txnstate = $P{state}
ORDER BY c.dtcreated DESC

[changeState]
UPDATE depositslip SET state = $P{state}
WHERE objid = $P{objid}

[approve]
UPDATE depositslip SET state = 'APPROVED'
WHERE objid = $P{objid}
	AND state = 'DRAFT'