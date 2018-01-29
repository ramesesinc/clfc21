[getList]
SELECT * FROM dailycollection
ORDER BY txndate DESC

[getListByState]
SELECT * FROM dailycollection
WHERE state = $P{state}
ORDER BY txndate DESC

[getCbs]
SELECT * FROM dailycollection_cbs
WHERE parentid = $P{objid}
ORDER BY txndate DESC, cbsno DESC

[getNoncashes]
SELECT * FROM dailycollection_noncash
WHERE parentid = $P{objid}
ORDER BY txndate DESC, route_description

[getOverages]
SELECT * FROM dailycollection_overage
WHERE parentid = $P{objid}
ORDER BY txndate DESC, refno DESC

[getShortages]
SELECT * FROM dailycollection_shortage
WHERE parentid = $P{objid}
ORDER BY txndate DESC, cbsno DESC

[getEncashments]
SELECT * FROM dailycollection_encashment
WHERE parentid = $P{objid}
ORDER BY txndate DESC

[getDepositSlips]
SELECT * FROM dailycollection_depositslip
WHERE parentid = $P{objid}
ORDER BY txndate DESC, controlno DESC

[getOtherReceipts]
SELECT * FROM dailycollection_otherreceipt
WHERE parentid = $P{objid}
ORDER BY txndate, name

[getPostedNoncashCollectionByDate]
SELECT r.objid, r.collector_objid, r.collector_name, r.txndate,
	(SELECT SUM(amount) FROM collection_remittance_detail WHERE parentid = r.objid AND payoption <> 'cash') AS amount,
	r.group_objid AS route_code,
	CASE
		WHEN r.group_type = 'route' THEN CONCAT(lr.description, ' - ', lr.area)
		WHEN r.group_type = 'online' THEN 'DIRECT'
		ELSE UCASE(r.group_type)
	END AS route_description
FROM collection_remittance r
INNER JOIN collection_remittance_detail d ON r.objid = d.parentid
LEFT JOIN loan_route lr ON r.group_objid = lr.code
WHERE d.payoption <> 'cash'
	AND r.txndate = $P{date}
GROUP BY r.objid
HAVING r.objid IS NOT NULL
ORDER BY r.txndate DESC

[findDailyCollectionByTxndate]
SELECT * FROM dailycollection
WHERE txndate = $P{txndate}

[findCbsByRefid]
SELECT * FROM dailycollection_cbs
WHERE refid = $P{refid}

[findEncashmentByRefid]
SELECT * FROM dailycollection_encashment
WHERE refid = $P{refid}

[findCurrentSendback]
SELECT s.*
FROM dailycollection_sendback s
WHERE s.parentid = $P{objid}
ORDER BY s.dtcreated DESC

[changeState]
UPDATE dailycollection SET state = $P{state}
WHERE objid = $P{objid}