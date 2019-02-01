[getList]
SELECT s.*
FROM (
	SELECT s.objid
	FROM resolve_lost_collection s
	WHERE s.route_description LIKE $P{searchtext}
	UNION
	SELECT s.objid
	FROM resolve_lost_collection s
	WHERE s.collector_name LIKE $P{searchtext}
	UNION
	SELECT s.objid
	FROM resolve_lost_collection s
	WHERE s.cbs_no LIKE $P{searchtext}
) q INNER JOIN resolve_lost_collection s ON q.objid = s.objid
ORDER BY s.dtcreated DESC

[getListByState]
SELECT s.*
FROM (
	SELECT s.objid
	FROM resolve_lost_collection s
	WHERE s.route_description LIKE $P{searchtext}
		AND s.txnstate = $P{state}
	UNION
	SELECT s.objid
	FROM resolve_lost_collection s
	WHERE s.collector_name LIKE $P{searchtext}
		AND s.txnstate = $P{state}
	UNION
	SELECT s.objid
	FROM resolve_lost_collection s
	WHERE s.cbs_no LIKE $P{searchtext}
		AND s.txnstate = $P{state}
) q INNER JOIN resolve_lost_collection s ON q.objid = s.objid
ORDER BY s.dtcreated DESC

[getPaymentDetails]
SELECT d.*
FROM loanpayment_detail d
WHERE d.parentid = $P{objid}
ORDER BY d.control_series

[getRouteList]
SELECT r.*
FROM loan_route r
WHERE r.description LIKE $P{searchtext}
ORDER BY r.description

[getPaymentList]
SELECT p.*
FROM loanpayment p
LEFT JOIN collection_remittance r ON p.objid = r.objid
WHERE p.objid LIKE $P{searchtext}
-- WHERE r.objid IS NULL
ORDER BY p.dtfiled DESC

[getBillingDetail]
SELECT d.* FROM ledger_billing_detail d
WHERE d.parentid = $P{objid}

[findCreatedDocumentByPaymentid]
SELECT s.*
FROM resolve_lost_collection s
WHERE s.loanpaymentid = $P{paymentid}
	AND s.objid <> $P{objid}
	AND s.txnstate IN ('DRAFT','APPROVED')

[findBillingByCollectorAndDate]
SELECT b.*
FROM ledger_billing b
WHERE b.collector_objid = $P{collectorid}
	AND billdate = $P{date}

[findBillingItemByParentidAndItemid]
SELECT i.*
FROM ledger_billing_item i
WHERE i.parentid = $P{objid}
	AND i.item_objid = $P{itemid}

[findBillingDetailByAppidAndBillingidAndItemid]
SELECT  d.*
FROM ledger_billing_detail d
WHERE d.loanappid = $P{appid}
	AND d.billingid = $P{billingid}
	AND d.parentid = $P{itemid}

[findFieldCollectionLoanByAppid]
SELECT f.*
FROM fieldcollection_loan f
WHERE f.loanapp_objid = $P{appid}

[findFieldCollectionPaymentByRefno]
SELECT p.*
FROM fieldcollection_payment p
WHERE p.refno = $P{refno}

[findLedgerPaymentByRefno]
SELECT p.*
FROM loan_ledger_payment p
WHERE p.refno = $P{refno}

[findCBSByCbsno]
SELECT c.*
FROM collection_cb c
WHERE c.cbsno = $P{cbsno}

