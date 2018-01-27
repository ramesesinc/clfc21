[getActiveList]
SELECT a.*
FROM amnesty_active a
LEFT JOIN amnesty_ledgeramnesty_migration m ON a.objid = m.objid
WHERE m.objid IS NULL

[getActiveListByType]
SELECT a.*, am.*
FROM amnesty_active a
INNER JOIN amnesty am ON a.objid = am.objid
LEFT JOIN amnesty_ledgeramnesty_migration m ON a.objid = m.objid
WHERE m.objid IS NULL
	AND am.amnestyoption = $P{type}

[getAmnestyListByState]
SELECT a.*
FROM amnesty a
LEFT JOIN amnesty_ledgeramnesty_migration m ON a.objid = m.objid
WHERE m.objid IS NULL
	AND a.txnstate = $P{state}

[getAmnestyListForDateReturnedResolve]
SELECT a.*
FROM ledgeramnesty a
INNER JOIN amnesty_ledgeramnesty_migration m ON a.objid = m.objid
WHERE a.dtreturned IS NULL

[getAvailedAmnestyForResolve]
SELECT a.*
FROM amnesty am
INNER JOIN ledgeramnesty a ON am.objid = a.objid 
WHERE am.txnstate = 'APPROVED'
	AND am.txntype = 'AVAIL'
	AND a.txnstate <> 'AVAILED'

[getRejectedAmnestyForResolve]
SELECT a.*
FROM amnesty am
INNER JOIN ledgeramnesty a ON a.objid = am.objid
WHERE am.txnstate = 'APPROVED' 
	AND am.txntype = 'REJECT'
	AND a.txnstate <> 'REJECTED'

[getBorrowersForTermResolve]
SELECT a.* 
FROM (
	SELECT DISTINCT a.ledgerid, a.objid
	FROM ledgeramnesty_active a
) q INNER JOIN ledgeramnesty_active a ON q.objid = a.objid
LEFT JOIN ledgeramnesty_resolve_term t ON q.ledgerid = t.objid
WHERE t.objid IS NULL

[xgetBorrowersForTermResolve]
SELECT a.* 
FROM (
	SELECT DISTINCT a.ledgerid, a.objid
	FROM ledgeramnesty_active a
) q INNER JOIN ledgeramnesty_active a ON q.objid = a.objid

[getBorrowersForTermResolveWithNoMaturity]
SELECT a.* 
FROM (
	SELECT DISTINCT a.ledgerid, a.objid
	FROM ledgeramnesty_active a
	WHERE a.dtended IS NULL
) q INNER JOIN ledgeramnesty_active a ON q.objid = a.objid
LEFT JOIN ledgeramnesty_resolve_term t ON q.ledgerid = t.objid
WHERE t.objid IS NULL

[xgetBorrowersForTermResolveWithNoMaturity]
SELECT a.* 
FROM (
	SELECT DISTINCT a.ledgerid, a.objid
	FROM ledgeramnesty_active a
	WHERE a.dtended IS NULL
) q INNER JOIN ledgeramnesty_active a ON q.objid = a.objid

[getActiveAmnestyByLedgerid]
SELECT a.*
FROM ledgeramnesty_active a
WHERE a.ledgerid = $P{ledgerid}
ORDER BY a.dtstarted DESC, a.dtfiled DESC

[findActiveAmnestyByLedgeridAndStartDate]
SELECT a.*
FROM ledgeramnesty_active a
WHERE a.ledgerid = $P{ledgerid}
	AND a.dtstarted = $P{dtstarted}
ORDER BY a.dtstarted DESC, a.dtfiled DESC

[findUnmigratedAmnesty]
SELECT a.*
FROM amnesty_active a
LEFT JOIN amnesty_ledgeramnesty_migration m ON a.objid = m.objid
WHERE m.objid IS NULL

[findUnmigratedAmnestyByType]
SELECT a.*, am.*
FROM amnesty_active a
INNER JOIN amnesty am ON a.objid = am.objid
LEFT JOIN amnesty_ledgeramnesty_migration m ON a.objid = m.objid
WHERE m.objid IS NULL
	AND am.amnestyoption = $P{type}