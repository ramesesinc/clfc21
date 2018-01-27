[getList]
SELECT a.*
FROM ledgeramnesty_fix a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnmode = $P{txnmode}
ORDER BY a.dtcreated DESC

[getListByState]
SELECT a.*
FROM ledgeramnesty_fix a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnmode = $P{txnmode}
	AND a.txnstate = $P{state}
ORDER BY a.dtcreated DESC

[findByRefid]
SELECT a.*
FROM ledgeramnesty_fix a 
WHERE a.refid = $P{refid}

[changeState]
UPDATE ledgeramnesty_fix SET txnstate = $P{txnstate}
WHERE objid = $P{objid}