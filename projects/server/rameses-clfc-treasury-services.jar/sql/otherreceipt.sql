[getList]
SELECT r.*
FROM (
	SELECT objid
	FROM otherreceipt
	WHERE cbsno LIKE $P{searchtext}
	UNION
	SELECT objid
	FROM otherreceipt
	WHERE collector_name LIKE $P{searchtext}
) q INNER JOIN otherreceipt r ON q.objid = r.objid
ORDER BY r.dtcreated DESC

[getListByState]
SELECT r.*
FROM (
	SELECT objid
	FROM otherreceipt
	WHERE cbsno LIKE $P{searchtext}
		AND txnstate = $P{state}
	UNION
	SELECT objid
	FROM otherreceipt
	WHERE collector_name LIKE $P{searchtext}
		AND txnstate = $P{state}
) q INNER JOIN otherreceipt r ON q.objid = r.objid
ORDER BY r.dtcreated DESC

[getDetails]
SELECT d.*
FROM otherreceipt_detail d
WHERE d.parentid = $P{objid}
ORDER BY d.dtcollected DESC

[getCollectorListByDate]
SELECT DISTINCT o.collector_objid AS objid, o.collector_name AS name
FROM otherreceipt o
WHERE o.txndate = $P{date}

[getCollectionListByDateAndCollector]
SELECT o.*
FROM otherreceipt o
WHERE o.txndate = $P{date}
	AND o.collector_objid = $P{collectorid}
ORDER BY o.name

[getPostedCollectionByStardateAndEnddate]
SELECT o.*, 0 AS shortage, 0 AS overage,
	IFNULL(o.totalamount, 0) AS amount,
	IFNULL((
		SELECT SUM(d.amount)
		FROM otherreceipt_detail d
		LEFT JOIN otherreceipt_voidrequest v ON d.objid = v.refid
		WHERE d.parentid = o.objid
			AND d.payoption = 'cash'
			AND (v.objid IS NULL OR v.txnstate <> 'APPROVED')
	), 0) AS totalcash,
	IFNULL((
		SELECT SUM(d.amount)
		FROM otherreceipt_detail d
		LEFT JOIN otherreceipt_voidrequest v ON d.objid = v.refid
		WHERE d.parentid = o.objid
			AND d.payoption = 'check'
			AND (v.objid IS NULL OR v.txnstate <> 'APPROVED')
	), 0) AS totalnoncash
FROM otherreceipt o
WHERE o.txndate BETWEEN $P{startdate} AND $P{enddate}
	AND o.txnstate = 'POSTED'
ORDER BY o.txndate DESC, o.dtcreated

[getListByDateAndState]
SELECT o.*
FROM otherreceipt o
WHERE o.txndate = $P{date}
	AND o.txnstate = $P{state}

[xgetCollectionByDateAndCollector]
SELECT o.*
FROM otherreceipt o
WHERE o.txndate = $P{date}
	AND o.collector_objid = $P{collectorid}

[findUnremittedOtherReceiptByDateAndCollector]
SELECT o.*
FROM otherreceipt o
WHERE o.txndate = $P{date}
	AND o.collector_objid = $P{collectorid}
	AND o.txnstate = 'DRAFT'

[findVoidRequestByRefidAndOtherreceiptidAndState]
SELECT v.*
FROM otherreceipt_voidrequest v
WHERE v.refid = $P{refid}
	AND v.otherreceiptid = $P{otherreceiptid}
	AND v.txnstate = $P{state}

