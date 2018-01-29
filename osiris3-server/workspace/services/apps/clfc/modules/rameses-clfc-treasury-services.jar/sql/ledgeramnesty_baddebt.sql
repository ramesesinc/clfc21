[getList]
SELECT a.*
FROM ledgeramnesty_baddebt a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnmode = $P{mode}
ORDER BY a.dtcreated DESC

[getListByState]
SELECT a.*
FROM ledgeramnesty_baddebt a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnmode = $P{mode}
	AND a.txnstate = $P{state}
ORDER BY a.dtcreated DESC

[findByLedgeridAndState]
SELECT b.*
FROM ledgeramnesty_baddebt b
WHERE b.ledger_objid = $P{ledgerid}
	AND b.txnstate = $P{state}

[changeState]
UPDATE ledgeramnesty_baddebt SET txnstate = $P{txnstate}
WHERE objid = $P{objid}