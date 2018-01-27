[getList]
SELECT c.objid AS borrower_objid, c.name AS borrower_name, c.address_text AS borrower_address,
	la.appno AS loanapp_appno, la.objid AS loanapp_objid, ll.*, la.loanamount AS loanapp_loanamount,
	DATE_ADD(ll.dtstarted, INTERVAL -1 DAY) AS dtreleased
FROM loan_ledger ll
INNER JOIN customer c ON ll.acctid=c.objid
INNER JOIN loanapp la ON ll.appid=la.objid
WHERE ll.state = 'OPEN'
	AND CURDATE() > ll.dtmatured
	AND c.name LIKE $P{searchtext}

[getLookupList]
SELECT l.*, b.objid AS borrower_objid, b.name AS borrower_name, b.address AS borrower_address,
	a.appno AS loanapp_appno, a.objid AS loanapp_objid, a.loanamount AS loanapp_loanamount,
	lc.dtreleased
FROM (
	SELECT l.objid
	FROM loan_ledger l
	INNER JOIN loanapp a ON l.appid = a.objid
	WHERE l.state = 'OPEN'
		AND CURDATE() > l.dtmatured
		AND a.appno LIKE $P{searchtext}
	UNION 
	SELECT l.objid
	FROM loan_ledger l
	INNER JOIN loanapp a ON l.appid = a.objid
	INNER JOIN borrower b ON a.borrower_objid = b.objid
	WHERE l.state = 'OPEN'
		AND CURDATE() > l.dtmatured
		AND b.name LIKE $P{searchtext}
) qry INNER JOIN loan_ledger l ON qry.objid = l.objid
INNER JOIN loanapp a ON l.appid = a.objid
INNER JOIN borrower b ON a.borrower_objid = b.objid
LEFT JOIN loanapp_capture lc ON a.objid = lc.objid
ORDER BY b.name, lc.dtreleased DESC