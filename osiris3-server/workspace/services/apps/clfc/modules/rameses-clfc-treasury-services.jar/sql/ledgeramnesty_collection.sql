[getList]
SELECT a.*,
	CASE 
		WHEN a.txnstate <> 'ACTIVE' THEN a.txnstate
		WHEN a.txnstate = 'ACTIVE' AND IFNULL(a.dtended, CURDATE()) < CURDATE() THEN 'EXPIRED' ELSE a.txnstate
	END AS state
FROM ledgeramnesty_collection a
WHERE a.borrower_name LIKE $P{searchtext}
ORDER BY a.dtcreated DESC

[getListByLedgerid]
SELECT a.*,
	CASE 
		WHEN a.txnstate <> 'ACTIVE' THEN a.txnstate
		WHEN a.txnstate = 'ACTIVE' AND IFNULL(a.dtended, CURDATE()) < CURDATE() THEN 'EXPIRED' ELSE a.txnstate
	END AS state
FROM ledgeramnesty_collection a
INNER JOIN loan_ledger l ON a.loanapp_objid = l.appid
WHERE a.borrower_name LIKE $P{searchtext}
	AND l.objid = $P{ledgerid}
ORDER BY a.dtstarted desc, a.dtcreated DESC

[getListByState]
SELECT a.*,
	CASE 
		WHEN a.txnstate <> 'ACTIVE' THEN a.txnstate
		WHEN a.txnstate = 'ACTIVE' AND IFNULL(a.dtended, CURDATE()) < CURDATE() THEN 'EXPIRED' ELSE a.txnstate
	END AS state
FROM ledgeramnesty_collection a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnstate = $P{state}
ORDER BY a.dtcreated DESC

[getListByStateAndLedgerid]
SELECT a.*,
	CASE 
		WHEN a.txnstate <> 'ACTIVE' THEN a.txnstate
		WHEN a.txnstate = 'ACTIVE' AND IFNULL(a.dtended, CURDATE()) < CURDATE() THEN 'EXPIRED' ELSE a.txnstate
	END AS state
FROM ledgeramnesty_collection a
INNER JOIN loan_ledger l ON a.loanapp_objid = l.appid
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnstate = $P{state}
	AND l.objid = $P{ledgerid}
ORDER BY a.dtstarted desc, a.dtcreated DESC

[findByRefid]
SELECT a.*
FROM ledgeramnesty_collection a
WHERE a.refid = $P{refid}
