[getList]
SELECT * FROM ledger_billing

[getLookupBillingDetailList]
SELECT l.*, l.acctid AS borrower_objid, l.acctname AS borrower_name, c.address_text AS borrower_address, 
	l.appno AS loanapp_appno, l.loanappid AS loanapp_objid, l.loanamount AS loanapp_loanamount,
	r.area AS route_area, r.description AS route_description,
	CONCAT(r.description, ' - ', r.area) AS route_name
FROM ledger_billing_detail l
INNER JOIN loan_route r ON l.route_code = r.code
INNER JOIN customer c ON l.acctid = c.objid
WHERE l.parentid = $P{itemid}
	AND l.acctname LIKE $P{searchtext}

[getPastBillingsNotDownloaded]
select i.*
from ledger_billing b
inner join ledger_billing_item i on b.objid=i.parentid
left join ledger_billing_cancelrequest r on i.objid=r.billing_itemid
where r.objid is null 
	and b.billdate < $P{date}
	and state = 'FOR_DOWNLOAD'

[xgetPastBillingsNotDownloaded]
SELECT li.*
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
WHERE l.billdate < $P{date}
	AND state = 'FOR_DOWNLOAD'

[getRoutes]
SELECT li.objid AS itemid, lr.code, lr.description,	lr.area, li.state
FROM ledger_billing_item li
INNER JOIN loan_route lr ON li.item_objid = lr.code
WHERE li.parentid = $P{objid}
ORDER BY lr.description

[getRoutesByState]
SELECT li.objid AS itemid, lr.code, lr.description,	lr.area, li.state
FROM ledger_billing_item li
INNER JOIN loan_route lr ON li.item_objid = lr.code
WHERE li.parentid = $P{objid}
	AND li.state = $P{state}
ORDER BY lr.description

[getBillingItems]
SELECT * FROM ledger_billing_item
WHERE parentid = $P{objid}

[getBillingItemsWithInfo]
SELECT li.objid, li.state, 
	li.item_objid AS itemid, 
	-- CASE WHEN li.item_type = 'route' THEN li.objid ELSE li.item_objid END AS itemid,
	li.item_type AS `type`, r.*,
	CASE WHEN s.txntype = 'REQUEST' THEN TRUE ELSE FALSE END AS isrequest,
	CASE WHEN cp.objid IS NOT NULL THEN TRUE ELSE FALSE END AS iscapture
FROM ledger_billing_item li
LEFT JOIN loan_route r ON li.item_objid = r.code
LEFT JOIN specialcollection s ON li.item_objid = s.objid
LEFT JOIN capture_payment cp ON li.item_objid = cp.specialcollectionid
WHERE li.parentid = $P{objid}
ORDER BY s.dtfiled, li.item_type, r.description

[xgetBillingItemsWithInfo]
SELECT li.objid, li.state, li.item_type AS `type`, r.*,
	CASE WHEN s.txntype = 'REQUEST' THEN TRUE ELSE FALSE END AS isrequest,
	CASE WHEN cp.objid IS NOT NULL THEN TRUE ELSE FALSE END AS iscapture,
	CASE WHEN li.item_type = 'route' THEN li.objid ELSE li.item_objid END AS itemid
FROM ledger_billing_item li
LEFT JOIN loan_route r ON li.item_objid = r.code
LEFT JOIN specialcollection s ON li.item_objid = s.objid
LEFT JOIN capture_payment cp ON li.item_objid = cp.specialcollectionid
WHERE li.parentid = $P{objid}
ORDER BY s.dtfiled, li.item_type, r.description

[xxgetBillingItemsWithInfo]
SELECT li.objid, li.state, 
	-- li.item_objid AS itemid, 
	CASE WHEN li.item_type = 'route' THEN li.objid ELSE li.item_objid END AS itemid,
	li.item_type AS `type`, r.*,
	CASE WHEN s.txntype = 'REQUEST' THEN TRUE ELSE FALSE END AS isrequest,
	CASE WHEN cp.objid IS NOT NULL THEN TRUE ELSE FALSE END AS iscapture
FROM ledger_billing_item li
LEFT JOIN loan_route r ON li.item_objid = r.code
LEFT JOIN specialcollection s ON li.item_objid = s.objid
LEFT JOIN capture_payment cp ON li.item_objid = cp.specialcollectionid
WHERE li.parentid = $P{objid}
ORDER BY s.dtfiled, li.item_type, r.description

[getBillingDetailsByBillingid]
SELECT d.*, 
	CASE 
		WHEN l.dtlastpaid IS NULL THEN (DATEDIFF(CURDATE(), DATE_SUB(l.dtstarted, INTERVAL -1 DAY)) + 1)
		WHEN DATEDIFF(CURDATE(), l.dtlastpaid) < 0 THEN 0
		ELSE (DATEDIFF(CURDATE(), l.dtlastpaid) + 1)
	END AS totaldays, l.dtstarted, l.dtlastpaid
FROM ledger_billing_detail d
INNER JOIN loan_ledger l ON d.ledgerid = l.objid
WHERE d.billingid = $P{objid}

[getBillingDetails]
SELECT d.*, d.route_code AS routecode, 
	CASE 
		WHEN l.dtlastpaid IS NULL THEN (DATEDIFF(CURDATE(), DATE_SUB(l.dtstarted, INTERVAL -1 DAY)) + 1)
		WHEN DATEDIFF(CURDATE(), l.dtlastpaid) < 0 THEN 0
		ELSE (DATEDIFF(CURDATE(), l.dtlastpaid) + 1)
	END AS totaldays, l.dtstarted, l.dtlastpaid
FROM ledger_billing_detail d
INNER JOIN loan_ledger l ON d.ledgerid = l.objid
WHERE d.parentid = $P{objid}

[getBillingDetailsWithNextto]
SELECT d.*, n.nexttoid AS nextto, d.route_code AS routecode, lb.isstart,
	CASE 
		WHEN l.dtlastpaid IS NULL THEN (DATEDIFF(CURDATE(), DATE_SUB(l.dtstarted, INTERVAL -1 DAY)) + 1)
		WHEN DATEDIFF(CURDATE(), l.dtlastpaid) < 0 THEN 0
		ELSE (DATEDIFF(CURDATE(), l.dtlastpaid) + 1)
	END AS totaldays, l.dtstarted, l.dtlastpaid
FROM ledger_billing_detail d
INNER JOIN loan_ledger l ON d.ledgerid = l.objid
INNER JOIN loanapp_borrower lb on d.loanappid=lb.parentid
LEFT JOIN loanapp_borrower_nextto n ON d.acctid = n.borrowerid
WHERE d.parentid = $P{objid} 
	AND lb.type='principal'

[getRouteListByState]
SELECT l.objid, li.state, li.objid AS itemid, lr.description AS route_description, lr.area AS route_area,
	l.collector_objid, l.collector_name, l.billdate, lr.code AS route_code,
	CONCAT(lr.description, " - ", lr.area) AS route_name
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
INNER JOIN loan_route lr ON li.item_objid = lr.code
WHERE li.state = $P{state}
ORDER BY billdate DESC, route_description 

[getRemittedRouteList]
SELECT l.objid, li.state, lr.description AS route_description, lr.area AS route_area,
	l.collector_objid, l.collector_name, l.billdate,
	CONCAT(lr.description, " - ", lr.area) AS route_name, r.state AS remittance_state
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
INNER JOIN loan_route lr ON li.item_objid = lr.code
INNER JOIN collection_remittance r ON r.collection_objid = l.objid AND r.group_objid = li.item_objid
WHERE r.state <> 'POSTED'
ORDER BY billdate DESC, route_description 

[getPostedRouteList]
SELECT l.objid, li.state, lr.description AS route_description, lr.area AS route_area,
	l.collector_objid, l.collector_name, l.billdate,
	CONCAT(lr.description, " - ", lr.area) AS route_name, r.state AS remittance_state
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
INNER JOIN loan_route lr ON li.item_objid = lr.code
INNER JOIN collection_remittance r ON r.collection_objid = l.objid AND r.group_objid = li.item_objid
WHERE r.state = 'POSTED'
ORDER BY billdate DESC, route_description 

[getRouteBillingByCollector]
SELECT i.objid AS itemid, i.parentid AS billingid, b.billdate, r.*,
	CASE WHEN i.state = 'FOR_DOWNLOAD' THEN 0 ELSE 1 END AS downloaded,
	CASE WHEN rm.objid IS NOT NULL THEN 1 ELSE 0 END AS remitted,
	CASE WHEN rm.state = 'POSTED' THEN 1 ELSE 0 END AS posted
FROM ledger_billing b
INNER JOIN ledger_billing_item i ON b.objid = i.parentid
INNER JOIN loan_route r ON i.item_objid = r.code
LEFT JOIN collection_remittance rm ON rm.collection_objid = b.objid AND rm.group_objid = r.code
LEFT JOIN ledger_billing_assist a ON b.objid = a.prevbillingid AND i.objid = a.previtemid
WHERE b.collector_objid = $P{objid}
	AND b.billdate >= $P{date}
	AND i.item_type = 'route'
	AND a.objid IS NULL
ORDER BY b.billdate

[xxxgetRouteBillingByCollector]
SELECT li.objid AS itemid, li.parentid AS billingid, l.billdate, r.*,
	CASE li.state 
		WHEN 'FOR_DOWNLOAD' THEN 0 ELSE 1
	END AS downloaded,
	CASE
		WHEN rm.objid IS NOT NULL THEN 1 ELSE 0
	END AS remitted,
	CASE
		WHEN rm.state = 'POSTED' THEN 1 ELSE 0 
	END AS posted
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
INNER JOIN loan_route r ON li.item_objid = r.code
LEFT JOIN collection_remittance rm ON rm.collection_objid = l.objid AND rm.group_objid = r.code
LEFT JOIN ledger_billing_assist a ON l.objid = a.prevbillingid
WHERE l.collector_objid = $P{objid}
	AND l.billdate >= $P{date}
	AND li.item_type ='route'
	AND a.objid IS NULL
ORDER BY l.billdate

[xxgetRouteBillingByCollector]
SELECT li.objid AS itemid, li.parentid AS billingid, l.billdate, r.*,
	CASE li.state 
		WHEN 'FOR_DOWNLOAD' THEN 0 ELSE 1
	END AS downloaded,
	CASE
		WHEN rm.objid IS NOT NULL THEN 1 ELSE 0
	END AS remitted,
	CASE
		WHEN rm.state = 'POSTED' THEN 1 ELSE 0 
	END AS posted
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
INNER JOIN loan_route r ON li.item_objid = r.code
LEFT JOIN collection_remittance rm ON rm.collection_objid = l.objid AND rm.group_objid = r.code
WHERE l.collector_objid = $P{objid}
	-- AND l.billdate >= $P{date}
	AND li.item_type ='route'
ORDER BY l.billdate

[xgetRouteBillingByCollector]
SELECT li.objid AS itemid, li.parentid AS billingid, l.billdate, r.*,
	CASE li.state 
		WHEN 'FOR_DOWNLOAD' THEN 0 ELSE 1
	END AS downloaded
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
INNER JOIN loan_route r ON li.item_objid = r.code
WHERE l.collector_objid = $P{objid}
	AND li.item_type ='route'
ORDER BY l.billdate

[getRouteBillingByCollectorByDate]
SELECT li.objid AS itemid, li.parentid AS billingid, l.billdate, r.*,
	CASE li.state 
		WHEN 'FOR_DOWNLOAD' THEN 0 ELSE 1
	END AS downloaded,
	CASE
		WHEN rm.objid IS NOT NULL THEN 1 ELSE 0
	END AS remitted,
	CASE
		WHEN rm.state = 'POSTED' THEN 1 ELSE 0 
	END AS posted
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
INNER JOIN loan_route r ON li.item_objid = r.code
LEFT JOIN collection_remittance rm ON rm.collection_objid = l.objid AND rm.group_objid = r.code
WHERE l.collector_objid = $P{objid}
	AND l.billdate = $P{date}
	AND li.item_type ='route'

[getSpecialBillingByCollector]
SELECT li.objid AS itemid, li.parentid AS billingid, l.billdate,
	CASE s.state 
		WHEN 'FOR_DOWNLOAD' THEN 0 ELSE 1
	END AS downloaded,
	CASE
		WHEN rm.objid IS NOT NULL THEN 1 ELSE 0
	END AS remitted,
	CASE
		WHEN rm.state = 'POSTED' THEN 1 ELSE 0 
	END AS posted
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
INNER JOIN specialcollection s ON li.objid = s.objid
LEFT JOIN collection_remittance rm ON rm.collection_objid = l.objid AND rm.group_objid = li.item_objid
WHERE l.collector_objid = $P{objid}
	AND li.item_type = $P{itemtype}
	AND l.billdate >= $P{date}
	AND s.txntype = 'ONLINE'
ORDER BY l.billdate, s.dtfiled

[xgetSpecialBillingByCollector]
SELECT li.objid AS itemid, li.parentid AS billingid, l.billdate,
	CASE s.state 
		WHEN 'FOR_DOWNLOAD' THEN 0 ELSE 1
	END AS downloaded
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
INNER JOIN specialcollection s ON li.objid = s.objid
WHERE l.collector_objid = $P{objid}
	AND li.item_type = $P{itemtype}
	AND s.txntype = 'ONLINE'
ORDER BY l.billdate, s.dtfiled

[getSpecialBillingByCollectorByDate]
SELECT li.objid AS itemid, li.parentid AS billingid, l.billdate,
	CASE s.state 
		WHEN 'FOR_DOWNLOAD' THEN 0 ELSE 1
	END AS downloaded
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
INNER JOIN specialcollection s ON li.objid = s.objid
WHERE l.collector_objid = $P{objid}
	AND l.billdate = $P{date}
	AND li.item_type = $P{itemtype}
	AND s.txntype = 'ONLINE'
ORDER BY s.dtfiled

[findAvgOverpaymentAmount]
SELECT MAX(sq.count) AS max, sq.groupbaseamount 
FROM (SELECT COUNT(ll.groupbaseamount) AS count, ll.groupbaseamount 
		FROM loan_ledger_detail ll
		WHERE ll.parentid = $P{parentid}
		GROUP BY ll.groupbaseamount) sq
GROUP BY sq.groupbaseamount
ORDER BY MAX DESC

[findDownloadedRoute]
SELECT objid
FROM ledger_billing_item
WHERE parentid = $P{objid}
	AND state = 'DOWNLOADED'

[findBillingDetailByBillingidAndAppid]
SELECT * FROM ledger_billing_detail
WHERE billingid = $P{billingid}
	AND loanappid = $P{appid}

[findBillingDetailByBillingidAndAppidAndItemid]
SELECT d.* 
FROM ledger_billing_item i
INNER JOIN ledger_billing_detail d ON d.parentid = i.objid
WHERE d.billingid = $P{billingid}
	AND d.loanappid = $P{appid}
	AND d.parentid = $P{itemid}
	AND i.state NOT IN ('CANCELLED','CLOSED')

[findByBillingidAndItemid]
SELECT *
FROM ledger_billing_item 
WHERE parentid = $P{objid}
	AND item_objid = $P{itemid}
	AND state NOT IN ('CANCELLED','CLOSED')

[findByBilldateAndItemid]
SELECT b.collector_objid, b.collector_name, i.*
FROM ledger_billing b
INNER JOIN ledger_billing_item i ON b.objid = i.parentid
LEFT JOIN ledger_billing_assist a ON b.objid = a.prevbillingid AND i.objid = a.previtemid
WHERE b.billdate = $P{billdate}
	AND i.item_objid = $P{itemid}
	AND a.objid IS NULL
	AND i.state NOT IN ('CANCELLED','CLOSED')


[xfindByBilldateAndItemid]
SELECT li.* 
FROM ledger_billing l
INNER JOIN ledger_billing_item li ON l.objid = li.parentid
LEFT JOIN ledger_billing_assist a ON l.objid = a.prevbillingid
WHERE l.billdate = $P{billdate}
	AND li.item_objid = $P{itemid}
	AND a.objid IS NULL

[findByCollectorAndBilldate]
SELECT b.*
FROM (
	SELECT b.objid
	FROM ledger_billing b
	INNER JOIN ledger_billing_item i ON b.objid = i.parentid
	LEFT JOIN ledger_billing_assist a ON b.objid = a.prevbillingid AND i.objid = a.previtemid
	WHERE b.collector_objid = $P{collectorid}
		AND b.billdate = $P{date}
		AND a.objid IS NULL
	GROUP BY b.objid 
	HAVING b.objid IS NOT NULL
) q INNER JOIN ledger_billing b ON q.objid = b.objid
ORDER BY dtcreated DESC

[xxfindByCollectorAndBilldate]
SELECT b.* 
FROM ledger_billing b
LEFT JOIN ledger_billing_assist a ON b.objid = a.prevbillingid
WHERE b.collector_objid = $P{collectorid}
	AND b.billdate = $P{date}
	AND a.objid IS NULL
ORDER BY dtcreated DESC

[xfindByCollectorAndBilldate]
SELECT * FROM ledger_billing
WHERE collector_objid = $P{collectorid}
	AND billdate = $P{date}
ORDER BY dtcreated DESC

[findUnuploadedCollectionByBilling]
SELECT objid FROM ledger_billing_item
WHERE parentid = $P{objid}
	AND state = 'DOWNLOADED'

[findUnremittedBillingByDate]
SELECT i.*
FROM (
	SELECT b.objid
	FROM ledger_billing b
	INNER JOIN ledger_billing_item i ON b.objid = i.parentid
	LEFT JOIN ledger_billing_assist a ON b.objid = a.prevbillingid AND i.objid = a.previtemid
	WHERE a.objid IS NULL
		AND b.billdate = $P{date}
) q INNER JOIN ledger_billing b ON q.objid = b.objid
INNER JOIN ledger_billing_item i ON b.objid = i.parentid
WHERE i.state IN ('FOR_DOWNLOAD', 'DOWNLOADED')

[xfindUnremittedBillingByDate]
SELECT i.*
FROM (
	SELECT b.objid
	FROM ledger_billing b
	LEFT JOIN ledger_billing_assist a ON b.objid = a.prevbillingid
	WHERE a.objid IS NULL
		AND b.billdate = $P{date}
) q INNER JOIN ledger_billing b ON q.objid = b.objid
INNER JOIN ledger_billing_item i ON b.objid = i.parentid
WHERE i.state IN ('FOR_DOWNLOAD', 'DOWNLOADED')


[removeBillingItemByType]
DELETE FROM ledger_billing_item
WHERE item_type = $P{type}
	AND parentid = $P{objid}

[removeForDownloadBillingItemByType]
DELETE FROM ledger_billing_item
WHERE item_type = $P{type}
	AND parentid = $P{objid}
	AND state = 'FOR_DOWNLOAD'

[removeBillingDetail]
DELETE FROM ledger_billing_detail
WHERE parentid = $P{objid}

[removeBillingDetailByBillingid]
DELETE FROM ledger_billing_detail
WHERE billingid = $P{objid}

[changeState]
UPDATE ledger_billing_item SET state = $P{state}
WHERE objid = $P{objid}

[updateBillingDetailBillingidByParentid]
UPDATE ledger_billing_detail SET billingid = $P{billingid}
WHERE parentid = $P{parentid}