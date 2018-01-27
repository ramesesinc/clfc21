[getList]
SELECT p.*
FROM phone_followup_result p
WHERE p.borrower_name LIKE $P{searchtext}
ORDER BY p.dtcreated DESC

[getListByState]
SELECT p.*
FROM phone_followup_result p
WHERE p.borrower_name LIKE $P{searchtext}
	AND p.txnstate = $P{state}
ORDER BY p.dtcreated DESC

[getLookupBorrowers]
SELECT a.borrower_objid, a.borrower_name, a.objid AS loanapp_objid, a.appno AS loanapp_appno,
	l.objid AS ledgerid, l.dtmatured AS loanapp_dtmatured, ac.dtreleased AS loanapp_dtreleased,
	a.loanamount AS loanapp_loanamount
FROM loan_ledger l
INNER JOIN loanapp a ON l.appid = a.objid
LEFT JOIN loanapp_capture ac ON a.objid = ac.objid
WHERE a.borrower_name LIKE $P{searchtext}
	AND l.state = 'OPEN'
ORDER BY a.borrower_name
