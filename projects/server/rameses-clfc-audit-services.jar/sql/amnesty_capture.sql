[getLedgerLookupList]
SELECT l.objid, a.appno, a.loanamount, a.borrower_name, ac.dtreleased, l.dtmatured
FROM (
	SELECT a.objid
	FROM loanapp a
	INNER JOIN loan_ledger l ON a.objid = l.appid
	WHERE a.borrower_name LIKE $P{searchtext}
		AND l.state = 'OPEN'
	UNION
	SELECT a.objid 
	FROM loanapp a
	INNER JOIN loan_ledger l ON a.objid = l.appid
	WHERE a.appno LIKE $P{searchtext}
		AND l.state = 'OPEN'
) q 
INNER JOIN loanapp a ON q.objid = a.objid
LEFT JOIN loanapp_capture ac ON a.objid = ac.objid
INNER JOIN loan_ledger l ON a.objid = l.appid
ORDER BY a.borrower_name, a.appno DESC

[getForFixLedgerLookupList]
SELECT l.objid, a.appno, a.loanamount, a.borrower_name, ac.dtreleased, l.dtmatured
FROM (
	SELECT a.objid
	FROM loanapp a
	INNER JOIN loan_ledger l ON a.objid = l.appid
	WHERE a.borrower_name LIKE $P{searchtext}
	UNION
	SELECT a.objid 
	FROM loanapp a
	INNER JOIN loan_ledger l ON a.objid = l.appid
	WHERE a.appno LIKE $P{searchtext}
) q 
INNER JOIN loanapp a ON q.objid = a.objid
LEFT JOIN loanapp_capture ac ON a.objid = ac.objid
INNER JOIN loan_ledger l ON a.objid = l.appid
ORDER BY a.borrower_name, ac.dtreleased DESC, a.appno DESC