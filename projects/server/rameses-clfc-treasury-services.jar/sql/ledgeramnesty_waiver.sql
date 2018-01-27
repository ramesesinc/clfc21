[getList]
SELECT a.*
FROM ledgeramnesty_waiver a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnmode = $P{txnmode}
ORDER BY a.dtcreated DESC

[getListByState]
SELECT a.*
FROM ledgeramnesty_waiver a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnmode = $P{txnmode}
	AND a.txnstate = $P{state}
ORDER BY a.dtcreated DESC

[changeState]
UPDATE ledgeramnesty_waiver SET txnstate = $P{txnstate}
WHERE objid = $P{objid}
