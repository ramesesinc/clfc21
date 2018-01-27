[getList]
SELECT a.* FROM loan_ledger_adjustment a
WHERE a.borrower_name LIKE $P{searchtext}
ORDER BY a.dtcreated DESC

[getListByState]
SELECT a.* FROM loan_ledger_adjustment a
WHERE a.txnstate = $P{state}
	AND a.borrower_name LIKE $P{searchtext}
ORDER BY a.dtcreated DESC

[getApprovedList]
SELECT b.*
FROM (
	SELECT DISTINCT a.objid
	FROM loan_ledger_adjustment a
	LEFT JOIN ledger_adjustment_request d ON a.objid = d.adjustmentid
	WHERE d.txnstate = 'DISAPPROVED'
		AND a.txnstate = 'APPROVED'
		AND a.borrower_name LIKE $P{searchtext}
) qry INNER JOIN loan_ledger_adjustment b ON qry.objid = b.objid

[getActiveListByLedgerid]
SELECT a.*
FROM loan_ledger_adjustment a
WHERE a.txnstate IN ('APPROVED', 'FOR_DELETE')
	AND a.ledgerid = $P{ledgerid}
ORDER BY a.dtcreated DESC

[getAdjustmentDeleteRequestList]
SELECT b.*
FROM (
	SELECT DISTINCT a.objid
	FROM loan_ledger_adjustment a
	INNER JOIN ledger_adjustment_request d ON a.objid = d.adjustmentid
	WHERE d.txnstate = $P{txnstate}
		AND a.borrower_name LIKE $P{searchtext}
) qry INNER JOIN loan_ledger_adjustment b ON qry.objid = b.objid

[getListByLedgerid]
SELECT a.* FROM loan_ledger_adjustment a
WHERE a.ledgerid = $P{ledgerid}
ORDER BY a.dtcreated DESC

[getListByStateAndLedgerid]
SELECT a.* FROM loan_ledger_adjustment a
WHERE a.txnstate = $P{state}
	AND a.ledgerid = $P{ledgerid}
ORDER BY a.dtcreated DESC

[getApprovedListByLedgerid]
SELECT b.*
FROM (
	SELECT DISTINCT a.objid
	FROM loan_ledger_adjustment a
	LEFT JOIN ledger_adjustment_request d ON a.objid = d.adjustmentid
	WHERE d.txnstate = 'DISAPPROVED'
		AND a.txnstate = 'APPROVED'
		AND a.ledgerid = $P{ledgerid}
) qry INNER JOIN loan_ledger_adjustment b ON qry.objid = b.objid

[getAdjustmentDeleteRequestListByLedgerid]
SELECT b.*
FROM (
	SELECT DISTINCT a.objid
	FROM loan_ledger_adjustment a
	INNER JOIN ledger_adjustment_request d ON a.objid = d.adjustmentid
	WHERE d.txnstate = $P{txnstate}
		AND a.ledgerid = $P{ledgerid}
) qry INNER JOIN loan_ledger_adjustment b ON qry.objid = b.objid