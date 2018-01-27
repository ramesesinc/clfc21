[getList]
SELECT a.*
FROM ledgeramnesty a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnmode = $P{txnmode}
ORDER BY a.dtcreated DESC

[getListByState]
SELECT a.*
FROM ledgeramnesty a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnstate = $P{state}
	AND a.txnmode = $P{txnmode}
ORDER BY a.dtcreated DESC

[getListByStateAndMode]
SELECT a.*
FROM ledgeramnesty a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnstate = $P{state}
	AND a.txnmode = $P{txnmode}
ORDER BY a.dtcreated DESC

[getDetails]
SELECT d.*
FROM ledgeramnesty_detail d
WHERE d.parentid = $P{objid}

[getActiveAmnestiesByLedgerid]
SELECT ac.* 
FROM ledgeramnesty_active ac
WHERE ac.ledgerid = $P{ledgerid}
ORDER BY ac.dtstarted DESC, ac.dtfiled DESC

[getActiveAmnestiesByLedgeridAndType]
SELECT ac.* 
FROM ledgeramnesty_active ac
WHERE ac.ledgerid = $P{ledgerid}
	AND ac.type = $P{type}
ORDER BY ac.dtstarted DESC, ac.dtfiled DESC

[getPreviousActiveAmnestyByLedgeridAndDate]
SELECT a.*
FROM (
	SELECT a.objid
	FROM ledgeramnesty_active a
	WHERE a.ledgerid = $P{ledgerid}
		AND a.dtstarted < $P{date}
	GROUP BY a.dtstarted, a.objid
	HAVING a.objid IS NOT NULL
	ORDER BY a.dtstarted, a.dtfiled DESC
) q INNER JOIN ledgeramnesty_active a ON q.objid = a.objid
ORDER BY a.dtstarted DESC

[getSucceedingActiveAmnestyByLedgeridAndDate]
SELECT a.*
FROM (
	SELECT a.objid
	FROM ledgeramnesty_active a
	WHERE a.ledgerid = $P{ledgerid}
		AND a.dtstarted > $P{date}
	GROUP BY a.dtstarted, a.objid
	HAVING a.objid IS NOT NULL
	ORDER BY a.dtstarted, a.dtfiled DESC
) q INNER JOIN ledgeramnesty_active a ON q.objid = a.objid
ORDER BY a.dtstarted DESC

[xgetCaptureList]
SELECT a.*
FROM ledgeramnesty a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnstate = $P{state}
ORDER BY a.dtcreated DESC

[xgetCaptureListByState]
SELECT a.*
FROM ledgeramnesty a
WHERE a.borrower_name LIKE $P{searchtext}
ORDER BY a.dtcreated DESC

[xgetCaptureListByStateAndMode]
SELECT a.*
FROM ledgeramnesty a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnstate = $P{state}
	AND a.txnmode = $P{txnmode}
ORDER BY a.dtcreated DESC

[xgetActiveAmnestiesByLedgerid]
SELECT ac.* 
FROM ledgeramnesty_active ac
INNER JOIN ledgeramnesty a ON a.objid = ac.amnestyid
WHERE a.ledger_objid = $P{ledgerid}
ORDER BY ac.dtstarted DESC, ac.dtfiled DESC

[getPostingDetail]
SELECT d.* 
FROM ledgeramnesty_postingdetail d
INNER JOIN ledger_detail_state_type s ON d.state = s.name 
WHERE d.parentid = $P{objid}
ORDER BY d.day, d.dtpaid, d.refno, s.level, d.txndate

[getExpiredListReportData]
SELECT q.*, a.borrower_objid, a.borrower_name
FROM (
	SELECT ac.ledgerid, ac.type, af.amount, af.dtstarted, af.dtended
	FROM ledgeramnesty_active ac 
	INNER JOIN ledgeramnesty_fix af ON ac.refid = af.objid
	WHERE af.dtended BETWEEN $P{startdate} AND $P{enddate}
) q INNER JOIN loan_ledger l ON q.ledgerid = l.objid
INNER JOIN loanapp a ON l.appid = a.objid
ORDER BY q.dtended, a.borrower_name

[findDetail]
SELECT d.*
FROM ledgeramnesty_detail d
WHERE d.parentid = $P{objid}

[findDetailByState]
SELECT d.*
FROM ledgeramnesty_detail d
WHERE d.parentid = $P{objid}
	AND d.txnstate = $P{state}

[findCurrentSendback]
SELECT a.*
FROM ledgeramnesty_sendback a
WHERE a.refid = $P{refid}
ORDER BY a.dtcreated DESC

[findCurrentSendbackByState]
SELECT a.*
FROM ledgeramnesty_sendback a
WHERE a.refid = $P{refid}
	AND a.state = $P{state}
ORDER BY a.dtcreated DESC

[findActiveByStartDateAndLedgerid]
SELECT ac.*
FROM ledgeramnesty_active ac 
WHERE ac.dtstarted = $P{dtstarted}
	AND ac.ledgerid = $P{ledgerid}
ORDER BY ac.dtfiled DESC

[findActiveByDateAndLedgerid]
SELECT ac.*
FROM (
	SELECT a.objid, a.dtstarted, IFNULL(a.dtended, CURDATE()) AS dtended
	FROM ledgeramnesty_active a
	WHERE a.ledgerid = $P{ledgerid}
) qry INNER JOIN ledgeramnesty_active ac ON qry.objid = ac.objid
WHERE $P{date} BETWEEN qry.dtstarted AND qry.dtended
ORDER BY ac.dtstarted DESC, ac.dtfiled DESC

[xxfindActiveByDateAndLedgerid]
SELECT ac.* 
FROM ledgeramnesty_active ac
WHERE $P{date} BETWEEN ac.dtstarted AND ac.dtended
	AND ac.ledgerid = $P{ledgerid}
ORDER BY ac.dtstarted DESC, ac.dtfiled DESC

[xfindActiveByDateAndLedgerid]
SELECT ac.* 
FROM ledgeramnesty_active ac
INNER JOIN ledgeramnesty a ON a.objid = ac.amnestyid
WHERE $P{date} BETWEEN ac.dtstarted AND ac.dtended
	AND a.ledger_objid = $P{ledgerid}
ORDER BY ac.dtstarted DESC, ac.dtfiled DESC

[findActiveByRefid]
SELECT a.*
FROM ledgeramnesty_active a
WHERE a.refid = $P{refid}

[findActiveByLedgeridAndType]
SELECT a.*
FROM ledgeramnesty_active a
WHERE a.ledgerid = $P{ledgerid}
	AND a.type = $P{type}

[findSucceedingActiveByLedgeridAndStartdate]
SELECT a.*
FROM ledgeramnesty_active a
WHERE a.ledgerid = $P{ledgerid}
	AND a.dtstarted > $P{date}
ORDER BY a.dtfiled DESC, a.dtstarted

[findPreviousActiveByLedgeridAndStartDate]
SELECT a.*
FROM ledgeramnesty_active a
WHERE a.ledgerid = $P{ledgerid}
	AND a.dtstarted < $P{date}
ORDER BY a.dtfiled DESC, a.dtstarted

[findCounterPostingDetail]
SELECT COUNT(d.objid) AS counter 
FROM ledgeramnesty_postingdetail d
WHERE d.parentid = $P{objid}

[changeState]
UPDATE ledgeramnesty SET txnstate = $P{txnstate}
WHERE objid = $P{objid}

[removePostingDetails]
DELETE FROM ledgeramnesty_postingdetail
WHERE parentid = $P{objid}